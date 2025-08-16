package tutorial.tutorial;

import javafx.animation.RotateTransition;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
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
import javafx.util.Duration;

import java.util.HashMap;

/**
 * animating an object implies creating illusion of its motion by rapid display. Animations are used in an application to
 * add certain special visual effects on elements like images, text, drawings, etc. You can specify the entry and exit
 * effects on a text, fading an image in and out, displaying bulleted points (if any) one after the other, etc. The concept
 * of animation is introduced to visually enhance an application.

 * The following are the kinds of transitions supported by JavaFX.
 * - Transitions that effects the attributes of the nodes: Fade, Fill, Stroke Transitions
 * - Transition that involve more than one basic transitions: Sequential, Parallel, Pause Transitions
 * - Transition that translate the object along the specified path: Path Transition
 */
public class Animations {
    ObservableList<Node> nodes;
    HashMap<String, Runnable> animations;
    Scene scene;

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

    private HashMap<String, Runnable> getAnimations() {
        if (animations == null) {
            animations = new HashMap<>();

            animations.put("RotateTrans", () -> {
                Polygon hexagon = new Polygon();

                hexagon.getPoints().addAll(
                        0.0, 0.0,
                        200.0, 0.0,
                        250.0, 100.0,
                        200.0, 200.0,
                        0.0, 200.0,
                        -50.0, 100.0
                );
                //Setting the fill color for the hexagon
                hexagon.setFill(Color.BLUE);

                rotateTransition(hexagon, 700, 110, 10, 50);
            });
        }

        return animations;
    }

    /**
     * axis − Specifies the axis of rotation for this RotateTransition.
     * node − The target node of this RotateTransition.
     * byAngle − Specifies the incremented stop angle value, from the start, of this RotateTransition.
     * fromAngle − Specifies the start angle value for this RotateTransition.
     * toAngle − Specifies the stop angle value for this RotateTransition.
     * duration − The duration of this RotateTransition.

     * Here we only enable "byAngle", however you can only apply one pair, either "fromAngle" with "toAngle" or "fromAngle"
     * with "byAngle". There is one exception, you can use "byAngle" alone and in this case the 0 angle is taken as "fromAngle" value
     * which is what we're doing here, if you want to try other combinations uncomment the property bindings in this function

     * Play with the controls, if animation is not playing or updating, stop it and play it again
     */
    private void rotateTransition(Node node, double translationX, double translationY, double boardX, double boardY) {
        Translate translate = new Translate(translationX, translationY);
        node.getTransforms().add(translate);

        RotateTransition rotateTransition = new RotateTransition();
        rotateTransition.setNode(node);
        rotateTransition.setCycleCount(RotateTransition.INDEFINITE);

        ObjectProperty<Duration> duration = new SimpleObjectProperty<>(Duration.millis(1000));

        Slider durationSlider = new Slider(0, 100, 0);
        Label durationLabel = new Label("Duration");
        Label durationValue = new Label("Value: 0s");
        durationSlider.setBlockIncrement(0.1);

//        rotateTransition.setDuration(Duration.millis(1000));
        rotateTransition.durationProperty().bind(duration);
        durationSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            duration.setValue(Duration.millis(newValue.doubleValue() * 6000));
            durationValue.setText(String.format("Duration: %.2f s", newValue.doubleValue() * 6));
        });

        Slider angleSlider = new Slider(-720, 720, 0);
        Label angleLabel = new Label("ByAngle");
        Label angleValue = new Label("Value: 0");
        angleSlider.setBlockIncrement(1);

//        rotateTransition.setByAngle(360);
        rotateTransition.byAngleProperty().bind(angleSlider.valueProperty());
        angleSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            angleValue.setText(String.format("Value: %d ", newValue.intValue()));
        });

        Slider fromAngleSlider = new Slider(-720, 720, 0);
        Label fromAngleLabel = new Label("FromAngle");
        Label fromAngleValue = new Label("Value: 0");
        fromAngleSlider.setBlockIncrement(1);

//        rotateTransition.setFromAngle(360);
//        rotateTransition.fromAngleProperty().bind(fromAngleSlider.valueProperty());
        fromAngleSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            fromAngleValue.setText(String.format("Value: %d ", newValue.intValue()));
        });

        Slider toAngleSlider = new Slider(-720, 720, 0);
        Label toAngleLabel = new Label("ToAngle");
        Label toAngleValue = new Label("Value: 0");
        toAngleSlider.setBlockIncrement(1);

//        rotateTransition.setByAngle(360);
//        rotateTransition.toAngleProperty().bind(toAngleSlider.valueProperty());
        toAngleSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            toAngleValue.setText(String.format("Value: %d ", newValue.intValue()));
        });

        //rotateTransition.setAutoReverse(false);
        CheckBox autoReverseCheckBox = new CheckBox("Auto Reverse");
        autoReverseCheckBox.setSelected(true);

        rotateTransition.autoReverseProperty().bind(autoReverseCheckBox.selectedProperty());

        Slider playSlider = new Slider(0, 2, 2);
        playSlider.setBlockIncrement(1);
        Label playLabel = new Label("0/stop, 1/pause, 2/play");
        Label playValue = new Label("Value: 2");

        playSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            playValue.setText(String.format("Value: %.2f", newValue.doubleValue()));

            switch (newValue.intValue()) {
                case 0 -> {
                    rotateTransition.stop();
                }
                case 1 -> {
                    rotateTransition.pause();
                }
                case 2 -> {
                    rotateTransition.play();
                }
            }
        });

        Text title = new Text("RotateTrans Controls(check function notes, by default this example only uses \"byAngle\", you have to uncomment code if you want to use the other properties)");
        title.setWrappingWidth(350);

        VBox vBox = new VBox(
                title,
                new Separator(Orientation.VERTICAL),
                durationLabel,
                durationSlider,
                durationValue,
                fromAngleLabel,
                fromAngleSlider,
                fromAngleValue,
                toAngleLabel,
                toAngleSlider,
                toAngleValue,
                angleLabel,
                angleSlider,
                angleValue,
                playLabel,
                playSlider,
                playValue,
                autoReverseCheckBox
        );

        vBox.setLayoutX(boardX);
        vBox.setLayoutY(boardY);

        rotateTransition.play();

        nodes.addAll(node, vBox);
    }
}
