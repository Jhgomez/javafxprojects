module sankeyplot {
    requires javafx.controls;
    requires javafx.fxml;

    opens eu.hansolo.fx.sankeyplot to javafx.fxml;
    exports eu.hansolo.fx.sankeyplot;
}