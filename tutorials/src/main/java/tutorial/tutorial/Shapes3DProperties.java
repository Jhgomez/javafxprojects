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
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.HashMap;

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

        ObservableList<String> options = FXCollections.observableArrayList(getAnimations().keySet());

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

        cullFace(cylinder, 250, 250, 15, 40);

        nodes.add(cylinder);

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

    private HashMap<String, Runnable> getAnimations() {
        if (animations == null) {
            animations = new HashMap<>();

            animations.put("Rotate Transition", () -> {
                // In this use case it lives as part of a group of animations/transitions
//                Cylinder cylinder = new Cylinder(50, 75, 10);
//
//                cullFace(cylinder.);
            });
        }

        return animations;
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
