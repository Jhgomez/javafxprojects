package tutorial.tutorial;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Shear;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;

import java.util.HashMap;

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
    HashMap<String, Runnable> transformations;
    Scene scene;

    public void displayScreen(Runnable runnable) {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setStyle("-fx-background: orange; -fx-background-color: orange;");
        Pane pane = new Pane();

        scrollPane.setContent(pane);

        nodes = pane.getChildren();

        HashMap<String, Runnable> transformations1 = getTransformations();
        ObservableList<String> options = FXCollections.observableArrayList(transformations1.keySet());

        ComboBox<String> transformationsComboBox = new ComboBox<>(options);
        transformationsComboBox.setPromptText("Choose Transformation");

        transformationsComboBox.valueProperty().addListener((observable, oldVal, newVal) -> {
            if(!newVal.equals(oldVal)) {
                nodes.clear();
                nodes.add(transformationsComboBox);

                if (transformations1.get(newVal) != null) {
                    transformations1.get(newVal).run();
                }

                for (Node node : nodes) {
                    DragUtil.setDraggable(node);
                }
            }
        });

        transformationsComboBox.setLayoutX(10);
        transformationsComboBox.setLayoutY(10);

        nodes.add(transformationsComboBox);

//        multipleTransformations();
        scene = new Scene(scrollPane, 1050, 540);

        scene.setFill(Paint.valueOf("#fdbf6f"));

        PerspectiveCamera camera = new PerspectiveCamera();

        scene.setCamera(camera);

        Stage stage = new Stage();

        stage.setTitle("2D Shapes");
        stage.setScene(scene);
        stage.sizeToScene();

        stage.show();

        stage.setOnCloseRequest(e -> runnable.run());
    }

    private HashMap<String, Runnable> getTransformations() {
        if (transformations == null) {
            transformations = new HashMap<>();

            transformations.put("Rotation", () -> {
                _2DRotation();
                _3DRotation();
            });

            transformations.put("Scaling/Rotation/Translation", () -> {
                enableScalingControl(
                        //Box is created at 0,0,0 by default
                        new Box(150, 150, 150),
                        200,
                        350,
                        0,
                        400,
                        50
                );

                enableScalingControl(
                        //Sphere is created at 0,0,0 by default
                        new Sphere(50, 150),
                        800,
                        350,
                        0,
                        900,
                        50
                );

                enableScalingControl(
                        //Sphere is created at 0,0,0 by default
                        new Cylinder(50, 50,50),
                        1300,
                        350,
                        0,
                        1420,
                        50
                );

//                sphereScaling();
            });
        }

        transformations.put("Scaling/Rotation/Translation/Shearing", () -> {
            enableShearingControl(
                    new Cylinder(150, 150, 150),
                    200,
                    350,
                    0,
                    400,
                    50
            );

            Polygon hexagon1 = new Polygon();

            //Adding coordinates to the hexagon
            hexagon1.getPoints().addAll(
                    0.0, 0.0,
                    200.0, 0.0,
                    250.0, 100.0,
                    200.0, 200.0,
                    0.0, 200.0,
                    -50.0, 100.0
            );

            hexagon1.setFill(Color.BLUE);
            hexagon1.setStroke(Color.BLACK);

            enableShearingControl(
                    hexagon1,
                    800,
                    350,
                    0,
                    1100,
                    50
            );
        });

        return transformations;
    }

    private void enableShearingControl(
            Node shape,
            double shapeXTranslate,
            double shapeYTranslate,
            double shapeZTranslate,
            double boardX,
            double boardY
    ) {
        enableScalingControl(shape, shapeXTranslate, shapeYTranslate, shapeZTranslate, boardX, boardY);

        Shear shear =  new Shear();
        shear.setPivotX(shapeXTranslate);
        shear.setPivotY(shapeYTranslate);

        //Setting the dimensions for the shear
        shear.setX(1);
        shear.setY(1);

        shape.getTransforms().add(shear);

        Slider pivotX = new Slider(-300, 300,0);
        Label pivotXLabel = new Label("Pivot X");
        Label pivotXValue =  new Label("Value: 0");
        pivotX.setBlockIncrement(1);

        shear.pivotXProperty().bind(pivotX.valueProperty());
        pivotX.valueProperty().addListener((observable, oldValue, newValue) -> {
            pivotXValue.setText("Value: " + String.format("%.2f", newValue.doubleValue()));
        });

        Slider pivotY = new Slider(-300, 300,0);
        Label pivotYLabel = new Label("Pivot Y");
        Label pivotYValue =  new Label("Value: 0");
        pivotY.setBlockIncrement(1);

        shear.pivotYProperty().bind(pivotY.valueProperty());
        pivotY.valueProperty().addListener((observable, oldValue, newValue) -> {
            pivotYValue.setText("Value: " + String.format("%.2f", newValue.doubleValue()));
        });

        Slider shearX = new Slider(-5, 5,0);
        Label shearXLabel = new Label("Shear X");
        Label shearXValue =  new Label("Value: 1");
        shearX.setBlockIncrement(0.1);

        shear.xProperty().bind(shearX.valueProperty());
        shearX.valueProperty().addListener((observable, oldValue, newValue) -> {
            shearXValue.setText("Value: " + String.format("%.2f", newValue.doubleValue()));
        });

        Slider shearY = new Slider(-5, 5,0);
        Label shearYLabel = new Label("Pivot Y");
        Label shearYValue =  new Label("Shear: 1");
        shearY.setBlockIncrement(0.1);

        shear.yProperty().bind(shearY.valueProperty());
        shearY.valueProperty().addListener((observable, oldValue, newValue) -> {
            shearYValue.setText("Value: " + String.format("%.2f", newValue.doubleValue()));
        });

        VBox vBox = new VBox(
                new Text("Shear Control"),
                new Separator(Orientation.VERTICAL),
                pivotXLabel,
                pivotX,
                pivotXValue,
                new Separator(Orientation.VERTICAL),
                pivotYLabel,
                pivotY,
                pivotYValue,
                new Separator(Orientation.VERTICAL),
                shearXLabel,
                shearX,
                shearXValue,
                new Separator(Orientation.VERTICAL),
                shearYLabel,
                shearY,
                shearYValue,
                new Separator(Orientation.VERTICAL)
        );

        vBox.setLayoutX(boardX + 285);
        vBox.setLayoutY(boardY);

        nodes.add(vBox);
    }

    /**
     * In the scaling transformation process, you either expand or compress the dimensions of the object. Scaling can be
     * achieved by multiplying the original coordinates of the object with the scaling factor to get the desired result.

     * Scaling transformation is used to change the size of an object.
     */
    private void enableScalingControl(
            Node shape3D,
            double shapeXTranslate,
            double shapeYTranslate,
            double shapeZTranslate,
            double boardX,
            double boardY
    ) {
        Rotate xRotate = new Rotate(0, 0, 0, 0, Rotate.X_AXIS);
        Rotate yRotate = new Rotate(0, 0, 0, 0, Rotate.Y_AXIS);
        Rotate zRotate = new Rotate(0, 0, 0, 0, Rotate.Z_AXIS);

        Translate translate = new Translate();
        translate.setX(shapeXTranslate);
        translate.setY(shapeYTranslate);
        translate.setZ(shapeZTranslate);

        Scale scale = new Scale();

        //Setting the dimensions for the transformation
        scale.setX(1);
        scale.setY(1);
        scale.setZ(1);

        scale.setPivotX(0);
        scale.setPivotY(0);
        scale.setPivotZ(0);

        shape3D.getTransforms().addAll(translate, scale, xRotate, yRotate, zRotate);

        //========================= Scale X and scale pivot X
        Slider scaleX = new Slider(-20, 20, 1);
        Label scaleXLabel = new Label("Scale X");
        Label ScaleXValue = new Label("value: 0");

        Slider scalePivotX = new Slider(-300, 300, 0);
        Label scalePivotXLabel = new Label("Scale Pivot X");
        Label scalePivotXValue = new Label("value: 0");

        scale.xProperty().bind(scaleX.valueProperty());
        scaleX.valueProperty().addListener((observable, oldValue, newValue) -> {
                    ScaleXValue.setText("value: " + String.format("%.2f", newValue.doubleValue()));
                }
        );

        scale.pivotXProperty().bind(scalePivotX.valueProperty());
        scalePivotX.valueProperty().addListener((observable, oldValue, newValue) -> {
                    scalePivotXValue.setText("value: " + String.format("%.2f", newValue.doubleValue()));
                }
        );

        //========================= Scale Y and scale pivot Y
        Slider scaleY = new Slider(-20, 20, 1);
        Label scaleYLabel = new Label("Scale Y");
        Label ScaleYValue = new Label("value: 0");

        Slider scalePivotY = new Slider(-300, 300, 0);
        Label scalePivotYLabel = new Label("Scale Pivot Y");
        Label scalePivotYValue = new Label("value: 0");

        scale.yProperty().bind(scaleY.valueProperty());
        scaleY.valueProperty().addListener((observable, oldValue, newValue) -> {
                    ScaleYValue.setText("value: " + String.format("%.2f", newValue.doubleValue()));
                }
        );

        scale.pivotYProperty().bind(scalePivotY.valueProperty());
        scalePivotY.valueProperty().addListener((observable, oldValue, newValue) -> {
                    scalePivotYValue.setText("value: " + String.format("%.2f", newValue.doubleValue()));
                }
        );

        //========================= Scale Z and scale pivot Z
        Slider scaleZ = new Slider(-20, 20, 1);
        Label scaleZLabel = new Label("Scale Z");
        Label ScaleZValue = new Label("value: 0");

        Slider scalePivotZ = new Slider(-300, 300, 0);
        Label scalePivotZLabel = new Label("Scale Pivot Z");
        Label scalePivotZValue = new Label("value: 0");

        scale.zProperty().bind(scaleZ.valueProperty());
        scaleZ.valueProperty().addListener((observable, oldValue, newValue) -> {
                    ScaleZValue.setText("value: " + String.format("%.2f", newValue.doubleValue()));
                }
        );

        scale.pivotZProperty().bind(scalePivotZ.valueProperty());
        scalePivotZ.valueProperty().addListener((observable, oldValue, newValue) -> {
                    scalePivotZValue.setText("value: " + String.format("%.2f", newValue.doubleValue()));
                }
        );

        //========================= Scale Sliders
        VBox scaleSlidersBox = new VBox(
                new Text("Scale controls"),
                new Separator(Orientation.VERTICAL),
                scaleXLabel,
                scaleX,
                ScaleXValue,
                new Separator(Orientation.VERTICAL),
                scaleYLabel,
                scaleY,
                ScaleYValue,
                new Separator(Orientation.VERTICAL),
                scaleZLabel,
                scaleZ,
                ScaleZValue,
                new Separator(Orientation.VERTICAL),
                new Text("Scale Pivot XYZ Sliders"),
                scalePivotXLabel,
                scalePivotX,
                scalePivotXValue,
                new Separator(Orientation.VERTICAL),
                scalePivotYLabel,
                scalePivotY,
                scalePivotYValue,
                new Separator(Orientation.VERTICAL),
                scalePivotZLabel,
                scalePivotZ,
                scalePivotZValue
        );

        //========================== X
        Slider xAngleSlider = new Slider(0, 720, 0);
        Label xAngleLabel = new Label("X Angle");
        Label xAngleValue = new Label("value: 0");

        Slider xXPivotSlider = new Slider(-300, 300, 0);
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

        Slider yYPivotSlider = new Slider(-300, 300, 0);
        Label yYLabel = new Label("Pivot Y-Y");
        Label yYValue = new Label("value: 400");

        Slider yZPivotSlider = new Slider(-300, 300, 0);
        Label yZLabel = new Label("Pivot X-Z");
        Label yZValue = new Label("value: 0");

        //============================ Z
        Slider zAngleSlider = new Slider(0, 720, 0);
        Label zAngleLabel = new Label("Z Angle");
        Label zAngleValue = new Label("value: 0");

        Slider zXPivotSlider = new Slider(-300, 300, 0);
        Label zXLabel = new Label("Pivot Z-X");
        Label zXValue = new Label("value: 0");

        Slider zYPivotSlider = new Slider(-300, 300, 0);
        Label zYLabel = new Label("Pivot Z-Y");
        Label zYValue = new Label("value: 0");

        Slider zZPivotSlider = new Slider(-300, 300, 0);
        Label zZLabel = new Label("Pivot X-Z");
        Label zZValue = new Label("value: 0");

        //==================== Scale Slider

        //==================== X
        xRotate.angleProperty().bind(xAngleSlider.valueProperty());
        xAngleSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                    xAngleValue.setText("value: " + String.format("%.2f", newValue.doubleValue()));
                }
        );

        xRotate.pivotXProperty().bind(xXPivotSlider.valueProperty());
        xXPivotSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                    xXValue.setText("value: " + String.format("%.2f", newValue.doubleValue()));
                }
        );

        xRotate.pivotYProperty().bind(xYPivotSlider.valueProperty());
        xYPivotSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                    xYValue.setText("value: " + String.format("%.2f", newValue.doubleValue()));
                }
        );

        xRotate.pivotZProperty().bind(xZPivotSlider.valueProperty());
        xZPivotSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                    xZValue.setText("value: " + String.format("%.2f", newValue.doubleValue()));
                }
        );

        //==================== Y
        yRotate.angleProperty().bind(yAngleSlider.valueProperty());
        yAngleSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                    yAngleValue.setText("value: " + String.format("%.2f", newValue.doubleValue()));
                }
        );

        yRotate.pivotXProperty().bind(yXPivotSlider.valueProperty());
        yXPivotSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                    yXValue.setText("value: " + String.format("%.2f", newValue.doubleValue()));
                }
        );

        yRotate.pivotYProperty().bind(yYPivotSlider.valueProperty());
        yYPivotSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                    yYValue.setText("value: " + String.format("%.2f", newValue.doubleValue()));
                }
        );

        yRotate.pivotZProperty().bind(yZPivotSlider.valueProperty());
        yZPivotSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                    yZValue.setText("value: " + String.format("%.2f", newValue.doubleValue()));
                }
        );
        //==================== Z
        zRotate.angleProperty().bind(zAngleSlider.valueProperty());
        zAngleSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                    zAngleValue.setText("value: " + String.format("%.2f", newValue.doubleValue()));
                }
        );


        zRotate.pivotXProperty().bind(zXPivotSlider.valueProperty());
        zXPivotSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                    zXValue.setText("value: " + String.format("%.2f", newValue.doubleValue()));
                }
        );

        zRotate.pivotYProperty().bind(zYPivotSlider.valueProperty());
        zYPivotSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                    zYValue.setText("value: " + String.format("%.2f", newValue.doubleValue()));
                }
        );

        zRotate.pivotZProperty().bind(zZPivotSlider.valueProperty());
        zZPivotSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                    zZValue.setText("value: " + String.format("%.2f", newValue.doubleValue()));
                }
        );


        VBox rotateSlidersBox = new VBox(
                new Text("Rotation Controls"),
                new Separator(Orientation.VERTICAL),
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

        HBox slidersBox = new HBox(rotateSlidersBox, scaleSlidersBox);
//        slidersBox.setLayoutX(500);
//        slidersBox.setLayoutY(50);
        slidersBox.setLayoutX(boardX);
        slidersBox.setLayoutY(boardY);

        Text title = new Text("3D Scaling");
        title.setFont(Font.font(16));
        title.setX(boardX);
        title.setY(boardY - 20);

        nodes.addAll(shape3D, title, slidersBox);
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
                new Text("Rotation Controls"),
                new Separator(Orientation.VERTICAL),
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
        slidersBox.setLayoutY(90);

        Text title = new Text("2D Rotation Transformation");
        title.setFont(Font.font(16));
        title.setX(10);
        title.setY(70);

        nodes.addAll(title, rectangle, slidersBox);
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

        /*
         here instead of defining the same coordinates as the layout X and layout Y of the box, I could have created the box
         without changing its coordinates(they will be 0,0,0 by default) and then all rotates and bindings between the rotate
         effects of each axis and the slider to control the angle could have been set to 0 this will save us the task of calculating
         the center of the axis and then I could have just applied a translate transformation to the box which move the box
         keeping all the values relative
         */
        Rotate rxBox = new Rotate(0, 900, 0, 0, Rotate.X_AXIS);
        Rotate ryBox = new Rotate(0, 0, 400, 0, Rotate.Y_AXIS);
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
        slidersBox.setLayoutY(90);

        Text title = new Text("3D Rotation Transformation");
        title.setFont(Font.font(16));
        title.setX(500);
        title.setY(70);

        nodes.addAll(title, box, slidersBox);
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
