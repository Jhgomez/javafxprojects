module funmenu {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.kordamp.ikonli.materialdesign;
    requires org.kordamp.ikonli.material;
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;

    opens eu.hansolo.fx.funmenu to javafx.fxml;
    exports eu.hansolo.fx.funmenu;
}