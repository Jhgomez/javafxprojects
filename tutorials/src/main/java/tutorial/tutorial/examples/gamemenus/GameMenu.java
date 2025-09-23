package tutorial.tutorial.examples.gamemenus;

import javafx.scene.Scene;
import javafx.scene.layout.Region;

public interface GameMenu {
    Region getMenu(Scene scene);

    void clearResources();
}
