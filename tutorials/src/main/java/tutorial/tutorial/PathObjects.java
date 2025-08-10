package tutorial.tutorial;

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
import javafx.stage.Stage;

/**
 * To draw complex structures JavaFX provides a class named Path. This class represents the geometrical outline of a shape.
 *
 * It is attached to an observable list which holds various Path Elements such as moveTo, LineTo, HlineTo, VlineTo,
 * ArcTo, QuadCurveTo, CubicCurveTo.
 *
 * On instantiating, this class constructs a path based on the given path elements.
 */
public class PathObjects {
    ObservableList<Node> nodes;

    public void displayScreen(Runnable runnable) {
        Group group = new Group();
//        AnchorPane ap = new AnchorPane();
        nodes = group.getChildren();

        aStarPath();
        crossedLines();
        vLineAndHLine();
        quadraticCurve();
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
    private void quadraticCurve() {
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
    private void vLineAndHLine() {
        Path path = new Path();

        ObservableList<PathElement> elements = path.getElements();

        MoveTo moveTo = new MoveTo();
        moveTo.setX(400.0);
        moveTo.setY(70.0f);
        elements.add(moveTo);

        VLineTo vlineTo = new VLineTo();
        vlineTo.setY(90);
        elements.add(vlineTo);

        HLineTo hLineTo = new HLineTo();
        hLineTo.setX(480);
        elements.add(hLineTo);

        moveTo = new MoveTo();
        moveTo.setX(480.0);
        moveTo.setY(90.0);
        elements.add(moveTo);

        vlineTo = new VLineTo();
        vlineTo.setY(70);
        elements.add(vlineTo);

        hLineTo = new HLineTo();
        hLineTo.setX(400);
        elements.add(hLineTo);

        Text rectanglePathText = new Text("Rectangle edge drawn using path objects VLine and HLine");
        rectanglePathText.setFont(new Font(16));
        rectanglePathText.setWrappingWidth(250);
        rectanglePathText.setX(335);
        rectanglePathText.setY(20);

        nodes.addAll(path, rectanglePathText);
    }

    private void crossedLines() {
        Path path = new Path();

        ObservableList<PathElement> elements = path.getElements();

        MoveTo moveTo = new MoveTo();
        moveTo.setX(240.0);
        moveTo.setY(70.0f);
        elements.add(moveTo);

        LineTo lineTo = new LineTo();
        lineTo.setX(300.0f);
        lineTo.setY(70.0f);
        elements.add(lineTo);

        MoveTo moveTo2 = new MoveTo();
        moveTo2.setX(270.0f);
        moveTo2.setY(40.0f);
        elements.add(moveTo2);

        LineTo lineTo2 = new LineTo();
        lineTo2.setX(270.0);
        lineTo2.setY(100.0f);
        elements.add(lineTo2);

        Text polyline_text = new Text("Crossed Lines");
        polyline_text.setX(220);
        polyline_text.setY(20);
        polyline_text.setFont(new Font(16));

        nodes.addAll(polyline_text, path);
    }

    /**
     * The Path Element MoveTo is used to move the current position of the path to a specified point. It is generally
     * used to set the starting point of a shape drawn using the path elements.
     */
    private void aStarPath() {
        Path path = new Path();

        ObservableList<PathElement> elements = path.getElements();

        //Moving to the starting point
        MoveTo moveTo = new MoveTo(8, 71);
        elements.add(moveTo);

        // each entry in first level array is an X,Y pair
        int[][] starCoordinates = {
                {221, 161},
                {26, 232},
                {132,52},
                {169, 250},
                {8, 71}
        };

        for (int[] coordinate : starCoordinates) {
            elements.add(new LineTo(coordinate[0], coordinate[1]));
        }

        Text polygon_text = new Text("Star Path");
        polygon_text.setX(20);
        polygon_text.setY(20);
        polygon_text.setFont(new Font(16));

        nodes.addAll(polygon_text, path);
    }
}
