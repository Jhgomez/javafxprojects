package tutorial.tutorial.examples.flappybirdgame;

import com.almasb.fxgl.texture.Texture;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

public class FXBirdGame {

    private IntegerProperty score = new SimpleIntegerProperty();
    private BooleanProperty running = new SimpleBooleanProperty();
    private Random random = new Random();

    private Point2D playerVelocity = new Point2D(0,0);

    private HashMap<KeyCode, Boolean> keys = new HashMap<>();

    private ArrayList<Node> walls = new ArrayList<>();

    private Scene scene;
    private Pane appRoot = new Pane();
    private Pane gameRoot = new Pane();

    private Node player;

    private Texture textureBird;
    private AudioClip audioShoot;

    // uiRoot
    protected void initUI() {
        Rectangle bg = new Rectangle();
        bg.setFill(Color.ORANGE);
        bg.widthProperty().bind(scene.widthProperty());
        bg.setHeight(600);

        Image playerImage = new Image(
                Objects.requireNonNull(getClass().getResource("/tutorial/tutorial/assets/textures/bird2.png")).toExternalForm(),
                35,
                35,
                true,
                true
        );

        player = new ImageView(playerImage);
        gameRoot.getChildren().add(player);

        // read level info, 1 is a platform, 0 is the avism
        for (int i = 0; i < 50; i++) {
            int height = random.nextInt(300);

            Node wall = new Rectangle(20, height);
            wall.setTranslateX(i * 200 + 700);

            Node wall2 = new Rectangle(20, 300 - height);
            wall2.setTranslateX(i * 200 + 700);
            wall2.setTranslateY(300 + height);

            walls.add(wall);
            walls.add(wall2);

            gameRoot.getChildren().addAll(wall, wall2);
        }

        final double lastPositionx = walls.getLast().getTranslateX();

        player.translateXProperty().addListener((obs, old, newValue) -> {
            int offset = newValue.intValue();

            if (offset > 600 && offset < lastPositionx - 600) {
                // this will affect all the nodes in the game
                // moving them to the left
                gameRoot.setLayoutX(-(offset - 600));
            }
        });

        appRoot.getChildren().addAll(bg, gameRoot);
    }

    protected void onUpdate() {
        fly(isPressed(KeyCode.W));

        movePlayerX();
    }

    private void movePlayerX() {
        for (Node platform : walls) {
            if (player.getBoundsInParent().intersects(platform.getBoundsInParent())) {
                gameRoot.setLayoutX(0);
                gameRoot.setLayoutY(0);
                player.setTranslateX(0);
                player.setTranslateY(0);
            }
        }

        player.setTranslateX(player.getTranslateX() + 2);
    }

    private boolean isPressed(KeyCode keyCode) {
        Boolean pressed = keys.get(keyCode);
        return pressed != null ? pressed : false;
    }

    private void fly(boolean isRising) {

        player.setTranslateY(player.getTranslateY() + (isRising ? -1 : 1));
    }

    public void displayScreen(Runnable runnable) {
        scene = new Scene(appRoot, 1200, 600);
        scene.setOnKeyPressed(event -> keys.put(event.getCode(), true));
        scene.setOnKeyReleased(event -> keys.put(event.getCode(), false));

        initUI();

        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();

        // by default gives us 60 frames per second, means this update method
        // will be called 60 times every second
        Timeline timer =  new Timeline(new KeyFrame(Duration.millis(10), e -> {
            onUpdate();
        }));

        timer.setCycleCount(Timeline.INDEFINITE);

        timer.play();

        stage.setOnCloseRequest(e -> {
            timer.stop();
            runnable.run();
        });
    }
}
