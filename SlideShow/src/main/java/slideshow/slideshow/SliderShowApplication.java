package slideshow.slideshow;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import slideshow.slideshow.slider.SliderShow;
import java.util.Objects;

public class SliderShowApplication extends Application {
    @Override
    public void start(Stage stage) {
        SliderShow sliderShow = new  SliderShow();
        Image I1 = new Image(Objects.requireNonNull(getClass().getResource("1.jpg")).toExternalForm());
        Image I2 = new Image(Objects.requireNonNull(getClass().getResource("2.jpg")).toExternalForm());
        Image I3 = new Image(Objects.requireNonNull(getClass().getResource("3.jpg")).toExternalForm());
        Image I4 = new Image(Objects.requireNonNull(getClass().getResource("4.jpg")).toExternalForm());

        sliderShow.setImages(I1, I2, I3, I4);

        sliderShow.initSliderShow(2, 4);

        sliderShow.setPrefHeight(600);
        sliderShow.setPrefWidth(1024);

        stage.setScene(new Scene(sliderShow));
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}