module basics.basics {
    requires javafx.controls;
    requires javafx.fxml;


    opens basics.basics to javafx.fxml;
    exports basics.basics;
}