package tutorial.tutorial;

import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Point3D;
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

                rotateTransition(hexagon, 450, 110, 10, 50);

                rotateTransition(new Cylinder(50, 75, 150), 1200, 250, 750, 50);
            });

            animations.put("ScaleTrans", () -> {
                Circle circle = new Circle();
                circle.setRadius(50.0);
                circle.setFill(Color.BROWN);
                circle.setStrokeWidth(20);

                scaleTransition(circle, 450, 110, 10, 50);

                scaleTransition(new Cylinder(50, 75, 10), 1200, 250, 750, 50);
            });
        }

        return animations;
    }

    /**
     * byX − Specifies the incremented stop X scale value, from the start, of this ScaleTransition.
     * byY − Specifies the incremented stop Y scale value, from the start, of this ScaleTransition.
     * byZ − Specifies the incremented stop Z scale value, from the start, of this ScaleTransition.
     * duration − The duration of this ScaleTransition
     * fromX − Specifies the start X scale value of this ScaleTransition.
     * fromY − Specifies the start Y scale value of this ScaleTransition.
     * fromZ − Specifies the start Z scale value of this ScaleTransition.
     * node − The target node of this ScaleTransition.
     * toX − Specifies the stop X scale value of this ScaleTransition.
     * toY − The stop Y scale value of this ScaleTransition.
     * toZ − The stop Z scale value of this ScaleTransition.

     * Similar as in rotation transition animation, here also, you should use pairs of the "by" variables with "from" variables,
     * or "from" with "to", or "by" variables alone in which case the "to" is assumed to be the original size. In this example
     * we will use the "by" variables only if you want to try setting up other variables please uncomment the proper code
     * in this function
     */
    private void scaleTransition(Node node, double nodeTranslationX, double nodeTranslationY, double boardX, double boardY) {
        node.setLayoutX(nodeTranslationX);
        node.setLayoutY(nodeTranslationY);

        ScaleTransition scaleTransition = new ScaleTransition();
        scaleTransition.setNode(node);
        scaleTransition.setCycleCount(ScaleTransition.INDEFINITE);

        //================================== DURATION PROPERTY
        ObjectProperty<Duration> duration = new SimpleObjectProperty<>(Duration.millis(1000));

        Slider durationSlider = new Slider(0, 100, 0);
        Label durationLabel = new Label("Duration");
        Label durationValue = new Label("Value: 0s");
        durationSlider.setBlockIncrement(0.1);

        scaleTransition.durationProperty().bind(duration);
        durationSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            duration.setValue(Duration.millis(newValue.doubleValue() * 6000));
            durationValue.setText(String.format("Duration: %.2f s", newValue.doubleValue() * 6));
        });

        //==================================== BY XYZ VALUES
        Slider by_x_slider = new Slider(-10, 10, 1);
        Label byXLabel = new Label("By X");
        Label byXValue = new Label("Value: 0");
        by_x_slider.setBlockIncrement(1);

        scaleTransition.byXProperty().bind(by_x_slider.valueProperty());
        by_x_slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            byXValue.setText(String.format("Value: %d ", newValue.intValue()));
        });

        Slider by_y_slider = new Slider(-10, 10, 1);
        Label byYLabel = new Label("By Y");
        Label byYValue = new Label("Value: 0");
        by_y_slider.setBlockIncrement(1);

        scaleTransition.byYProperty().bind(by_y_slider.valueProperty());
        by_y_slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            byYValue.setText(String.format("Value: %d ", newValue.intValue()));
        });

        Slider by_z_slider = new Slider(-10, 10, 1);
        Label byZLabel = new Label("By Z");
        Label byZValue = new Label("Value: 0");
        by_z_slider.setBlockIncrement(1);

        scaleTransition.byZProperty().bind(by_z_slider.valueProperty());
        by_z_slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            byZValue.setText(String.format("Value: %d ", newValue.intValue()));
        });

        //======================================= FROM XYZ VALUES
        Slider from_x_slider = new Slider(-10, 10, 1);
        Label fromXLabel = new Label("From X");
        Label fromXValue = new Label("Value: 0");
        from_x_slider.setBlockIncrement(1);

        scaleTransition.fromXProperty().bind(from_x_slider.valueProperty());
        from_x_slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            fromXValue.setText(String.format("Value: %d ", newValue.intValue()));
        });

        Slider from_y_slider = new Slider(-10, 10, 1);
        Label fromYLabel = new Label("From Y");
        Label fromYValue = new Label("Value: 0");
        from_y_slider.setBlockIncrement(1);

        scaleTransition.fromYProperty().bind(from_y_slider.valueProperty());
        from_y_slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            fromYValue.setText(String.format("Value: %d ", newValue.intValue()));
        });

        Slider from_z_slider = new Slider(-10, 10, 1);
        Label fromZLabel = new Label("From Z");
        Label fromZValue = new Label("Value: 0");
        from_z_slider.setBlockIncrement(1);

        scaleTransition.fromZProperty().bind(from_z_slider.valueProperty());
        from_z_slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            fromZValue.setText(String.format("Value: %d ", newValue.intValue()));
        });

        //======================================= TO XYZ VALUES
        Slider to_x_slider = new Slider(-10, 10, 1);
        Label toXLabel = new Label("To X");
        Label toXValue = new Label("Value: 0");
        to_x_slider.setBlockIncrement(1);

        scaleTransition.toXProperty().bind(to_x_slider.valueProperty());
        to_x_slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            toXValue.setText(String.format("Value: %d ", newValue.intValue()));
        });

        Slider to_y_slider = new Slider(-10, 10, 1);
        Label toYLabel = new Label("To Y");
        Label toYValue = new Label("Value: 0");
        to_y_slider.setBlockIncrement(1);

        scaleTransition.toYProperty().bind(to_y_slider.valueProperty());
        to_y_slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            toYValue.setText(String.format("Value: %d ", newValue.intValue()));
        });

        Slider to_z_slider = new Slider(-10, 10, 1);
        Label toZLabel = new Label("To Z");
        Label toZValue = new Label("Value: 0");
        to_z_slider.setBlockIncrement(1);

        scaleTransition.toZProperty().bind(to_z_slider.valueProperty());
        to_z_slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            toZValue.setText(String.format("Value: %d ", newValue.intValue()));
        });

        //======================================== AUTOREVERSE
        CheckBox autoReverseCheckBox = new CheckBox("Auto Reverse");
        autoReverseCheckBox.setSelected(true);

        scaleTransition.autoReverseProperty().bind(autoReverseCheckBox.selectedProperty());

        Slider playSlider = new Slider(0, 2, 2);
        playSlider.setBlockIncrement(1);
        Label playLabel = new Label("0/stop, 1/pause, 2/play");
        Label playValue = new Label("Value: 2");

        playSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            playValue.setText(String.format("Value: %.2f", newValue.doubleValue()));

            switch (newValue.intValue()) {
                case 0 -> {
                    scaleTransition.stop();
                }
                case 1 -> {
                    scaleTransition.pause();
                }
                case 2 -> {
                    scaleTransition.play();
                }
            }
        });

        HashMap<String, Interpolator> interpolators = new HashMap<>();
        interpolators.put("Ease Both", Interpolator.EASE_BOTH);
        interpolators.put("Ease In", Interpolator.EASE_IN);
        interpolators.put("Discrete", Interpolator.DISCRETE);
        interpolators.put("Ease Out", Interpolator.EASE_OUT);
        interpolators.put("Linear", Interpolator.LINEAR);
        interpolators.put("Step End", Interpolator.STEP_END);
        interpolators.put("Step Start", Interpolator.STEP_START);
        interpolators.put("SP Line", Interpolator.SPLINE(0, 1, 1, 0));
        interpolators.put("Steps None", Interpolator.STEPS(3, Interpolator.StepPosition.NONE));
        interpolators.put("Steps Start", Interpolator.STEPS(3, Interpolator.StepPosition.START));
        interpolators.put("Steps End", Interpolator.STEPS(3, Interpolator.StepPosition.END));
        interpolators.put("Steps Both", Interpolator.STEPS(3, Interpolator.StepPosition.BOTH));
        interpolators.put("Tangent", Interpolator.TANGENT(Duration.seconds(1.3), 4));

        ComboBox<String> interpolatorComboBox = new ComboBox<>(FXCollections.observableArrayList(interpolators.keySet()));
        interpolatorComboBox.setPromptText("Choose Interpolator");

        interpolatorComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals(oldValue)) {
                scaleTransition.setInterpolator(interpolators.get(newValue));
            }
        });

        Text title = new Text("Scale Transition, Check code notes about how controls are working");
        title.setWrappingWidth(350);

        VBox vBox = new VBox(
                title,
                new Separator(Orientation.VERTICAL),
                durationLabel,
                durationSlider,
                durationValue,
                byYLabel,
                by_y_slider,
                byYValue,
                byZLabel,
                by_z_slider,
                byZValue,
                byXLabel,
                by_x_slider,
                byXValue,
                fromYLabel,
                from_y_slider,
                fromYValue,
                fromZLabel,
                from_z_slider,
                fromZValue,
                fromXLabel,
                from_x_slider,
                fromXValue,
                toYLabel,
                to_y_slider,
                toYValue,
                toZLabel,
                to_z_slider,
                toZValue,
                toXLabel,
                to_x_slider,
                toXValue,
                playLabel,
                playSlider,
                playValue,
                autoReverseCheckBox,
                new Separator(Orientation.VERTICAL),
                interpolatorComboBox
        );

        vBox.setLayoutX(boardX);
        vBox.setLayoutY(boardY);

        scaleTransition.play();

        nodes.addAll(node, vBox);
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
    private void rotateTransition(Node node, double nodeTranslationX, double nodeTranslationY, double boardX, double boardY) {
//        Translate translate = new Translate(nodeTranslationX, nodeTranslationY);
//        node.getTransforms().add(translate);
        node.setLayoutX(nodeTranslationX);
        node.setLayoutY(nodeTranslationY);

        RotateTransition rotateTransition = new RotateTransition();
        rotateTransition.setNode(node);
        rotateTransition.setCycleCount(RotateTransition.INDEFINITE);

        ObjectProperty<Duration> duration = new SimpleObjectProperty<>(Duration.millis(1000));

        Slider durationSlider = new Slider(0, 20, 0);
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

        ObjectProperty<Point3D> axis = new SimpleObjectProperty<>(Rotate.X_AXIS);

        Slider axisSlider = new Slider(0, 3, 0);
        Label axisLabel = new Label("Axis, 0/X, 1/Y, 2/Z, 3 Sample XYZ point");
        Label axisValue = new Label("Value: 0");
        axisSlider.setBlockIncrement(1);

        rotateTransition.axisProperty().bind(axis);
        axisSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            axisValue.setText(String.format("Value: %d", newValue.intValue()));

            switch (newValue.intValue()) {
                case 0 -> axis.setValue(Rotate.X_AXIS);
                case 1 -> axis.setValue(Rotate.Y_AXIS);
                case 2 -> axis.setValue(Rotate.Z_AXIS);
                case 3 -> axis.setValue(new Point3D(5, 10, 20));
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
                axisLabel,
                axisSlider,
                axisValue,
                autoReverseCheckBox
        );

        vBox.setLayoutX(boardX);
        vBox.setLayoutY(boardY);

        rotateTransition.play();

        nodes.addAll(node, vBox);
    }
}
