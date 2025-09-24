package tutorial.tutorial.examples.gamemenus;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

public class GameMenus {
    private static GameMenu currentMenu;
    private static List<Node> nodes;
    private static final ComboBox<String> transformationsComboBox = new ComboBox<>();

    public void displayScreen(Runnable runnable) {
        Stage stage = new Stage();
        stage.setTitle("Game Menus");
        stage.sizeToScene();

        var group = new Group();
        nodes = group.getChildren();

        var scene = new Scene(group, 400, 200);

        scene.setFill(Paint.valueOf("#000000"));

        HashMap<String, Function<Scene, Pane>> transformations1 = getMenus();
        ObservableList<String> options = FXCollections.observableArrayList(transformations1.keySet());

        transformationsComboBox.setItems(options);
        transformationsComboBox.setPromptText("Choose Transformation");

        transformationsComboBox.valueProperty().addListener((observable, oldVal, newVal) -> {
            if (currentMenu != null) currentMenu.clearResources();

            nodes.clear();
            nodes.add(transformationsComboBox);

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

        stage.setOnCloseRequest(e -> runnable.run());
    }

    private HashMap<String, Function<Scene, Pane>> getMenus() {
        HashMap<String, Function<Scene, Pane>> menus = new HashMap<>();
        menus.put("Mortal Kombat X", scene -> {
            currentMenu = new MortalKombatX(scene);
            return currentMenu.getMenu();
        });

        return menus;
    }

    public static void clearScreen() {
        currentMenu = null;
        // this will clear nodes from the listener
        transformationsComboBox.setValue(null);
    }
}
