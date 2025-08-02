package slideshow.slideshow.util;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;

public class DraggableUtil {
    private static double x;
    private static double y;

    // this function lets us move the stage by tracking a click and drag on the node component
    // this means the node should be currently being rendered on the current scene that the stage is showing
    public static void setDraggable(Node node, Stage stage) {

        node.setOnMousePressed(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                x = event.getSceneX();
                y = event.getSceneY();
                stage.setOpacity(0.5);
            }
        });

        node.setOnMouseDragged(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                double newX = event.getScreenX() - x;
                double newY = event.getScreenY() - y;

                stage.setX(newX);
                stage.setY(newY);
            }
        });

        node.setOnMouseReleased(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                stage.setOpacity(1);
            }
        });
    }
}
