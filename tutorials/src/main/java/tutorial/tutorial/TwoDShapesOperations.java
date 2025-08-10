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
        substractionOperation();

        Scene scene = new Scene(group, 1025, 640);

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
     * Returns the area of the first shape excluding the area overlapped by the second one
     */
    private void substractionOperation() {
        Circle circle = new Circle(165, 332, 150);
        circle.setFill(Color.GREEN);

        SVGPath triangleSvgPath = new SVGPath();
        String triagnlePath = "M 100 100 L 300 100 L 200 300 z";
        //Setting the SVGPath in the form of string
        triangleSvgPath.setContent(triagnlePath);
        triangleSvgPath.setLayoutX(-39);
        triangleSvgPath.setLayoutY(155);

        SVGPath bezierSvgPath = new SVGPath();

        String bezierPath = "M 70 110 C 70 180, 210 180, 210 110";

        //Setting the SVGPath in the form of string
        bezierSvgPath.setContent(bezierPath);

        // Setting the stroke and fill of the path
        bezierSvgPath.setStroke(Color.BLACK);
        bezierSvgPath.setFill(Color.YELLOW);
        bezierSvgPath.setLayoutX(21);
        bezierSvgPath.setLayoutY(154);

        Shape subtractionShapeResult = Shape.subtract(circle, Shape.subtract(triangleSvgPath, bezierSvgPath));
        subtractionShapeResult.setLayoutX(270);
        subtractionShapeResult.setLayoutY(150);


        Text pathText = new Text("Same situation, individual objects, then shape result of substraction between triangle and bezier and result is subtracted from circle");
        pathText.setWrappingWidth(450);
        pathText.setFont(new Font(16));
        pathText.setX(20);
        pathText.setY(135);

        nodes.addAll(circle, triangleSvgPath, bezierSvgPath, subtractionShapeResult, pathText);
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
