module tutorial.tutorial {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires java.desktop;


    opens tutorial.tutorial to javafx.fxml;
    exports tutorial.tutorial;
}