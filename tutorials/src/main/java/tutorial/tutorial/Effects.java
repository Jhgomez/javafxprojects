package tutorial.tutorial;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
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

import java.util.Objects;

/**
 * In JavaFX, you can set various effects to a node such as bloom, blur and glow. These classes are available in a package
 * named javafx.scene.effect.
 */
public class Effects {
    ObservableList<Node> nodes;
    Image image = new Image(Objects.requireNonNull(getClass().getResource("tree.png")).toExternalForm());

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
        bloomEffect();

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

    private void bloomEffect() {
        Text text = new Text("Same Text with Bloom applied and without Bloom(no effect)");
        text.setFont(Font.font(16));
        text.setX(315);
        text.setY(365);

        nodes.add(text);

        text = new Text("JavaFX Tutorial App");

        //Setting font to the text
        text.setFont(Font.font(null, FontWeight.BOLD, 40));

        text.setX(335);
        text.setY(420);

        text.setFill(Color.DARKSEAGREEN);

        Rectangle rectangle = new Rectangle();

        rectangle.setX(340.0f);
        rectangle.setY(380.0f);
        rectangle.setWidth(380.0f);
        rectangle.setHeight(60.0f);
        rectangle.setFill(Color.TEAL);

        Bloom bloom = new Bloom();
        bloom.setThreshold(0.1);
        text.setEffect(bloom);

        nodes.addAll(rectangle, text);

        text = new Text("JavaFX Tutorial App");

        text.setFont(Font.font(null, FontWeight.BOLD, 40));

        text.setX(335);
        text.setY(500);

        text.setFill(Color.DARKSEAGREEN);

        rectangle = new Rectangle();

        rectangle.setX(340.0f);
        rectangle.setY(460.0f);
        rectangle.setWidth(380.0f);
        rectangle.setHeight(60.0f);
        rectangle.setFill(Color.TEAL);

        nodes.addAll(rectangle, text);
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

//        Circle blendColorInput = new Circle();
//        blendColorInput.setCenterX(50.0f);
//        blendColorInput.setCenterY(320.0f);
//        blendColorInput.setRadius(30.0f);
//
//        //Setting the fill color of the circle
//        blendColorInput.setFill(Color.RED);
//
//        //Instantiating the blend class
//        Blend blend = new Blend();
//
//        //Preparing the to input object
//        ColorInput topInput = new ColorInput(15, 285, 75, 40, Color.GRAY);
//
//        //setting the top input to the blend object
////        blend.setBottomInput(topInput);
//        blend.setTopInput(topInput);
//
//        //setting the blend mode
//        blend.setMode(BlendMode.SOFT_LIGHT);
//
//        //Applying the blend effect to circle
//        blendColorInput.setEffect(blend);
//
//        Text text = new Text("Blend Effect");
//        text.setFont(Font.font(16));
//        text.setX(740);
//        text.setY(20);

//        nodes.addAll(text, blendColorInput);
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

    private void glowEffect() {

        //Setting the image view
        ImageView imageView = new ImageView(image);

        //Setting the position of the image
        imageView.setX(0);
        imageView.setY(0);

        //setting the fit height and width of the image view
        imageView.setFitHeight(100);
        imageView.setFitWidth(200);

        //Setting the preserve ratio of the image view
        imageView.setPreserveRatio(true);

        Glow glow = new Glow();
        //setting the level property
        glow.setLevel(0.9);

        imageView.setEffect(glow);

        ImageView imageView2 = new ImageView(image);
        imageView2.setFitHeight(100);
        imageView2.setFitWidth(200);
        imageView2.setPreserveRatio(true);
        imageView2.setX(300);
        imageView2.setY(20);

        Circle circle = new Circle();
        circle.setFill(Color.GREEN);
        circle.setRadius(30);
        circle.setCenterX(30);
        circle.setCenterY(150);
        circle.setEffect(glow);

        Circle circle2 = new Circle();
        circle2.setFill(Color.GREEN);
        circle2.setRadius(30);
        circle2.setCenterX(80);
        circle2.setCenterY(150);

        nodes.addAll(imageView, imageView2, circle, circle2);
    }
}
