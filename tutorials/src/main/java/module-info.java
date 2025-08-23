module tutorial.tutorial {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires java.desktop;


    opens tutorial.tutorial to javafx.fxml;
    exports tutorial.tutorial;
//    exports tutorial.tutorial.examples.vector;
    opens tutorial.tutorial.examples.vector to javafx.fxml;
//    exports tutorial.tutorial.examples.platformgame;
    opens tutorial.tutorial.examples.platformgame to javafx.fxml;
//    exports tutorial.tutorial.examples.vector;
//    opens tutorial.tutorial.examples.platformgame to javafx.fxml;
}