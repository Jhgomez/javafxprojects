package tutorial.tutorial.examples.networking;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.util.Duration;
import tutorial.tutorial.examples.networking.events.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

// This class is using ServerSocket and Socket APIs which uses TCP under the hood
// remember TCP is used when you need to guarantee packages will be delivered, so it
// is reliable but is less performant than UDP, that is why we have an implementation using
//
public class ClientServerSimpleGame {
    private final List<Node> groupNodes;
    private List<Node> gameNodes;
    private final ComboBox<String> transformationsComboBox = new ComboBox<>();
    ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
    private final Map<Short, Player> playerNodes = new ConcurrentHashMap<>();
    private final Map<Short, Runnable> clientClosingCallBacks = new ConcurrentHashMap<>();
    final Map<Short, ObjectOutputStream> writers = new ConcurrentHashMap<>();
    private final KeyCode[] keys = new KeyCode[4];
    private final AtomicInteger idCounter = new AtomicInteger(0);
    private ObjectOutputStream out;
    private short playerId = -1;

    private ServerSocket serverSocket;
    Socket clientSocket;
    private final Scene scene;
    private final Stage stage;
    private boolean isServer = false;

    public ClientServerSimpleGame() {
        stage = new Stage();

        var group = new Group();
        groupNodes = group.getChildren();

        scene = new Scene(group, 600, 400);

        scene.setFill(Paint.valueOf("#000000"));
    }

    public void displayScreen(Runnable runnable) {
        HashMap<String, Function<Scene, Pane>> transformations1 = getScreens();
        ObservableList<String> options = FXCollections.observableArrayList(transformations1.keySet());

        transformationsComboBox.setItems(options);
        transformationsComboBox.setPromptText("Choose Transformation");

        Slider slider = new Slider(0, 100, 10);
        slider.setBlockIncrement(1);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit(10);
        slider.setMinorTickCount(5);

        Timeline keyBoarTimeline =  new Timeline(new KeyFrame(Duration.millis(slider.getValue()*5), _ -> keyBoardUpdate()));

        keyBoarTimeline.setCycleCount(Timeline.INDEFINITE);
        keyBoarTimeline.play();

        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            // reconfigure powerups timer
            keyBoarTimeline.stop();
            keyBoarTimeline.getKeyFrames().clear();
            keyBoarTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(newValue.doubleValue() * 5), _ -> keyBoardUpdate()));
            keyBoarTimeline.play();
        });


        transformationsComboBox.valueProperty().addListener((observable, oldVal, newVal) -> {
            groupNodes.clear();

            if (transformations1.get(newVal) != null) {
                Pane newNode = transformations1.get(newVal).apply(scene);
                newNode.getChildren().add(slider);
                groupNodes.add(newNode);

                stage.setWidth(newNode.getPrefWidth());
                stage.setHeight(newNode.getPrefHeight());

            } else {
                stage.sizeToScene();
            }

            stage.centerOnScreen();
        });

        transformationsComboBox.setLayoutX(10);
        transformationsComboBox.setLayoutY(10);

        groupNodes.add(transformationsComboBox);

        stage.setScene(scene);

        stage.show();

        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.A) {
                keys[0] = KeyCode.A;
            } else if (e.getCode() == KeyCode.D) {
                keys[1] = KeyCode.D;
            } else if (e.getCode() == KeyCode.W) {
                keys[2] = KeyCode.W;
            } else if (e.getCode() == KeyCode.S) {
                keys[3] = KeyCode.S;
            }
        });

        scene.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.A) {
                keys[0] = null;
            } else if (e.getCode() == KeyCode.D) {
                keys[1] = null;
            } else if (e.getCode() == KeyCode.W) {
                keys[2] = null;
            } else if (e.getCode() == KeyCode.S) {
                keys[3] = null;
            }
        });

        stage.setOnCloseRequest(e -> {
            clientClosingCallBacks.forEach((key, value) -> {
                //io.println("closing client socket 100");
                value.run();
            });

            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException ex) {
                    executor.close();
                    runnable.run();
                    throw new RuntimeException(ex);
                }
            }

            if (clientSocket != null) {
                try {
                    clientSocket.close();
                } catch (IOException ex) {
                    executor.close();
                    runnable.run();
                    throw new RuntimeException(ex);
                }
            }

            // closing executor waits until all task have finished, which is a good way to ensure
            // all resources are cleared(means all sockets are closed)
            executor.close();
            runnable.run();
        });
    }

    private void keyBoardUpdate() {
        // server handles updates differently
        boolean shouldUpdate = false;
        for (var k : keys) {
            if (k != null) {
                shouldUpdate = true;
                break;
            }
        }

        if (isServer && shouldUpdate) {
            var serverPlayer = playerNodes.get(playerId);

            translatePlayer(serverPlayer, keys);

            var updateRequest = new TranslatePlayer(serverPlayer.getPlayerId(), serverPlayer.getTranslateX(), serverPlayer.getTranslateY());

            writers.forEach((playerId, outputStream) -> {
                executor.execute(() -> {
                    try {
                        outputStream.writeObject(updateRequest);
                        outputStream.flush();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            });

            return;
        } else if (!isServer && shouldUpdate) {
            if (out != null) {
                try {
                    KeyCode[] newKeys = Arrays.copyOf(keys, keys.length);
                    if (playerId < 0) {
                        IO.println("Player id not init");
                        return;
                    } else if (playerId > 1000){
                        IO.println("Player id too high");
                    }

                    out.writeObject(new PressedKeys(playerId, newKeys));
                    out.flush();
                } catch (IOException ex) {
                    //io.println("Error sending keys to server");
                }
            }
        }
    }

    private HashMap<String, Function<Scene, Pane>> getScreens() {
        var map = new HashMap<String, Function<Scene, Pane>>();

        map.put("Server", this::serverPlayer);
        map.put("Client", this::clientPlayer);

        return map;
    }

    private Pane clientPlayer(Scene scene) {
        var pane = new Pane();

        pane.setPrefSize(225, 175);
        pane.setStyle("-fx-background-color: #123456");

        gameNodes = Collections.synchronizedList(pane.getChildren());

        initClientSocket();

        return pane;
    }

    @SuppressWarnings("unchecked")
    private void initClientSocket() {
        executor.execute(() -> {
            try {
                clientSocket = new Socket("192.168.1.5", 12346);

                clientClosingCallBacks.put((short) 1, () -> {
                    try {
                        clientSocket.close();
                    } catch (IOException e) {
                        //io.println("Couldn't close playerFactory screen");
                        throw new RuntimeException(e);
                    }
                });

                var in = new ObjectInputStream(new BufferedInputStream(clientSocket.getInputStream()));

                // Start a thread to handle incoming messages
                executor.execute(() -> {

                    var response = new Object();

                    try {
                        response = in.readObject();
                    } catch (IOException | ClassNotFoundException e) {
                        //io.println("unable to read first response " + e.getMessage());
                    }

                    try {
                        out = new ObjectOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    while (response != null) {
                        switch (response) {
                            case Map<?, ?> remotePlayersFactory
                                    when remotePlayersFactory.keySet().iterator().next() instanceof Short
                                    && remotePlayersFactory.entrySet().iterator().next().getValue() instanceof PlayerFactory -> {

                                // this case is not currently valid, I was previously storing playerFactory factories in a map
                                // but not anymore
                                var entrySet = ((Map<Short, PlayerFactory>) remotePlayersFactory).entrySet();
                                Platform.runLater(() -> {
                                    entrySet.forEach(entry -> {
                                        var player = entry.getValue().getPlayer();
                                        playerNodes.put(entry.getKey(), player);
                                        gameNodes.add(player);

                                        //io.println("Player " + 1 + " screen added playerId " + entry.getKey());
                                    });
                                });
                            }
                            // this one has to be synchronous to avoid missing updates
                            case AddPlayer p -> addPlayer(p.playerFactory());
                            // this one helps us assign the playerId
                            case SetUpNewPlayer p -> {
                                addPlayer(p.playerFactory());

                                playerId = p.playerFactory().getPlayerId();
                            }
                            case TranslatePlayer request -> executor.execute(() -> {
                                if (request.playerId() < 0 || playerNodes.get(request.playerId()) == null) {
                                    IO.println("index invalido " + request.playerId());
                                } else {
                                    final var player = playerNodes.get(request.playerId());

//                                    IO.println("no nulo " + request.playerId());
                                    Platform.runLater(() -> {
                                        player.setTranslateX(request.x());
                                        player.setTranslateY(request.y());
                                    });
                                }
                            });
                            case DeletePlayer d -> Platform.runLater(() -> {
                                gameNodes.remove(playerNodes.get(d.id()));
                                playerNodes.remove(d.id());
                            });
                            default -> {} //io.println("Couldn't read response or cast was not successful, waiting for next message");
                        }

                        try {
                            response = in.readObject();
                        } catch (ClassNotFoundException e) {
                            response = new Object();
                            //io.println("unable to read response " + e.getMessage());
                        } catch (IOException e) {
                            response = null;
                            //io.println("unable to read response " + e.getMessage());
                        }
                    }
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void addPlayer(PlayerFactory p) {
        var player = p.getPlayer();

        Platform.runLater(() -> gameNodes.add(player));

        playerNodes.put(p.getPlayerId(), player);

        //io.println("Player " + 1 + " screen added single playerFactory Id " + p.getPlayerId());
    }

    private Pane serverPlayer(Scene scene) {
        isServer = true;
        playerId = (short) idCounter.getAndIncrement();

        final Random random = new Random();

        initServerSocket(random);

        var playerFactory = new PlayerFactory(
                playerId,
                "RED",
                random.nextDouble(400),
                random.nextDouble(300)
        );

        var player = playerFactory.getPlayer();

        var pane = new Pane(player);

        playerNodes.put(playerFactory.getPlayerId(), player);

        gameNodes = Collections.synchronizedList(pane.getChildren());

        pane.setPrefSize(500, 400);
        pane.setStyle("-fx-background-color: #123456");

        return pane;
    }

    private void initServerSocket(Random random) {
        try {
            this.serverSocket = new ServerSocket( 12346);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        executor.execute(()-> {
            try {
                while (!serverSocket.isClosed()) {
                    //io.println("server ready");
                    Socket client;
                    try {
                        client = serverSocket.accept();
                        //io.println("client connected");
                    } catch (SocketException e) {
                        //io.println("Server closed");
                        return;
                    }

                    executor.execute(() -> {
                        var id = (short) idCounter.getAndIncrement();

                        clientClosingCallBacks.put(id, () -> {
                            try {
                                client.close();
                            } catch (IOException e) {
                                System.err.println("Error closing client socket");
                            }
                        });

                        try {
                            var out = new ObjectOutputStream(new BufferedOutputStream(client.getOutputStream()));

                            // virtual thread for creating players
                            executor.execute(() -> {
                                var playerFactory = new PlayerFactory(
                                        id,
                                        "WHITE",
                                        random.nextDouble(400),
                                        random.nextDouble(300)
                                );

                                //io.println("new playerFactory " + id);

                                //sends current players
                                playerNodes.forEach((playerId, player) -> {
                                    try {

                                        // we could have done it this way but we lose the iterator ability a concurrent map has
                                        // which is iterate on entries that are added concurrently so we will do it with a for each
//                                                var currentPlayers = playerNodes
//                                                        .entrySet()
//                                                        .stream()
//                                                        .collect(
//                                                                Collectors.toMap(
//                                                                        Map.Entry::getKey,
//                                                                        entry -> entry.getValue().toFactory()
//                                                                )
//                                                        );
//                                                out.writeObject(currentPlayers);

                                        out.writeObject(new AddPlayer(player.toFactory()));
                                        out.flush();
                                        //io.println("wrote prev playerFactory " + playerId + " to playerId " + id);
                                    } catch (IOException e) {
//                                    //io.println("Error sending previous players to client " + playerId);
                                        //io.println("Error sending previous players " + playerId + " to client " + id);
                                        throw new RuntimeException(e);
                                    }
                                });

                                // concurrently add to all collections used in controlling the connections and trasmission
                                // logic, and keeping clients updated
                                executor.execute(() -> {
                                    writers.put(id, out);

                                    // concurrently send new playerFactory individually to all connected clients
                                    executor.execute(() -> {
                                        var request = new AddPlayer(playerFactory);

                                        writers.forEach((playerId, writter) -> {
                                            try {
                                                if (playerId == id) {
                                                    writter.writeObject(new SetUpNewPlayer(playerFactory));
                                                    writter.flush();
                                                } else {
                                                    writter.writeObject(request);
                                                    writter.flush();
                                                }
                                            } catch (IOException e) {
                                                //io.println("Error sending new playerFactory " + id + " to playerFactory " + playerId);
                                            }
                                        });
                                    });

                                    // add new playerFactory visually in server
                                    Platform.runLater(() -> {
                                        var player = playerFactory.getPlayer();
                                        playerNodes.put(playerFactory.getPlayerId(), player);

                                        gameNodes.add(player);
                                        //io.println("added playerFactory to game nodes in parent " + id);
                                    });
                                });
                            });

                            // Virtual thread is blocked here because it will waiting for new streams from client
                            try {
                                //io.println("1 playerId " + id);
                                var in = new ObjectInputStream(new BufferedInputStream(client.getInputStream()));
                                //io.println("2 playerId " + id);

                                var request = in.readObject();
                                //io.println("3 playerId " + id);
                                while (request != null) {
//                                    if (request instanceof Request r) {
//
//                                    }
                                    switch (request) {
                                        case PressedKeys r -> {
                                            executor.execute(() -> {
                                                var requestedPlayer = playerNodes.get(r.playerId());

                                                translatePlayer(requestedPlayer, r.keys());
//                                                //io.println("llegaron keys " + r.keys());

                                                executor.execute(() -> {
                                                    var updatedPlayerInfo =
                                                            new TranslatePlayer(r.playerId(), requestedPlayer.getTranslateX(), requestedPlayer.getTranslateY());

                                                    if (r.playerId() < 0) {
                                                        IO.println("2 Player id not correct");
                                                    }

                                                    writers.forEach((playerId, writer) -> {
                                                        executor.execute(() -> {
                                                            try {
                                                                writer.writeObject(updatedPlayerInfo);
                                                                writer.flush();
                                                            } catch (IOException e) {
                                                                //io.println("Error sending updating playerFactory " + r.playerId() + " in playerFactory " + playerId);
                                                            }
                                                        });
                                                    });
                                                });
                                            });
                                        }
                                        case String _ -> {} //io.println("Client sent message " + request);
                                        default -> throw new IllegalStateException("Unexpected value: " + request);
                                    }

                                    request = in.readObject();
                                }
                            } catch (EOFException | ClassNotFoundException e) {
                                // An EOFException will be throw when the corresponding ObjectOutputStream(in the client side)
                                // to ObjectInputStream we are reading objects from, is disconnected, this is what would've
                                // happened if we are reading objects from a file and it reaches the end of the file
                                // it won't return null or -1 as in other InputStreams, there is a way to tell if there is
                                // more bytes to read with the `available` method to avoid relying on the EOF exception to
                                // tell when a stream is either closed or reached end of file, but it seems that it could not be too
                                // accurate and return 0 even when there is some bytes left in the stream, so we rather
                                // using the catch to tell when the stream is closed or reached end of file, when working
                                // with files this exception is thrown when the end of file is reached, and when working
                                // with sockets it is thrown when the connection is closed
                                System.out.println("Client disconnected after catch in server");
                                writers.remove(id);
                                // inform all other writers a playerFactory has disconnected

                                var deleteRequest = new DeletePlayer(id);
                                writers.forEach((playerId, writer) -> {
                                    try {
                                        writer.writeObject(deleteRequest);
                                        writer.flush();
                                    } catch (IOException ex) {
                                        //io.println("Error sending delete request of " + id + " to playerFactory playerId " + playerId);
                                    }
                                });

                                clientClosingCallBacks.get(id).run();
                                clientClosingCallBacks.remove(id);


                                Platform.runLater(() -> {
                                    gameNodes.remove(playerNodes.get(id));
                                    playerNodes.remove(id);
//                                    playersFactory.remove(playerId);
                                });

                                try {
                                    client.close();
                                } catch (IOException ex) {
                                    throw new RuntimeException(ex);
                                }
                            }

                            System.out.println("Player " + id + " listener thread done");
                        } catch (IOException e) {
                            try {
                                client.close();
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }

//                            throw new RuntimeException(e);
                        }
                    });
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void translatePlayer(Player requestedPlayer, KeyCode[] pressedKeys) {
        for (var k : pressedKeys) {
            if (k == KeyCode.W) {
                requestedPlayer.setTranslateY(requestedPlayer.getTranslateY() - 5);
//                                                        IO.println("W");
            } else if (k == KeyCode.A) {
                requestedPlayer.setTranslateX(requestedPlayer.getTranslateX() - 5);
//                                                        IO.println("A");
            } else if (k == KeyCode.S) {
                requestedPlayer.setTranslateY(requestedPlayer.getTranslateY() + 5);
//                                                        IO.println("S");
            } else if (k == KeyCode.D) {
                requestedPlayer.setTranslateX(requestedPlayer.getTranslateX() + 5);
//                                                        IO.println("D");
            }
        }
    }
}
