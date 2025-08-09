package tutorial.tutorial;

import javafx.scene.Node;
import javafx.scene.input.MouseButton;

public class DragUtil {
    private static double currentX;
    private static double clickSceneX;
    private static double currentY;
    private static double clickSceneY;

    public static void setDraggable(Node node) {
        node.setOnMousePressed(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                currentX = node.getLayoutX();
                clickSceneX = event.getSceneX();
                currentY = node.getLayoutY();
                clickSceneY = event.getSceneY();
            }
        });

        node.setOnMouseDragged(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                double newX = currentX + (event.getSceneX() - clickSceneX);
                double newY = currentY + (event.getSceneY() - clickSceneY);

                node.setLayoutX(newX);
                node.setLayoutY(newY);
            }
        });
    }
}
