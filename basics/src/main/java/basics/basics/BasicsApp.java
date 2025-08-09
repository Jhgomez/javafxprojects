package basics.basics;

import javafx.animation.PathTransition;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class BasicsApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Group group = new Group();
//        AnchorPane ap = new AnchorPane();
        ObservableList<Node> nodes = group.getChildren();

        // Drawing a Line
        nodes.add(new Line(10, 100 , 20, 200));

        // Drawing a "rounded "rectangle
        Rectangle rectangle = new Rectangle(50, 20, 250, 180);
        rectangle.setArcHeight(40);
        rectangle.setArcWidth(30);
        rectangle.setFill(Paint.valueOf("#cab2d6"));

        nodes.add(rectangle);
//        ap.setBackground(Background.fill(Paint.valueOf("#cab2d6")));
//        Color.hsb(50, 1, 1)

//        root.getChildren().add(ap);

        // Drawing olympics symbols with using cirles
        Object[][] olympicsSymbolArray = new Object[][] {
                {100.0, 275.0, 50.0, Color.BLUE},
                {175.0, 275.0, 50.0, Color.BLACK},
                {250.0, 275.0, 50.0, Color.RED},
                {135.0, 340.0, 50.0, Color.YELLOW},
                {215.0, 340.0, 50.0, Color.GREEN}
        };

        for (Object[] o : olympicsSymbolArray) {
            Circle circle = new Circle((Double) o[0], (Double)o[1], (Double)o[2]);
            circle.setStroke((Color) o[3]);
            circle.setFill(Color.WHITE);

            nodes.add(circle);
        }

        // Drawing an planet's orbit using an ellipse and animate it
        Ellipse orbit = new Ellipse(500, 250, 150, 100);
        orbit.setFill(Color.WHITE);
        orbit.setStroke(Color.BLACK);

        // Drawing a circular planet
        Circle planet = new Circle(500, 150, 40);

        //Creating the animation
        PathTransition pathTransition = new PathTransition();
        pathTransition.setDuration(Duration.millis(2000));
        pathTransition.setNode(planet);
        pathTransition.setPath(orbit);
        pathTransition.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
        pathTransition.setCycleCount(PathTransition.INDEFINITE);
        pathTransition.setAutoReverse(false);
        pathTransition.play();

        nodes.addAll(orbit, planet); // is important to add orbit first so planet is draw on top of it

        Scene scene = new Scene(group, 520, 440);
        scene.setFill(Paint.valueOf("#fdbf6f"));

        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}