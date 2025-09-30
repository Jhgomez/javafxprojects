package tutorial.tutorial.examples.networking;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

public class ClientServerSimpleGame {
    private List<Node> nodes;
    private final ComboBox<String> transformationsComboBox = new ComboBox<>();
    ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
    private List<Node> gameNodes = new ArrayList<>();
    private List<Runnable> clientClosingCallBacks = new ArrayList<>();

    private ServerSocket serverSocket;
    Socket clientSocket;

    public void displayScreen(Runnable runnable) {
        Stage stage = new Stage();
        stage.setTitle("Networking");

        var group = new Group();
        nodes = group.getChildren();

        var scene = new Scene(group, 600, 400);

        scene.setFill(Paint.valueOf("#000000"));

        HashMap<String, Function<Scene, Pane>> transformations1 = getScreens();
        ObservableList<String> options = FXCollections.observableArrayList(transformations1.keySet());

        transformationsComboBox.setItems(options);
        transformationsComboBox.setPromptText("Choose Transformation");

        transformationsComboBox.valueProperty().addListener((observable, oldVal, newVal) -> {
//            if (currentMenu != null) currentMenu.clearResources();

            nodes.clear();

            if (transformations1.get(newVal) != null) {
                Pane newNode = transformations1.get(newVal).apply(scene);
                nodes.add(newNode);

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

        var player = new Player(2);

        var pane = new Pane(player);
        pane.setPrefSize(800, 600);
        pane.setStyle("-fx-background-color: #123456");

        gameNodes.add(player);

        return pane;
    }

    private void initClientSocket() {
        executor.execute(() -> {
            try {
                clientSocket = new Socket("localhost", 12346);

                // Setting up input and output streams
                var out = new PrintWriter(new BufferedOutputStream(clientSocket.getOutputStream()), true);
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

        var player = new Player(1);

        var pane = new Pane(player);
        pane.setPrefSize(800, 600);
        pane.setStyle("-fx-background-color: #123456");

        gameNodes.add(player);

        return pane;
    }

    private void initServerSocket() {
        final List<PrintWriter> writters = new ArrayList<>();
        final Deque<String> messagesToClients = new ArrayDeque<>();
        try {
            this.serverSocket = new ServerSocket( 12346);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        executor.execute(()-> {

            try {

//                executor.execute(() -> {
//                    var scanner = new Scanner(System.in);
//
//                    while (!server.isClosed()) {
//                        System.out.print("> ");
//
//                        var userInput = scanner.nextLine();
//                        messagesToClients.offer(userInput);
//
//                        // broadcast virtual thread
//                        executor.execute(() -> {
//                            while(!messagesToClients.isEmpty() && !writters.isEmpty()) {
////            System.out.println(messagesToClients);
//                                var message = messagesToClients.poll();
////            System.out.println("polling message " + message);
//
//                                for (var out : writters) {
//                                    out.println(message);
////                System.out.println("Message sent to client ");
//                                }
//                            }
//                        });
//                    }
//                    System.out.println("server is closed");
//                });

                while (!serverSocket.isClosed()) {
                    Socket client;
                    try {
                        client = serverSocket.accept();
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

                    executor.submit(() -> {
                        try {
                            var out = new PrintWriter(new BufferedOutputStream(client.getOutputStream()), true);
                            writters.add(out);

                            var in = new BufferedReader(new InputStreamReader(client.getInputStream()));

//                        System.out.println("Client connected");

                            // broadcast virtual thread
                            executor.execute(() -> {
                                while(!messagesToClients.isEmpty() && !writters.isEmpty()) {
                                    var message = messagesToClients.poll();

                                    for (var outWritter : writters) {
                                        outWritter.println(message);
                                    }
                                }
                            });

                            var message = "";

                            try {
                                while ((message = in.readLine()) != null) {
//                            System.out.println("Message received from client: " + message);
                                    System.out.println("\n- " + message);
                                    System.out.print("> ");
                                }
                            } catch (SocketException _) {
                                // ignoring exception since this means the server is disconnected and all clients have
                                // been, therefore, disconnected
                            }

                        System.out.println("Client disconnected in server");
                            writters.remove(out);
                        } catch (IOException e) {
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
