module fxlauncher.front {
    requires javafx.controls;
    requires javafx.fxml;
    requires fxlauncher.lib;
    requires javafx.graphics;


    opens fxlauncher.front to javafx.fxml, javafx.graphics;

    exports fxlauncher.front;
}