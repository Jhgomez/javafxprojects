module cardnav {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.material;

    opens eu.hansolo.fx.cardnav to javafx.fxml;
    exports eu.hansolo.fx.cardnav;
}