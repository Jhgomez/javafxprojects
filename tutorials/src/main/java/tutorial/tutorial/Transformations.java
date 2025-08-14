package tutorial.tutorial;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;

/**
 * Transformation is changing graphics into something else by applying some rules. These rules allow you to
 * apply various types of transformations such as shifting the position of the object by maintaining its shape, rotating
 * the object based on an angle, changing the size of the object, etc.

 * You can apply transformations on either a single node or group of nodes. You can also apply a single type
 * of transformation or multiple transformations at a time to a node.

 * The Transform class implements affine transformations on JavaFX nodes. Affine transformations are nothing but the type
 * of transformations that preserve the points, straight lines, and parallelism of these straight lines of the source object
 * in the output object. These transformations can be applied on the JavaFX nodes with the help of Affine class extending
 * the Transform class.

 * JavaFX allows you to perform transformations along three coordinates. However, to display objects with 3 dimensions
 * (length, breadth and depth), JavaFX makes use of the concept called Z-buffering.

 * Z-buffering, also known as depth buffering, is a type of buffer in computer graphics which is used to preserve the depth
 * of a 3D object. This ensures that the perspective of a virtual object is the same as the real one: where the foreground
 * surface blocks the view of the background surface (like it looks to an eye).

 * If you want to create a 3-D effect transformation, specify all three coordinates x, y and z to the transformation
 * constructors along with x-axis and y-axis. And, to be able to see the 3-D objects and transformation effects in JavaFX,
 * users must enable the perspective camera.
 */
public class Transformations {
    ObservableList<Node> nodes;

    public void displayScreen(Runnable runnable) {
        Group group = new Group();
//        AnchorPane ap = new AnchorPane();
        nodes = group.getChildren();

        _2DRotation();
        _3DRotation();
//        multipleTransformations();
        Scene scene = new Scene(group, 1050, 540);

        scene.setFill(Paint.valueOf("#fdbf6f"));

        Stage stage = new Stage();

        stage.setTitle("2D Shapes");
        stage.setScene(scene);
        stage.sizeToScene();

        for (Node node : nodes) {
            if(!(node instanceof Slider)) {
                DragUtil.setDraggable(node);
            }
        }

        stage.show();

        stage.setOnCloseRequest(e -> runnable.run());
    }

    private void _2DRotation() {
        Rectangle rectangle = new Rectangle(260, 70, 50, 75);
        rectangle.setFill(Color.BURLYWOOD);
        rectangle.setStroke(Color.BLACK);

        Slider angleSlider = new Slider(0, 720, 0);
        Label angleLabel = new Label("Angle");
        Label angleValue = new Label("value: 0");

        Slider xSlider = new Slider(0, 520, 260);
        Label xLabel = new Label("Pivot X");
        Label xValue = new Label("value: 0");

        Slider ySlider = new Slider(0, 300, 70);
        Label yLabel = new Label("Pivot Y");
        Label yValue = new Label("value: 0");

        Rotate rotate = new Rotate();
        rotate.angleProperty().bind(angleSlider.valueProperty());
        rotate.pivotXProperty().bind(xSlider.valueProperty());
        rotate.pivotYProperty().bind(ySlider.valueProperty());

        rectangle.getTransforms().addAll(rotate);

        angleSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                    angleValue.setText("value: " + String.format("%.2f", newValue.doubleValue()));
                }
        );

        xSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                    xValue.setText("value: " + String.format("%.2f", newValue.doubleValue()));
                }
        );

        ySlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                    yValue.setText("value: " + String.format("%.2f", newValue.doubleValue()));
                }
        );

        Separator separator1 = new Separator(Orientation.VERTICAL);
        separator1.setStyle("-fx-background: orange; -fx-background-color: orange;");

        VBox slidersBox = new VBox(
                new Text("Rotate rectangle sliders"),
                angleLabel,
                angleSlider,
                angleValue,
                separator1,
                xLabel,
                xSlider,
                xValue,
                new Separator(Orientation.VERTICAL),
                yLabel,
                ySlider,
                yValue
        );

        slidersBox.setAlignment(Pos.CENTER);
        slidersBox.setPadding(new Insets(8, 8, 8, 8));

        slidersBox.setLayoutX(5);
        slidersBox.setLayoutY(5);

        nodes.addAll(rectangle, slidersBox);
    }

    private void _3DRotation() {
        //Drawing a Box
        Box box = new Box();

        //Setting the properties of the Box
        box.setWidth(150.0);
        box.setHeight(150.0);
        box.setDepth(150.0);
        box.setLayoutX(900);
        box.setLayoutY(400);

        //Creating the translation transformation
//        Translate translate = new Translate();
//        translate.setX(400);
//        translate.setY(150);
//        translate.setZ(25);

        Rotate rxBox = new Rotate(0, 0, 0, 0, Rotate.X_AXIS);
        Rotate ryBox = new Rotate(0, 0, 0, 0, Rotate.Y_AXIS);
        Rotate rzBox = new Rotate(0, 0, 0, 0, Rotate.Z_AXIS);

        rxBox.setAngle(30);
        ryBox.setAngle(50);
        rzBox.setAngle(30);

        box.getTransforms().addAll(rxBox, ryBox, rzBox);

        //========================== X
        Slider xAngleSlider = new Slider(0, 720, 0);
        Label xAngleLabel = new Label("X Angle");
        Label xAngleValue = new Label("value: 0");

        Slider xXPivotSlider = new Slider(600, 1200, 900);
        Label xXLabel = new Label("Pivot X-X");
        Label xXValue = new Label("value: 900");

        Slider xYPivotSlider = new Slider(-300, 300, 0);
        Label xYLabel = new Label("Pivot X-Y");
        Label xYValue = new Label("value: 0");

        Slider xZPivotSlider = new Slider(-300, 300, 0);
        Label xZLabel = new Label("Pivot X-Z");
        Label xZValue = new Label("value: 0");

        //============================ Y
        Slider yAngleSlider = new Slider(0, 720, 0);
        Label yAngleLabel = new Label("Y Angle");
        Label yAngleValue = new Label("value: 0");

        Slider yXPivotSlider = new Slider(-300, 300, 0);
        Label yXLabel = new Label("Pivot Y-X");
        Label yXValue = new Label("value: 0");

        Slider yYPivotSlider = new Slider(100, 700, 400);
        Label yYLabel = new Label("Pivot Y-Y");
        Label yYValue = new Label("value: 400");

        Slider yZPivotSlider = new Slider(-300, 300, 0);
        Label yZLabel = new Label("Pivot X-Z");
        Label yZValue = new Label("value: 0");

        //============================ Z
        Slider zAngleSlider = new Slider(0, 720, 0);
        Label zAngleLabel = new Label("Z Angle");
        Label zAngleValue = new Label("value: 0");

        Slider zXPivotSlider = new Slider(0, 400, 0);
        Label zXLabel = new Label("Pivot Z-X");
        Label zXValue = new Label("value: 0");

        Slider zYPivotSlider = new Slider(0, 400, 0);
        Label zYLabel = new Label("Pivot Z-Y");
        Label zYValue = new Label("value: 0");

        Slider zZPivotSlider = new Slider(-300, 300, 0);
        Label zZLabel = new Label("Pivot X-Z");
        Label zZValue = new Label("value: 0");

        //==================== X
        rxBox.angleProperty().bind(xAngleSlider.valueProperty());
        xAngleSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                    xAngleValue.setText("value: " + String.format("%.2f", newValue.doubleValue()));
                }
        );

        rxBox.pivotXProperty().bind(xXPivotSlider.valueProperty());
        xXPivotSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                    xXValue.setText("value: " + String.format("%.2f", newValue.doubleValue()));
                }
        );

        rxBox.pivotYProperty().bind(xYPivotSlider.valueProperty());
        xYPivotSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                    xYValue.setText("value: " + String.format("%.2f", newValue.doubleValue()));
                }
        );

        rxBox.pivotZProperty().bind(xZPivotSlider.valueProperty());
        xZPivotSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                    xZValue.setText("value: " + String.format("%.2f", newValue.doubleValue()));
                }
        );

        //==================== Y
        ryBox.angleProperty().bind(yAngleSlider.valueProperty());
        yAngleSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                    yAngleValue.setText("value: " + String.format("%.2f", newValue.doubleValue()));
                }
        );

        ryBox.pivotXProperty().bind(yXPivotSlider.valueProperty());
        yXPivotSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                    yXValue.setText("value: " + String.format("%.2f", newValue.doubleValue()));
                }
        );

        ryBox.pivotYProperty().bind(yYPivotSlider.valueProperty());
        yYPivotSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                    yYValue.setText("value: " + String.format("%.2f", newValue.doubleValue()));
                }
        );

        ryBox.pivotZProperty().bind(yZPivotSlider.valueProperty());
        yZPivotSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                    yZValue.setText("value: " + String.format("%.2f", newValue.doubleValue()));
                }
        );
        //==================== Z
        rzBox.angleProperty().bind(zAngleSlider.valueProperty());
        zAngleSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                    zAngleValue.setText("value: " + String.format("%.2f", newValue.doubleValue()));
                }
        );


        rzBox.pivotXProperty().bind(zXPivotSlider.valueProperty());
        zXPivotSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                    zXValue.setText("value: " + String.format("%.2f", newValue.doubleValue()));
                }
        );

        rzBox.pivotYProperty().bind(zYPivotSlider.valueProperty());
        zYPivotSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                    zYValue.setText("value: " + String.format("%.2f", newValue.doubleValue()));
                }
        );

        rzBox.pivotZProperty().bind(zZPivotSlider.valueProperty());
        zZPivotSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                    zZValue.setText("value: " + String.format("%.2f", newValue.doubleValue()));
                }
        );


        VBox slidersBox = new VBox(
                new Text("X Axis Sliders"),
                xAngleLabel,
                xAngleSlider,
                xAngleValue,
                new Separator(Orientation.VERTICAL),
                xXLabel,
                xXPivotSlider,
                xXValue,
                new Separator(Orientation.VERTICAL),
                xYLabel,
                xYPivotSlider,
                xYValue,
                new Separator(Orientation.VERTICAL),
                xZLabel,
                xZPivotSlider,
                xZValue,
                new Separator(Orientation.VERTICAL),
                new Text("Y Axis Sliders"),
                yAngleLabel,
                yAngleSlider,
                yAngleValue,
                new Separator(Orientation.VERTICAL),
                yXLabel,
                yXPivotSlider,
                yXValue,
                new Separator(Orientation.VERTICAL),
                yYLabel,
                yYPivotSlider,
                yYValue,
                new Separator(Orientation.VERTICAL),
                yZLabel,
                yZPivotSlider,
                yZValue,
                new Separator(Orientation.VERTICAL),
                new Text("Z Axis Sliders"),
                zAngleLabel,
                zAngleSlider,
                zAngleValue,
                new Separator(Orientation.VERTICAL),
                zXLabel,
                zXPivotSlider,
                zXValue,
                new Separator(Orientation.VERTICAL),
                zYLabel,
                zYPivotSlider,
                zYValue,
                new Separator(Orientation.VERTICAL),
                zZLabel,
                zZPivotSlider,
                zZValue,
                new Separator(Orientation.VERTICAL)
        );

        slidersBox.setLayoutX(500);

        nodes.addAll(box, slidersBox);
    }

    private void multipleTransformations() {
        Rectangle rectangle = new Rectangle(5, 5, 50, 75);
        rectangle.setFill(Color.BURLYWOOD);
        rectangle.setStroke(Color.BLACK);

        Slider angleSlider = new Slider(0, 720, 0);
        angleSlider.setLayoutX(70);
        angleSlider.setLayoutY(70);

        Slider xSlider = new Slider(0, 300, 0);
        xSlider.setLayoutX(70);
        xSlider.setLayoutY(85);

        Slider ySlider = new Slider(0, 300, 0);
        ySlider.setLayoutX(70);
        ySlider.setLayoutY(100);

        Rotate rotate = new Rotate();
        rotate.angleProperty().bind(angleSlider.valueProperty());
        rotate.pivotXProperty().bind(xSlider.valueProperty());
        rotate.pivotYProperty().bind(ySlider.valueProperty());

        Scale scale = new Scale();
        scale.setX(3.5);
        scale.setY(3.5);
        scale.setPivotX(300);
        scale.setPivotY(135);


        Translate translate = new Translate();
        translate.setX(250);
        translate.setY(0);
        translate.setZ(0);

        rectangle.getTransforms().addAll(rotate);

        nodes.addAll(rectangle, angleSlider, xSlider, ySlider);
    }
}
