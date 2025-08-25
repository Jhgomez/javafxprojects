package tutorial.tutorial.examples.geometrywars;

import com.almasb.fxgl.texture.Texture;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.*;
import java.util.stream.Stream;

public class WarsGame {

    private IntegerProperty score = new SimpleIntegerProperty();

    private Random random = new Random();

    private Point2D playerVelocity = new Point2D(0,0);
    private Point2D bulletsVelocity = new Point2D(0,0);

    private HashMap<KeyCode, Boolean> keys = new HashMap<>();
    private boolean isLeftMouseButtonPressed = false;

    private ArrayList<Node> enemies = new ArrayList<>();

    private ArrayList<Node> bullets = new ArrayList<>();

    Timeline enemiesTimeline = new Timeline();
    Timeline shootBulletTimeLine = new Timeline();

    private Pane appRoot = new Pane();
    private Pane gameRoot = new Pane();
    private Scene scene =  new Scene(appRoot, 1280, 720);

    private Node player;

    private boolean canShoot = true;
    private boolean isMouseDragging = false;

    private double mouseXPosition;
    private double mouseYPosition;

    // uiRoot
    protected void initUI() {
        // set up score text
        Text scoreText = new Text();
        scoreText.xProperty().bind(scene.widthProperty().add(-100));
        scoreText.setY(50);
        scoreText.textProperty().bind(score.asString());

        gameRoot.getChildren().add(scoreText);

        // set up keys and mouse events on the scene
        scene.setOnKeyPressed(event -> keys.put(event.getCode(), true));
        scene.setOnKeyReleased(event -> keys.put(event.getCode(), false));

        // this would be first mouse event called
        scene.setOnMousePressed(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                isLeftMouseButtonPressed = true;

                if (!isMouseDragging) {
                    mouseXPosition = event.getSceneX();
                    mouseYPosition = event.getSceneY();
                }
            }
        });

        // if mouse is dragged after it is pressed, this will be called
        scene.setOnMouseDragged(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                isMouseDragging = true;
                mouseXPosition = event.getSceneX();
                mouseYPosition = event.getSceneY();
            }
        });

        // this will be the last mouse callback
        scene.setOnMouseReleased(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                isLeftMouseButtonPressed = false;
                isMouseDragging = false;
            }
        });

        // create player
        player = new Rectangle(40, 40, Color.BLUE);
        player.setTranslateX(640);
        player.setTranslateY(360);
        gameRoot.getChildren().add(player);

        // start a thread that will create enemies every second
        enemiesTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(1000), event -> {
            spawnEnemy();
        }));

        enemiesTimeline.setCycleCount(Timeline.INDEFINITE);
        enemiesTimeline.play();

        // start a thread that lets shoot only every 0.25 seconds
        shootBulletTimeLine.getKeyFrames().add(new KeyFrame(Duration.millis(250), event -> {
            canShoot = true;
        }));

        shootBulletTimeLine.setCycleCount(Timeline.INDEFINITE);
        shootBulletTimeLine.play();

        appRoot.getChildren().addAll(gameRoot);
    }

    private void spawnEnemy() {
        Node enemy = new Circle(20, Color.RED);
        enemy.setTranslateX(random.nextInt(1200));
        enemy.setTranslateY(random.nextInt(700));

        enemy.getProperties().put("alive", true);

        enemies.add(enemy);
        gameRoot.getChildren().add(enemy);
    }

    private void onUpdateEnemies() {
        playerVelocity = new Point2D(player.getTranslateX(), player.getTranslateY());
        Point2D enemyPoint;

        for (Node node : enemies) {
            enemyPoint = playerVelocity
                    .subtract(node.getTranslateX(), node.getTranslateY())
                    .normalize()
                    .multiply(2);

            node.setTranslateX(node.getTranslateX() + enemyPoint.getX());
            node.setTranslateY(node.getTranslateY() + enemyPoint.getY());
        }
    }

    private void spawnBullet() {
        // center of player
        double playerCenterX = player.getTranslateX() + 20;
        double playerCenterY = player.getTranslateY() + 20;

        //
        bulletsVelocity = new Point2D(mouseXPosition, mouseYPosition)
                .subtract(playerCenterX, playerCenterY)
                .normalize()
                .multiply(10);

        Line bullet = new Line(playerCenterX, playerCenterY, playerCenterX + bulletsVelocity.getX(), playerCenterY + bulletsVelocity.getY());
        bullet.setStrokeWidth(1);
        bullet.getStrokeDashArray().addAll(3d);
        bullet.setStrokeDashOffset(1);

        bullet.getProperties().put("alive", true);
        bullet.getProperties().put("vector", bulletsVelocity);

        bullets.add(bullet);
        gameRoot.getChildren().add(bullet);

        bullet.toBack();
    }

    private void onUpdateBullets() {
        for (Node node : bullets) {
            Point2D bulletVector = (Point2D) node.getProperties().get("vector");

            node.setTranslateX(node.getTranslateX() + bulletVector.getX());
            node.setTranslateY(node.getTranslateY() + bulletVector.getY());
        }
    }

    protected void onUpdate() {
        if (isKeyPressed(KeyCode.W)) {
            movePlayer(0, -5);
        }

        if (isKeyPressed(KeyCode.A)) {
            movePlayer(-5, 0);
        }

        if (isKeyPressed(KeyCode.S)) {
            movePlayer(0, 5);
        }

        if (isKeyPressed(KeyCode.D)) {
            movePlayer(5, 0);
        }

        if (isLeftMouseButtonPressed && canShoot) {
            canShoot = false;
            spawnBullet();
        }

        for (Node enemy : enemies) {
            if (player.getBoundsInParent().intersects(enemy.getBoundsInParent())) {
                enemy.getProperties().put("alive", false);
                score.set(score.get() - 1000);
            }
        }

        for (Node bullet : bullets) {
            for (Node enemy : enemies) {
                if (enemy.getBoundsInParent().intersects(bullet.getBoundsInParent())) {
                    bullet.getProperties().put("alive", false);
                    enemy.getProperties().put("alive", false);
                    score.set(score.get() + 100);
                }
            }
        }

        List<Node> bulletsToDelete = bullets.stream().filter(bullet -> !(Boolean)bullet.getProperties().get("alive")).toList();
        List<Node> enemiesToDelete = enemies.stream().filter(enemy -> !(Boolean)enemy.getProperties().get("alive")).toList();

        enemies.removeIf(enemiesToDelete::contains);
        bullets.removeIf(bulletsToDelete::contains);

        for (Node bullet : bulletsToDelete) {
            gameRoot.getChildren().remove(bullet);
        }

        for (Node enemy : enemiesToDelete) {
            gameRoot.getChildren().remove(enemy);
        }
    }

    private void movePlayer(int x, int y) {
        player.setTranslateX(player.getTranslateX() + x);
        player.setTranslateY(player.getTranslateY() + y);

        for (Node enemy : enemies) {
            if (player.getBoundsInParent().intersects(enemy.getBoundsInParent())) {
                enemy.getProperties().put("alive", false);
                score.set(score.get() - 1000);
            }
        }
    }

    private boolean isKeyPressed(KeyCode keyCode) {
        Boolean pressed = keys.get(keyCode);
        return pressed != null ? pressed : false;
    }

    public void displayScreen(Runnable runnable) {
        initUI();

        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();

        Slider time = new Slider(0, 10, 10);
        time.setBlockIncrement(1);
        time.setShowTickLabels(true);
        time.setShowTickMarks(true);
        time.setMajorTickUnit(2);
        time.setMinorTickCount(1);

        Line bullet = new Line(50, 50, 500, 500);
//        bullet.setStrokeWidth(10);
        bullet.getStrokeDashArray().addAll(10d);
        bullet.strokeDashOffsetProperty().bind(time.valueProperty().multiply(20));

        // by default gives us 60 frames per second, means this update method
        // will be called 60 times every second
        Timeline timer =  new Timeline(new KeyFrame(Duration.millis(10), e -> {
            onUpdate();
        }));

        timer.setCycleCount(Timeline.INDEFINITE);

        timer.play();



        Timeline enemiesTimer =  new Timeline(new KeyFrame(Duration.millis(500), e -> {
            onUpdateEnemies();
            onUpdateBullets();
        }));

        enemiesTimer.setCycleCount(Timeline.INDEFINITE);

        enemiesTimer.play();

        time.valueProperty().addListener((observable, oldValue, newValue) -> {
            enemiesTimer.stop();
            enemiesTimer.getKeyFrames().clear();

            enemiesTimer.getKeyFrames().add(new KeyFrame(Duration.millis(newValue.doubleValue() * 10), e -> {
                onUpdateEnemies();
                onUpdateBullets();
            }));

            enemiesTimer.play();

            timer.stop();
            timer.getKeyFrames().clear();

            timer.getKeyFrames().add(new KeyFrame(Duration.millis(time.getValue() * 10), e -> {
                onUpdate();
            }));

            timer.play();
        });

        time.layoutXProperty().bind(appRoot.widthProperty().add(-200));
        time.setLayoutY(30);
        appRoot.getChildren().add(time);

        stage.setOnCloseRequest(e -> {
            timer.stop();
            runnable.run();
        });
    }
}
