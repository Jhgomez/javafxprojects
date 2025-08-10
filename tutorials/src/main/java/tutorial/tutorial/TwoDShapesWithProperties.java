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
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

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
        smoothProperty();

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
     * The smooth property in JavaFX is used to smoothen the edges of a certain 2D shape. This property is of the type Boolean. If this value is true, then the edges of the shape will be smooth.
     */
    private void smoothProperty() {
        Circle circle = new Circle(850.0, 450.0, 200.0);

        circle.setFill(Color.BLUE);
        circle.setStroke(Color.BLACK);
        circle.setStrokeWidth(16);
        circle.setSmooth(true);

        Circle circle1 = new Circle(1275.0, 450.0, 200.0);

        circle1.setFill(Color.BLUE);
        circle1.setStroke(Color.BLACK);
        circle1.setStrokeWidth(16);
        circle1.setSmooth(false);

        Text circleText = new Text("Circles, first with setSmooth \"true\"(default val in all shapes), second set to false");
        circleText.setFont(new Font(16));
        circleText.setX(830);
        circleText.setY(230);

        nodes.addAll(circle, circle1, circleText);
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
}
