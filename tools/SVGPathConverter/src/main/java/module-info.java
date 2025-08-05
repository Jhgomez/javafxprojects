module slideshow.svgpathconverter {
    requires javafx.controls;
    requires javafx.fxml;


    opens slideshow.svgpathconverter to javafx.fxml;
    exports slideshow.svgpathconverter;
}