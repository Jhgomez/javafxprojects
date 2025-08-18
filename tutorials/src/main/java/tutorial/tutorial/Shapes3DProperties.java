package tutorial.tutorial;

import javafx.animation.*;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Shear;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Objects;

/**
 * 3D objects properties can range from deciding the material of a shape: interior and exterior, rendering the 3D object
 * geometry and culling faces of the 3D shape.

 * All these properties are offered in order to improvise the look and feel of a 3D object
 */
public class Shapes3DProperties {
    ObservableList<Node> nodes;
    HashMap<String, Runnable> animations;
    Scene scene;
    HashMap<String, Interpolator> interpolators;

    public void displayScreen(Runnable runnable) {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setStyle("-fx-background: orange; -fx-background-color: orange;");
        Pane pane = new Pane();

        scrollPane.setContent(pane);

        nodes = pane.getChildren();

        ObservableList<String> options = FXCollections.observableArrayList();

        ComboBox<String> transformationsComboBox = new ComboBox<>(options);
        transformationsComboBox.setPromptText("Choose Transformation");

        transformationsComboBox.valueProperty().addListener((observable, oldVal, newVal) -> {
            if(!newVal.equals(oldVal)) {
                nodes.clear();
                nodes.add(transformationsComboBox);

                if (animations.get(newVal) != null) {
                    animations.get(newVal).run();
                }

                for (Node node : nodes) {
                    DragUtil.setDraggable(node);
                }
            }
        });

        transformationsComboBox.setLayoutX(10);
        transformationsComboBox.setLayoutY(10);

//        nodes.add(transformationsComboBox);
        Cylinder cylinder = new Cylinder(50, 75, 10);
        Box box = new Box(60, 60, 60);

        cullFace(cylinder, 0, 0, 15, 40);
        cullFace(box, 0, 0, 780, 40);

        drawMode(cylinder, 0, 0, 15, 120);
        drawMode(box, 0, 0, 780, 120);

        material(cylinder, 0, 0, 15, 200);
        material(box, 0, 0, 780, 200);

        enableShearingControl(cylinder, 590, 600, 0,275, 40);
        enableShearingControl(box, 1355, 600, 0, 1040, 40);

//        nodes.addAll(cylinder, box);

        for (Node node : nodes) {
            DragUtil.setDraggable(node);
        }

//        multipleTransformations();
        scene = new Scene(scrollPane, 1050, 540);

        scene.setFill(Paint.valueOf("#fdbf6f"));

        // Without perspective camera the Z axis animations wont be sometimes visible in different situations
        PerspectiveCamera camera = new PerspectiveCamera();

        scene.setCamera(camera);

        Stage stage = new Stage();

        stage.setTitle("2D Shapes");
        stage.setScene(scene);
        stage.sizeToScene();

        stage.show();

        stage.setOnCloseRequest(e -> runnable.run());
    }

    /**
     * This property is used to choose the surface of the material of a 3D shape.
     */
    private void material(Shape3D node, double nodeTranslationX, double nodeTranslationY, double boardX, double boardY) {
        node.setTranslateX(nodeTranslationX);
        node.setTranslateY(nodeTranslationY);

        Image one = new Image(Objects.requireNonNull(Shapes3DProperties.class.getResource("1.png")).toExternalForm());
        Image two = new Image(Objects.requireNonNull(Shapes3DProperties.class.getResource("2.png")).toExternalForm());
        Image three = new Image(Objects.requireNonNull(Shapes3DProperties.class.getResource("3.png")).toExternalForm());
        Image four = new Image(Objects.requireNonNull(Shapes3DProperties.class.getResource("tree2.png")).toExternalForm());

        ObservableList<Image> list = FXCollections.observableArrayList(one, two, three, four, null);

        PhongMaterial material = new PhongMaterial();
        node.setMaterial(material);

        //======================== BumpMap
        ComboBox<Image> bumpComboBox = new ComboBox<>(list);
        bumpComboBox.setPromptText("Choose Bump Map(null)");

        material.bumpMapProperty().bind(bumpComboBox.valueProperty());

        //======================== DiffuseMap
        ComboBox<Image> diffuseComboBox = new ComboBox<>(list);
        diffuseComboBox.setPromptText("Choose Diffuse Map(null)");

        material.diffuseMapProperty().bind(diffuseComboBox.valueProperty());

        //======================== SelfIllumination
        ComboBox<Image> selfIlluminationComboBox = new ComboBox<>(list);
        selfIlluminationComboBox.setPromptText("Choose SelfIllumination Map(null)");

        material.selfIlluminationMapProperty().bind(selfIlluminationComboBox.valueProperty());

        //======================== Specular
        ComboBox<Image> specularComboBox = new ComboBox<>(list);
        specularComboBox.setPromptText("Choose Specular Map(null)");

        material.specularMapProperty().bind(specularComboBox.valueProperty());

        //======================= diffuseColor
        TextField diffuseColor =  new TextField();
        diffuseColor.setPromptText("Enter Diffuse Color Name");

        //======================= specularColor
        TextField specularColor =  new TextField();
        specularColor.setPromptText("Enter Specular Color Name");

        //======================= specularPower
        TextField specularPower =  new TextField();
        specularPower.setPromptText("Enter Specular Power(double)");

        Button button = new Button("Apply");
        button.setOnAction(e -> {
            if (!diffuseColor.getText().isEmpty()) {
                material.setDiffuseColor(Color.valueOf(diffuseColor.getText()));
            }

            if (!specularColor.getText().isEmpty()) {
                material.setSpecularColor(Color.valueOf(specularColor.getText()));
            }

            if (!specularPower.getText().isEmpty()) {
                material.setSpecularPower(Double.parseDouble(specularPower.getText()));
            }

        });

        Text title = new Text("Material Property Controls(Phong)");
        title.setFont(Font.font(16));

        VBox vBox = new VBox();
        vBox.getChildren().addAll(
                title,
                new Separator(Orientation.VERTICAL),
                bumpComboBox,
                diffuseComboBox,
                selfIlluminationComboBox,
                specularComboBox,
                diffuseColor,
                specularColor,
                specularPower,
                button
        );

        vBox.setLayoutX(boardX);
        vBox.setLayoutY(boardY);

        nodes.add(vBox);
    }

    /**
     * Let you choose the type of drawing mode used to draw the current 3D shape. In JavaFX, you can choose two draw modes
     * to draw a 3D shape, which are −

     * Fill − This mode draws and fills a 2D shape (DrawMode.FILL).
     * Line − This mode draws a 3D shape using lines (DrawMode.LINE).

     * By default, the drawing mode of a 3Dimensional shape is fill.
     */
    private void drawMode(Shape3D node, double nodeTranslationX, double nodeTranslationY, double boardX, double boardY) {
        node.setLayoutX(nodeTranslationX);
        node.setLayoutY(nodeTranslationY);

        ObservableList<DrawMode> list = FXCollections.observableArrayList(DrawMode.FILL, DrawMode.LINE);
        ComboBox<DrawMode> comboBox = new ComboBox<>(list);
        comboBox.setValue(DrawMode.FILL);
        comboBox.setPromptText("Choose DrawMode");

        node.drawModeProperty().bind(comboBox.valueProperty());

        Text title = new Text("Draw Property(Default is \"FILL\")");
        title.setFont(Font.font(16));

        VBox vBox = new VBox();
        vBox.getChildren().addAll(
                title,
                new Separator(Orientation.VERTICAL),
                comboBox
        );

        vBox.setLayoutX(boardX);
        vBox.setLayoutY(boardY);

        nodes.add(vBox);
    }


    /**
     * In general, culling is the removal of improperly oriented parts of a shape (which are not visible in the view area).

     * The stroke type of a shape can be −
     * None − No culling is performed (CullFace.NONE).
     * Front − All the front facing polygons are culled. (CullFace.FRONT).
     * Back − All the back facing polygons are culled. (StrokeType.BACK).

     * By default, the cull face of a 3-Dimensional shape is Back.
     */
    private void cullFace(Shape3D node, double nodeTranslationX, double nodeTranslationY, double boardX, double boardY) {
        node.setLayoutX(nodeTranslationX);
        node.setLayoutY(nodeTranslationY);

        ObservableList<CullFace> list = FXCollections.observableArrayList(CullFace.NONE, CullFace.FRONT, CullFace.BACK);
        ComboBox<CullFace> comboBox = new ComboBox<>(list);
        comboBox.setValue(CullFace.BACK);
        comboBox.setPromptText("Choose CullFace");

        node.cullFaceProperty().bind(comboBox.valueProperty());

        Text title = new Text("CullFace Property(Default is \"Back\")");
        title.setFont(Font.font(16));

        VBox vBox = new VBox();
        vBox.getChildren().addAll(
                title,
                new Separator(Orientation.VERTICAL),
                comboBox
        );

        vBox.setLayoutX(boardX);
        vBox.setLayoutY(boardY);

        nodes.add(vBox);
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
}
