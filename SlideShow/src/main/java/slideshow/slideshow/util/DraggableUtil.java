package slideshow.slideshow.util;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;

public class DraggableUtil {
    private static double x;
    private static double y;

    public static void setDraggable(Node node, Stage scene) {

        node.setOnMousePressed(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                x = event.getSceneX();
                y = event.getSceneY();
            }
        });

        node.setOnMouseDragged(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                double newX = event.getScreenX() - x;
                double newY = event.getScreenY() - y;

                scene.setX(newX);
                scene.setY(newY);
            }
        });
    }
}
