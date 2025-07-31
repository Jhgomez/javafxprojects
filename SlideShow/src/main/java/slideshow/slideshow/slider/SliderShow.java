package slideshow.slideshow.slider;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.util.Duration;
import slideshow.slideshow.util.AnchorUtil;

public class SliderShow extends AnchorPane {
    private static FadeTransition transition;
    private final StackPane stackPane = new StackPane();
    private final AnchorPane backPane = new AnchorPane();
    private final AnchorPane frontPane = new AnchorPane();
    private final ObservableList<Image> backImages = FXCollections.observableArrayList();
    private final ObservableList<Image> frontImages = FXCollections.observableArrayList();
    private int frontIndex = 0;
    private int backIndex = 0;

    public SliderShow() {
        initialize();
    }

    private void initialize() {
        this.stackPane.getChildren().addAll(backPane, frontPane);
        frontPane.toFront();
        frontPane.setOpacity(0);
        backPane.toBack();

        AnchorUtil.setAnchor(stackPane, 0.0, 0.0, 0.0, 0.0);
        getChildren().add(stackPane);
    }

    public void setImages(Image... images) {
        if (images.length > 3) {
            for (int i = 0; i < images.length; i++) {
                if (i%2 == 0) {
                    frontImages.add(images[i]);
                } else {
                    backImages.add(images[i]);
                }
            }

            setBackgroundImage(backPane, backImages.getFirst());
            setBackgroundImage(frontPane, frontImages.getFirst());
        }
    }

    public void initSliderShow(int animateDelay, int visibilityDelay) {
        Runnable rn = () -> {
            Platform.runLater(() -> {
                frontPane.opacityProperty().addListener((observable, oldValue, newValue) -> {
                    PauseTransition pt;

                    if (newValue.doubleValue() == 0.0) {
                        frontIndex++;

                        if (frontIndex == frontImages.size()) {
                            frontIndex = 0;
                        }

                        setBackgroundImage(frontPane, frontImages.get(frontIndex));

                        pt = new PauseTransition(Duration.seconds(visibilityDelay));

                        pt.setOnFinished(event -> {
                            transition.play();
                        });

                        transition.pause();
                        pt.play();
                    } else if (newValue.doubleValue() == 1.0) {
                        backIndex++;

                        if (backIndex == backImages.size()) {
                            backIndex = 0;
                        }

                        setBackgroundImage(backPane, backImages.get(backIndex));

                        pt = new PauseTransition(Duration.seconds(visibilityDelay));

                        pt.setOnFinished(event -> {
                            transition.play();
                        });

                        transition.pause();
                        pt.play();
                    }
                });

                transition = new FadeTransition(Duration.seconds(animateDelay), frontPane);
                transition.setFromValue(0);
                transition.setToValue(1);
                transition.setAutoReverse(true);
                transition.setCycleCount(-1);
                transition.play();
            });
        };

        Thread.ofVirtual().name("InitSlider").start(rn);
    }

    public synchronized void stop() {
        if (transition != null) {
            transition.stop();
        }
    }

    private void setBackgroundImage(AnchorPane target, Image image) {
//        BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, false
        BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, true, true);
        BackgroundImage backgroundImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, backgroundSize);
        Background background = new Background(backgroundImage);
        target.setBackground(background);
    }

}
