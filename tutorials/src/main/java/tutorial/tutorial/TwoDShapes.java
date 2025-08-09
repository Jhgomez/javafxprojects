package tutorial.tutorial;

import javafx.animation.PathTransition;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.DropShadow;
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
        drawCubicCurves();
        drawQuadraticCurves();
        drawArc();

        Scene scene = new Scene(group, 1400, 640);

        scene.setFill(Paint.valueOf("#fdbf6f"));

        Stage stage = new Stage();

        stage.setTitle("2D Shapes");
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();

        stage.setOnCloseRequest(e -> runnable.run());
    }

    /**
     * An arc in simple geometry is defined as a portion of a circumference of an ellipse or a circle.
     * It will have the following properties
     *  - startAngle − The starting angle of the arc in degrees.
     *  - length − The angular extent of the arc in degrees.
     *  - radiusX − The width of the full Ellipse of which the current arc is a part of.
     *  - radiusY − The height of the full Ellipse of which the current arc is a part of.
     *
     *  If both radiusX and radiusY are same, then the arc is a part of a circle circumference.
     *
     *  In JavaFX, you can draw three kinds of arcs namely
     *  - Open − An arc which is not closed at all is known as an open arc.
     *  - Chord − A chord is a type of an arc which is closed by straight line.
     *  - Round − The Round arc is an arc which is closed by joining the starting and ending point to the center of the ellipse.
     *
     *  You can set the type of the arc using the method setType() by passing any of the following properties − ArcType.OPEN,
     *  ArcType.CHORD, ArcType.Round.
     */
    private void drawArc() {
        Arc arc = new Arc();

        arc.setCenterX(750.0);
        arc.setCenterY(555.0);
        arc.setRadiusX(50.0f);
        arc.setRadiusY(50.0f);
        arc.setStartAngle(40.0f);
        arc.setLength(239.0f);

        arc.setType(ArcType.ROUND);
        arc.setStroke(Color.RED);

        Arc arc2 = new Arc();

        arc2.setCenterX(840.0);
        arc2.setCenterY(555.0);
        arc2.setRadiusX(50.0f);
        arc2.setRadiusY(50.0f);
        arc2.setStartAngle(40.0f);
        arc2.setLength(239.0f);

        arc2.setFill(Color.TRANSPARENT);
        arc2.setStroke(Color.BLACK);

        arc2.setType(ArcType.OPEN);

        Arc arc3 = new Arc();

        arc3.setCenterX(930.0);
        arc3.setCenterY(555.0);
        arc3.setRadiusX(50.0f);
        arc3.setRadiusY(50.0f);
        arc3.setStartAngle(40.0f);
        arc3.setLength(239.0f);

        arc3.setFill(Color.TRANSPARENT);
        arc3.setStroke(Color.BLACK);

        arc3.setType(ArcType.CHORD);

        Text circleText = new Text("Arcs types, Round, Open and Chord");
        circleText.setFont(new Font(16));
        circleText.setX(710);
        circleText.setY(497.0);

        nodes.addAll(arc, arc2, arc3, circleText);
    }

    /**
     * Mathematically, a quadratic curve is one that is described by a quadratic function like − y = ax2 + bx + c.
     *
     * A quadratic curve is a Bezier parametric curve in the XY plane which is a curve of degree 2. It is drawn using
     * three points: start point, end point and control point
     */
    private void drawQuadraticCurves() {
        QuadCurve quadCurve = new QuadCurve();

        quadCurve.setStartX(950.0);
        quadCurve.setStartY(370.0f);
        quadCurve.setEndX(1350.0f);
        quadCurve.setEndY(370.0f);
        quadCurve.setControlX(1100.0f);
        quadCurve.setControlY(150.0f);

        Text quadCurveText = new Text("Quad Curve/Bezier quadrilateral curve");
        quadCurveText.setFont(new Font(16));
        quadCurveText.setX(975);
        quadCurveText.setY(250);

        //Quadrilateral curve with bloom effect
        QuadCurve quadCurve2 = new QuadCurve();

        //Adding properties to the Quad Curve
        quadCurve2.setStartX(1050.0);
        quadCurve2.setStartY(500.0f);
        quadCurve2.setEndX(1250.0f);
        quadCurve2.setEndY(500.0f);
        quadCurve2.setControlX(1200.0f);
        quadCurve2.setControlY(280.0f);

        quadCurve2.setFill(Color.RED);

        //Instantiating the Bloom class
        Bloom bloom = new Bloom();

        //setting threshold for bloom
        bloom.setThreshold(0.1);

        //Applying bloom effect to quadCurve
        quadCurve2.setEffect(bloom);

        Text quadCurveText2 = new Text("Quad Curve/Bezier quadrilateral curve with blossom effect");
        quadCurveText2.setFont(new Font(16));
        quadCurveText2.setX(975);
        quadCurveText2.setY(390);

        nodes.addAll(quadCurve, quadCurveText, quadCurve2, quadCurveText2);
    }

    /**
     * A Cubic Curve is described by a third-degree polynomial function of two variables
     *
     * These Bezier curves are generally used in computer graphics. They are parametric curves which appear reasonably
     * smooth at all scales.
     *
     * A cubic curve is a Bezier parametric curve in the XY plane is a curve of degree 3. It is drawn using four
     * points − Start Point, End Point, Control Point and Control Point2. These are passed as parameters
     */
    private void drawCubicCurves() {
        CubicCurve cubicCurve = new CubicCurve();
        cubicCurve.setStartX(975.0f);
        cubicCurve.setStartY(80.0f);
        cubicCurve.setControlX1(1275.0f);
        cubicCurve.setControlY1(-30.0f);
        cubicCurve.setControlX2(1050.0f);
        cubicCurve.setControlY2(180.0f);
        cubicCurve.setEndX(1375.0f);
        cubicCurve.setEndY(80.0f);

        Text cubicCurveText = new Text("Cubic Curves");
        cubicCurveText.setFont(new Font(16));
        cubicCurveText.setX(1000);
        cubicCurveText.setY(40);

        //DropShadow effect on a Cubic Curve
        CubicCurve cubicCurve2 = new CubicCurve();

        cubicCurve2.setStartX(1025.0f);
        cubicCurve2.setStartY(200.0f);
        cubicCurve2.setControlX1(1175.0f);
        cubicCurve2.setControlY1(140.0f);
        cubicCurve2.setControlX2(1125.0f);
        cubicCurve2.setControlY2(300.0f);
        cubicCurve2.setEndX(1175.0f);
        cubicCurve2.setEndY(150.0f);

        cubicCurve2.setFill(Color.RED);

        //Instantiating the DropShadow class
        DropShadow ds = new DropShadow();

        //Applying DropShadow effect to cubicCurve
        cubicCurve2.setEffect(ds);

        Text cubicCurveText2 = new Text("Drop Shadow Effect on A Cubic Curve");
        cubicCurveText2.setFont(new Font(16));
        cubicCurveText2.setX(1000);
        cubicCurveText2.setY(140);

        nodes.addAll(cubicCurveText, cubicCurve, cubicCurveText2, cubicCurve2);
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
