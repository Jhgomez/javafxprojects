package tutorial.tutorial;

import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
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

        Scene scene = new Scene(group, 1500, 740);

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
        circle.setCenterY(10.0f);
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
