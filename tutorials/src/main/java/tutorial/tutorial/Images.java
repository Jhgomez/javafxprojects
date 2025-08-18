package tutorial.tutorial;

import javafx.animation.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Objects;
import java.util.function.Function;

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

            animations.put("Image(URL) with ImageView", () ->
                imageWithUrlNImageView(10, 60, 350, 50)
            );

            animations.put("Image(InputStream) with ImageView", () ->
                    imageWithInputStreamNImageView(10, 60, 350, 50)
            );

            animations.put("Imageview with URL(Could not be most efficient)", () ->
                imageViewAlone(350, 50)
            );

            animations.put("Read and Write Pixels", () -> {
                readNWriteImagePixels(10, 60, 350, 50);
            });
        }

        return animations;
    }

    /**
     * Creating an ImageView without an ImageView might not be the best choice if you want to be careful with the resources
     * that the app is using because it will load the plain image while if creating it from an "Image" object you can
     * specify the size you want to load which will reduce the resources used to display the image, for example if you create
     * an ImageView from an URL and then set its size to a small size it will consume as much resources as if you would be
     * loading/displaying the image in full size
     */
    private void imageViewAlone(double imageX, double imageY) {
        String url = Objects.requireNonNull(Images.class.getResource("tree.png")).toExternalForm();
        ImageView imageView = new ImageView(url);
        imageView.setX(imageX);
        imageView.setY(imageY);

        nodes.add(imageView);
    }

    private void readNWriteImagePixels(double comboBoxLayoutX, double comboBoxLayoutY, double imageX, double imageY) {
        ObservableList<String> options = FXCollections.observableArrayList();
        options.addAll("Darker", "Brighter", "Desaturate", "Grayscale", "invert", "saturate", "Interpolate", "Derive Color");

        ComboBox<String> comboBox = new ComboBox<>(options);
        comboBox.setPromptText("Select Rewrite Image Change");
        comboBox.setLayoutX(comboBoxLayoutX);
        comboBox.setTranslateY(comboBoxLayoutY);
        nodes.add(comboBox);

        VBox vbox = new VBox();
        vbox.setLayoutX(comboBoxLayoutX);
        vbox.setLayoutY(comboBoxLayoutY + 70);
        DragUtil.setDraggable(vbox);
        nodes.add(vbox);

        String url = Objects.requireNonNull(Images.class.getResource("tree.png")).toExternalForm();
        Image image = new Image(url, 300, 400, true, true);
        ImageView original = new ImageView(image);

        original.setX(imageX);
        original.setY(imageY);
        nodes.add(original);

        int width = (int)image.getWidth();
        int height = (int)image.getHeight();

        PixelReader pixelReader = image.getPixelReader();

        comboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            vbox.getChildren().clear();

            WritableImage wImage = new WritableImage(width, height);

            PixelWriter writer = wImage.getPixelWriter();

            switch (newValue) {
                case "Darker" -> writeImage(pixelReader, writer, Color::darker, height, width);
                case "Brighter" -> writeImage(pixelReader, writer, Color::brighter, height, width);
                case "Desaturate" -> writeImage(pixelReader, writer, Color::desaturate, height, width);
                case "Grayscale" -> writeImage(pixelReader, writer, Color::grayscale, height, width);
                case "Saturate" -> writeImage(pixelReader, writer, Color::invert, height, width);
                case "Interpolate" -> {
                    TextField colorField = new TextField();
                    colorField.setPromptText("Enter Color Name(required)");

                    Slider interpolator = new Slider(0, 1, 0);
                    Label interpolatorLabel = new Label("Interpolator Fraction");
                    Label valuesLabel = new Label("Value: 0");
                    interpolator.setBlockIncrement(0.1);

                    interpolator.valueProperty().addListener(((observable1, oldValue1, newValue1) ->
                            valuesLabel.setText(String.format("Value: %.2f", newValue1.doubleValue()))
                    ));

                    Button button = new Button("Load Image");

                    button.setOnAction(event ->
                        writeImage(
                                pixelReader,
                                writer,
                                color -> color.interpolate(Color.valueOf(colorField.getText()),
                                        interpolator.getValue()),
                                height,
                                width
                        )
                    );

                    vbox.getChildren().addAll(colorField, interpolatorLabel, interpolator, valuesLabel, button);
                }
                case "Derive Color" -> {
                    TextField hueShift = new TextField();
                    TextField saturationFactor = new TextField();
                    TextField brightnessFactor = new TextField();
                    TextField opacityFactor = new TextField();

                    hueShift.setPromptText("Double(required)");
                    saturationFactor.setPromptText("Double(required)");
                    brightnessFactor.setPromptText("Double(required)");
                    opacityFactor.setPromptText("Double(required)");

                    Button button = new Button("Load Image");

                    button.setOnAction(event -> {
                        double hue = Double.parseDouble(hueShift.getText());
                        double saturation = Double.parseDouble(saturationFactor.getText());
                        double brightness = Double.parseDouble(brightnessFactor.getText());
                        double opacity = Double.parseDouble(opacityFactor.getText());

                        writeImage(
                                pixelReader,
                                writer,
                                color -> color.deriveColor(hue, saturation,  brightness, opacity),
                                height,
                                width
                        );
                    });

                    vbox.getChildren().addAll(hueShift, saturationFactor, brightnessFactor, opacityFactor, button);
                }
            }


            //Setting the view for the writable image
            ImageView imageView = new ImageView(wImage);

            imageView.setX(imageX + image.getWidth());
            imageView.setY(imageY);

            DragUtil.setDraggable(imageView);

            nodes.add(imageView);
        });
    }

    private void writeImage(PixelReader pixelReader, PixelWriter writer, Function<Color, Color> colorConverter, int height, int width) {
        for(int y = 0; y < height; y++) {
            for(int x = 0; x < width; x++) {
                Color color = pixelReader.getColor(x, y);

                writer.setColor(x, y, colorConverter.apply(color));
            }
        }
    }

    /**
     * An Image object can also be resized while they are being loaded, in order to reduce its memory storage amount.
     */
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

        // My user has a white space that is traduce to "%20" when I use the "getResource" function, also it adds a
        // ":file/" function that is why I need to replace "%20" with a white space(" ") and remove the ":file/" at the beggining
        // So I thake this "file:\C:\Users\Juan%20Enrique\javafxprojects\tutorials\build\resources\main\tutorial\tutorial\tree.png"
        // to this "C:\Users\Juan Enrique\javafxprojects\tutorials\build\resources\main\tutorial\tutorial\tree.png"
        String file = Objects.requireNonNull(Images.class.getResource("tree.png")).toExternalForm().substring(6).replace("%20", " ");

        Text title = new Text("If a value is missing, default constructor will be used(InputStream value only) and passed to an ImageView. Note there is no BackgroundLoading option when using an inputstream");
        title.setWrappingWidth(250);

        Button button = new Button("Load Image");

        comboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            nodes.clear();
            nodes.addAll(transformationsComboBox, comboBox, vbox);

            vbox.getChildren().clear();
//            content.setBackground(null);

            // This is "Requested Width/Height, Preserve Ratio, Smooth, BackgroundLoading" case
                    TextField requestedWidth = new TextField();
                    TextField requestedHeight = new TextField();
                    TextField preserveRatio = new TextField();
                    TextField smooth = new TextField();

                    requestedWidth.setPromptText("requestedWith(required)");
                    requestedHeight.setPromptText("requestedHeight(required)");
                    preserveRatio.setPromptText("preserveRatio(boolean, required)");
                    smooth.setPromptText("smooth(boolean, required)");

                    button.setOnAction(event -> {
                        try(InputStream inputStream = new FileInputStream(file)) {
                            String requestedWidthText = requestedWidth.getText();
                            String requestedHeightText = requestedHeight.getText();
                            String preserveRatioText = preserveRatio.getText();
                            String smoothText = smooth.getText();

                            ImageView imageView;
                            if (requestedHeightText.isEmpty() || requestedWidthText.isEmpty() || preserveRatioText.isEmpty() || smoothText.isEmpty()) {
                                imageView = new ImageView(new Image(inputStream));

                            } else {
                                Image image = new Image(
                                        inputStream,
                                        Double.parseDouble(requestedWidthText),
                                        Double.parseDouble(requestedHeightText),
                                        Boolean.parseBoolean(preserveRatioText),
                                        Boolean.parseBoolean(smoothText)
                                );

                                imageView = new ImageView(image);

                            }

                            imageView.setX(imageX);
                            imageView.setY(imageY);
                            DragUtil.setDraggable(imageView);

                            nodes.add(imageView);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });

                    vbox.getChildren().addAll(title, requestedWidth, requestedHeight, preserveRatio, smooth, button);
//            try {
//                inputStream.close();
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//            switch (newValue) {
//                case "Background Loading" -> {
//                    TextField backgroundLoading = new TextField();
//                    backgroundLoading.setPromptText("backgroundLoading(boolean)");
//
//                    button.setOnAction(event -> {
//                        String backgroundText = backgroundLoading.getText();
//
//                        ImageView imageView;
//                        if (!backgroundText.isEmpty()) {
//                            if (Boolean.parseBoolean(backgroundText)) {
//                                Image image = new Image(inputStream);
//                                BackgroundImage backgroundImage = new BackgroundImage(image, null, null, null, null);
//                                Background background = new Background(backgroundImage);
//                                content.setBackground(background);
//                            } else {
//                                imageView = new ImageView(new Image(inputStream, false));
//                                imageView.setX(imageX);
//                                imageView.setY(imageY);
//                                DragUtil.setDraggable(imageView);
//
//                                nodes.add(imageView);
//                            }
//
//                        } else {
//                            content.setBackground(null);
//                            imageView = new ImageView(new Image(inputStream));
//                            imageView.setX(imageX);
//                            imageView.setY(imageY);
//                            DragUtil.setDraggable(imageView);
//
//                            nodes.add(imageView);
//                        }
//                    });
//
//                    vbox.getChildren().addAll(title, backgroundLoading, button);
//                }
//                default -> {
//                    content.setBackground(null);
//
//                }
//            }
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
