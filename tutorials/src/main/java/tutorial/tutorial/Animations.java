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
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
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

        nodes.add(transformationsComboBox);

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

                rotateTransition(hexagon, 325, 110, 10, 50, RotateTransition.INDEFINITE).play();

                Cylinder cylinder = new Cylinder(50, 75, 150);
                rotateTransition(cylinder, 950, 250, 625, 50, RotateTransition.INDEFINITE).play();

                nodes.addAll(hexagon, cylinder);
            });

            animations.put("Scale Transition", () -> {
                Circle circle = new Circle();
                circle.setRadius(50.0);
                circle.setFill(Color.BROWN);
                circle.setStrokeWidth(20);

                scaleTransition(circle, 300, 250, 10, 50, ScaleTransition.INDEFINITE).play();

                Cylinder cylinder = new Cylinder(50, 75, 10);
                scaleTransition(cylinder, 800, 250, 500, 50, ScaleTransition.INDEFINITE).play();

                nodes.addAll(circle, cylinder);
            });

            animations.put("Translate Transition", () -> {
                Circle circle = new Circle();
                circle.setRadius(50.0);
                circle.setFill(Color.BROWN);
                circle.setStrokeWidth(20);

                translateTransition(circle, 300, 250, 10, 50, TranslateTransition.INDEFINITE).play();

                Cylinder cylinder = new Cylinder(50, 75, 10);
                translateTransition(cylinder, 800, 250, 500, 50, TranslateTransition.INDEFINITE).play();

                nodes.addAll(circle, cylinder);
            });

            animations.put("Fade Transition", () -> {
                Circle circle = new Circle();
                circle.setRadius(50.0);
                circle.setFill(Color.BROWN);
                circle.setStrokeWidth(20);

                // this animation seems to not work on Shape3D objects
                fadeTransition(circle, 300, 250, 10, 50, FadeTransition.INDEFINITE).play();

                nodes.add(circle);
            });

            animations.put("Fill Transition", () -> {
                Circle circle = new Circle();
                circle.setRadius(50.0);
                circle.setFill(Color.BROWN);
                circle.setStrokeWidth(20);

                // this animation seems to not work on Shape3D objects
                fillTransition(circle, 300, 250, 10, 50, FillTransition.INDEFINITE).play();

                nodes.add(circle);
            });

            animations.put("Stroke Transition", () -> {
                Circle circle = new Circle();
                circle.setRadius(50.0);
                circle.setFill(Color.BROWN);
                circle.setStrokeWidth(10);
                circle.setStrokeType(StrokeType.CENTERED);

                // this animation seems to not work on Shape3D objects
                strokeTransition(circle, 300, 250, 10, 50, StrokeTransition.INDEFINITE).play();

                nodes.add(circle);
            });

            animations.put("Path Transition", () -> {
                Circle circle = new Circle();
                circle.setRadius(50.0);
                circle.setFill(Color.BROWN);
                circle.setStrokeWidth(20);

                pathTransition(circle, null, 170, 250, 10, 50, TranslateTransition.INDEFINITE).play();

                Path path = new Path();
                MoveTo moveTo = new MoveTo(108, 71);
                LineTo line1 = new LineTo(321, 161);
                LineTo line2 = new LineTo(126,232);
                LineTo line3 = new LineTo(232,52);
                LineTo line4 = new LineTo(269, 250);
                LineTo line5 = new LineTo(108, 71);
                path.getElements().add(moveTo);
                path.getElements().addAll(line1, line2, line3, line4, line5);

                Cylinder cylinder = new Cylinder(50, 75, 10);
                pathTransition(cylinder, path, 620, 250, 400, 50, TranslateTransition.INDEFINITE).play();

                nodes.addAll(circle, cylinder);
            });

            animations.put("Pause Transition", () -> {
                // There is at least two different ways you could use this transition, one is inside a group of transition
                // with "Sequential Transition" and the other is playing the pause transition and setting an "onFinished"
                // listener to play(continue), or whatever, another transition
                Circle circle = new Circle();
                circle.setRadius(50.0);
                circle.setFill(Color.BROWN);
                circle.setStrokeWidth(20);

                // In this use case, pause transition can be similar to a thread sleep but it is not realy sleeping a thread,
                // it would be acting more like a timer
                Animation translation =
                        translateTransition(circle, 250, 350, 10, 50, TranslateTransition.INDEFINITE);

                translation.play();

                Text info = new Text("To see this pause animation, change it's state to pause/stop back to play");
                info.setWrappingWidth(200);
                info.setLayoutY(260);
                info.setLayoutX(210);

                Animation pause = pauseTransition(210, 50, 2);
                pause.statusProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue == Animation.Status.RUNNING) {
                        translation.pause();
                    }
                });

                pause.setOnFinished(e -> {
                    translation.play();
                });

                // In this use case it lives as part of a group of animations/transitions
                Cylinder cylinder = new Cylinder(50, 75, 10);
                sequentialTransition(
                        cylinder,
                        1825,
                        425,
                        470,
                        50,
                        TranslateTransition.INDEFINITE,
                        "rotate",
                        "pause",
                        "translate",
                        "pause",
                        "scale"
                );

                nodes.addAll(info, circle, cylinder);
            });

            animations.put("Sequential Transition", () -> {
                Circle circle = new Circle();
                circle.setCenterX(150.0f);
                circle.setCenterY(135.0f);
                circle.setRadius(100.0f);
                circle.setStrokeWidth(8);
                circle.setStrokeType(StrokeType.OUTSIDE);
                circle.setStroke(Color.BLACK);
                circle.setFill(Color.BROWN);

                sequentialTransition(
                        circle,
                        1100,
                        425,
                        10,
                        50,
                        SequentialTransition.INDEFINITE,
                        "rotate",
                        "fill",
                        "pause",
                        "translate",
                        "scale",
                        "fade",
                        "stroke"
                );

                nodes.add(circle);
            });

            animations.put("Parallel Transition", () -> {
                Circle circle = new Circle();
                circle.setCenterX(150.0f);
                circle.setCenterY(135.0f);
                circle.setRadius(100.0f);
                circle.setStrokeWidth(8);
                circle.setStrokeType(StrokeType.OUTSIDE);
                circle.setStroke(Color.BLACK);
                circle.setFill(Color.BROWN);

                Cylinder cylinder = new Cylinder(50, 75, 10);

                parallelTransition(
                        cylinder,
                        1100,
                        425,
                        10,
                        50,
                        SequentialTransition.INDEFINITE,
                        "rotate",
                        "pause",
                        "translate",
                        "scale",
                        "fade",
                        "stroke",
                        "path"
                );

                nodes.add(cylinder);
            });
        }

        return animations;
    }

    private HashMap<String, Interpolator> getInterpolators() {
        if (interpolators == null) {
            interpolators = new HashMap<>();

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
        }

        return interpolators;
    }

    /**
     * Be aware pause transition may make no much sense in this context
     */
    private Animation parallelTransition(
            Node node,
            double nodeTranslationX,
            double nodeTranslationY,
            double boardX,
            double boardY,
            int cycleCount,
            String... animations
    ) {
        ParallelTransition parallelTransition = new ParallelTransition();
        parallelTransition.setCycleCount(cycleCount);

        Slider playSlider = new Slider(0, 2, 0);
        playSlider.setBlockIncrement(1);
        Label playLabel = new Label("0/stop, 1/pause, 2/play");
        Label playValue = new Label("Value: 0");

        playSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            playValue.setText(String.format("Value: %.2f", newValue.doubleValue()));

            switch (newValue.intValue()) {
                case 0 -> {
                    parallelTransition.stop();
                }
                case 1 -> {
                    parallelTransition.pause();
                }
                case 2 -> {
                    parallelTransition.play();
                }
            }
        });

        ComboBox<String> interpolatorComboBox = new ComboBox<>(FXCollections.observableArrayList(getInterpolators().keySet()));
        interpolatorComboBox.setPromptText("Choose Interpolator");

        interpolatorComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals(oldValue)) {
                parallelTransition.setInterpolator(interpolators.get(newValue));
            }
        });

        Text title = new Text("Parallel Transition, Be aware some transitions doesn't work with Shape3D, Set up animations as you'd like then change to Play state to start");
        title.setWrappingWidth(180);

        //======================================== AUTOREVERSE
        CheckBox autoReverseCheckBox = new CheckBox("Auto Reverse");
        autoReverseCheckBox.setSelected(true);

        parallelTransition.autoReverseProperty().bind(autoReverseCheckBox.selectedProperty());

        VBox vBox = new VBox(
                title,
                new Separator(Orientation.VERTICAL),
                playLabel,
                playSlider,
                playValue,
                autoReverseCheckBox,
                new Separator(Orientation.VERTICAL),
                interpolatorComboBox
        );

        vBox.setLayoutX(boardX);
        vBox.setLayoutY(boardY);

        nodes.add(vBox);

        if (cycleCount == 0 || cycleCount == SequentialTransition.INDEFINITE) {
            cycleCount = 2;
        }

        for (String animation : animations) {
            switch(animation) {
                case "path" -> {
                    boardX += 200;

                    parallelTransition.getChildren().add(
                            pathTransition(node, null, 0, 0, boardX, boardY, cycleCount)
                    );
                }
                case "rotate" -> {
                    boardX += 200;

                    parallelTransition.getChildren().add(
                            rotateTransition(node, 0, 0, boardX, boardY, cycleCount)
                    );
                }
                case "translate" -> {
                    boardX += 200;

                    parallelTransition.getChildren().add(
                            translateTransition(node, 0, 0, boardX, boardY, cycleCount)                    );
                }
                case "scale" -> {
                    boardX += 200;

                    parallelTransition.getChildren().add(
                            scaleTransition(node, 0, 0, boardX, boardY, cycleCount)
                    );
                }
                case "fade" -> {
                    boardX += 200;

                    parallelTransition.getChildren().add(
                            fadeTransition(node, 0, 0, boardX, boardY, cycleCount)
                    );
                }
                case "fill" -> {
                    if (node instanceof Shape3D) {
                        continue;
                    }

                    boardX += 200;

                    parallelTransition.getChildren().add(
                            fillTransition(node, 0, 0, boardX, boardY, cycleCount)
                    );
                }
                case "stroke" -> {
                    if (node instanceof Shape3D) {
                        continue;
                    }

                    boardX += 200;

                    parallelTransition.getChildren().add(
                            strokeTransition(node, 0, 0, boardX, boardY, cycleCount)
                    );
                }
                case "pause" -> {
                    boardX += 200;

                    parallelTransition.getChildren().add(
                            pauseTransition(boardX, boardY, cycleCount)
                    );
                }
            }
        }

        node.setLayoutX(nodeTranslationX);
        node.setLayoutY(nodeTranslationY);

        return parallelTransition;
    }

    /**
     * You can apply one transition on a JavaFX node, or multiple transitions together. However, there are two different
     * ways when you want to apply multiple transitions on a single node.

     * Sequential transition is applied on a JavaFX node when you want to apply multiple transitions on a JavaFX node one
     * after the other.
     */
    private Animation sequentialTransition(
            Node node,
            double nodeTranslationX,
            double nodeTranslationY,
            double boardX,
            double boardY,
            int cycleCount,
            String... animations
    ) {
        SequentialTransition sequentialTransition = new SequentialTransition();
        sequentialTransition.setCycleCount(cycleCount);

        Slider playSlider = new Slider(0, 2, 0);
        playSlider.setBlockIncrement(1);
        Label playLabel = new Label("0/stop, 1/pause, 2/play");
        Label playValue = new Label("Value: 0");

        playSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            playValue.setText(String.format("Value: %.2f", newValue.doubleValue()));

            switch (newValue.intValue()) {
                case 0 -> {
                    sequentialTransition.stop();
                }
                case 1 -> {
                    sequentialTransition.pause();
                }
                case 2 -> {
                    sequentialTransition.play();
                }
            }
        });

        ComboBox<String> interpolatorComboBox = new ComboBox<>(FXCollections.observableArrayList(getInterpolators().keySet()));
        interpolatorComboBox.setPromptText("Choose Interpolator");

        interpolatorComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals(oldValue)) {
                sequentialTransition.setInterpolator(interpolators.get(newValue));
            }
        });

        Text title = new Text("Sequential Transition, Be aware some transitions doesn't work with Shape3D, Set up animations as you'd like then change to Play state to start");
        title.setWrappingWidth(180);

        //======================================== AUTOREVERSE
        CheckBox autoReverseCheckBox = new CheckBox("Auto Reverse");
        autoReverseCheckBox.setSelected(true);

        sequentialTransition.autoReverseProperty().bind(autoReverseCheckBox.selectedProperty());

        VBox vBox = new VBox(
                title,
                new Separator(Orientation.VERTICAL),
                playLabel,
                playSlider,
                playValue,
                autoReverseCheckBox,
                new Separator(Orientation.VERTICAL),
                interpolatorComboBox
        );

        vBox.setLayoutX(boardX);
        vBox.setLayoutY(boardY);

        nodes.add(vBox);

        if (cycleCount == 0 || cycleCount == SequentialTransition.INDEFINITE) {
            cycleCount = 2;
        }

        for (String animation : animations) {
            switch(animation) {
                case "path" -> {
                    boardX += 200;

                    sequentialTransition.getChildren().add(
                            pathTransition(node, null, 0, 0, boardX, boardY, cycleCount)
                    );
                }
                case "rotate" -> {
                    boardX += 200;

                    sequentialTransition.getChildren().add(
                            rotateTransition(node, 0, 0, boardX, boardY, cycleCount)
                    );
                }
                case "translate" -> {
                    boardX += 200;

                    sequentialTransition.getChildren().add(
                            translateTransition(node, 0, 0, boardX, boardY, cycleCount)                    );
                }
                case "scale" -> {
                    boardX += 200;

                    sequentialTransition.getChildren().add(
                            scaleTransition(node, 0, 0, boardX, boardY, cycleCount)
                    );
                }
                case "fade" -> {
                    boardX += 200;

                    sequentialTransition.getChildren().add(
                            fadeTransition(node, 0, 0, boardX, boardY, cycleCount)
                    );
                }
                case "fill" -> {
                    if (node instanceof Shape3D) {
                        continue;
                    }

                    boardX += 200;

                    sequentialTransition.getChildren().add(
                            fillTransition(node, 0, 0, boardX, boardY, cycleCount)
                    );
                }
                case "stroke" -> {
                    if (node instanceof Shape3D) {
                        continue;
                    }

                    boardX += 200;

                    sequentialTransition.getChildren().add(
                            strokeTransition(node, 0, 0, boardX, boardY, cycleCount)
                    );
                }
                case "pause" -> {
                    boardX += 200;

                    sequentialTransition.getChildren().add(
                            pauseTransition(boardX, boardY, cycleCount)
                    );
                }
            }
        }

        node.setLayoutX(nodeTranslationX);
        node.setLayoutY(nodeTranslationY);

        return sequentialTransition;
    }

    private Animation pauseTransition(double boardX, double boardY, int cycleCount) {
        PauseTransition pauseTransition = new PauseTransition();
        pauseTransition.setCycleCount(cycleCount);


        //================================== DURATION PROPERTY
        ObjectProperty<Duration> duration = new SimpleObjectProperty<>(Duration.millis(1000));

        Slider durationSlider = new Slider(0, 100, 0.167);
        Label durationLabel = new Label("Duration");
        Label durationValue = new Label("Value: 1s");
        durationSlider.setBlockIncrement(0.1);

        pauseTransition.durationProperty().bind(duration);
        durationSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            duration.setValue(Duration.millis(newValue.doubleValue() * 6000));
            durationValue.setText(String.format("Duration: %.2f s", newValue.doubleValue() * 6));
        });

        //======================================== AUTOREVERSE
        CheckBox autoReverseCheckBox = new CheckBox("Auto Reverse");
        autoReverseCheckBox.setSelected(true);

        pauseTransition.autoReverseProperty().bind(autoReverseCheckBox.selectedProperty());

        Slider playSlider = new Slider(0, 2, 2);
        playSlider.setBlockIncrement(1);
        Label playLabel = new Label("0/stop, 1/pause, 2/play");
        Label playValue = new Label("Value: 2");

        playSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            playValue.setText(String.format("Value: %.2f", newValue.doubleValue()));

            switch (newValue.intValue()) {
                case 0 -> {
                    pauseTransition.stop();
                }
                case 1 -> {
                    pauseTransition.pause();
                }
                case 2 -> {
                    pauseTransition.play();
                }
            }
        });

        ComboBox<String> interpolatorComboBox = new ComboBox<>(FXCollections.observableArrayList(getInterpolators().keySet()));
        interpolatorComboBox.setPromptText("Choose Interpolator");

        interpolatorComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals(oldValue)) {
                pauseTransition.setInterpolator(interpolators.get(newValue));
            }
        });

        Text title = new Text("Pause Transition Controls");
        title.setWrappingWidth(180);

        VBox vBox = new VBox(
                title,
                new Separator(Orientation.VERTICAL),
                durationLabel,
                durationSlider,
                durationValue,
                playLabel,
                playSlider,
                playValue,
                autoReverseCheckBox,
                new Separator(Orientation.VERTICAL),
                interpolatorComboBox
        );

        vBox.setLayoutX(boardX);
        vBox.setLayoutY(boardY);

        nodes.add(vBox);

        return pauseTransition;
    }

    /**
     * A path transition in JavaFX is used to move a JavaFX node (or object) around a specific path. This is similar to
     * translate transition, as it also moves the object from one position to another. However, the translate transition
     * does not provide a continuous path through which the object moves; which path transition does.

     * Any JavaFX node, like a 2D or 3D shape, text, image etc., can be moved along any path: straight or curved.

     * This translation along a path is done by updating the coordinates of the node in both X and Y directions, and by
     * updating the orientation to OrientationType.ORTHOGONAL_TO_TANGENT, at regular interval.

     * duration − The duration of this Transition.
     * node − The target node of this PathTransition.
     * orientation − Specifies the upright orientation of node along the path.
     * path − The shape on which outline the node should be animated.
     */
    private Animation pathTransition(Node node, Path path, double nodeTranslationX, double nodeTranslationY, double boardX, double boardY, int cycleCount) {
        node.setLayoutX(nodeTranslationX);
        node.setLayoutY(nodeTranslationY);

        if (path == null) {
            path = new Path();

            MoveTo moveTo = new MoveTo(100, 150);

            CubicCurveTo cubicCurveTo = new CubicCurveTo(400, 40, 175, 250, 500, 150);

            path.getElements().add(moveTo);
            path.getElements().add(cubicCurveTo);
        }

//        rotateTransition(node, nodeTranslationX + 115, nodeTranslationY, boardX + 195, boardY);

        PathTransition pathTransition = new PathTransition();
        pathTransition.setNode(node);
        pathTransition.setPath(path);
        pathTransition.setCycleCount(cycleCount);

        //================================== DURATION PROPERTY
        ObjectProperty<Duration> duration = new SimpleObjectProperty<>(Duration.millis(1000));

        Slider durationSlider = new Slider(0, 100, 0.167);
        Label durationLabel = new Label("Duration");
        Label durationValue = new Label("Value: 1s");
        durationSlider.setBlockIncrement(0.1);

        pathTransition.durationProperty().bind(duration);
        durationSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            duration.setValue(Duration.millis(newValue.doubleValue() * 6000));
            durationValue.setText(String.format("Duration: %.2f s", newValue.doubleValue() * 6));
        });


        //======================================= ORIENTATION
        Slider orientation_slider = new Slider(0, 1, 1);
        ObjectProperty<PathTransition.OrientationType> orientation =
                new SimpleObjectProperty<>(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
        Label orientation_Label = new Label("OrientationType property, 0/NONE, \n1/ORTHOGONAL_TO_TANGENT");
        Label orientation_Value = new Label("Value: 1");
        orientation_slider.setBlockIncrement(1);

        pathTransition.orientationProperty().bind(orientation);
        orientation_slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            orientation_Value.setText(String.format("Value: %d", newValue.intValue()));

            switch (newValue.intValue()) {
                case 0 -> orientation.setValue(PathTransition.OrientationType.NONE);
                case 1 -> orientation.setValue(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
            }
        });

        //======================================== AUTOREVERSE
        CheckBox autoReverseCheckBox = new CheckBox("Auto Reverse");
        autoReverseCheckBox.setSelected(true);

        pathTransition.autoReverseProperty().bind(autoReverseCheckBox.selectedProperty());

        Slider playSlider = new Slider(0, 2, 2);
        playSlider.setBlockIncrement(1);
        Label playLabel = new Label("0/stop, 1/pause, 2/play");
        Label playValue = new Label("Value: 2");

        playSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            playValue.setText(String.format("Value: %.2f", newValue.doubleValue()));

            switch (newValue.intValue()) {
                case 0 -> {
                    pathTransition.stop();
                }
                case 1 -> {
                    pathTransition.pause();
                }
                case 2 -> {
                    pathTransition.play();
                }
            }
        });

        ComboBox<String> interpolatorComboBox = new ComboBox<>(FXCollections.observableArrayList(getInterpolators().keySet()));
        interpolatorComboBox.setPromptText("Choose Interpolator");

        interpolatorComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals(oldValue)) {
                pathTransition.setInterpolator(interpolators.get(newValue));
            }
        });

        Text title = new Text("Path Transition Controls");
        title.setWrappingWidth(180);

        VBox vBox = new VBox(
                title,
                new Separator(Orientation.VERTICAL),
                durationLabel,
                durationSlider,
                durationValue,
                orientation_Label,
                orientation_slider,
                orientation_Value,
                playLabel,
                playSlider,
                playValue,
                autoReverseCheckBox,
                new Separator(Orientation.VERTICAL),
                interpolatorComboBox
        );

        vBox.setLayoutX(boardX);
        vBox.setLayoutY(boardY);

        nodes.add(vBox);

        return pathTransition;
    }

    /**
     * Doesn't work with Shape3D objects

     * A shape in JavaFX can consist of three types of strokes: inside, outside and centered; with different properties
     * applied to it. This transition is ignorant to the property of a stroke and instead just changes its color over a
     * course of duration specified.

     * duration − The duration of this StrokeTransition.
     * shape − The target shape of this StrokeTransition.
     * fromValue − Specifies the start color value for this StrokeTransition.
     * toValue − Specifies the stop color value for this StrokeTransition.
     */
    private Animation strokeTransition(Node node, double nodeTranslationX, double nodeTranslationY, double boardX, double boardY, int cycleCount) {
        node.setLayoutX(nodeTranslationX);
        node.setLayoutY(nodeTranslationY);

//        rotateTransition(node, nodeTranslationX + 115, nodeTranslationY, boardX + 195, boardY);

        StrokeTransition strokeTransition = new StrokeTransition();
        strokeTransition.setShape((Shape)node);
        strokeTransition.setCycleCount(cycleCount);

        //================================== DURATION PROPERTY
        ObjectProperty<Duration> duration = new SimpleObjectProperty<>(Duration.millis(1000));

        Slider durationSlider = new Slider(0, 100, 0.167);
        Label durationLabel = new Label("Duration");
        Label durationValue = new Label("Value: 1s");
        durationSlider.setBlockIncrement(0.1);

        strokeTransition.durationProperty().bind(duration);
        durationSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            duration.setValue(Duration.millis(newValue.doubleValue() * 6000));
            durationValue.setText(String.format("Duration: %.2f s", newValue.doubleValue() * 6));
        });


        //======================================= FROM VALUES
        Slider from_paint_slider = new Slider(0, 2, 1);
        ObjectProperty<Color> paintFrom = new SimpleObjectProperty<>(Color.TURQUOISE);
        Label from_paint_Label = new Label("From property, 0/BROWN, \n1/TURQUOISE, 2/PINK");
        Label from_paint_Value = new Label("Value: 1");
        from_paint_slider.setBlockIncrement(1);

        strokeTransition.fromValueProperty().bind(paintFrom);
        from_paint_slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            from_paint_Value.setText(String.format("Value: %d", newValue.intValue()));

            switch (newValue.intValue()) {
                case 0 -> paintFrom.setValue(Color.BROWN);
                case 1 -> paintFrom.setValue(Color.TURQUOISE);
                case 2 -> paintFrom.setValue(Color.PINK);
            }
        });

        //======================================= TO VALUES
        Slider to_paint_slider = new Slider(0, 2, 2);
        ObjectProperty<Color> paintTo = new SimpleObjectProperty<>(Color.YELLOW);
        Label to_paint_Label = new Label("To property, 0/GRAY,\n1/GREEN, 2/YELLOW");
        Label to_paint_Value = new Label("Value: 2");
        to_paint_slider.setBlockIncrement(1);

        strokeTransition.toValueProperty().bind(paintTo);
        to_paint_slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            to_paint_Value.setText(String.format("Value: %d", newValue.intValue()));

            switch (newValue.intValue()) {
                case 0 -> paintTo.setValue(Color.GRAY);
                case 1 -> paintTo.setValue(Color.DARKGREEN);
                case 2 -> paintTo.setValue(Color.YELLOW);
            }
        });

        //======================================== AUTOREVERSE
        CheckBox autoReverseCheckBox = new CheckBox("Auto Reverse");
        autoReverseCheckBox.setSelected(true);

        strokeTransition.autoReverseProperty().bind(autoReverseCheckBox.selectedProperty());

        Slider playSlider = new Slider(0, 2, 2);
        playSlider.setBlockIncrement(1);
        Label playLabel = new Label("0/stop, 1/pause, 2/play");
        Label playValue = new Label("Value: 2");

        playSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            playValue.setText(String.format("Value: %.2f", newValue.doubleValue()));

            switch (newValue.intValue()) {
                case 0 -> {
                    strokeTransition.stop();
                }
                case 1 -> {
                    strokeTransition.pause();
                }
                case 2 -> {
                    strokeTransition.play();
                }
            }
        });

        ComboBox<String> interpolatorComboBox = new ComboBox<>(FXCollections.observableArrayList(getInterpolators().keySet()));
        interpolatorComboBox.setPromptText("Choose Interpolator");

        interpolatorComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals(oldValue)) {
                strokeTransition.setInterpolator(interpolators.get(newValue));
            }
        });

        Text title = new Text("Stroke Transition, doesn't work with Shape3D");
        title.setWrappingWidth(180);

        VBox vBox = new VBox(
                title,
                new Separator(Orientation.VERTICAL),
                durationLabel,
                durationSlider,
                durationValue,
                from_paint_Label,
                from_paint_slider,
                from_paint_Value,
                to_paint_Label,
                to_paint_slider,
                to_paint_Value,
                playLabel,
                playSlider,
                playValue,
                autoReverseCheckBox,
                new Separator(Orientation.VERTICAL),
                interpolatorComboBox
        );

        vBox.setLayoutX(boardX);
        vBox.setLayoutY(boardY);

//        strokeTransition.play();

        nodes.add(vBox);

        return strokeTransition;
    }

    /**
     * This animation doesn't work with Shape3D objects

     * duration − The duration of this FillTransition.
     * shape − The target shape of this FillTransition.
     * fromValue − Specifies the start color value for this FillTransition.
     * toValue − Specifies the stop color value for this FillTransition.
     */
    private Animation fillTransition(Node node, double nodeTranslationX, double nodeTranslationY, double boardX, double boardY, int cycleCount) {
        node.setLayoutX(nodeTranslationX);
        node.setLayoutY(nodeTranslationY);

//        rotateTransition(node, nodeTranslationX + 115, nodeTranslationY, boardX + 195, boardY);

        FillTransition fillTransition = new FillTransition();
        fillTransition.setShape((Shape)node);
        fillTransition.setCycleCount(cycleCount);

        //================================== DURATION PROPERTY
        ObjectProperty<Duration> duration = new SimpleObjectProperty<>(Duration.millis(1000));

        Slider durationSlider = new Slider(0, 100, 0.167);
        Label durationLabel = new Label("Duration");
        Label durationValue = new Label("Value: 1s");
        durationSlider.setBlockIncrement(0.1);

        fillTransition.durationProperty().bind(duration);
        durationSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            duration.setValue(Duration.millis(newValue.doubleValue() * 6000));
            durationValue.setText(String.format("Duration: %.2f s", newValue.doubleValue() * 6));
        });


        //======================================= FROM VALUES
        Slider from_paint_slider = new Slider(0, 2, 0);
        ObjectProperty<Color> paintFrom = new SimpleObjectProperty<>(Color.RED);
        Label from_paint_Label = new Label("From property, 0/RED, \n1/BLUE, 2/PINK");
        Label from_paint_Value = new Label("Value: 0");
        from_paint_slider.setBlockIncrement(1);

        fillTransition.fromValueProperty().bind(paintFrom);
        from_paint_slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            from_paint_Value.setText(String.format("Value: %d", newValue.intValue()));

            switch (newValue.intValue()) {
                case 0 -> paintFrom.setValue(Color.RED);
                case 1 -> paintFrom.setValue(Color.BLUE);
                case 2 -> paintFrom.setValue(Color.PINK);
            }
        });

        //======================================= TO VALUES
        Slider to_paint_slider = new Slider(0, 2, 1);
        ObjectProperty<Color> paintTo = new SimpleObjectProperty<>(Color.BLUEVIOLET);
        Label to_paint_Label = new Label("To property, 0/GRAY,\n1/BLUEVIOLET, 2/PURPLE");
        Label to_paint_Value = new Label("Value: 1");
        to_paint_slider.setBlockIncrement(1);

        fillTransition.toValueProperty().bind(paintTo);
        to_paint_slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            to_paint_Value.setText(String.format("Value: %d", newValue.intValue()));

            switch (newValue.intValue()) {
                case 0 -> paintTo.setValue(Color.GRAY);
                case 1 -> paintTo.setValue(Color.BLUEVIOLET);
                case 2 -> paintTo.setValue(Color.PURPLE);
            }
        });

        //======================================== AUTOREVERSE
        CheckBox autoReverseCheckBox = new CheckBox("Auto Reverse");
        autoReverseCheckBox.setSelected(true);

        fillTransition.autoReverseProperty().bind(autoReverseCheckBox.selectedProperty());

        Slider playSlider = new Slider(0, 2, 2);
        playSlider.setBlockIncrement(1);
        Label playLabel = new Label("0/stop, 1/pause, 2/play");
        Label playValue = new Label("Value: 2");

        playSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            playValue.setText(String.format("Value: %.2f", newValue.doubleValue()));

            switch (newValue.intValue()) {
                case 0 -> {
                    fillTransition.stop();
                }
                case 1 -> {
                    fillTransition.pause();
                }
                case 2 -> {
                    fillTransition.play();
                }
            }
        });

        ComboBox<String> interpolatorComboBox = new ComboBox<>(FXCollections.observableArrayList(getInterpolators().keySet()));
        interpolatorComboBox.setPromptText("Choose Interpolator");

        interpolatorComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals(oldValue)) {
                fillTransition.setInterpolator(interpolators.get(newValue));
            }
        });

        Text title = new Text("Fill Transition, doesn't work with Shape3D");
        title.setWrappingWidth(180);

        VBox vBox = new VBox(
                title,
                new Separator(Orientation.VERTICAL),
                durationLabel,
                durationSlider,
                durationValue,
                from_paint_Label,
                from_paint_slider,
                from_paint_Value,
                to_paint_Label,
                to_paint_slider,
                to_paint_Value,
                playLabel,
                playSlider,
                playValue,
                autoReverseCheckBox,
                new Separator(Orientation.VERTICAL),
                interpolatorComboBox
        );

        vBox.setLayoutX(boardX);
        vBox.setLayoutY(boardY);

//        fillTransition.play();

        nodes.add(vBox);

        return fillTransition;
    }

    /**
     * A Fade transition is a type of a geometrical transition that changes the opacity property of an object. Using fade
     * transition, you can either reduce the opacity of the object or increase it. This transition is known as a geometrical
     * transition as it deals with the geometry of an object.

     * byValue − Specifies the incremented stop opacity value, from the start, of this FadeTransition.
     * duration − The duration of this FadeTransition.
     * fromValue − Specifies the start opacity value for this FadeTransition.
     * node − The target node of this Transition.
     * toValue − Specifies the stop opacity value for this FadeTransition.

     * This animation has the same behaviour as every else, you can configure it using "from" and "to", or "from" and
     * "by" pairs. The exception is only setting/using "by" alone and the "from" is the node natural/start/original
     * position by default and is what we do in our example, only setting "by" alone, if you want to try the other settings
     * uncomment binding code in this function

     * This animation can be applied to Shape3D objects but seems to make no effect on them
     */
    private Animation fadeTransition(Node node, double nodeTranslationX, double nodeTranslationY, double boardX, double boardY, int cycleCount) {
        node.setLayoutX(nodeTranslationX);
        node.setLayoutY(nodeTranslationY);

//        rotateTransition(node, nodeTranslationX + 115, nodeTranslationY, boardX + 195, boardY);

        FadeTransition fadeTransition = new FadeTransition();
        fadeTransition.setNode(node);
        fadeTransition.setCycleCount(cycleCount);

        //================================== DURATION PROPERTY
        ObjectProperty<Duration> duration = new SimpleObjectProperty<>(Duration.millis(1000));

        Slider durationSlider = new Slider(0, 100, 0.167);
        Label durationLabel = new Label("Duration");
        Label durationValue = new Label("Value: 1s");
        durationSlider.setBlockIncrement(0.1);

        fadeTransition.durationProperty().bind(duration);
        durationSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            duration.setValue(Duration.millis(newValue.doubleValue() * 6000));
            durationValue.setText(String.format("Duration: %.2f s", newValue.doubleValue() * 6));
        });

        //==================================== BY VALUES
        Slider by_x_slider = new Slider(-1, 1, -1);
        Label byXLabel = new Label("By property");
        Label byXValue = new Label("Value: -1");
        by_x_slider.setBlockIncrement(0.1);

        fadeTransition.byValueProperty().bind(by_x_slider.valueProperty());
        by_x_slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            byXValue.setText(String.format("Value: %.2f", newValue.doubleValue()));
        });


        //======================================= FROM VALUES
        Slider from_x_slider = new Slider(0, 1, 0);
        Label fromXLabel = new Label("From property");
        Label fromXValue = new Label("Value: 0");
        from_x_slider.setBlockIncrement(.1);

//        fadeTransition.fromValueProperty().bind(from_x_slider.valueProperty());
        from_x_slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            fromXValue.setText(String.format("Value: %.2f", newValue.doubleValue()));
        });

        //======================================= TO VALUES
        Slider to_x_slider = new Slider(0, 1, 1);
        Label toXLabel = new Label("To property");
        Label toXValue = new Label("Value: 1");
        to_x_slider.setBlockIncrement(.1);

//        fadeTransition.toValueProperty().bind(to_x_slider.valueProperty());
        to_x_slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            toXValue.setText(String.format("Value: %.2f", newValue.doubleValue()));
        });

        //======================================== AUTOREVERSE
        CheckBox autoReverseCheckBox = new CheckBox("Auto Reverse");
        autoReverseCheckBox.setSelected(true);

        fadeTransition.autoReverseProperty().bind(autoReverseCheckBox.selectedProperty());

        Slider playSlider = new Slider(0, 2, 2);
        playSlider.setBlockIncrement(1);
        Label playLabel = new Label("0/stop, 1/pause, 2/play");
        Label playValue = new Label("Value: 2");

        playSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            playValue.setText(String.format("Value: %.2f", newValue.doubleValue()));

            switch (newValue.intValue()) {
                case 0 -> {
                    fadeTransition.stop();
                }
                case 1 -> {
                    fadeTransition.pause();
                }
                case 2 -> {
                    fadeTransition.play();
                }
            }
        });

        ComboBox<String> interpolatorComboBox = new ComboBox<>(FXCollections.observableArrayList(getInterpolators().keySet()));
        interpolatorComboBox.setPromptText("Choose Interpolator");

        interpolatorComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals(oldValue)) {
                fadeTransition.setInterpolator(interpolators.get(newValue));
            }
        });

        Text title = new Text("Fade Transition, Check code notes about how controls are working");
        title.setWrappingWidth(180);

        VBox vBox = new VBox(
                title,
                new Separator(Orientation.VERTICAL),
                durationLabel,
                durationSlider,
                durationValue,
                byXLabel,
                by_x_slider,
                byXValue,
                fromXLabel,
                from_x_slider,
                fromXValue,
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

//        fadeTransition.play();

        nodes.add(vBox);

        return fadeTransition;
    }

    /**
     * byX − Specifies the incremented stop X coordinate value, from the start, of this TranslateTransition.
     * byY − Specifies the incremented stop Y coordinate value, from the start, of this TranslateTransition.
     * byZ − Specifies the incremented stop Z coordinate value, from the start, of this TranslateTransition.
     * duration − The duration of this TranslateTransition
     * fromX − Specifies the start X coordinate value of this TranslateTransition.
     * fromY − Specifies the start Y coordinate value of this TranslateTransition.
     * fromZ − Specifies the start Z coordinate value of this TranslateTransition.
     * node − The target node of this TranslateTransition.
     * toX − Specifies the stop X coordinate value of this TranslateTransition.
     * toY − The stop Y coordinate value of this TranslateTransition.
     * toZ − The stop Z coordinate value of this TranslateTransition.

     * This animation has the same behaviour as every else, you can configure it using "from" and "to", or "from" and
     * "by" pairs. The exception is only setting/using "by" alone and the "from" is the node natural/start/original
     * position by default and is what we do in our example, only setting "by" alone, if you want to try the other settings
     * uncomment binding code in this function
     */
    private Animation translateTransition(Node node, double nodeTranslationX, double nodeTranslationY, double boardX, double boardY, int cycleCount) {
        node.setLayoutX(nodeTranslationX);
        node.setLayoutY(nodeTranslationY);

//        rotateTransition(node, nodeTranslationX + 115, nodeTranslationY, boardX + 195, boardY);

        TranslateTransition translateTransition = new TranslateTransition();
        translateTransition.setNode(node);
        translateTransition.setCycleCount(cycleCount);

        //================================== DURATION PROPERTY
        ObjectProperty<Duration> duration = new SimpleObjectProperty<>(Duration.millis(1000));

        Slider durationSlider = new Slider(0, 100, 0.167);
        Label durationLabel = new Label("Duration");
        Label durationValue = new Label("Value: 1s");
        durationSlider.setBlockIncrement(0.1);

        translateTransition.durationProperty().bind(duration);
        durationSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            duration.setValue(Duration.millis(newValue.doubleValue() * 6000));
            durationValue.setText(String.format("Duration: %.2f s", newValue.doubleValue() * 6));
        });

        //==================================== BY XYZ VALUES
        Slider by_x_slider = new Slider(-1000, 1000, 100);
        Label byXLabel = new Label("By X");
        Label byXValue = new Label("Value: 100");
        by_x_slider.setBlockIncrement(50);

        translateTransition.byXProperty().bind(by_x_slider.valueProperty());
        by_x_slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            byXValue.setText(String.format("Value: %d ", newValue.intValue()));
        });

        Slider by_y_slider = new Slider(-1000, 1000, 100);
        Label byYLabel = new Label("By Y");
        Label byYValue = new Label("Value: 100");
        by_y_slider.setBlockIncrement(50);

        translateTransition.byYProperty().bind(by_y_slider.valueProperty());
        by_y_slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            byYValue.setText(String.format("Value: %d ", newValue.intValue()));
        });

        Slider by_z_slider = new Slider(-1000, 1000, 100);
        Label byZLabel = new Label("By Z");
        Label byZValue = new Label("Value: 100");
        by_z_slider.setBlockIncrement(50);

        translateTransition.byZProperty().bind(by_z_slider.valueProperty());
        by_z_slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            byZValue.setText(String.format("Value: %d ", newValue.intValue()));
        });

        //======================================= FROM XYZ VALUES
        Slider from_x_slider = new Slider(-1000, 1000, 1);
        Label fromXLabel = new Label("From X");
        Label fromXValue = new Label("Value: 1");
        from_x_slider.setBlockIncrement(50);

//        translateTransition.fromXProperty().bind(from_x_slider.valueProperty());
        from_x_slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            fromXValue.setText(String.format("Value: %d ", newValue.intValue()));
        });

        Slider from_y_slider = new Slider(-1000, 1000, 1);
        Label fromYLabel = new Label("From Y");
        Label fromYValue = new Label("Value: 1");
        from_y_slider.setBlockIncrement(50);

//        translateTransition.fromYProperty().bind(from_y_slider.valueProperty());
        from_y_slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            fromYValue.setText(String.format("Value: %d ", newValue.intValue()));
        });

        Slider from_z_slider = new Slider(-1000, 1000, 1);
        Label fromZLabel = new Label("From Z");
        Label fromZValue = new Label("Value: 1");
        from_z_slider.setBlockIncrement(50);

//        translateTransition.fromZProperty().bind(from_z_slider.valueProperty());
        from_z_slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            fromZValue.setText(String.format("Value: %d ", newValue.intValue()));
        });

        //======================================= TO XYZ VALUES
        Slider to_x_slider = new Slider(-1000, 1000, 100);
        Label toXLabel = new Label("To X");
        Label toXValue = new Label("Value: 100");
        to_x_slider.setBlockIncrement(50);

//        translateTransition.toXProperty().bind(to_x_slider.valueProperty());
        to_x_slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            toXValue.setText(String.format("Value: %d ", newValue.intValue()));
        });

        Slider to_y_slider = new Slider(-1000, 1000, 100);
        Label toYLabel = new Label("To Y");
        Label toYValue = new Label("Value: 100");
        to_y_slider.setBlockIncrement(50);

//        translateTransition.toYProperty().bind(to_y_slider.valueProperty());
        to_y_slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            toYValue.setText(String.format("Value: %d ", newValue.intValue()));
        });

        Slider to_z_slider = new Slider(-1000, 1000, 100);
        Label toZLabel = new Label("To Z");
        Label toZValue = new Label("Value: 100");
        to_z_slider.setBlockIncrement(50);

//        translateTransition.toZProperty().bind(to_z_slider.valueProperty());
        to_z_slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            toZValue.setText(String.format("Value: %d ", newValue.intValue()));
        });

        //======================================== AUTOREVERSE
        CheckBox autoReverseCheckBox = new CheckBox("Auto Reverse");
        autoReverseCheckBox.setSelected(true);

        translateTransition.autoReverseProperty().bind(autoReverseCheckBox.selectedProperty());

        Slider playSlider = new Slider(0, 2, 2);
        playSlider.setBlockIncrement(1);
        Label playLabel = new Label("0/stop, 1/pause, 2/play");
        Label playValue = new Label("Value: 2");

        playSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            playValue.setText(String.format("Value: %.2f", newValue.doubleValue()));

            switch (newValue.intValue()) {
                case 0 -> {
                    translateTransition.stop();
                }
                case 1 -> {
                    translateTransition.pause();
                }
                case 2 -> {
                    translateTransition.play();
                }
            }
        });

        ComboBox<String> interpolatorComboBox = new ComboBox<>(FXCollections.observableArrayList(getInterpolators().keySet()));
        interpolatorComboBox.setPromptText("Choose Interpolator");

        interpolatorComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals(oldValue)) {
                translateTransition.setInterpolator(interpolators.get(newValue));
            }
        });

        Text title = new Text("Translate Transition, Check code notes about how controls are working");
        title.setWrappingWidth(180);

        VBox vBox = new VBox(
                title,
                new Separator(Orientation.VERTICAL),
                durationLabel,
                durationSlider,
                durationValue,
                byXLabel,
                by_x_slider,
                byXValue,
                byYLabel,
                by_y_slider,
                byYValue,
                byZLabel,
                by_z_slider,
                byZValue,
                fromXLabel,
                from_x_slider,
                fromXValue,
                fromYLabel,
                from_y_slider,
                fromYValue,
                fromZLabel,
                from_z_slider,
                fromZValue,
                toXLabel,
                to_x_slider,
                toXValue,
                toYLabel,
                to_y_slider,
                toYValue,
                toZLabel,
                to_z_slider,
                toZValue,
                playLabel,
                playSlider,
                playValue,
                autoReverseCheckBox,
                new Separator(Orientation.VERTICAL),
                interpolatorComboBox
        );

        vBox.setLayoutX(boardX);
        vBox.setLayoutY(boardY);

//        translateTransition.play();

        nodes.add(vBox);

        return translateTransition;
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
    private Animation scaleTransition(Node node, double nodeTranslationX, double nodeTranslationY, double boardX, double boardY, int cycleCount) {
        node.setLayoutX(nodeTranslationX);
        node.setLayoutY(nodeTranslationY);

//        rotateTransition(node, nodeTranslationX + 115, nodeTranslationY, boardX + 195, boardY);

        ScaleTransition scaleTransition = new ScaleTransition();
        scaleTransition.setNode(node);
        scaleTransition.setCycleCount(cycleCount);

        //================================== DURATION PROPERTY
        ObjectProperty<Duration> duration = new SimpleObjectProperty<>(Duration.millis(1000));

        Slider durationSlider = new Slider(0, 100, 0.167);
        Label durationLabel = new Label("Duration");
        Label durationValue = new Label("Value: 1s");
        durationSlider.setBlockIncrement(0.1);

        scaleTransition.durationProperty().bind(duration);
        durationSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            duration.setValue(Duration.millis(newValue.doubleValue() * 6000));
            durationValue.setText(String.format("Duration: %.2f s", newValue.doubleValue() * 6));
        });

        //==================================== BY XYZ VALUES
        Slider by_x_slider = new Slider(-10, 10, 1);
        Label byXLabel = new Label("By X");
        Label byXValue = new Label("Value: 1");
        by_x_slider.setBlockIncrement(1);

        scaleTransition.byXProperty().bind(by_x_slider.valueProperty());
        by_x_slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            byXValue.setText(String.format("Value: %d ", newValue.intValue()));
        });

        Slider by_y_slider = new Slider(-10, 10, 1);
        Label byYLabel = new Label("By Y");
        Label byYValue = new Label("Value: 1");
        by_y_slider.setBlockIncrement(1);

        scaleTransition.byYProperty().bind(by_y_slider.valueProperty());
        by_y_slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            byYValue.setText(String.format("Value: %d ", newValue.intValue()));
        });

        Slider by_z_slider = new Slider(-10, 10, 1);
        Label byZLabel = new Label("By Z");
        Label byZValue = new Label("Value: 1");
        by_z_slider.setBlockIncrement(1);

        scaleTransition.byZProperty().bind(by_z_slider.valueProperty());
        by_z_slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            byZValue.setText(String.format("Value: %d ", newValue.intValue()));
        });

        //======================================= FROM XYZ VALUES
        Slider from_x_slider = new Slider(-10, 10, 1);
        Label fromXLabel = new Label("From X");
        Label fromXValue = new Label("Value: 1");
        from_x_slider.setBlockIncrement(1);

//        scaleTransition.fromXProperty().bind(from_x_slider.valueProperty());
        from_x_slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            fromXValue.setText(String.format("Value: %d ", newValue.intValue()));
        });

        Slider from_y_slider = new Slider(-10, 10, 1);
        Label fromYLabel = new Label("From Y");
        Label fromYValue = new Label("Value: 1");
        from_y_slider.setBlockIncrement(1);

//        scaleTransition.fromYProperty().bind(from_y_slider.valueProperty());
        from_y_slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            fromYValue.setText(String.format("Value: %d ", newValue.intValue()));
        });

        Slider from_z_slider = new Slider(-10, 10, 1);
        Label fromZLabel = new Label("From Z");
        Label fromZValue = new Label("Value: 1");
        from_z_slider.setBlockIncrement(1);

//        scaleTransition.fromZProperty().bind(from_z_slider.valueProperty());
        from_z_slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            fromZValue.setText(String.format("Value: %d ", newValue.intValue()));
        });

        //======================================= TO XYZ VALUES
        Slider to_x_slider = new Slider(-10, 10, 1);
        Label toXLabel = new Label("To X");
        Label toXValue = new Label("Value: 1");
        to_x_slider.setBlockIncrement(1);

//        scaleTransition.toXProperty().bind(to_x_slider.valueProperty());
        to_x_slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            toXValue.setText(String.format("Value: %d ", newValue.intValue()));
        });

        Slider to_y_slider = new Slider(-10, 10, 1);
        Label toYLabel = new Label("To Y");
        Label toYValue = new Label("Value: 1");
        to_y_slider.setBlockIncrement(1);

//        scaleTransition.toYProperty().bind(to_y_slider.valueProperty());
        to_y_slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            toYValue.setText(String.format("Value: %d ", newValue.intValue()));
        });

        Slider to_z_slider = new Slider(-10, 10, 1);
        Label toZLabel = new Label("To Z");
        Label toZValue = new Label("Value: 1");
        to_z_slider.setBlockIncrement(1);

//        scaleTransition.toZProperty().bind(to_z_slider.valueProperty());
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

        ComboBox<String> interpolatorComboBox = new ComboBox<>(FXCollections.observableArrayList(getInterpolators().keySet()));
        interpolatorComboBox.setPromptText("Choose Interpolator");

        interpolatorComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals(oldValue)) {
                scaleTransition.setInterpolator(interpolators.get(newValue));
            }
        });

        Text title = new Text("Scale Transition, Check code notes about how controls are working");
        title.setWrappingWidth(180);

        VBox vBox = new VBox(
                title,
                new Separator(Orientation.VERTICAL),
                durationLabel,
                durationSlider,
                durationValue,
                byXLabel,
                by_x_slider,
                byXValue,
                byYLabel,
                by_y_slider,
                byYValue,
                byZLabel,
                by_z_slider,
                byZValue,
                fromXLabel,
                from_x_slider,
                fromXValue,
                fromYLabel,
                from_y_slider,
                fromYValue,
                fromZLabel,
                from_z_slider,
                fromZValue,
                toXLabel,
                to_x_slider,
                toXValue,
                toYLabel,
                to_y_slider,
                toYValue,
                toZLabel,
                to_z_slider,
                toZValue,
                playLabel,
                playSlider,
                playValue,
                autoReverseCheckBox,
                new Separator(Orientation.VERTICAL),
                interpolatorComboBox
        );

        vBox.setLayoutX(boardX);
        vBox.setLayoutY(boardY);

//        scaleTransition.play();

        nodes.add(vBox);

        return scaleTransition;
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
    private Animation rotateTransition(Node node, double nodeTranslationX, double nodeTranslationY, double boardX, double boardY, int cycleCount) {
//        Translate translate = new Translate(nodeTranslationX, nodeTranslationY);
//        node.getTransforms().add(translate);
        node.setLayoutX(nodeTranslationX);
        node.setLayoutY(nodeTranslationY);

        RotateTransition rotateTransition = new RotateTransition();
        rotateTransition.setNode(node);
        rotateTransition.setCycleCount(cycleCount);

        ObjectProperty<Duration> duration = new SimpleObjectProperty<>(Duration.millis(1000));

        Slider durationSlider = new Slider(0, 20, 0.167);
        Label durationLabel = new Label("Duration");
        Label durationValue = new Label("Value: 1s");
        durationSlider.setBlockIncrement(0.1);

//        rotateTransition.setDuration(Duration.millis(1000));
        rotateTransition.durationProperty().bind(duration);
        durationSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            duration.setValue(Duration.millis(newValue.doubleValue() * 6000));
            durationValue.setText(String.format("Duration: %.2f s", newValue.doubleValue() * 6));
        });

        Slider angleSlider = new Slider(-720, 720, 90);
        Label angleLabel = new Label("ByAngle");
        Label angleValue = new Label("Value: 90");
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

        ComboBox<String> interpolatorComboBox = new ComboBox<>(FXCollections.observableArrayList(getInterpolators().keySet()));
        interpolatorComboBox.setPromptText("Choose Interpolator");

        interpolatorComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals(oldValue)) {
                rotateTransition.setInterpolator(interpolators.get(newValue));
            }
        });

        Text title = new Text("RotateTrans Controls(check function notes, by default this example only uses \"byAngle\", you have to uncomment code if you want to use the other properties)");
        title.setWrappingWidth(200);

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
                autoReverseCheckBox,
                new Separator(Orientation.VERTICAL),
                interpolatorComboBox
        );

        vBox.setLayoutX(boardX);
        vBox.setLayoutY(boardY);

//        rotateTransition.play();

        nodes.add(vBox);

        return rotateTransition;
    }
}
