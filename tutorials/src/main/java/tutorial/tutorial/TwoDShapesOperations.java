package tutorial.tutorial;

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

import java.awt.geom.Ellipse2D;

/**
 * A 2D shape is defined as any figure that can be displayed on a two-dimensional plane.
 *
 *  To offer permutations and combinations of these shapes, JavaFX also allows you to perform some operations on them.
 *
 *  There are three operations available in JavaFX that can be performed on 2D shapes.
 *
 *  - Union Operation
 *
 *  - Intersection Operation
 *
 *  - Subtraction Operation
 */
public class TwoDShapesOperations {
    ObservableList<Node> nodes;

    public void displayScreen(Runnable runnable) {
        Group group = new Group();
        nodes = group.getChildren();

        unionOperation();
        intersectionOperation();
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
     * The areas of two or more shapes are intersected together and the common area of these shapes is obtained as a result.
     */
    private void intersectionOperation() {
        Circle circle = new Circle(650, 200, 150);
        circle.setFill(Color.GREEN);

        Rectangle rectangle = new Rectangle(500, 60, 200, 75);
        rectangle.setFill(Color.BROWN);

        Text circleText = new Text("Same situation, individual objects, then shape obtained from intersection operation");
        circleText.setWrappingWidth(450);
        circleText.setFont(new Font(16));
        circleText.setX(530);
        circleText.setY(20.0);

        Shape intersectionShape = Shape.intersect(circle, rectangle);
        intersectionShape.setFill(Color.VIOLET);
        intersectionShape.setLayoutX(300);

        nodes.addAll(circle, rectangle, intersectionShape, circleText);
    }

    /**
     * Creates a single shape by combining the area of two shapes
     */
    private void unionOperation() {
        Line line = new Line(20, 80.0, 220, 80);

        line.setStroke(Color.GREEN);
        line.setStrokeWidth(16);
        line.setStrokeLineCap(StrokeLineCap.BUTT);

        Line line1 = new Line(20, 96, 220, 96);

        line1.setStroke(Color.YELLOW);
        line1.setStrokeWidth(16);
        line1.setStrokeLineCap(StrokeLineCap.ROUND);

        Line line2 = new Line(20, 104, 220, 104);

        line2.setStroke(Color.BLUE);
        line2.setStrokeWidth(16);
        line2.setStrokeLineCap(StrokeLineCap.SQUARE);

        Shape mergedLinesShape = Shape.union(line, Shape.union(line1, line2));

        mergedLinesShape.setLayoutX(250);
        mergedLinesShape.setFill(Color.RED);

        Text quadCurveText2 = new Text("Two set of lines with stroke line cap, Butt, Square, Round, first set is separated/individual lines, second set is actually all these lines \"merged\" in a single object by the Union operator");
        quadCurveText2.setWrappingWidth(500);
        quadCurveText2.setFont(new Font(16));
        quadCurveText2.setX(20);
        quadCurveText2.setY(20);

        nodes.addAll(line, line1, line2, mergedLinesShape, quadCurveText2);
    }

}
