package tutorial.tutorial.examples.platformgame;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashMap;

public class PlatformGame {

    private HashMap<KeyCode, Boolean> keys = new HashMap<>();

    private ArrayList<Node> platforms = new ArrayList<>();

    private Pane appRoot = new Pane();
    private Pane gameRoot = new Pane();
    private Pane uiRoot = new Pane();

    private Node player;
    private Point2D playerVelocity = new Point2D(0,0);
    private boolean canJump = true;

    private int levelWidth;

    private void initContent() {
        // Multiply by 60 because everything in the game will be of 60 pixels wide and tall
        // except player, which will be 40
        levelWidth = LevelData.LEVEL1[0].length() * 60;

        Rectangle bg = new Rectangle(1200, 720);

        // read level info, 1 is a platform, 0 is the avism
        for (int i = 0; i < LevelData.LEVEL1.length; i++) {
            String line = LevelData.LEVEL1[i];
            for (int j = 0; j < line.length(); j++) {
                // if 0 we do nothing, if 1 we create a platform
                if (line.charAt(j) == '1') {
                        Node platform = createEntity(j*60, i*60, 60, 60, Color.BROWN);
                        platforms.add(platform);
                }
            }
        }

        // player is 40 pixels width and height
        player = createEntity(0,600,40,40, Color.BLUE);

        player.translateXProperty().addListener((obs, old, newValue) -> {
            int offset = newValue.intValue();

            if (offset > 600 && offset < levelWidth - 600) {
                // this will affect all the nodes in the game
                // moving them to the left
                gameRoot.setLayoutX(-(offset - 600));
            }
        });

        appRoot.getChildren().addAll(bg, gameRoot, uiRoot);
    }

    private void update() {
        if (isPressed(KeyCode.W) && player.getTranslateY() >= 5) {
            jumpPlayer();
        }

        if (isPressed(KeyCode.A) && player.getTranslateX() >= 5) {
            movePlayerX(-5);
        }

        if (isPressed(KeyCode.D) && player.getTranslateX() +40 <= levelWidth - 5) {
            movePlayerX(5);
        }

        // this will be "gravity", 10 is just basically a random number
        if (playerVelocity.getY() < 10) {
            // the 1 in the y axis would be an acceleration
            playerVelocity = playerVelocity.add(0,1);
        }

        movePlayerY((int)playerVelocity.getY());
    }

    // here we do some collision detection
    private void movePlayerX(int value) {
        boolean movingRight = value > 0;

        for (int i = 0; i <= Math.abs(value); i++) {
            for (Node platform : platforms) {
                if (player.getBoundsInParent().intersects(platform.getBoundsInParent())) {
                    if (movingRight) {
                        if (player.getTranslateX() + 40 == platform.getBoundsInParent().getMinX()) {
                            return;
                        }

                        // this else means player is moving left
                        // 60 is the width of the platform
                    } else if (player.getTranslateX() == platform.getTranslateX() + 60) {
                        return;
                    }
                }
            }

            player.setTranslateX(player.getTranslateX() + (movingRight ? 1 : -1));
        }
    }

    private void movePlayerY(int value) {
        boolean movingDown = value > 0;

        for (int i = 0; i <= Math.abs(value); i++) {
            for (Node platform : platforms) {
                if (player.getBoundsInParent().intersects(platform.getBoundsInParent())) {
                    if (movingDown) {
                        if (player.getTranslateY() + 40 == platform.getTranslateY()) {
                            player.setTranslateY(player.getTranslateY() - 1);
                            canJump = true;
                            return;
                        }

                        // this else means player is moving down by gravity
                        // 60 is the height of the platform
                    } else if (player.getTranslateY() == platform.getTranslateY() + 60) {
                        return;
                    }
                }
            }

            player.setTranslateY(player.getTranslateY() + (movingDown ? 1 : -1));
        }
    }

    private void jumpPlayer() {
        if (canJump) {
            playerVelocity = playerVelocity.add(0,-30);
            canJump = false;
        }
    }

    private Node createEntity(int x, int y, int w, int h, Color color) {
        Rectangle entity = new Rectangle(w, h);
        entity.setTranslateX(x);
        entity.setTranslateY(y);
        entity.setFill(color);

        gameRoot.getChildren().add(entity);
        return entity;
    }

    private boolean isPressed(KeyCode key) {
        return keys.getOrDefault(key, false);
    }

    public void displayScreen(Runnable runnable) {
        initContent();

        Scene scene = new Scene(appRoot);
        scene.setOnKeyPressed(event -> keys.put(event.getCode(), true));
        scene.setOnKeyReleased(event -> keys.put(event.getCode(), false));

        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();

        // by default gives us 60 frames per second, means this update method
        // will be called 60 times every second
        Timeline timer =  new Timeline(new KeyFrame(Duration.millis(10), e -> {
            update();
        }));

        timer.setCycleCount(Timeline.INDEFINITE);

        timer.play();

        stage.setOnCloseRequest(e -> {
            timer.stop();
            runnable.run();
        });
    }
}
