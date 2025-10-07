package tutorial.tutorial.examples.networking.udp;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.util.Duration;
import tutorial.tutorial.examples.networking.Player;
import tutorial.tutorial.examples.networking.PlayerFactory;
import tutorial.tutorial.examples.networking.events.*;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

// This class is using DatagramSocket along DatagramPacket API, these APIs uses UDP
// under the hood, which is more performant but less reliable
public class UDPClientServerSimpleGame {
    private final int SERVER_PORT = 9876;
    private final Random random = new Random();
    private final List<Node> groupNodes;
    private List<Node> gameNodes;
    private final ComboBox<String> transformationsComboBox = new ComboBox<>();
    ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
    private final Map<Short, Player> playerNodes = new ConcurrentHashMap<>();
    private final Map<Short, Runnable> clientClosingCallBacks = new ConcurrentHashMap<>();
    final Map<Short, DatagramPacket> writers = new ConcurrentHashMap<>();
    private final KeyCode[] keys = new KeyCode[4];
    private final AtomicInteger idCounter = new AtomicInteger(0);
    private final AtomicInteger pooledCount = new AtomicInteger(0);
    private final AtomicInteger createdInputsCount = new AtomicInteger(0);
    private short playerId = -1;
    private final InetAddress serverAddress = InetAddress.getByName("localhost");
    private final Queue<OutputSetUp> outputObjects = new ConcurrentLinkedQueue<>();
    private final Queue<InputSetUp> inputObjects = new ConcurrentLinkedQueue<>();

    private OutputSetUp outputSetUp = null;
    private InputSetUp inputSetUp = null;

    private DatagramSocket serverSocket;
    private DatagramSocket clientSocket;
    private final Scene scene;
    private final Stage stage;
    private boolean isServer = false;

    public UDPClientServerSimpleGame() throws UnknownHostException {
        stage = new Stage();

        var group = new Group();
        groupNodes = group.getChildren();

        scene = new Scene(group, 600, 400);

        scene.setFill(Paint.valueOf("#000000"));
    }

    public void displayScreen(Runnable runnable) {
        try {

            var readerBuffer = new byte[0];

            for (var i = 0; i < 10; i++) {
                // objects used in sending datagrams
                var byteStream = new ByteArrayOutputStream();
                var objectStream = new ObjectOutputStream(byteStream);

                outputObjects.add(new OutputSetUp(byteStream, objectStream));

//                // object used in receiving datagrams
//                var buffer = new byte[256];
//
//                var datagramPacket = new DatagramPacket(buffer, 256);
//
////                var byteInputStream = new ByteArrayInputStream(datagramPacket.getData());
//                var byteInputStream = new ByteArrayInputStream(buffer);
//
//                var objectInputStream = new ObjectInputStream(new BufferedInputStream(byteInputStream));
//
//                inputObjects.add(new InputSetUp(datagramPacket, byteInputStream, objectInputStream));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }



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

                var button = new Button("NOdes");

                button.setLayoutX(170);
                button.setLayoutY(10);

                button.setOnAction(e -> {
                    writers.forEach((playerId, writer) -> IO.println("Updating powerups timer for player " + playerId + " to " + writer.getPort()));
                });

                newNode.getChildren().addAll(slider, button);

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
            if (!isServer) {
                try {
                    var arrayOutputStream = new ByteArrayOutputStream();
                    var objectOutputStream = new ObjectOutputStream(arrayOutputStream);

                    objectOutputStream.writeObject(new DropClient(playerId));

                    var bytes = arrayOutputStream.toByteArray();

                    var datagram = new DatagramPacket(
                            bytes,
                            bytes.length,
                            serverAddress,
                            SERVER_PORT
                    );

                    clientSocket.send(datagram);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }

            clientClosingCallBacks.forEach((key, value) -> {
                //io.println("closing client socket 100");
                value.run();
            });

            if (serverSocket != null) {
                serverSocket.close();
            }

            if (clientSocket != null) {
                clientSocket.close();
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
                        var byteArrayOutputStream = new ByteArrayOutputStream();
                        var objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);

                        objectOutputStream.writeObject(updateRequest);

                         var bytes = byteArrayOutputStream.toByteArray();

                         var datagram = new DatagramPacket(
                                 bytes,
                                 bytes.length,
                                 outputStream.getAddress(),
                                 outputStream.getPort()
                         );

                         serverSocket.send(datagram);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            });

            return;
        } else if (!isServer && shouldUpdate) {
            if (clientSocket != null) {
                try {
                    KeyCode[] newKeys = Arrays.copyOf(keys, keys.length);
                    if (playerId < 0) {
                        IO.println("Player id not init");
                        return;
                    } else if (playerId > 1000) {
                        IO.println("Player id too high");
                    }

                    var byteArrayOutputStream = new ByteArrayOutputStream();
                    var objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);

                    objectOutputStream.writeObject(new PressedKeys(playerId, newKeys));
//                    outputObjects.objectOutputStream().flush();

                    var bytes =  byteArrayOutputStream.toByteArray();

                    // Create a packet to send the data
                    DatagramPacket sendPacket = new DatagramPacket(
                            bytes,
                            bytes.length,
                            serverAddress,
                            SERVER_PORT
                    );

                    // Send the packet
                    clientSocket.send(sendPacket);
                } catch (IOException ex) {
//                    io.println("Error sending keys to server");
                    throw new RuntimeException(ex);
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
                clientSocket = new DatagramSocket();

                IO.println("Starting conn");

                clientClosingCallBacks.put((short) 1, clientSocket::close);

                var arrayOutputStream = new ByteArrayOutputStream();
                var objectOutputStream = new ObjectOutputStream(arrayOutputStream);

                objectOutputStream.writeObject(new NewConnection());

                var bytes = arrayOutputStream.toByteArray();

                var datagram = new DatagramPacket(
                        bytes,
                        bytes.length,
                        serverAddress,
                        SERVER_PORT
                );

                IO.println("Sending conn");
                clientSocket.send(datagram);

                // Start a thread to handle incoming messages
                executor.execute(() -> {

                    var response = new Object();

                    while (response != null) {
                        try {
//                            IO.println("Listening response from server");
                            var buffer = new byte[2048];
                            var datagramPacket = new DatagramPacket(buffer, buffer.length);

                            clientSocket.receive(datagramPacket);

                            var byteInputStream = new ByteArrayInputStream(datagramPacket.getData());

                            var objectInputStream = new ObjectInputStream(byteInputStream);

                            response = objectInputStream.readObject();
//                            IO.println("received response from server");

                            switch (response) {
                                // this one has to be synchronous to avoid missing updates
                                case AddPlayer p -> {
                                    IO.println("client adding prev player");
                                    addPlayer(p.playerFactory());
                                }
                                // this one helps us assign the playerId
                                case SetUpNewPlayer p -> {
                                    IO.println("setting up new player");
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
                                default -> {
                                } //io.println("Couldn't read response or cast was not successful, waiting for next message");
                            }

//                            inputObjects.byteArrayInputStream().close();
//                            inputObjects.objectInputStream().close();

//                            inputObjects.packet().setData(new byte[2048]);
//                            this.inputObjects.add(inputObjects);
                        } catch (IOException | ClassNotFoundException e) {
                            response = null;
                            clientSocket.close();
                            IO.println("Client closed with error");
                        }
                    }
                    IO.println("Client Socket listener stoped without error");
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
            this.serverSocket = new DatagramSocket(SERVER_PORT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        executor.execute(() -> {

            // Virtual thread is blocked here because it will waiting for new streams from client
            try {
                // io.println("1 playerId " + id);
                // io.println("2 playerId " + id);

                var request = new Object();
                //io.println("3 playerId " + id);

                while (true) {
                    var buffer = new byte[2048];
                    var datagramPacket = new DatagramPacket(buffer, buffer.length);

                    serverSocket.receive(datagramPacket);

                    var byteInputStream = new ByteArrayInputStream(datagramPacket.getData());

                    var objectInputStream = new ObjectInputStream(byteInputStream);

//                    request = inputObjects.objectInputStream().readObject();
                    request = objectInputStream.readObject();;

                    switch (request) {
                        case NewConnection _ -> {
                            IO.println("server received new connection");
                            onNewConnection(datagramPacket);
                        }
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
                                                var byteArrayOutputStream = new ByteArrayOutputStream();
                                                var objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                                                objectOutputStream.writeObject(updatedPlayerInfo);
//                                                streams.objectOutputStream().flush();

                                                var byteArray = byteArrayOutputStream.toByteArray();
//                                                streams.byteArrayOutputStream().flush();

                                                DatagramPacket sendPacket = new DatagramPacket(
                                                        byteArray,
                                                        byteArray.length,
                                                        writer.getAddress(),
                                                        writer.getPort()
                                                );

                                                serverSocket.send(sendPacket);
                                            } catch (IOException e) {
                                                IO.println("Error sending updating playerFactory " + r.playerId() + " in playerFactory " + playerId);
                                            }
                                        });
                                    });
                                });
                            });
                        }
                        case DropClient r -> executor.execute(() -> {
                            writers.remove(r.playerId());

                            // inform all other writers(players) a player has disconnected
                            var deleteRequest = new DeletePlayer(r.playerId());

                            writers.forEach((playerId, writer) -> {
                                try {
                                    var byteArrayOutputStream = new ByteArrayOutputStream();
                                    var objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);

                                    objectOutputStream.writeObject(deleteRequest);
//                                    streams.objectOutputStream().flush();

                                    var byteArray = byteArrayOutputStream.toByteArray();
//                                    streams.byteArrayOutputStream().flush();

                                    var datagram = new DatagramPacket(
                                            byteArray,
                                            byteArray.length,
                                            writer.getAddress(),
                                            writer.getPort()
                                    );

                                    serverSocket.send(datagram);
                                } catch (IOException ex) {
                                    //io.println("Error sending delete request of " + id + " to playerFactory playerId " + playerId);
                                }
                            });

                            Platform.runLater(() -> {
                                gameNodes.remove(playerNodes.get(r.playerId()));
                                playerNodes.remove(r.playerId());
                            });
                        });
                        case String _ -> {} //io.println("Client sent message " + request);
                        default -> throw new IllegalStateException("Unexpected value: " + request);
                    }

//                    inputObjects.byteArrayInputStream().close();
//                    inputObjects.objectInputStream().close();

//                    inputObjects.packet().setData(new byte[2048]);
//                    this.inputObjects.add(inputObjects);
                }
            } catch (IOException e) {
                serverSocket.close();
                IO.println("Server stopped with error");
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void onNewConnection(DatagramPacket newConnectionPacket) throws IOException {

        // virtual thread for creating players

//        executor.execute(() -> {
            //io.println("new playerFactory " + id);

            //sends current players to new player
            playerNodes.forEach((playerId, player) -> {
                try {
                    var byteArrayOutputStream = new ByteArrayOutputStream();
                    var objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);

                    objectOutputStream.writeObject(new AddPlayer(player.toFactory()));

                    var sendBuffer = byteArrayOutputStream.toByteArray();

                    IO.println("Received conn 3" + newConnectionPacket.getAddress());
                    // Create a packet to send the data
                    DatagramPacket newPlayer = new DatagramPacket(
                            sendBuffer,
                            sendBuffer.length,
                            newConnectionPacket.getAddress(),
                            newConnectionPacket.getPort()
                    );
//                    IO.println("Received conn 4" + newConnectionPacket.getAddress());

                    // Send the packet
                    serverSocket.send(newPlayer);
                } catch (IOException ex) {
//                    io.println("Error sending keys to server");
                    throw new RuntimeException(ex);
                }


            });

//        IO.println("Received conn 5" + newConnectionPacket.getAddress());

            var id = (short) idCounter.getAndIncrement();
            var playerFactory = new PlayerFactory(
                    id,
                    "WHITE",
                    random.nextDouble(400),
                    random.nextDouble(300)
            );

        DatagramPacket connection = new DatagramPacket(new byte[2048], 2048, newConnectionPacket.getAddress(),
                newConnectionPacket.getPort());

//        IO.println("Received conn 6");
        writers.put(id, connection);
            // concurrently send new playerFactory individually to all connected clients
//            executor.execute(() -> {
//        IO.println("Received conn 7" + newConnectionPacket.getAddress());
                var request = new AddPlayer(playerFactory);

//                for (var i = 0; i < 2; i++) {
                    writers.forEach((playerId, datagramPacket) -> {
                        try {
                            var byteArrayOutputStream = new ByteArrayOutputStream();
                            var objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);

                            if (playerId == id) {
                                objectOutputStream.writeObject(new SetUpNewPlayer(playerFactory));
                            } else {
                                objectOutputStream.writeObject(request);
                            }

                            var sendBuffer = byteArrayOutputStream.toByteArray();

//                        IO.println("Received conn 8" + datagramPacket.getAddress());
                            DatagramPacket sendPacket = new DatagramPacket(
                                    sendBuffer,
                                    sendBuffer.length,
                                    datagramPacket.getAddress(),
                                    datagramPacket.getPort()
                            );

//                        IO.println("Received conn 9" + datagramPacket.getAddress());

                            serverSocket.send(sendPacket);
                            IO.println("Received conn 10" + datagramPacket.getAddress());
                        } catch (IOException e) {
                            IO.println("Error sending new playerFactory " + id + " to playerFactory " + playerId);
                        }
                    });
//                }
//            });

            // add new playerFactory visually in server
            Platform.runLater(() -> {
                var player = playerFactory.getPlayer();
                playerNodes.put(playerFactory.getPlayerId(), player);

                gameNodes.add(player);
                //io.println("added playerFactory to game nodes in parent " + id);
            });

//            outputObjects.add(streams);
//        });
    }

    private OutputSetUp getOutputObjects() {
//        var pair = outputObjects.poll();

        if (outputSetUp == null) {
            try {
                var byteArrayOutputStream = new ByteArrayOutputStream();
                var objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);

                outputSetUp = new OutputSetUp(byteArrayOutputStream, objectOutputStream);
//                outputObjects.add(pair);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return outputSetUp;
    }

    private InputSetUp getNextRequestInputObjects(DatagramSocket socket) {
//        var objects = inputObjects.poll();

        try {
            if (inputSetUp == null) {
                var buffer = new byte[2048];
                var datagramPacket = new DatagramPacket(buffer, buffer.length);

                socket.receive(datagramPacket);

                IO.println("Received packet " + datagramPacket.getAddress());

                var byteInputStream = new ByteArrayInputStream(datagramPacket.getData());

                var objectInputStream = new ObjectInputStream(byteInputStream);



                inputSetUp = new InputSetUp(datagramPacket, byteInputStream, objectInputStream);
//                objects.packet().setData(new byte[256]);
//                inputObjects.add(objects);
            }

            return inputSetUp;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

//        return objects;
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
