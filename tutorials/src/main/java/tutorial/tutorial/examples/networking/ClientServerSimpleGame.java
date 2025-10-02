package tutorial.tutorial.examples.networking;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class ClientServerSimpleGame {
    private List<Node> groupNodes;
    private List<Node> gameNodes;
    private final ComboBox<String> transformationsComboBox = new ComboBox<>();
    ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
//    private Map<Short, PlayerFactory> playersFactory = new ConcurrentHashMap<>();
    private Map<Short, Player> playerNodes = new ConcurrentHashMap<>();
    private final Map<Short, Runnable> clientClosingCallBacks = new ConcurrentHashMap<>();
    private final AtomicInteger idCounter = new AtomicInteger(0);

    private ServerSocket serverSocket;
    Socket clientSocket;
    private final Scene scene;
    private final Stage stage;

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

        transformationsComboBox.valueProperty().addListener((observable, oldVal, newVal) -> {
//            if (currentMenu != null) currentMenu.clearResources();

            groupNodes.clear();

            if (transformations1.get(newVal) != null) {
                Pane newNode = transformations1.get(newVal).apply(scene);
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

        stage.setOnCloseRequest(e -> {
            clientClosingCallBacks.forEach((key, value) -> {
                IO.println("closing client socket 100");
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

    private HashMap<String, Function<Scene, Pane>> getScreens() {
        var map = new HashMap<String, Function<Scene, Pane>>();

        map.put("Server", this::serverPlayer);
        map.put("Client", this::clientPlayer);

        return map;
    }

    private Pane clientPlayer(Scene scene) {
        var pane = new Pane();

        pane.setPrefSize(800, 600);
        pane.setStyle("-fx-background-color: #123456");

        gameNodes = Collections.synchronizedList(pane.getChildren());

        initClientSocket();

        return pane;
    }

    @SuppressWarnings("unchecked")
    private void initClientSocket() {
        executor.execute(() -> {
            try {
                clientSocket = new Socket("localhost", 12346);

                clientClosingCallBacks.put((short) 1, () -> {
                    try {
                        clientSocket.close();
                    } catch (IOException e) {
                        IO.println("Couldn't close playerFactory screen");
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
                        IO.println("unable to read first response " + e.getMessage());
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

                                        IO.println("Player " + 1 + " screen added playerId " + entry.getKey());
                                    });
                                });
                            }
                            case AddPlayer p -> {
                                addPlayer(p.playerFactory());
                            }
                            case SetUpNewPlayer p -> {
                                addPlayer(p.playerFactory());

                                final ObjectOutputStream out;

                                try {
                                    out = new ObjectOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));

                                    scene.setOnKeyPressed(e -> {
                                        try {
                                            if (e.getCode() == KeyCode.A) {
                                                out.writeObject(new UpdatePlayer(p.playerFactory().getPlayerId(), KeyCode.A));
                                                out.flush();
                                            } else if (e.getCode() == KeyCode.D) {
                                                out.writeObject(new UpdatePlayer(p.playerFactory().getPlayerId(), KeyCode.D));
                                                out.flush();
                                            } else if (e.getCode() == KeyCode.W) {
                                                out.writeObject(new UpdatePlayer(p.playerFactory().getPlayerId(), KeyCode.W));
                                                out.flush();
                                            } else if (e.getCode() == KeyCode.S) {
                                                out.writeObject(new UpdatePlayer(p.playerFactory().getPlayerId(), KeyCode.S));
                                                out.flush();
                                            }
                                        } catch (IOException ex) {
                                            throw new RuntimeException(ex);
                                        }
                                    });
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }


                            }
                            case DeletePlayer d -> Platform.runLater(() -> {
                                gameNodes.remove(playerNodes.get(d.id()));
                                playerNodes.remove(d.id());
                            });
                            default -> IO.println("Couldn't read response or cast was not successful, waiting for next message");
                        }

                        try {
                            response = in.readObject();
                        } catch (ClassNotFoundException e) {
                            response = new Object();
                            IO.println("unable to read response " + e.getMessage());
                        } catch (IOException e) {
                            response = null;
                            IO.println("unable to read response " + e.getMessage());
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

        IO.println("Player " + 1 + " screen added single playerFactory Id " + p.getPlayerId());
    }

    private Pane serverPlayer(Scene scene) {
        final Random random = new Random();

        initServerSocket(random);

        var playerFactory = new PlayerFactory(
                (short) idCounter.getAndIncrement(),
                "RED",
                random.nextDouble(775),
                random.nextDouble(575)
        );

        var player = playerFactory.getPlayer();

        var pane = new Pane(player);

        playerNodes.put(playerFactory.getPlayerId(), player);

        gameNodes = Collections.synchronizedList(pane.getChildren());

        pane.setPrefSize(800, 600);
        pane.setStyle("-fx-background-color: #123456");

        return pane;
    }

    private void initServerSocket(Random random) {
        final Map<Short, ObjectOutputStream> writters = new ConcurrentHashMap<>();
        final Deque<PlayerFactory> movedPlayers = new ArrayDeque<>();


        try {
            this.serverSocket = new ServerSocket( 12346);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        executor.execute(()-> {
            try {
                while (!serverSocket.isClosed()) {
                    IO.println("server ready");
                    Socket client;
                    try {
                        client = serverSocket.accept();
                        IO.println("client connected");
                    } catch (SocketException e) {
                        IO.println("Server closed");
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
                                        random.nextDouble(775),
                                        random.nextDouble(575)
                                );

                                IO.println("new playerFactory " + id);

                                //sends a message and all current players
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
                                        IO.println("wrote prev playerFactory " + playerId + " to playerId " + id);
                                    } catch (IOException e) {
//                                    IO.println("Error sending previous players to client " + id);
                                        IO.println("Error sending previous players " + playerId + " to client " + id);
                                        throw new RuntimeException(e);
                                    }
                                });

                                // concurrently add to all collections used in controlling the connections and trasmission
                                // logic, and keeping clients updated
                                executor.execute(() -> {
//                                    players.put(playerFactory.getPlayerId(), new Player((short) 2));
//                                    playersFactory.put(playerFactory.getPlayerId(), playerFactory);
//                                    players.put(playerFactory.getPlayerId(), new Player((short) 3));

                                    writters.put(id, out);

                                    // concurrently send new playerFactory individually to all connected clients
                                    executor.execute(() -> {
                                        var request = new AddPlayer(playerFactory);

                                        writters.forEach((playerId, writter) -> {
                                            try {
                                                if (playerId == id) {
                                                    writter.writeObject(new SetUpNewPlayer(playerFactory));
                                                    writter.flush();
                                                } else {
                                                    writter.writeObject(request);
                                                    writter.flush();
                                                }
                                            } catch (IOException e) {
                                                IO.println("Error sending new playerFactory " + id + " to playerFactory " + playerId);
                                            }
                                        });
                                    });

                                    // add new playerFactory visually in server
                                    Platform.runLater(() -> {
                                        var player = playerFactory.getPlayer();
                                        playerNodes.put(playerFactory.getPlayerId(), player);

                                        gameNodes.add(player);
                                        IO.println("added playerFactory to game nodes in parent " + id);
                                    });
                                });
                            });

                            // Virtual thread is blocked here because it will waiting for new streams from client
                            try {
                                IO.println("1 id " + id);
                                var in = new ObjectInputStream(new BufferedInputStream(client.getInputStream()));
                                IO.println("2 id " + id);

                                var request = in.readObject();
                                IO.println("3 id " + id);
                                while (request != null) {
//                                    if (request instanceof Request r) {
//
//                                    }
                                    switch (request) {
                                        case UpdatePlayer r -> {
                                            var requestedPlayer = playerNodes.get(r.playerId());

                                            executor.execute(() -> {
                                                if (r.code() == KeyCode.W) {
                                                    requestedPlayer.setTranslateY(requestedPlayer.getTranslateY() - 5);
                                                } else if (r.code() == KeyCode.A) {
                                                    requestedPlayer.setTranslateX(requestedPlayer.getTranslateX() - 5);
                                                } else if (r.code() == KeyCode.S) {
                                                    requestedPlayer.setTranslateY(requestedPlayer.getTranslateY() + 5);
                                                } else if (r.code() == KeyCode.D) {
                                                    requestedPlayer.setTranslateX(requestedPlayer.getTranslateX() + 5);
                                                }
                                            });

                                            executor.execute(() -> {
                                                var updatedPlayerInfo =
                                                        new Update(r.playerId(), requestedPlayer.getTranslateX(), requestedPlayer.getTranslateY());

                                                writters.forEach((playerId, writer) -> {
                                                    try {
                                                        writer.writeObject(updatedPlayerInfo);
                                                        writer.flush();
                                                    } catch (IOException e) {
                                                        IO.println("Error sending updating playerFactory " + r.playerId() + " in playerFactory " + playerId);
                                                    }
                                                });
                                            });
                                        }
                                        case String _ -> IO.println("Client sent message " + request);
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
                                writters.remove(id);
                                // inform all other writers a playerFactory has disconnected

                                var deleteRequest = new DeletePlayer(id);
                                writters.forEach((playerId, writer) -> {
                                    try {
                                        writer.writeObject(deleteRequest);
                                        writer.flush();
                                    } catch (IOException ex) {
                                        IO.println("Error sending delete request of " + id + " to playerFactory id " + playerId);
                                    }
                                });

                                clientClosingCallBacks.get(id).run();
                                clientClosingCallBacks.remove(id);


                                Platform.runLater(() -> {
                                    gameNodes.remove(playerNodes.get(id));
                                    playerNodes.remove(id);
//                                    playersFactory.remove(id);
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

                            throw new RuntimeException(e);
                        }
                    });
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
