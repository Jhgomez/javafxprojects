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
    private final List<Runnable> clientClosingCallBacks = new ArrayList<>();
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
            clientClosingCallBacks.forEach(Runnable::run);

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

                // Setting up input and output streams
                var out = new ObjectOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));
                out.writeObject("Ok");
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
        final List<ObjectOutputStream> writters = new CopyOnWriteArrayList<>();
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

                    clientClosingCallBacks.add(() -> {
                        try {
                            client.close();
                        } catch (IOException e) {
                            System.err.println("Error closing client socket");
                        }
                    });

                    executor.execute(() -> {
                        try {
                            var out = new ObjectOutputStream(new BufferedOutputStream(client.getOutputStream()));

//                        System.out.println("Client connected");

                            // virtual thread for creating players
                            IO.println("Streams Created");
                            executor.execute(() -> {
                                var player = new Player((short) idCounter.getAndIncrement());
                                IO.println("new player");

                                try {
                                    //sends a message and all current players to this client
//                                    out.writeObject("Player Created");
                                    out.writeObject(players);
                                    IO.println("wrote prev players");

                                    executor.execute(() -> {
                                        writters.add(out);
                                        players.put(player.getPlayerId(), player);

                                        Platform.runLater(() -> {
                                            gameNodes.add(player);
                                            IO.println("added player to game nodes in parent");
                                        });

                                        for (var writer : writters) {
                                            try {
                                                writer.writeObject(player);
                                            } catch (IOException e) {
                                                IO.println("Error sending newly added player");
                                            }
                                        }
                                    });
                                } catch (IOException e) {
                                    IO.println("Error sending previous players to client");
                                    throw new RuntimeException(e);
                                }
                            });

                            try {
                                var in = new ObjectInputStream(new BufferedInputStream(client.getInputStream()));

                                var request = in.readObject();
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

                                            for (var writer: writters) {
                                                try {
                                                    writer.writeObject(updatedPlayerInfo);
                                                } catch (IOException e) {
                                                    IO.println("Error sending updated player");
                                                }
                                            }
                                        }
                                        case String _ -> System.out.println("Client sent ok");
                                        default -> throw new IllegalStateException("Unexpected value: " + request);
                                    }
                                    request = in.readObject();
                                }
                            } catch (Exception e) {
                                // ignoring exception since this means the server is disconnected and all clients have
                                // been, therefore, disconnected
                                System.out.println("Client disconnected from catch in server");
                                writters.remove(out);
                                throw new RuntimeException(e);
                            }

                            System.out.println("Client disconnected in server");
                            writters.remove(out);
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
