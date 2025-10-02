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
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
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
    private ConcurrentMap<Short, Node> players = new ConcurrentHashMap<>();
    private final Map<Short, Runnable> clientClosingCallBacks = new ConcurrentHashMap<>();
    private final AtomicInteger idCounter = new AtomicInteger(0);

    private ServerSocket serverSocket;
    Socket clientSocket;

    public void displayScreen(Runnable runnable) {
        Stage stage = new Stage();
        stage.setTitle("Networking");

        var group = new Group();
        groupNodes = group.getChildren();

        var scene = new Scene(group, 600, 400);

        scene.setFill(Paint.valueOf("#000000"));

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

        group.getChildren().add(transformationsComboBox);

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
        initClientSocket();

        var pane = new Pane();

        pane.setPrefSize(800, 600);
        pane.setStyle("-fx-background-color: #123456");

        gameNodes = Collections.synchronizedList(pane.getChildren());

        return pane;
    }

    private void initClientSocket() {
        executor.execute(() -> {
            try {
                clientSocket = new Socket("localhost", 12346);

                // Setting up input and output streams, the receiver objectinputstream will be blocked
                // until I flush this stream, this means I need to do manual flushing, without it, the
                // the messages won't be sent either, so the first flush is just to be able to let the receiver's
                // constructor to build
                var out = new ObjectOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));
                out.writeObject("Ok");
                out.flush();

                var in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                // Start a thread to handle incoming messages
//                executor.execute(() -> {
//                    try {
//                        var message = "";
//                        while ((message = in.readLine()) != null) {
//                            System.out.println("\n- " + message);
//                            System.out.print("> ");
//                        }
//                    } catch (IOException e) {
//                        throw new RuntimeException(e);
//                    }
//                });

                try {
                    var message = "";
                    while ((message = in.readLine()) != null) {
                        System.out.println("\n- " + message);
                        System.out.print("> ");
                    }
                } catch (SocketException e) {
                    IO.println("Client disconnected");
                }catch (IOException e) {
                    throw new RuntimeException(e);
                }

//                // Read messages from the console and send to the server
//                Scanner scanner = new Scanner(System.in);
//                String userInput = "";
//                while (true) {
////                System.out.print("Waiting for client input: ");
//                    System.out.print("> ");
//
//                    userInput = scanner.nextLine();
//
//                    out.println(userInput);
//
////                System.out.println("Input sent to server: " + userInput);
//                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private Pane serverPlayer(Scene scene) {
        initServerSocket();

        var player = new Player((short) idCounter.getAndIncrement());

        var pane = new Pane(player);

        gameNodes = Collections.synchronizedList(pane.getChildren());

        pane.setPrefSize(800, 600);
        pane.setStyle("-fx-background-color: #123456");

        players.put(player.getPlayerId(), player);

        return pane;
    }

    private void initServerSocket() {
        final Map<Short, ObjectOutputStream> writters = new ConcurrentHashMap<>();
        final Deque<Player> movedPlayers = new ArrayDeque<>();
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
                                var player = new Player(id);
                                IO.println("new player " + id);

                                try {
                                    //sends a message and all current players to new client
                                    out.writeObject("Player Created");
                                    out.writeObject(players);
                                    IO.println("wrote prev players to playerId " + id);
                                } catch (IOException e) {
//                                    IO.println("Error sending previous players to client " + id);
                                    IO.println("Error sending previous players to client " + id);
                                    throw new RuntimeException(e);
                                }

                                // concurrently add to all collections used in controlling the connections and trasmission
                                // logic, and keeping clients updated
                                executor.execute(() -> {
                                    players.put(player.getPlayerId(), player);
                                    writters.put(id, out);

                                    // concurrently send new player individually to all connected clients
                                    executor.execute(() -> {
                                        writters.forEach((playerId, val) -> {
                                            try {
                                                val.writeObject(player);
                                            } catch (IOException e) {
                                                IO.println("Error sending new player " + id + " to player " + playerId);
                                            }
                                        });
                                    });

                                    // add new player visually in server
                                    Platform.runLater(() -> {
                                        gameNodes.add(player);
                                        IO.println("added player to game nodes in parent " + id);
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
                                        case Request r -> {
                                            var requestedPlayer = players.get(r.id());

                                            for (var key: r.codes()) {
                                                if (key == KeyCode.W) {
                                                    requestedPlayer.setTranslateY(requestedPlayer.getTranslateY() - 5);
                                                } else if (key == KeyCode.A) {
                                                    requestedPlayer.setTranslateX(requestedPlayer.getTranslateX() - 5);
                                                } else if (key == KeyCode.S) {
                                                    requestedPlayer.setTranslateY(requestedPlayer.getTranslateY() + 5);
                                                } else if (key == KeyCode.D) {
                                                    requestedPlayer.setTranslateX(requestedPlayer.getTranslateX() + 5);
                                                }
                                            }

                                            var updatedPlayerInfo =
                                                    new Update(r.id(), requestedPlayer.getTranslateX(), requestedPlayer.getTranslateY());

                                            writters.forEach((playerId, writer) -> {
                                                try {
                                                    writer.writeObject(updatedPlayerInfo);
                                                } catch (IOException e) {
                                                    IO.println("Error sending updating player " + r.id() + " in player " + playerId);
                                                }
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
                                writters.remove(out);

                                clientClosingCallBacks.get(id).run();
                                clientClosingCallBacks.remove(id);

                                Platform.runLater(() -> {
                                    gameNodes.remove(players.get(id));
                                    players.remove(id);
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
