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
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
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
        cullFace(box, 0, 0, 515, 40);

        drawMode(cylinder, 0, 0, 15, 120);
        drawMode(box, 0, 0, 515, 120);

        material(cylinder, 375, 250, 15, 200);
        material(box, 875, 250, 515, 200);

        nodes.addAll(cylinder, box);

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
}
