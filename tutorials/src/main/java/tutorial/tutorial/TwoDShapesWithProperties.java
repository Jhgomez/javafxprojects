package tutorial.tutorial;

import javafx.animation.PathTransition;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.effect.Bloom;
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
        strokeLineJoinProperty();
        strokeMiterLimitProperty();
        strokeLineCapProperty();
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
     * Lines's edges can be structure in a different ways. The ends of a line are also known as end caps/edges. These
     * end caps are sharp, by default. However, using various properties provided by JavaFX, a user can change the
     * structure of these end caps. This property is known as Stroke Line Cap Property.
     *
     * The stroke line cap can be
     * - Butt − The butt line cap is applied at the end of the lines (StrokeLineCap.BUTT). This one seems to cut the line
     *          by a few pixels, but will have square edges/caps
     * - Square − The square line cap is applied at the end of the lines (StrokeLineCap.SQUARE).
     * - Round − The round line cap is applied at the end of the lines (StrokeLineCap.ROUND).
     *
     * Note that this property basically only has effect on a line and no other 2D objects like rectangle or polygon. Also
     * note that "setFill" property doesn't apply to line objects
     */
    private void strokeLineCapProperty() {
        Line line = new Line(800, 50.0, 1000, 50);

        line.setStroke(Color.GREEN);
        line.setStrokeWidth(16);
        line.setStrokeLineCap(StrokeLineCap.BUTT);

        Line line1 = new Line(800, 75.0, 1000, 75.0);

        line1.setStroke(Color.YELLOW);
        line1.setStrokeWidth(16);
        line1.setStrokeLineCap(StrokeLineCap.SQUARE);

        Line line2 = new Line(800, 100.0, 1000, 100.0);

        line2.setStroke(Color.BLUE);
        line2.setStrokeWidth(16);
        line2.setStrokeLineCap(StrokeLineCap.ROUND);

        Text quadCurveText2 = new Text("Lines with stroke line cap, Butt, Square, Round");
        quadCurveText2.setFont(new Font(16));
        quadCurveText2.setX(780);
        quadCurveText2.setY(20);

        nodes.addAll(line, line1, line2,quadCurveText2);
    }

    /**
     * This property represents the limit for the distance between the inside point of the joint and the outside point
     * of the joint. If the distance between these two points exceeds the given limit, the miter is cut at the edge.
     */
    private void strokeMiterLimitProperty() {
        Polygon triangle = new Polygon();

        //Adding coordinates to the polygon
        triangle.getPoints().addAll(
                450.0, 300.0,
                550.0, 230.0,
                650.0, 300.0
        );

        triangle.setFill(Color.BLUE);
        triangle.setStroke(Color.BLACK);
        triangle.setStrokeWidth(7.0);
        triangle.setStrokeMiterLimit(2);

        Text cubicCurveText2 = new Text("Triangle with Stroke Miter Limit Property");
        cubicCurveText2.setFont(new Font(16));
        cubicCurveText2.setX(400);
        cubicCurveText2.setY(215);

        nodes.addAll(triangle, cubicCurveText2);
    }

    /**
     * With this property JavaFX allows you to join multiple 2D shape objects to form another bigger object.
     *
     * The Stroke Line Join Property is used to designate the shape of the joint used to combine two line objects while
     * forming another shape.
     *
     * The stroke line join is of three types.
     * - Bevel − In bevel join, the outside edges of the intersection are connected with a line segment.
     * - Miter − In miter join, the outside edges of the intersection are joined together forming a sharp edge.
     * - Round − In round join, the outside edges of the intersection are joined by rounding off the corner, the radius
     *           of this will be exactly half the width of the join.
     */
    private void strokeLineJoinProperty() {
        double[] hexagon_coordinates = {
                450.0, 80.0,
                500.0, 80.0,
                520.0, 130.0,
                500.0, 180.0,
                450.0, 180.0,
                430.0, 130.0
        };

        Polygon polygon_hexagon = new Polygon(hexagon_coordinates);
        polygon_hexagon.setStrokeLineJoin(StrokeLineJoin.BEVEL);
        polygon_hexagon.setStroke(Color.RED);
        polygon_hexagon.setStrokeWidth(16);

        Polygon polygon_hexagon1 = new Polygon(hexagon_coordinates);
        polygon_hexagon1.setStrokeLineJoin(StrokeLineJoin.MITER);
        polygon_hexagon1.setStroke(Color.RED);
        polygon_hexagon1.setStrokeWidth(16);
        polygon_hexagon1.setLayoutX(110);

        Polygon polygon_hexagon2 = new Polygon(hexagon_coordinates);
        polygon_hexagon2.setStrokeLineJoin(StrokeLineJoin.ROUND);
        polygon_hexagon2.setStroke(Color.RED);
        polygon_hexagon2.setStrokeWidth(16);
        polygon_hexagon2.setLayoutX(220);

        Text polygon_text = new Text("Same hexagon polygon with BEVEL, METER and ROUND stroke line join property respectively");
        polygon_text.setWrappingWidth(360);
        polygon_text.setTextAlignment(TextAlignment.CENTER);
        polygon_text.setX(400);
        polygon_text.setY(20);
        polygon_text.setFont(new Font(16));
        polygon_text.setFont(new Font(16));

        nodes.addAll(polygon_text, polygon_hexagon, polygon_hexagon1, polygon_hexagon2);
    }

    private void strokeTypeProperty() {
        double[] triangle_coordinates = {
                25.0, 75.0,
                95.0, 175.0,
                25.0, 275.0,
        };

        Polygon polygon_triangle1 = new Polygon(triangle_coordinates);
        polygon_triangle1.setStroke(Color.RED);
        polygon_triangle1.setStrokeWidth(16);
        polygon_triangle1.setStrokeType(StrokeType.CENTERED);

        Polygon polygon_triangle2 = new Polygon(triangle_coordinates);
        polygon_triangle2.setStroke(Color.RED);
        polygon_triangle2.setStrokeWidth(16);
        polygon_triangle2.setStrokeType(StrokeType.INSIDE);
        polygon_triangle2.setLayoutX(100);

        Polygon polygon_triangle3 = new Polygon(triangle_coordinates);
        polygon_triangle3.setStroke(Color.RED);
        polygon_triangle3.setStrokeWidth(16);
        polygon_triangle3.setStrokeType(StrokeType.OUTSIDE);
        polygon_triangle3.setLayoutX(210);
        polygon_triangle3.setLayoutY(30);

        Text polygon_text = new Text("Same Polygon(triangle) but with CENTER, INSIDE and OUTSIDE Stroke type");
        polygon_text.setWrappingWidth(360);
        polygon_text.setTextAlignment(TextAlignment.CENTER);
        polygon_text.setX(20);
        polygon_text.setY(20);
        polygon_text.setFont(new Font(16));

        nodes.addAll(polygon_text, polygon_triangle1, polygon_triangle2, polygon_triangle3);
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
