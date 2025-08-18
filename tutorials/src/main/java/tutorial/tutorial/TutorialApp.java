package tutorial.tutorial;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
        Button _2DShapesProperties =  new Button("Properties 2D Shapes");
        Button _2DShapesOperations =  new Button("Operations on 2D Shapes");
        Button pathObjects =  new Button("Path Objects");
        Button colorsAndTextures =  new Button("Color And Textures");
        Button effects = new Button("Effects");
        Button transformations = new Button("Transformations");
        Button animations = new Button("Animations");
        Button images = new Button("Images");
        Button _3DProperties = new Button("Properties 3D Shapes");

        Runnable runnable = stage::show;

        _2DShapes.setOnAction(e -> {
            new TwoDShapes().displayScreen(runnable);
            stage.hide();
        });

        _2DShapesProperties.setOnAction(e -> {
            new TwoDShapesWithProperties().displayScreen(runnable);
            stage.hide();
        });

        _2DShapesOperations.setOnAction(e -> {
            new TwoDShapesOperations().displayScreen(runnable);
            stage.hide();
        });

        pathObjects.setOnAction(e -> {
            new PathObjects().displayScreen(runnable);
            stage.hide();
        });

        colorsAndTextures.setOnAction(e -> {
            new ColorsAndTextures().displayScreen(runnable);
            stage.hide();
        });

        effects.setOnAction(e -> {
            new Effects().displayScreen(runnable);
            stage.hide();
        });

        transformations.setOnAction(e -> {
            new Transformations().displayScreen(runnable);
            stage.hide();
        });

        animations.setOnAction(e -> {
            new Animations().displayScreen(runnable);
            stage.hide();
        });

        images.setOnAction(e -> {
            new Images().displayScreen(runnable);
            stage.hide();
        });

        _3DProperties.setOnAction(e -> {
            new Shapes3DProperties().displayScreen(runnable);
            stage.hide();
        });

        VBox root = new VBox(
                16.0,
                _2DShapes,
                _2DShapesProperties,
                _2DShapesOperations,
                pathObjects,
                colorsAndTextures,
                effects,
                transformations,
                animations,
                images,
                _3DProperties
        );

        root.setAlignment(Pos.CENTER);

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