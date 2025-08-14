package tutorial.tutorial;

import javafx.animation.PathTransition;
import javafx.animation.StrokeTransition;
import javafx.beans.value.ChangeListener;
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
import javafx.scene.layout.Background;
import javafx.scene.layout.Border;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Formatter;

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

        rotation();
//        multipleTransformations();
//        _3DRotation();
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

    private void rotation() {
        Rectangle rectangle = new Rectangle(260, 70, 50, 75);
        rectangle.setFill(Color.BURLYWOOD);
        rectangle.setStroke(Color.BLACK);

        Slider angleSlider = new Slider(0, 720, 0);
//        angleSlider.setLayoutX(70);
//        angleSlider.setLayoutY(70);

        Label angleLabel = new Label("Angle");
        Label angleValue = new Label("value: 0");

        Slider xSlider = new Slider(0, 520, 260);
//        xSlider.setLayoutX(70);
//        xSlider.setLayoutY(85);

        Label xLabel = new Label("Pivot X");
        Label xValue = new Label("value: 0");

        Slider ySlider = new Slider(0, 300, 70);
//        ySlider.setLayoutX(70);
//        ySlider.setLayoutY(100);
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

        //Creating the translation transformation
        Translate translate = new Translate();
        translate.setX(400);
        translate.setY(150);
        translate.setZ(25);

        Rotate rxBox = new Rotate(0, 0, 0, 0, Rotate.X_AXIS);
        Rotate ryBox = new Rotate(0, 0, 0, 0, Rotate.Y_AXIS);
        Rotate rzBox = new Rotate(0, 0, 0, 0, Rotate.Z_AXIS);
        rxBox.setAngle(30);
        ryBox.setAngle(50);
        rzBox.setAngle(30);
        box.getTransforms().addAll(translate,rxBox, ryBox, rzBox);

        nodes.add(box);
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
