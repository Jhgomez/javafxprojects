package tutorial.tutorial;

import javafx.animation.PathTransition;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class TutorialApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Button _2DShapes =  new Button("2D Shapes");
        Runnable runnable = stage::show;

        _2DShapes.setOnAction(e -> {
            TwoDShapes.getTwoDShapes(runnable);
            stage.hide();
        });

        VBox root = new VBox(
                16.0,
                _2DShapes
        );

        root.setPadding(new Insets(24, 80, 24, 80));

        Scene scene = new Scene(root);
        scene.setFill(Paint.valueOf("#fdbf6f"));

        stage.setTitle("Tutorials");
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}