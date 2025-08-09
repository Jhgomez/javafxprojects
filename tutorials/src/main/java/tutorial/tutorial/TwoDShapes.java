package tutorial.tutorial;

import javafx.animation.PathTransition;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class TwoDShapes {
    ObservableList<Node> nodes;

    public void displayTwoDShapes(Runnable runnable) {
        Group group = new Group();
//        AnchorPane ap = new AnchorPane();
        nodes = group.getChildren();

        drawLine();
        drawRoundedRectangle();
        drawOlympicsSymbolUsingCircles();
        drawAndAnimatePlanetOrbitUsingEllipse();
        drawPolygons();
        drawPolylines();

        Scene scene = new Scene(group, 1000, 640);

        scene.setFill(Paint.valueOf("#fdbf6f"));

        Stage stage = new Stage();

        stage.setTitle("2D Shapes");
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();

        stage.setOnCloseRequest(e -> runnable.run());
    }

    private void drawPolylines() {
        //A Polyline is same as a polygon except that a polyline is not closed in the end.
        // Hexagon polyline means it is a polyline of 5 lines and not 6 because the Hexagon is not closed
        Polyline polyline_hexagon = new Polyline();

        polyline_hexagon.getPoints().addAll(
                100.0, 430.0,
                300.0, 430.0,
                350.0, 530.0,
                300.0, 630.0,
                100.0, 630.0,
                50.0, 530.0
        );

        // Polyline of 4 vertices
        Polyline polyline_pentagon = new Polyline(
                520.0, 430.0,
                680.0, 530.0,
                520.0, 630.0,
                380.0, 530.0
        );

        Text polyline_text = new Text("Polylines");
        polyline_text.setX(340);
        polyline_text.setY(420);
        polyline_text.setFont(new Font(16));

        nodes.addAll(polyline_text, polyline_hexagon, polyline_pentagon);
    }

    private void drawPolygons() {
        Polygon polygon_hexagon = new Polygon();

        // you need to pass points, every two values is an X,Y point
        polygon_hexagon.getPoints().addAll(
                700.0, 50.0,
                900.0, 50.0,
                950.0, 150.0,
                900.0, 250.0,
                700.0, 250.0,
                650.0, 150.0
        );

        Polygon polygon_rhombus = new Polygon(
                800.0, 275.0,
                950.0, 375.0,
                800.0, 475.0,
                650.0, 375.0
        );

        Text polygon_text = new Text("Polygons");
        polygon_text.setX(800);
        polygon_text.setY(40);
        polygon_text.setFont(new Font(16));

        nodes.addAll(polygon_text, polygon_hexagon, polygon_rhombus);
    }

    private void drawAndAnimatePlanetOrbitUsingEllipse() {
        // ============= Drawing a planet's orbit using an ellipse and animate it =================
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
    }

    private void drawOlympicsSymbolUsingCircles() {
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
    }

    private void drawRoundedRectangle() {
        Rectangle rectangle = new Rectangle(50, 20, 250, 180);
        rectangle.setArcHeight(40);
        rectangle.setArcWidth(30);
        rectangle.setFill(Paint.valueOf("#cab2d6"));

        nodes.add(rectangle);
    }

    private void drawLine() {
        nodes.add(new Line(10, 100 , 20, 200));
    }
}
