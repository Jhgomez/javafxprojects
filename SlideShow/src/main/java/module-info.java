module slideshow.slideshow {
    requires javafx.controls;
    requires javafx.fxml;


    opens slideshow.slideshow to javafx.fxml;
    exports slideshow.slideshow;
}