package tutorial.tutorial;

import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
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

    private void uniformColorInTextAndShape() {
        //Drawing a Circle
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
