module fxlauncher.front {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires fxlauncher.lib;

    opens fxlauncher.front to javafx.fxml, javafx.graphics;

    exports fxlauncher.front;
}