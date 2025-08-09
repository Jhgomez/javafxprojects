module tutorial.tutorial {
    requires javafx.controls;
    requires javafx.fxml;


    opens tutorial.tutorial to javafx.fxml;
    exports tutorial.tutorial;
}