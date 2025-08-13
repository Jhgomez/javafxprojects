package tutorial.tutorial;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.*;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * In JavaFX, you can set various effects to a node such as bloom, blur and glow. These classes are available in a package
 * named javafx.scene.effect.
 */
public class Effects {
    ObservableList<Node> nodes;
    Image image = new Image(Objects.requireNonNull(getClass().getResource("tree.png")).toExternalForm());
    HashMap<String, Supplier<Effect>> effects;

    public void displayScreen(Runnable runnable) {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setStyle("-fx-background: orange; -fx-background-color: orange;");
        Pane pane = new Pane();

        scrollPane.setContent(pane);

        nodes = pane.getChildren();

        // original picture as a point of comparison
        ImageView imageView = new ImageView(image);

        imageView.setX(15);
        imageView.setY(30);

        imageView.setFitHeight(200);
        imageView.setFitWidth(400);
        imageView.setPreserveRatio(true);

        Text text = new Text("Original Picture");
        text.setFont(Font.font(16));
        text.setX(20);
        text.setY(20);

        nodes.addAll(imageView,text);
//        glowEffect();
        adjustColor();
        colorInput();
        imageInput();
        blendEffect();
        allOtherEffects();

        Scene scene = new Scene(scrollPane, 1050, 730);

        scene.setFill(Paint.valueOf("#fdbf6f"));

        Stage stage = new Stage();

        stage.setTitle("2D Shapes");
        stage.setScene(scene);
        stage.sizeToScene();

        for (Node node : nodes) {
            DragUtil.setDraggable(node);
        }

        stage.show();

        stage.setOnCloseRequest(e -> runnable.run());
    }

    private void allOtherEffects() {
        HashMap<String, Supplier<Effect>>  effects = getEffects();

        ObservableList<String> options = FXCollections.observableArrayList(effects.keySet());

        Text textTitle = new Text("Choose Between Different Effect For Each Text");
        textTitle.setFont(Font.font(16));
        textTitle.setX(335);
        textTitle.setY(365);

        nodes.add(textTitle);

        Text modifiableText1 = new Text("JavaFX Tutorial App");
        modifiableText1.setFont(Font.font(null, FontWeight.BOLD, 40));

        modifiableText1.setX(335);
        modifiableText1.setY(420);

        modifiableText1.setFill(Color.DARKSEAGREEN);

        Rectangle rectangle = new Rectangle();

        rectangle.setX(340.0f);
        rectangle.setY(380.0f);
        rectangle.setWidth(380.0f);
        rectangle.setHeight(60.0f);
        rectangle.setFill(Color.TEAL);

        ComboBox<String> topTextComboBox = new ComboBox<>(options);
        topTextComboBox.setPromptText("Top Text Effect");

        topTextComboBox.valueProperty().addListener((observable, oldVal, newVal) -> {
            if (effects.get(newVal) != null) {
                if(newVal.equals("Point Spot") || newVal.equals("Spot Light")) {
                    modifiableText1.setFill(Color.RED);
                } else if(oldVal != null && (oldVal.equals("Point Spot") || oldVal.equals("Spot Light"))) {
                    modifiableText1.setFill(Color.DARKSEAGREEN);
                }

                modifiableText1.setEffect(effects.get(newVal).get());
            } else {
                modifiableText1.setEffect(null);
            }
        });

        topTextComboBox.setLayoutX(740);
        topTextComboBox.setLayoutY(400);

        nodes.addAll(rectangle, modifiableText1, topTextComboBox);

        Text modifiableText2 = new Text("JavaFX Tutorial App");
        modifiableText2.setFont(Font.font(null, FontWeight.BOLD, 40));
        modifiableText2.setX(335);
        modifiableText2.setY(500);
        modifiableText2.setFill(Color.DARKSEAGREEN);

        rectangle = new Rectangle();

        rectangle.setX(340.0f);
        rectangle.setY(460.0f);
        rectangle.setWidth(380.0f);
        rectangle.setHeight(60.0f);
        rectangle.setFill(Color.TEAL);

        ComboBox<String> bottomTextComboBox = new ComboBox<>(options);
        bottomTextComboBox.setPromptText("Bottom Text Effect");

        bottomTextComboBox.valueProperty().addListener((observable, oldVal, newVal) -> {
            if (effects.get(newVal) != null) {
                if(newVal.equals("Point Spot") || newVal.equals("Spot Light")) {
                    modifiableText2.setFill(Color.RED);
                } else if(oldVal != null && (oldVal.equals("Point Spot") || oldVal.equals("Spot Light"))) {
                    modifiableText2.setFill(Color.DARKSEAGREEN);
                }

                modifiableText2.setEffect(effects.get(newVal).get());
            } else {
                modifiableText2.setEffect(null);
            }
        });

        bottomTextComboBox.setLayoutX(740);
        bottomTextComboBox.setLayoutY(470);

        nodes.addAll(rectangle, modifiableText2, bottomTextComboBox);

        Text text = new Text("Choose between different effects for each image and circle");
        text.setWrappingWidth(360);
        text.setFont(Font.font(16));
        text.setX(350);
        text.setY(560);

        nodes.add(text);

        ImageView imageView = new ImageView(image);
        imageView.setX(350);
        imageView.setY(600);
        imageView.setFitHeight(300);
        imageView.setFitWidth(200);
        imageView.setPreserveRatio(true);

        ComboBox<String> leftImageComboBox = new ComboBox<>(options);
        leftImageComboBox.setPromptText("Left Image Effect");

        leftImageComboBox.valueProperty().addListener((observable, oldVal, newVal) -> {
            if (effects.get(newVal) != null) {
                imageView.setEffect(effects.get(newVal).get());
            } else {
                imageView.setEffect(null);
            }
        });

        leftImageComboBox.setLayoutX(780);
        leftImageComboBox.setLayoutY(600);

        nodes.addAll(imageView, leftImageComboBox);

        ImageView imageView2 = new ImageView(image);
        imageView2.setFitHeight(300);
        imageView2.setFitWidth(200);
        imageView2.setPreserveRatio(true);
        imageView2.setX(565);
        imageView2.setY(600);

        ComboBox<String> rightImageComboBox = new ComboBox<>(options);
        rightImageComboBox.setPromptText("Right Image Effect");

        rightImageComboBox.valueProperty().addListener((observable, oldVal, newVal) -> {
            if (effects.get(newVal) != null) {
                imageView2.setEffect(effects.get(newVal).get());
            } else {
                imageView2.setEffect(null);
            }
        });

        rightImageComboBox.setLayoutX(780);
        rightImageComboBox.setLayoutY(635);

        nodes.addAll(imageView2, rightImageComboBox);

        Circle circle = new Circle();
        circle.setFill(Color.GREEN);
        circle.setRadius(30);
        circle.setCenterX(520);
        circle.setCenterY(910);

        ComboBox<String> leftCircleComboBox = new ComboBox<>(options);
        leftCircleComboBox.setPromptText("Right Image Effect");

        leftCircleComboBox.valueProperty().addListener((observable, oldVal, newVal) -> {
            if (effects.get(newVal) != null) {
                if(newVal.equals("Point Spot") || newVal.equals("Spot Light")) {
                    circle.setFill(Color.CORNFLOWERBLUE);
                } else if(oldVal != null && (oldVal.equals("Point Spot") || oldVal.equals("Spot Light"))) {
                    circle.setFill(Color.GREEN);
                }

                circle.setEffect(effects.get(newVal).get());
            } else {
                circle.setEffect(null);
            }
        });

        leftCircleComboBox.setLayoutX(680);
        leftCircleComboBox.setLayoutY(890);

        nodes.addAll(circle, leftCircleComboBox);

        Circle circle2 = new Circle();
        circle2.setFill(Color.GREEN);
        circle2.setRadius(30);
        circle2.setCenterX(610);
        circle2.setCenterY(910);

        ComboBox<String> rightCircleComboBox = new ComboBox<>(options);
        rightCircleComboBox.setPromptText("Right Image Effect");

        rightCircleComboBox.valueProperty().addListener((observable, oldVal, newVal) -> {
            if (effects.get(newVal) != null) {
                if(newVal.equals("Point Spot") || newVal.equals("Spot Light")) {
                    circle2.setFill(Color.CORNFLOWERBLUE);
                } else if(oldVal != null && (oldVal.equals("Point Spot") || oldVal.equals("Spot Light"))) {
                    circle2.setFill(Color.GREEN);
                }

                circle2.setEffect(effects.get(newVal).get());
            } else {
                circle2.setEffect(null);
            }
        });

        rightCircleComboBox.setLayoutX(680);
        rightCircleComboBox.setLayoutY(925);

        nodes.addAll(circle2, rightCircleComboBox);
    }

    private HashMap<String, Supplier<Effect>> getEffects() {
        if (effects != null) {
            return effects;
        }

        effects = new HashMap<>();

        effects.put("No Effect", null);

        effects.put("Bloom", () -> {
            Bloom bloom = new Bloom();
            bloom.setThreshold(0.1);
            return bloom;
        });

        effects.put("Glow", () -> {
            Glow glow = new Glow();
            glow.setLevel(0.9);
            return glow;
        });

        /*
         * iterations − This property is of an integer type representing the number of iterations of the effect, which are
         * to be applied on the node. This is done to improve its quality or smoothness.
         */
        effects.put("Box Blur", () -> {
            BoxBlur boxblur = new BoxBlur();
            boxblur.setWidth(8.0f);
            boxblur.setHeight(3.0f);
            boxblur.setIterations(3);

            return boxblur;
        });

        /*
         * is an effect to blur the nodes in JavaFX. The only difference is that in Gaussian Blur Effect, a Gaussian
         * convolution kernel is used to produce the blurring effect.
         */
        effects.put("GaussianBlur", () -> {
            GaussianBlur gaussianBlur = new GaussianBlur();
            gaussianBlur.setRadius(10.5);

            return gaussianBlur;
        });

        effects.put("Reflection", () -> {
            Reflection reflection = new Reflection();
            reflection.setBottomOpacity(0.0);
            reflection.setTopOpacity(0.5);
            reflection.setTopOffset(0.0);
            reflection.setFraction(0.7);

            return reflection;
        });

        /*
        in general changes the image from the black and white color to a reddish brown color.
         */
        effects.put("SepiaTone", () -> {
            SepiaTone sepiaTone = new SepiaTone();
            sepiaTone.setLevel(0.8);

            return sepiaTone;
        });

        /*
        creates a duplicate of the specified node with blurry edges.
         */
        effects.put("Shadow", () -> {
            Shadow shadow = new Shadow();

            shadow.setBlurType(BlurType.GAUSSIAN);
            shadow.setColor(Color.ROSYBROWN);
            shadow.setHeight(5);
            shadow.setWidth(5);
            shadow.setRadius(5);

            return shadow;
        });

        /*
         is a type of a shadow effect; on applying this effect to a node, a shadow will be created behind the specified node.
         */
        effects.put("Drop Shadow", () -> {
            DropShadow dropShadow = new DropShadow();

            dropShadow.setBlurType(BlurType.GAUSSIAN);
            dropShadow.setColor(Color.ROSYBROWN);
            dropShadow.setHeight(5);
            dropShadow.setWidth(5);
            dropShadow.setRadius(5);
            dropShadow.setOffsetX(3);
            dropShadow.setOffsetY(2);
            dropShadow.setSpread(12);

            return dropShadow;
        });

        //  creates a shadow inside the edges of the node
        effects.put("Inner Shadow", () -> {
            InnerShadow innerShadow = new InnerShadow();
            innerShadow.setOffsetX(4);
            innerShadow.setOffsetY(4);
            innerShadow.setColor(Color.GRAY);

            return innerShadow;
        });

        /*
        is used to simulate a light from a light source. There are different kinds of light sources which
        include − Point, Distant and Spot.
         */
        effects.put("Lighting", Lighting::new);

        effects.put("Distant Light", () -> {
            Light.Distant light = new Light.Distant();
            light.setAzimuth(45.0);
            light.setElevation(30.0);

            Lighting lighting = new Lighting();
            lighting.setLight(light);

            return lighting;
        });

        /*
        is a simulation of a Spot Light on a node. By applying this effect on a node, a light is projected on it, as if
        it is being generated by a spot light.
         */
        effects.put("Spot Light", () -> {
            Light.Spot light = new Light.Spot();
            light.setColor(Color.GRAY);

            //setting the position of the light, if X and Y values are too high, light might be difficult to find
            light.setX(70);
            light.setY(55);
            light.setZ(45);

            Lighting lighting = new Lighting();
            lighting.setLight(light);

            return lighting;
        });

        /*
        type of light source where the light is located at a specific point and the light emitted from it is shone
        equally in all directions.
         */
        effects.put("Point Spot", () -> {
            Light.Point light = new Light.Point();
            light.setColor(Color.GRAY);

            //setting the position of the light, if X and Y values are too high, light might be difficult to find
            light.setX(70);
            light.setY(55);
            light.setZ(45);

            Lighting lighting = new Lighting();
            lighting.setLight(light);

            return lighting;
        });

        /*
        an image is composed of infinite number of pixels. The Displacement Map Effect shifts the pixels of an input
        image by a certain distance and produces an output image with different locations of these pixels.
         */
        effects.put("Displacement Map", () -> {
            int width = 220;
            int height = 100;

            // A buffer that contains floating point data, intended for use as a parameter to effects such as DisplacementMap
            FloatMap floatMap = new FloatMap();
            floatMap.setWidth(width);
            floatMap.setHeight(height);

            for (int i = 0; i < width; i++) {
                double v = (Math.sin(i / 20.0 * Math.PI) - 0.5) / 40.0;
                for (int j = 0; j < height; j++) {
                    floatMap.setSamples(i, j, 0.0f, (float) v);
                }
            }

            DisplacementMap displacementMap = new DisplacementMap();
            displacementMap.setMapData(floatMap);

            return displacementMap;
        });

        return effects;
    }

    /**
     * - bottomInput − This property is of the type Effect and it represents the bottom input to the blend effect.
     * - topInput − This property is of the type Effect and it represents the top input to the blend effect.
     * - opacity − This property is of double type and it represents the opacity value modulated with the top input.
     * - mode − This property is of the type BlendMode and it represents the mode used to blend the two inputs together.
     */
    private void blendEffect() {
        Text text = new Text("Blend Effect, both with top input(effect to blend is on top) not bottom input, first column uses ColorInput effect, second uses ColorAdjust");
        text.setFont(Font.font(16));
        text.setWrappingWidth(350);
        text.setX(20);
        text.setY(290);

        nodes.add(text);

        BlendMode[] blendModes = {
                BlendMode.ADD,
                BlendMode.MULTIPLY,
                BlendMode.OVERLAY,
                BlendMode.DIFFERENCE,
                BlendMode.RED,
                BlendMode.BLUE,
                BlendMode.GREEN,
                BlendMode.EXCLUSION,
                BlendMode.COLOR_BURN,
                BlendMode.COLOR_DODGE,
                BlendMode.LIGHTEN,
                BlendMode.DARKEN,
                BlendMode.SCREEN,
                BlendMode.HARD_LIGHT,
                BlendMode.SOFT_LIGHT,
                BlendMode.SRC_ATOP,
                BlendMode.SRC_OVER
        };

        int centerX = 120;
        int centerY = 380;
        int radius = 30;
        int colorInputX = 85;
        int colorInputY = 345;

        for (int i =  1; i <= 2; i++) {
            Effect input = null;

            if (i == 2) {
               input = new ColorAdjust(0.4, -0.05, 0.9, 0.8);
               centerX = 195;
               centerY = 380;
               colorInputX = 160;
               colorInputY = 345;
            }

            for (BlendMode blendMode : blendModes) {
                Text blendColorText = new Text(blendMode.name());
                blendColorText.setX(20);
                blendColorText.setY(centerY);

                Circle blendColorInput = new Circle();
                blendColorInput.setCenterX(centerX);
                blendColorInput.setCenterY(centerY);
                blendColorInput.setRadius(radius);
                blendColorInput.setFill(Color.RED);

                if (i == 1) {
                    input = new ColorInput(colorInputX, colorInputY, 75, 40, Color.GRAY);
                }

                Blend blend = new Blend();
                blend.setTopInput(input);
//                blend.setBottomInput(input);
                blend.setMode(blendMode);

                blendColorInput.setEffect(blend);

                centerY += radius + 35;
                colorInputY += radius + 35;

                nodes.addAll(blendColorInput, blendColorText);
            }
        }
    }

    /**
     * Embeds an image to the JavaFX screen. Just like in the Color Input effect, it is
     * used to pass the specified colored rectangular region as an input to another effect. An Image Input effect is used
     * to pass the specified image as an input to another effect.
     *
     * On applying this effect, the image specified will not be modified. This effect is applied to any node.
     */
    private void imageInput() {
        Rectangle rectangle = new Rectangle();

        Image image = new Image(Objects.requireNonNull(Effects.class.getResource("tree2.png")).toExternalForm());

        ImageInput imageInput = new ImageInput();
        imageInput.setX(540);
        imageInput.setY(40);
        imageInput.setSource(image);

        rectangle.setEffect(imageInput);

        Text text = new Text("Image Input Effect");
        text.setFont(Font.font(16));
        text.setX(575);
        text.setY(20);

        nodes.addAll(rectangle,text);
    }

    /**
     * Color Input Effect gives the same output as drawing a rectangle and filling it with color. Unlike other effects,
     * if this effect is applied to any node, it displays only a rectangular box (not the node). This effect is mostly
     * used to pass as an input for other effects.
     *
     * For example, while applying the blend effect, it requires an object of effect type as input. There we can pass this
     * as an input.
     */
    private void colorInput() {
        Rectangle rectangle = new Rectangle();

        ColorInput colorInput = new ColorInput();
        colorInput.setX(410);
        colorInput.setY(50);
        colorInput.setHeight(40);
        colorInput.setWidth(70);
        colorInput.setPaint(Color.CHOCOLATE);

        rectangle.setEffect(colorInput);

        Text text = new Text("Color Input Effect");
        text.setFont(Font.font(16));
        text.setX(415);
        text.setY(20);

        nodes.addAll(rectangle, text);
    }

    /**
     * You can adjust the color of an image by applying the color adjust effect to it. This includes the adjustment of
     * the Hue, Saturation, Brightness and Contrast on each pixel.
     *
     * - input − This property is of the Effect type and it represents an input to the color adjust effect.
     * = brightness − This property is of Double type and it represents the brightness adjustment value for this effect.
     * - contrast − This property is of Double type and it represents the contrast adjustment value for this effect.
     * - hue − This property is of Double type and it represents the hue adjustment value for this effect.
     * - saturation − This property is of Double type and it represents the saturation adjustment value for this effect.
     */
    private void adjustColor() {
        ImageView imageView = new ImageView(image);

        imageView.setX(180);
        imageView.setY(40);

        imageView.setFitHeight(200);
        imageView.setFitWidth(400);
        imageView.setPreserveRatio(true);

        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setContrast(0.4);
        colorAdjust.setHue(-0.05);
        colorAdjust.setBrightness(0.9);
        colorAdjust.setSaturation(0.8);

        imageView.setEffect(colorAdjust);

        Text text = new Text("Adjust Color Effect (brightness, saturation, Hue, Contrast)");
        text.setWrappingWidth(270);
        text.setFont(Font.font(16));
        text.setX(180);
        text.setY(15);

        nodes.addAll(imageView, text);
    }
}
