package tutorial.tutorial.examples.geometrywars;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.*;

public class WarsGame {

    private IntegerProperty score = new SimpleIntegerProperty();

    private Random random = new Random();

    private HashMap<KeyCode, Boolean> keys = new HashMap<>();
    private boolean isLeftMouseButtonPressed = false;

    private ArrayList<Node> enemies = new ArrayList<>();
    private ArrayList<Node> bullets = new ArrayList<>();
    private ArrayList<Node> particles = new ArrayList<>();

    Timeline enemiesTimeline = new Timeline();
    Timeline shootBulletTimeLine = new Timeline();
    Timeline powerupSTimeLine = new Timeline();
    Timeline laserTimeLine = new Timeline();
    Timeline traceTimeLine = new Timeline();

    private Pane appRoot = new Pane();
    private Pane gameRoot = new Pane();
    private Scene scene =  new Scene(appRoot, 1280, 720);
    
//    private ObservableList<Node> gameNodes = gameRoot.getChildren();

    private Node player;

    private boolean canShoot = true;
    private boolean canShootLaser = false;
    private boolean isMouseDragging = false;

    private double mouseXPosition;
    private double mouseYPosition;

    private Text fpsText = new Text();

    private AudioClip soundShoot, soundPower, soundExplosion, soundLaserReady, soundLaserShoot;

    // uiRoot
    protected void initUI() {
        // add background
        Rectangle bg = new Rectangle(0, 0);
        bg.widthProperty().bind(scene.widthProperty());
        bg.heightProperty().bind(scene.heightProperty());

        gameRoot.getChildren().add(bg);

        // intialize audio objects
        soundShoot = new AudioClip(Objects.requireNonNull(getClass().getResource("/tutorial/tutorial/assets/sounds/shoot.wav")).toExternalForm());
        soundExplosion = new AudioClip(Objects.requireNonNull(getClass().getResource("/tutorial/tutorial/assets/sounds/explosion.wav")).toExternalForm());
        soundLaserReady = new AudioClip(Objects.requireNonNull(getClass().getResource("/tutorial/tutorial/assets/sounds/laser_ready.wav")).toExternalForm());
        soundLaserShoot = new AudioClip(Objects.requireNonNull(getClass().getResource("/tutorial/tutorial/assets/sounds/laser_shoot.wav")).toExternalForm());
        soundPower = new AudioClip(Objects.requireNonNull(getClass().getResource("/tutorial/tutorial/assets/sounds/powerup.wav")).toExternalForm());

        // set up score text
        Text scoreText = new Text();
        scoreText.xProperty().bind(scene.widthProperty().add(-100));
        scoreText.setY(100);
        scoreText.textProperty().bind(score.asString());
        scoreText.setFill(Color.WHITE);

        gameRoot.getChildren().add(scoreText);

        // set up keys and mouse events on the scene
        scene.setOnKeyPressed(event -> {
            if (event.getCode() != KeyCode.ESCAPE) {
                keys.put(event.getCode(), true);
//                gameRoot.fireEvent(new javafx.event.Event());
            }
        });
        scene.setOnKeyReleased(event -> keys.put(event.getCode(), false));

        // difference between keyTyped and keyPRessed is that keyPressed is captured while again
        // and again while the key is pressed while keyTyped is only triggered once, meaning
        // keyPressed is capturing holding a key
        scene.setOnKeyTyped(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                keys.put(event.getCode(), true);
            }
        });

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

        spawnPlayer();

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

        laserTimeLine.getKeyFrames().add(new KeyFrame(Duration.millis(5000), event -> {
            canShootLaser = true;
        }));

        shootBulletTimeLine.setCycleCount(Timeline.INDEFINITE);
        shootBulletTimeLine.play();

        appRoot.getChildren().addAll(gameRoot);
    }

    private void spawnPlayer() {
        // create player
        player = new Rectangle(40, 40, Color.BLUE);
        player.setTranslateX(640);
        player.setTranslateY(360);

        gameRoot.getChildren().add(player);

        player.addEventHandler(GameEvent.DEATH, event -> {
            event.callback.run();

            // `target` is the node that was assigned this handler, in this case it is "enemy", we could also get a
            // `source` which should be the Pane this node is living in
            Node player = (Node) event.getTarget();

            soundExplosion.play();

            playScoreAnimation(player, -1000);

            int middleX = scene.widthProperty().intValue() / 2;
            int middleY = scene.heightProperty().intValue() / 2;

            double newX = random.nextInt(scene.widthProperty().intValue());
            double newY = random.nextInt(scene.heightProperty().intValue());

            double maxX = scene.widthProperty().get() - 40;

            if (newX > maxX) {
                newX = maxX;
            }

            double maxY = scene.heightProperty().get() - 40 - 5;

            if (newY > maxY) {
                newY = maxY;
            }

            player.setTranslateX(newX);
            player.setTranslateY(newY);

            // we consume the event so it doesn't bubble to other nodes in the buildEventDispatchChain
            event.consume();
        });
    }

    private void spawnEnemy() {
        if (enemies.size() > 7) return;

        Node enemy = new Circle(20, Color.RED);
        enemy.setTranslateX(random.nextInt(1280));
        enemy.setTranslateY(random.nextInt(720));

        enemy.getProperties().put("alive", true);

        enemies.add(enemy);
        gameRoot.getChildren().add(enemy);

        enemy.addEventHandler(GameEvent.DEATH, event -> {
            event.callback.run();

            // `target` is the node that was assigned this handler, in this case it is "enemy", we could also get a
            // `source` which should be the Pane this node is living in
            Node deadEnemy = (Node) event.getTarget();

            gameRoot.getChildren().remove(deadEnemy);

            // we consume the event so it doesn't bubble to other nodes in the buildEventDispatchChain
            event.consume();
        });
    }

    private void updateEnemies() {
        // This is the player point so we can direct the enemies node to this coordinate
        Point2D playerVelocity = new Point2D(player.getTranslateX(), player.getTranslateY());
        Point2D enemyToPlayerVector;

        for (Node node : enemies) {
            enemyToPlayerVector = playerVelocity
                    .subtract(node.getTranslateX(), node.getTranslateY())
                    .normalize()
                    .multiply(2);

            node.setTranslateX(node.getTranslateX() + enemyToPlayerVector.getX());
            node.setTranslateY(node.getTranslateY() + enemyToPlayerVector.getY());
        }
    }

    private void spawnBullet() {
        // center of player, we will originate bullets from this coordinate
        double playerCenterX = player.getTranslateX() + 20;
        double playerCenterY = player.getTranslateY() + 20;

        // this is vector from
        Point2D bulletsOriginToMousePositionVector = new Point2D(mouseXPosition, mouseYPosition)
                .subtract(playerCenterX, playerCenterY)
                .normalize()
                .multiply(10);

        // We create the bullet with the right angle using just the vector position
        Line bullet = new Line(playerCenterX, playerCenterY, playerCenterX + bulletsOriginToMousePositionVector.getX(), playerCenterY + bulletsOriginToMousePositionVector.getY());
        bullet.setStroke(Color.WHITE);
        bullet.setStrokeWidth(1);
        bullet.getStrokeDashArray().addAll(3d);
        bullet.setStrokeDashOffset(1);

        bullet.getProperties().put("alive", true);
        bullet.getProperties().put("vector", bulletsOriginToMousePositionVector);

        bullets.add(bullet);
        gameRoot.getChildren().add(bullet);

//        bullet.toBack();

        soundShoot.play();
    }

    private void updateBullets() {
        for (Node node : bullets) {
            Point2D bulletVector = (Point2D) node.getProperties().get("vector");

            node.setTranslateX(node.getTranslateX() + bulletVector.getX());
            node.setTranslateY(node.getTranslateY() + bulletVector.getY());

            // JavaFx increases from 0 to an positive number in the XAXIS from left to right and in the YAXIS from top top
            // to bottom, no matter how you create your strings the maxY is always going to be the edge that is in the bottom direction
            // and the maxX will be the edge that is in right direction, this is why if I want to delete bullets that goes beyodn the
            // left limit I need to check the maxX, bullets beyond top limit I need to check for maxY, right limit I need to check
            // minX and bottom limit I need to check the minY

            boolean reachedLeftLimit = node.getBoundsInParent().getMaxX() <= 0;
            boolean reachedTopLimit = node.getBoundsInParent().getMaxY() <= 0;

            boolean reachedRightLimit = node.getBoundsInParent().getMinX() >= scene.widthProperty().get();
            boolean reachedBottomLimit = node.getBoundsInParent().getMinY() >= scene.heightProperty().get();

            if (reachedBottomLimit || reachedLeftLimit || reachedTopLimit || reachedRightLimit) {
                node.getProperties().put("alive", false);
            }
        }
    }

    protected void mainUpdate() {
        if (isKeyPressed(KeyCode.W)) {
            moveUp(-5);
        }

        if (isKeyPressed(KeyCode.A)) {
            moveLeft(-5);
        }

        if (isKeyPressed(KeyCode.S)) {
            moveDown(5);
        }

        if (isKeyPressed(KeyCode.D)) {
            moveRight(5);
        }

        if (isKeyPressed(KeyCode.SPACE)) {
//            spawnLaser();
        }

        if (isLeftMouseButtonPressed && canShoot) {
            canShoot = false;
            spawnBullet();
        }

        // we update(remove) enemies here so it has the same response time as the player
        for (Node enemy : enemies) {
            if (player.getBoundsInParent().intersects(enemy.getBoundsInParent())) {
                enemy.getProperties().put("alive", false);
//                score.set(score.get() - 1000);
                enemy.fireEvent(new GameEvent(GameEvent.DEATH, () -> {}));

                player.fireEvent(new GameEvent(GameEvent.DEATH, () -> {}));
            }
        }

        // we update(remove) bullets here so it has the same response time as the player
        for (Node bullet : bullets) {
            for (Node enemy : enemies) {
                if (enemy.getBoundsInParent().intersects(bullet.getBoundsInParent())) {
                    bullet.getProperties().put("alive", false);
                    enemy.getProperties().put("alive", false);
//                    score.set(score.get() + 100);

                    // I would remove from list here also but it would cause a ConcurrentModificationException
                    // so for that I will keep using the "alive" property and the event handler will automatically
                    // delete the node from the pane containing it and the node will be removed from the list of
                    // enemies somewhere else
                    enemy.fireEvent(new GameEvent(GameEvent.DEATH, () -> {
                        playScoreAnimation(enemy, 100);
                        playDeathAnimation(enemy);
                    }));

                    bullet.fireEvent(new GameEvent(GameEvent.DEATH, () -> {}));
                }
            }
        }

        List<Node> bulletsToDelete = bullets.stream().filter(bullet -> !(Boolean)bullet.getProperties().get("alive")).toList();
        List<Node> enemiesToDelete = enemies.stream().filter(enemy -> !(Boolean)enemy.getProperties().get("alive")).toList();

        enemies.removeAll(enemiesToDelete);
        bullets.removeAll(bulletsToDelete);

        gameRoot.getChildren().removeAll(bulletsToDelete);
//        gameRoot.getChildren().removeAll(enemiesToDelete);
//        enemies.removeIf(enemiesToDelete::contains);
//        bullets.removeIf(bulletsToDelete::contains);

//        for (Node bullet : bulletsToDelete) {
//            gameRoot.getChildren().remove(bullet);
//        }

//        for (Node enemy : enemiesToDelete) {
//            gameRoot.getChildren().remove(enemy);
//        }
    }

    private void playDeathAnimation(Node enemy) {
        // we don't do all angles to avoid loading too many particles
        for (int i = 0; i < 360; i = i + 16) {
            for (int j = 0; j <= 4; j++) {
                Circle particle = new Circle(2, Color.RED);
                particle.setTranslateX(Math.cos(i) * (j * 4 + 2) + enemy.getTranslateX());
                particle.setTranslateY(Math.sin(i) * (j * 4 + 2) + enemy.getTranslateY());

                Point2D vector = new Point2D(random.nextDouble() - 0.5, random.nextDouble() - 0.8).multiply(2);

                particle.getProperties().put("alive", true);
                particle.getProperties().put("vector", vector);

                particles.add(particle);
                appRoot.getChildren().add(particle);
            }
        }
    }

    private void updateParticles() {
        for (Node node : particles) {
            Point2D vector = (Point2D)node.getProperties().get("vector");

            node.getProperties().put("vector", vector.add(0, 0.05));

            node.setTranslateX(node.getTranslateX() + vector.getX());
            node.setTranslateY(node.getTranslateY() + vector.getY());

            // I could remove particles here directly
            if (node.getTranslateY() >= scene.heightProperty().get()) {
                node.getProperties().put("alive", false);
            }
        }

        // but I will remove it here
        List<Node> particlesToRemove = particles.stream().filter(p -> !(Boolean)p.getProperties().get("alive")).toList();
        particles.removeAll(particlesToRemove);
        appRoot.getChildren().removeAll(particlesToRemove);
    }

    private void playScoreAnimation(Node player, int score) {
        Text textScore = new Text(String.valueOf(score));
        textScore.setFill(Color.WHITE);
        textScore.setTranslateX(player.getTranslateX());
        textScore.setTranslateY(player.getTranslateY());

        gameRoot.getChildren().add(textScore);

        TranslateTransition tt = new TranslateTransition(Duration.millis(1500), textScore);
        tt.setToX(scene.widthProperty().add(-120).getValue());
        tt.setToY(100);
        tt.setOnFinished(event -> {
            gameRoot.getChildren().remove(textScore);
            this.score.set(this.score.get() + score);
        });
        tt.play();
    }

    private void moveLeft(int x) {
        // player.getTranslateX() could be substituted by player.getBoundsInParent().getMinX()
        boolean reachedLeftLimit = player.getTranslateX() <= 0;

        if (reachedLeftLimit) {
            return;
        }

        player.setTranslateX(player.getTranslateX() + x);
    }

    private void moveRight(int x) {
        boolean reachedRightLimit = player.getBoundsInParent().getMaxX() >= scene.widthProperty().get();

        if (reachedRightLimit) {
            return;
        }

        player.setTranslateX(player.getTranslateX() + x);
    }

    private void moveDown(int y) {
        boolean reachedBottomLimit = player.getBoundsInParent().getMaxY() >= scene.heightProperty().getValue();

        if (reachedBottomLimit) {
            return;
        }

        player.setTranslateY(player.getTranslateY() + y);
    }

    private void moveUp(int y) {
        boolean reachedTopLimit = player.getTranslateY() <= 0;

        if (reachedTopLimit) {
            return;
        }

        player.setTranslateY(player.getTranslateY() + y);
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

        Slider slider = new Slider(0, 100, 10);
        slider.setBlockIncrement(1);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit(10);
        slider.setMinorTickCount(5);

        // we could have managed all items in one thread but this we we leverage concurrency and make each type of element
        // move as much concurrently as possible

        // thread updating player position, dead bullets and dead enemies
        Timeline mainTimeLine =  new Timeline(new KeyFrame(Duration.millis(slider.getValue()*5), e -> {
            mainUpdate();
        }));
        mainTimeLine.setCycleCount(Timeline.INDEFINITE);
        mainTimeLine.play();

        // thread updating enemies position
        Timeline enemiesTimeLine =  new Timeline(new KeyFrame(Duration.millis(slider.getValue()*5), e -> {
            updateEnemies();
        }));
        enemiesTimeLine.setCycleCount(Timeline.INDEFINITE);
        enemiesTimeLine.play();

        // thread updating bullets position
        Timeline bulletsTimeLine =  new Timeline(new KeyFrame(Duration.millis(slider.getValue()*5), e -> {
            updateBullets();
        }));
        bulletsTimeLine.setCycleCount(Timeline.INDEFINITE);
        bulletsTimeLine.play();

        Timeline particlesTimeLine = new Timeline(new KeyFrame(Duration.millis(slider.getValue()*5), e -> {
            updateParticles();
        }));
        particlesTimeLine.setCycleCount(Timeline.INDEFINITE);
        particlesTimeLine.play();

        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            // reconfigure enemies timer
            enemiesTimeLine.stop();
            enemiesTimeLine.getKeyFrames().clear();
            enemiesTimeLine.getKeyFrames().add(new KeyFrame(Duration.millis(newValue.doubleValue() * 5), e -> {
                updateEnemies();
            }));
            enemiesTimeLine.play();

            // reconfigure bullets timer
            bulletsTimeLine.stop();
            bulletsTimeLine.getKeyFrames().clear();
            bulletsTimeLine.getKeyFrames().add(new KeyFrame(Duration.millis(newValue.doubleValue() * 5), e -> {
                updateBullets();
            }));
            bulletsTimeLine.play();

            // reconfigure main timer
            mainTimeLine.stop();
            mainTimeLine.getKeyFrames().clear();
            mainTimeLine.getKeyFrames().add(new KeyFrame(Duration.millis(newValue.doubleValue() * 5), e -> {
                mainUpdate();
            }));
            mainTimeLine.play();

            // reconfigure particles timer
            particlesTimeLine.stop();
            particlesTimeLine.getKeyFrames().clear();
            particlesTimeLine.getKeyFrames().add(new KeyFrame(Duration.millis(newValue.doubleValue() * 5), e -> {
                updateParticles();
            }));
            particlesTimeLine.play();
        });

        slider.layoutXProperty().bind(appRoot.widthProperty().subtract(200));
        slider.setLayoutY(30);
        appRoot.getChildren().add(slider);

        stage.setOnCloseRequest(e -> {
            mainTimeLine.stop();
            bulletsTimeLine.stop();
            enemiesTimeLine.stop();
            particlesTimeLine.stop();

            enemiesTimeline.stop();
            shootBulletTimeLine.stop();

            runnable.run();
        });
    }
}
