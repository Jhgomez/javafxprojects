package tutorial.tutorial;

import javafx.animation.PathTransition;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

public class TwoDShapesWithProperties {
    ObservableList<Node> nodes;

    public void displayScreen(Runnable runnable) {
        Group group = new Group();
//        AnchorPane ap = new AnchorPane();
        nodes = group.getChildren();
        
        strokeTypeProperty();
        drawPolylines();
        drawCubicCurves();
        drawQuadraticCurves();
        drawArc();
        drawSVGs();

        Scene scene = new Scene(group, 1500, 740);

        scene.setFill(Paint.valueOf("#fdbf6f"));

        Stage stage = new Stage();

        stage.setTitle("2D Shapes");
        stage.setScene(scene);
        stage.sizeToScene();

        for (Node node : nodes) {
            DragUtil.setDraggable(node);
        }

        stage.show();

        stage.setOnCloseRequest(e -> runnable.run());
    }

    /**
     * SVG (Scalable Vector Graphics) is an XML based language to define vector based graphics. The <path> element in
     * the SVG library is the most powerful while drawing basic shapes. Using paths, you can draw lines, curves, arcs,
     * and also various complex shapes including them.
     *
     * Even though a path is similar to the polyline element while creating complex shapes, the scale of complex shapes
     * drawn using a polyline element is not larger than shapes drawn using path element.
     *
     * A path in SVG is defined by only one parameter. This parameter holds series of commands, like line, curve or arc
     * commands. And each of these commands are instantiated using a single letter; for example, the letter 'M' calls the
     * "Move To" command, the letter 'L' calls the "line" command and 'C' calls "Curve" command. And these letters can
     * either be specified as either a lowercase or an uppercase letter. The lowercase letter specifies relative
     * coordinates, while the uppercase letter specifies absolute coordinates.
     *
     * The same concept of SVGPath is adopted by JavaFX, in order to create objects.
     *
     * In JavaFX we can construct images by parsing SVG paths. Such shapes are represented by the class named SVGPath.
     */
    private void drawSVGs() {
        SVGPath triangleSvgPath = new SVGPath();
        String triagnlePath = "M 100 100 L 300 100 L 200 300 z";
        //Setting the SVGPath in the form of string
        triangleSvgPath.setContent(triagnlePath);
        triangleSvgPath.setLayoutX(885);
        triangleSvgPath.setLayoutY(420);

        SVGPath bezierSvgPath = new SVGPath();

        String bezierPath = "M 70 110 C 70 180, 210 180, 210 110";

        //Setting the SVGPath in the form of string
        bezierSvgPath.setContent(bezierPath);

        // Setting the stroke and fill of the path
        bezierSvgPath.setStroke(Color.BLACK);
        bezierSvgPath.setFill(Color.ORANGE);

        bezierSvgPath.setLayoutX(1150);
        bezierSvgPath.setLayoutY(420);

        Text pathText = new Text("SVGs Paths, drawing a triangle and bezier curve using SVG Paths");
        pathText.setFont(new Font(16));
        pathText.setX(970);
        pathText.setY(517);

        nodes.addAll(triangleSvgPath, bezierSvgPath, pathText);
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

    private void strokeTypeProperty() {
        double[] triangle_coordinates = {
                25.0, 75.0,
                95.0, 175.0,
                25.0, 275.0,
        };

        Polygon triangle_hexagon1 = new Polygon(triangle_coordinates);
        triangle_hexagon1.setStroke(Color.RED);
        triangle_hexagon1.setStrokeWidth(16);
        triangle_hexagon1.setStrokeType(StrokeType.CENTERED);

        Polygon triangle_hexagon2 = new Polygon(triangle_coordinates);
        triangle_hexagon2.setStroke(Color.RED);
        triangle_hexagon2.setStrokeWidth(16);
        triangle_hexagon2.setStrokeType(StrokeType.INSIDE);
        triangle_hexagon2.setLayoutX(100);

        Polygon triangle_hexagon3 = new Polygon(triangle_coordinates);
        triangle_hexagon3.setStroke(Color.RED);
        triangle_hexagon3.setStrokeWidth(16);
        triangle_hexagon3.setStrokeType(StrokeType.OUTSIDE);
        triangle_hexagon3.setLayoutX(210);

        Text polygon_text = new Text("Same Polygon(triangle) but with CENTER, INSIDE and OUTSIDE Stroke type");
        polygon_text.setWrappingWidth(360);
        polygon_text.setTextAlignment(TextAlignment.CENTER);
        polygon_text.setX(20);
        polygon_text.setY(20);
        polygon_text.setFont(new Font(16));

        nodes.addAll(polygon_text, triangle_hexagon1, triangle_hexagon2, triangle_hexagon3);
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
