package tutorial.tutorial;

import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Paint is the base class of all the classes that are used to apply colors.
 *
 * - Uniform − In this pattern, color is applied uniformly throughout node.
 * - Image Pattern − This lets you to fill the region of the node with an image pattern.
 *  - Gradient − In this pattern, the color applied to the node varies from one point to the other. It has two kinds of
 *    gradients namely Linear Gradient and Radial Gradient.
 */
public class ColorsAndTextures {
    ObservableList<Node> nodes;

    public void displayScreen(Runnable runnable) {
        //============================== DIFFERENT WAYS OF CREATING A COLOR ============================================
        Color a = Color.rgb(0,0,255);

        //creating color object by passing HSB values
        Color b = Color.hsb(270,1.0,1.0);

        //creating color object by passing the hash code for web
        Color c = Color.web("0x0000FF",1.0);

        Group group = new Group();
//        AnchorPane ap = new AnchorPane();
        nodes = group.getChildren();

        uniformColorInTextAndShape();
        imagePatternColoring();
        linearGradient();
        radialGradient();

        Scene scene = new Scene(group, 1050, 730);

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

    /**
     * The other types of gradient patterns are Radial, Angular, Reflected, Diamond gradient patterns. Here we will learn
     * about the Radial Gradient Pattern.
     *
     * The Radial Gradient Pattern starts from a center point and flows in a circular manner up to a radius. Simply put,
     * the radial gradient contains two or more color stops in the form of concentric circles.
     *
     * Parameters
     * - startX, startY − These double properties represent the x and y coordinates of the starting point of the gradient.
     * - endX, endY − These double properties represent the x and y coordinates of the ending point of the gradient.
     * - cycleMethod − This argument defines how the regions outside the color gradient bounds are defined by the starting
     *                 and ending points and how they should be filled.
     * - proportional − This is a Boolean Variable; on setting this property to true the start and end locations are set
     *                  to a proportion.
     * - Stops − This argument defines the color-stop points along the gradient line.
     *
     * Radial Gradient Pattern does not work with shapes that are non-circular; i.e., you can only apply radial gradient
     * on circular and elliptical shapes.
     */
    private void radialGradient() {
        Circle circle = new Circle();

        //Setting the properties of the circle
        circle.setCenterX(500.0f);
        circle.setCenterY(390.0f);
        circle.setRadius(75.0f);

        //Drawing a text
        Text text = new Text("Radial Gradient");

        //Setting the font of the text
        text.setFont(Font.font("Edwardian Script ITC", 50));

        //Setting the position of the text
        text.setX(410);
        text.setY(290);

        //Setting the radial gradient
        Stop[] stops = new Stop[] {
                new Stop(0.0, Color.WHITE),
                new Stop(0.3, Color.RED),
                new Stop(1.0, Color.DARKRED)
        };
        RadialGradient radialGradient =
                new RadialGradient(
                        0,
                        0,
                        500,
                        390,
                        60,
                        false,
                        CycleMethod.NO_CYCLE,
                        stops
                );

        //Setting the radial gradient to the circle and text
        circle.setFill(radialGradient);
        text.setFill(radialGradient);

        nodes.addAll(circle, text);
    }

    /**
     * A color gradient, in color science, is defined as the progression of colors depending on their positions. Hence,
     * a color gradient is also known as color ramp or color progression.
     *
     * in a linear gradient pattern, the colors are flowing in a single direction. Even if the shape to be coloured is
     * not linear, like a circle or an ellipse, the colors would still be arranged in one direction.
     *
     * Parameters
     * - startX, startY − These double properties represent the x and y coordinates of the starting point of the gradient.
     * - endX, endY − These double properties represent the x and y coordinates of the ending point of the gradient.
     * - cycleMethod − This argument defines how the regions outside the color gradient bounds, defined by the starting and ending points, should be filled.
     * - proportional − This is a Boolean Variable; on setting this property to true, the start and end locations are set to a proportion.
     * - Stops − This argument defines the color-stop points along the gradient line.
     */
    private void linearGradient() {
        Circle circle = new Circle();

        //Setting the properties of the circle
        circle.setCenterX(85.0f);
        circle.setCenterY(390.0f);
        circle.setRadius(75.0f);

        //Drawing a text
        Text text = new Text("Linear Gradient");

        //Setting the font of the text
        text.setFont(Font.font("Edwardian Script ITC", 55));

        //Setting the position of the text
        text.setX(20);
        text.setY(300);

        //Setting the linear gradient
        Stop[] stops = new Stop[] {
                new Stop(0, Color.DARKSLATEBLUE),
                new Stop(1, Color.DARKRED)
        };

        LinearGradient linearGradient =
                new LinearGradient(10, 315, 160, 465, false, CycleMethod.NO_CYCLE, stops);

//        LinearGradient linearGradient =
//                new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops);

        //Setting the linear gradient to the circle and text
        circle.setFill(linearGradient);
        text.setFill(linearGradient);

        nodes.addAll(circle, text);
        //====================== TRIANGLE =============================================
        Polygon triangle = new Polygon();

        triangle.getPoints().addAll(
                90.0, 500.0,
                160.0, 600.0,
                90.0, 700.0
        );

        linearGradient =
                new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops);


        triangle.setFill(linearGradient);

        nodes.add(triangle);
    }

    /**
     * We use the class "ImagePattern" it accepts the following parameters
     *
     * - Image − The object of the image using which you want to create the pattern.
     * - x and y − Double variables representing the (x, y) coordinates of origin of the anchor rectangle.
     * - height and width − Double variables representing the height and width of the image that is used to create a pattern.
     * - isProportional − This is a Boolean Variable; on setting this property to true, the start and end locations are set to be proportional.
     */
    private void imagePatternColoring() {
        Circle circle = new Circle();

        //Setting the properties of the circle
        circle.setCenterX(680.0f);
        circle.setCenterY(150.0f);
        circle.setRadius(75.0f);

        //Setting the image pattern
        String link = "https://www.101convert.com/img/app-icon/64/12605.png";

        Image image = new Image(link);
        ImagePattern imagePattern = new ImagePattern(image, 20, 20, 40, 40, false);

        //Setting color to the circle
        circle.setFill(imagePattern);

        //Setting the stroke width
        circle.setStrokeWidth(3);

        //Setting color to the stroke
        circle.setStroke(Color.DARKSLATEBLUE);

        //Drawing a text
        Text text = new Text("Image Pattern Coloring");

        //Setting the font of the text
        text.setFont(Font.font("Edwardian Script ITC", 82));

        //Setting the position of the text
        text.setX(400);
        text.setY(64);

        //Setting color to the text
        text.setFill(imagePattern);
        text.setStrokeWidth(1);
        text.setStroke(Color.DARKSLATEBLUE);

        nodes.addAll(text, circle);
    }

    private void uniformColorInTextAndShape() {
        Circle circle = new Circle();

        //Setting the properties of the circle
        circle.setCenterX(85.0f);
        circle.setCenterY(150.0f);
        circle.setRadius(75.0f);

        //Setting color to the circle
        circle.setFill(Color.DARKRED);

        //Setting the stroke width
        circle.setStrokeWidth(3);

        //Setting color to the stroke
        circle.setStroke(Color.DARKSLATEBLUE);

        //Drawing a text
        Text text = new Text("Uniform Coloring");

        //Setting the font of the text
        text.setFont(Font.font("Edwardian Script ITC", 64));

        //Setting the position of the text
        text.setX(20);
        text.setY(50);

        //Setting color to the text
        text.setFill(Color.RED);
        text.setStrokeWidth(1);
        text.setStroke(Color.DARKSLATEBLUE);

        nodes.addAll(text, circle);
    }
}
