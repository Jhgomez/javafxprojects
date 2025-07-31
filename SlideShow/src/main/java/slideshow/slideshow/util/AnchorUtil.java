package slideshow.slideshow.util;

import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

public class AnchorUtil {
    
    public static void setAnchor(Node node, Double left, Double top, Double right, Double bottom) {
        AnchorPane.setLeftAnchor(node, left);
        AnchorPane.setTopAnchor(node, top);
        AnchorPane.setRightAnchor(node, right);
        AnchorPane.setBottomAnchor(node, bottom);
    }
}
