package tutorial.tutorial;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import java.io.IOException;

public class TutorialApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Button _2DShapes =  new Button("2D Shapes");
        Runnable runnable = stage::show;

        _2DShapes.setOnAction(e -> {
            new TwoDShapes().displayTwoDShapes(runnable);
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