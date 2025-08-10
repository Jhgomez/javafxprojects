package tutorial.tutorial;

import javafx.animation.PathTransition;
import javafx.animation.StrokeTransition;
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
        cubicCurveTo();
        arcTo();

        Scene scene = new Scene(group, 1050, 540);

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
    private void arcTo() {
        Path path = new Path();

        //Moving to the starting point
        MoveTo moveTo = new MoveTo();
        moveTo.setX(700.0);
        moveTo.setY(450.0);

        //Instantiating the arcTo class
        ArcTo arcTo = new ArcTo();

        //setting properties of the path element arc
        arcTo.setX(750.0);
        arcTo.setY(250.0);

        arcTo.setRadiusX(50.0);
        arcTo.setRadiusY(50.0);

        //Adding the path elements to Observable list of the Path class
        path.getElements().add(moveTo);
        path.getElements().add(arcTo);
        path.setStrokeWidth(6);

        //Creating a path transition
        PathTransition pathTransition = new PathTransition();

        //Setting the duration of the path transition
        pathTransition.setDuration(Duration.millis(1500));

        // Drawing a circle
        Circle circle = new Circle(750, 250, 40.0f);

        //Setting the node
        pathTransition.setNode(circle);

        //Setting the path
        pathTransition.setPath(path);

        //Setting the orientation of the path
        pathTransition.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);

        //Setting the cycle count for the transition
        pathTransition.setCycleCount(PathTransition.INDEFINITE);

        //Setting auto reverse value to true
        pathTransition.setAutoReverse(true);

        //Playing the animation
        pathTransition.play();

        Text pathText = new Text("SVGs Paths, drawing a triangle and bezier curve using SVG Paths");
        pathText.setFont(new Font(16));
        pathText.setX(550);
        pathText.setY(215);

        nodes.addAll(path, circle, pathText);
    }

    /**
     * A Cubic curve is a two dimensional structure that is a type of a Bezier curve. A Bezier curve is defined as a curve
     * that passes through a set of control points (P0...Pn). It is called a Cubic curve when the number of control points
     * are 4 (or, if the order of the curve is 3).
     */
    private void cubicCurveTo() {
        Path path = new Path();

        //Moving to the starting point
        MoveTo moveTo = new MoveTo();
        moveTo.setX(630.0);
        moveTo.setY(110.0);

        //Instantiating the class CubicCurve
        CubicCurveTo cubicCurveTo = new CubicCurveTo();

        //Setting properties of the class CubicCurve
        cubicCurveTo.setControlX1(930.0f);
        cubicCurveTo.setControlY1(0.0f);
        cubicCurveTo.setControlX2(705.0f);
        cubicCurveTo.setControlY2(210.0f);
        cubicCurveTo.setX(1030.0f);
        cubicCurveTo.setY(110.0f);

        //creating stroke transition
        StrokeTransition st = new StrokeTransition();

        //Setting the duration of the transition
        st.setDuration(Duration.millis(2000));

        //Setting the shape for the transition
        st.setShape(path);

        //Setting the fromValue property of the transition (color)
        st.setFromValue(Color.BLACK);

        //Setting the toValue property of the transition (color)
        st.setToValue(Color.BROWN);

        //Setting the cycle count for the transition
        st.setCycleCount(PathTransition.INDEFINITE);

        //Setting auto reverse value to true
        st.setAutoReverse(true);

        //Playing the animation
        st.play();

        //Adding the path elements to Observable list of the Path class
        path.getElements().add(moveTo);
        path.getElements().add(cubicCurveTo);
        path.setStrokeWidth(16);

        Text cubicCurvePathText = new Text("CubicCurveTo Path object");
        cubicCurvePathText.setFont(new Font(16));
        cubicCurvePathText.setX(610);
        cubicCurvePathText.setY(20.0);

        nodes.addAll(path, cubicCurvePathText);
    }

    /**
     * Mathematically, a quadratic curve is one that is described by a quadratic function like − y = ax2 + bx + c.
     *
     * A quadratic curve is a Bezier parametric curve in the XY plane which is a curve of degree 2. It is drawn using
     * three points: start point, end point and control point
     */
    private void quadraticCurve() {
        Path path = new Path();

        //Moving to the starting point
        MoveTo moveTo = new MoveTo();
        moveTo.setX(20);
        moveTo.setY(390.0);

        //Instantiating the class QuadCurveTo
        QuadCurveTo quadCurveTo = new QuadCurveTo();

        //Setting properties of the class QuadCurve
        quadCurveTo.setX(420);
        quadCurveTo.setY(460.0f);
        quadCurveTo.setControlX(170.0f);
        quadCurveTo.setControlY(240.0f);

        //Adding the path elements to Observable list of the Path class
        path.getElements().add(moveTo);
        path.getElements().add(quadCurveTo);

        Text quadCurveText2 = new Text("QuadCurveTo path object");
        quadCurveText2.setFont(new Font(16));
        quadCurveText2.setX(20);
        quadCurveText2.setY(300);

        nodes.addAll(path, quadCurveText2);
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
