package tutorial.tutorial;

import javafx.animation.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Objects;

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
public class Images {
    ObservableList<Node> nodes;
    HashMap<String, Runnable> animations;
    Pane content;
    Scene scene;
    ComboBox<String> transformationsComboBox;
    HashMap<String, Interpolator> interpolators;

    public void displayScreen(Runnable runnable) {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setStyle("-fx-background: orange; -fx-background-color: orange;");
        content = new Pane();

        scrollPane.setContent(content);

        nodes = content.getChildren();

        ObservableList<String> options = FXCollections.observableArrayList(getImageOptions().keySet());

        transformationsComboBox = new ComboBox<>(options);
        transformationsComboBox.setPromptText("Choose Image Signature");

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

    private HashMap<String, Runnable> getImageOptions() {
        if (animations == null) {
            animations = new HashMap<>();

            animations.put("Image(URL) with ImageView", () -> {
                imageWithUrlNImageView(10, 60, 350, 50);
            });

            animations.put("Image(InputStream) with ImageView", () -> {
                imageWithInputStreamNImageView(10, 60, 350, 50);
            });
        }

        return animations;
    }

    private void imageWithInputStreamNImageView(double comboBoxLayoutX, double comboBoxLayoutY, double imageX, double imageY) {
        ObservableList<String> options = FXCollections.observableArrayList();
        options.add("Requested Width/Height, Preserve Ratio, Smooth");

        ComboBox<String> comboBox = new ComboBox<>(options);
        comboBox.setPromptText("Select Signature");
        comboBox.setLayoutX(comboBoxLayoutX);
        comboBox.setTranslateY(comboBoxLayoutY);
        nodes.add(comboBox);

        VBox vbox = new VBox();
        vbox.setLayoutX(comboBoxLayoutX);
        vbox.setLayoutY(comboBoxLayoutY + 70);
        DragUtil.setDraggable(vbox);

        String url = Objects.requireNonNull(Images.class.getResource("tree.png")).toExternalForm();

        Text title = new Text("If a value is missing, default constructor will be used(InputStream value only) and passed to an ImageView. Note there is no BackgroundLoading option when using an inputstream");
        title.setWrappingWidth(250);

        Button button = new Button("Load Image");

        comboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            nodes.clear();
            nodes.addAll(transformationsComboBox, comboBox, vbox);

            vbox.getChildren().clear();
            content.setBackground(null);

            switch (newValue) {
                case "Background Loading" -> {
                    TextField backgroundLoading = new TextField();
                    backgroundLoading.setPromptText("backgroundLoading(boolean)");

                    button.setOnAction(event -> {
                        String backgroundText = backgroundLoading.getText();

                        ImageView imageView;
                        if (!backgroundText.isEmpty()) {
                            if (Boolean.parseBoolean(backgroundText)) {
                                Image image = new Image(url, true);
                                BackgroundImage backgroundImage = new BackgroundImage(image, null, null, null, null);
                                Background background = new Background(backgroundImage);
                                content.setBackground(background);
                            } else {
                                imageView = new ImageView(new Image(url, false));
                                imageView.setX(imageX);
                                imageView.setY(imageY);
                                DragUtil.setDraggable(imageView);

                                nodes.add(imageView);
                            }

                        } else {
                            content.setBackground(null);
                            imageView = new ImageView(new Image(url));
                            imageView.setX(imageX);
                            imageView.setY(imageY);
                            DragUtil.setDraggable(imageView);

                            nodes.add(imageView);
                        }
                    });

                    vbox.getChildren().addAll(title, backgroundLoading, button);
                }
                default -> {
                    content.setBackground(null);
                    // This is "Requested Width/Height, Preserve Ratio, Smooth, BackgroundLoading" case
                    TextField requestedWidth = new TextField();
                    TextField requestedHeight = new TextField();
                    TextField preserveRatio = new TextField();
                    TextField smooth = new TextField();
                    TextField backgroundLoading = new TextField();

                    requestedWidth.setPromptText("requestedWith(required)");
                    requestedHeight.setPromptText("requestedHeight(required)");
                    preserveRatio.setPromptText("preserveRatio(boolean, required)");
                    smooth.setPromptText("smooth(boolean, required)");
                    backgroundLoading.setPromptText("backgroundLoading(boolean, optional)");

                    button.setOnAction(event -> {
                        String requestedWidthText = requestedWidth.getText();
                        String requestedHeightText = requestedHeight.getText();
                        String preserveRatioText = preserveRatio.getText();
                        String smoothText = smooth.getText();
                        String backgroundLoadingText = backgroundLoading.getText();

                        ImageView imageView;
                        if (requestedHeightText.isEmpty() || requestedWidthText.isEmpty() || preserveRatioText.isEmpty() || smoothText.isEmpty()) {
                            imageView = new ImageView(new Image(url));

                            imageView.setX(imageX);
                            imageView.setY(imageY);
                            DragUtil.setDraggable(imageView);

                            nodes.add(imageView);
                        } else {
                            if (backgroundLoadingText.isEmpty()) {
                                Image image = new Image(
                                        url,
                                        Double.parseDouble(requestedWidthText),
                                        Double.parseDouble(requestedHeightText),
                                        Boolean.parseBoolean(preserveRatioText),
                                        Boolean.parseBoolean(smoothText)
                                );

                                imageView = new ImageView(image);

                                imageView.setX(imageX);
                                imageView.setY(imageY);
                                DragUtil.setDraggable(imageView);

                                nodes.add(imageView);
                            } else {
                                if (Boolean.parseBoolean(backgroundLoadingText)) {
                                    Image image = new Image(
                                            url,
                                            Double.parseDouble(requestedWidthText),
                                            Double.parseDouble(requestedHeightText),
                                            Boolean.parseBoolean(preserveRatioText),
                                            Boolean.parseBoolean(smoothText),
                                            true
                                    );

                                    BackgroundImage backgroundImage = new BackgroundImage(image, null, null, null, null);
                                    Background background = new Background(backgroundImage);
                                    content.setBackground(background);
                                } else {
                                    Image image = new Image(
                                            url,
                                            Double.parseDouble(requestedWidthText),
                                            Double.parseDouble(requestedHeightText),
                                            Boolean.parseBoolean(preserveRatioText),
                                            Boolean.parseBoolean(smoothText),
                                            false
                                    );

                                    imageView = new ImageView(image);

                                    imageView.setX(imageX);
                                    imageView.setY(imageY);
                                    DragUtil.setDraggable(imageView);

                                    nodes.add(imageView);
                                }
                            }
                        }
                    });

                    vbox.getChildren().addAll(title, requestedWidth, requestedHeight, preserveRatio, smooth, backgroundLoading, button);
                }
            }
        });
    }

    private void imageWithUrlNImageView(double comboBoxLayoutX, double comboBoxLayoutY, double imageX, double imageY) {
        ObservableList<String> options = FXCollections.observableArrayList();
        options.add("Background Loading");
        options.add("Requested Width/Height, Preserve Ratio, Smooth, BackgroundLoading");

        ComboBox<String> comboBox = new ComboBox<>(options);
        comboBox.setPromptText("Select Signature");
        comboBox.setLayoutX(comboBoxLayoutX);
        comboBox.setTranslateY(comboBoxLayoutY);
        nodes.add(comboBox);

        VBox vbox = new VBox();
        vbox.setLayoutX(comboBoxLayoutX);
        vbox.setLayoutY(comboBoxLayoutY + 70);
        DragUtil.setDraggable(vbox);

        String url = Objects.requireNonNull(Images.class.getResource("tree.png")).toExternalForm();

        Text title = new Text("If no value is entered default constructor will be used(URL value only) and passed to an ImageView");
        title.setWrappingWidth(250);

        Button button = new Button("Load Image");

        comboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals(oldValue)) {
                nodes.clear();
                nodes.addAll(transformationsComboBox, comboBox, vbox);

                vbox.getChildren().clear();
                content.setBackground(null);

                switch (newValue) {
                    case "Background Loading" -> {
                        TextField backgroundLoading = new TextField();
                        backgroundLoading.setPromptText("backgroundLoading(boolean)");

                        button.setOnAction(event -> {
                            String backgroundText = backgroundLoading.getText();

                            ImageView imageView;
                            if (!backgroundText.isEmpty()) {
                                if (Boolean.parseBoolean(backgroundText)) {
                                    Image image = new Image(url, true);
                                    BackgroundImage backgroundImage = new BackgroundImage(image, null, null, null, null);
                                    Background background = new Background(backgroundImage);
                                    content.setBackground(background);
                                } else {
                                    imageView = new ImageView(new Image(url, false));
                                    imageView.setX(imageX);
                                    imageView.setY(imageY);
                                    DragUtil.setDraggable(imageView);

                                    nodes.add(imageView);
                                }

                            } else {
                                content.setBackground(null);
                                imageView = new ImageView(new Image(url));
                                imageView.setX(imageX);
                                imageView.setY(imageY);
                                DragUtil.setDraggable(imageView);

                                nodes.add(imageView);
                            }
                        });

                        vbox.getChildren().addAll(title, backgroundLoading, button);
                    }
                    default -> {
                        content.setBackground(null);
                        // This is "Requested Width/Height, Preserve Ratio, Smooth, BackgroundLoading" case
                        TextField requestedWidth = new TextField();
                        TextField requestedHeight = new TextField();
                        TextField preserveRatio = new TextField();
                        TextField smooth = new TextField();
                        TextField backgroundLoading = new TextField();

                        requestedWidth.setPromptText("requestedWith(required)");
                        requestedHeight.setPromptText("requestedHeight(required)");
                        preserveRatio.setPromptText("preserveRatio(boolean, required)");
                        smooth.setPromptText("smooth(boolean, required)");
                        backgroundLoading.setPromptText("backgroundLoading(boolean, optional)");

                        button.setOnAction(event -> {
                            String requestedWidthText = requestedWidth.getText();
                            String requestedHeightText = requestedHeight.getText();
                            String preserveRatioText = preserveRatio.getText();
                            String smoothText = smooth.getText();
                            String backgroundLoadingText = backgroundLoading.getText();

                            ImageView imageView;
                            if (requestedHeightText.isEmpty() || requestedWidthText.isEmpty() || preserveRatioText.isEmpty() || smoothText.isEmpty()) {
                                imageView = new ImageView(new Image(url));

                                imageView.setX(imageX);
                                imageView.setY(imageY);
                                DragUtil.setDraggable(imageView);

                                nodes.add(imageView);
                            } else {
                                if (backgroundLoadingText.isEmpty()) {
                                    Image image = new Image(
                                            url,
                                            Double.parseDouble(requestedWidthText),
                                            Double.parseDouble(requestedHeightText),
                                            Boolean.parseBoolean(preserveRatioText),
                                            Boolean.parseBoolean(smoothText)
                                    );

                                    imageView = new ImageView(image);

                                    imageView.setX(imageX);
                                    imageView.setY(imageY);
                                    DragUtil.setDraggable(imageView);

                                    nodes.add(imageView);
                                } else {
                                    if (Boolean.parseBoolean(backgroundLoadingText)) {
                                        Image image = new Image(
                                                url,
                                                Double.parseDouble(requestedWidthText),
                                                Double.parseDouble(requestedHeightText),
                                                Boolean.parseBoolean(preserveRatioText),
                                                Boolean.parseBoolean(smoothText),
                                                true
                                        );

                                        BackgroundImage backgroundImage = new BackgroundImage(image, null, null, null, null);
                                        Background background = new Background(backgroundImage);
                                        content.setBackground(background);
                                    } else {
                                        Image image = new Image(
                                                url,
                                                Double.parseDouble(requestedWidthText),
                                                Double.parseDouble(requestedHeightText),
                                                Boolean.parseBoolean(preserveRatioText),
                                                Boolean.parseBoolean(smoothText),
                                                false
                                        );

                                        imageView = new ImageView(image);

                                        imageView.setX(imageX);
                                        imageView.setY(imageY);
                                        DragUtil.setDraggable(imageView);

                                        nodes.add(imageView);
                                    }
                                }
                            }
                        });

                        vbox.getChildren().addAll(title, requestedWidth, requestedHeight, preserveRatio, smooth, backgroundLoading, button);
                    }
                }
            }
        });
//        Image image = new Image("",);
    }
}
