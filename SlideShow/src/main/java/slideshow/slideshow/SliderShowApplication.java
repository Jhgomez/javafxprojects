package slideshow.slideshow;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import slideshow.slideshow.slider.SliderShow;
import slideshow.slideshow.util.DraggableUtil;

import java.util.Objects;

public class SliderShowApplication extends Application {
    Stage stage;

    @Override
    public void start(Stage stage) {
        this.stage = stage;

        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResource("1.jpg")).toExternalForm()));
        SliderShow sliderShow = new  SliderShow();
        Image I1 = new Image(Objects.requireNonNull(getClass().getResource("1.jpg")).toExternalForm());
        Image I2 = new Image(Objects.requireNonNull(getClass().getResource("2.jpg")).toExternalForm());
        Image I3 = new Image(Objects.requireNonNull(getClass().getResource("3.jpg")).toExternalForm());
        Image I4 = new Image(Objects.requireNonNull(getClass().getResource("4.jpg")).toExternalForm());

        sliderShow.setImages(I1, I2, I3, I4);

        sliderShow.initSliderShow(2, 4);

        sliderShow.setPrefHeight(600);
        sliderShow.setPrefWidth(1024);

        this.stage.setScene(new Scene(sliderShow));

        DraggableUtil.setDraggable(sliderShow, this.stage);
        this.stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}