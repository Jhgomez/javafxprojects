package tutorial.tutorial.examples.geometrywars;

import javafx.animation.*;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.*;

public class WarsGame {

    private IntegerProperty score = new SimpleIntegerProperty();
//    private BooleanProperty laserAlive = new SimpleBooleanProperty(false);
    private Rectangle laserBar = new Rectangle(0, 20);

    private Random random = new Random();

    private HashMap<KeyCode, Boolean> keys = new HashMap<>();
    private boolean isLeftMouseButtonPressed = false;

    private final ArrayList<Node> enemies = new ArrayList<>();
    private final ArrayList<Node> bullets = new ArrayList<>();
    private final ArrayList<Node> particles = new ArrayList<>();
    private final List<Node> powerUps = new ArrayList<>();

    Timeline spanwEnemyTimeline = new Timeline();
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

    private Line laser;

    private Text fpsText = new Text();

    private AudioClip soundShoot, soundPower, soundExplosion, soundLaserReady, soundLaserShoot;

    private final long[] frameTimes = new long[100];
    private int frameTimeIndex = 0 ;
    private boolean arrayFilled = false ;

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

        // set up laser text
        Text laserText = new Text("SPACE");
        laserText.setFont(Font.font(18));
        laserText.visibleProperty().bind(laserBar.widthProperty().greaterThanOrEqualTo(100.0));
        laserText.visibleProperty().addListener((obs, old, newVal) -> {
            if (newVal.booleanValue()) {
                soundLaserReady.play();
            }
        });

        laserBar.setFill(Color.YELLOWGREEN);

        StackPane stack = new StackPane();
        stack.setTranslateX(50);
        stack.setTranslateY(50);

        stack.setAlignment(Pos.CENTER);

        stack.getChildren().addAll(laserBar, laserText);

        gameRoot.getChildren().add(stack);

        fpsText.setX(50);
        fpsText.setY(100);
        fpsText.setFill(Color.WHITE);

        gameRoot.getChildren().add(fpsText);

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
        spanwEnemyTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(1000), event -> {
            spawnEnemy();
        }));

        spanwEnemyTimeline.setCycleCount(Timeline.INDEFINITE);
        spanwEnemyTimeline.play();

        // start a thread that lets shoot only every 0.25 seconds
        shootBulletTimeLine.getKeyFrames().add(new KeyFrame(Duration.millis(250), event -> {
            canShoot = true;
        }));

        shootBulletTimeLine.setCycleCount(Timeline.INDEFINITE);
        shootBulletTimeLine.play();

        // this runs every 5 seconds basically, since 10 milliseconds by 500 repetitions is 5000ms == 5s
        laserTimeLine.getKeyFrames().add(new KeyFrame(Duration.millis(10), event -> {
            if (laserText.visibleProperty().not().get()) {
                laserBar.widthProperty().set(laserBar.widthProperty().add(0.2).get());
            }
        }));

        laserTimeLine.setCycleCount(Timeline.INDEFINITE);
        laserTimeLine.play();

        //
        traceTimeLine.getKeyFrames().add(new KeyFrame(Duration.millis(100), event -> {
            spawnTrace();
        }));

        traceTimeLine.setCycleCount(Timeline.INDEFINITE);
        traceTimeLine.play();

        //
        powerupSTimeLine.getKeyFrames().add(new KeyFrame(Duration.seconds(5), event -> {
            spawnPowerup();
        }));

        powerupSTimeLine.setCycleCount(Timeline.INDEFINITE);
        powerupSTimeLine.play();

        AnimationTimer frameRateMeter = new AnimationTimer() {

            @Override
            public void handle(long now) {
                long oldFrameTime = frameTimes[frameTimeIndex] ;
                frameTimes[frameTimeIndex] = now ;
                frameTimeIndex = (frameTimeIndex + 1) % frameTimes.length ;
                if (frameTimeIndex == 0) {
                    arrayFilled = true ;
                }
                if (arrayFilled) {
                    long elapsedNanos = now - oldFrameTime ;
                    long elapsedNanosPerFrame = elapsedNanos / frameTimes.length ;
                    double frameRate = 1_000_000_000.0 / elapsedNanosPerFrame ;
                    fpsText.setText(String.format("Current frame rate: %.3f", frameRate));
                }
            }
        };

        frameRateMeter.start();

        appRoot.getChildren().addAll(gameRoot);
    }

    private void spawnPowerup() {
        Image powerup = new Image(Objects.requireNonNull(getClass().getResource("/tutorial/tutorial/assets/textures/powerup_0" + random.nextInt(1,7) + ".png")).toExternalForm());
        ImageView powerupImageView = new ImageView(powerup);

        powerupImageView.getProperties().put("alive", true);

        double width = powerup.getWidth() / 8;

        final double[] minX = {width};

        powerupImageView.setViewport(new Rectangle2D(0, 0, width, powerup.getHeight()));

        Timeline powerUpTimeline = new Timeline();

        powerUpTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(60),  event -> {
            Rectangle2D rectangle = new Rectangle2D(minX[0], 0, width, powerup.getHeight());
            powerupImageView.setViewport(rectangle);

            if (minX[0] + width >= powerup.getWidth()) {
                minX[0] = 0;
            } else {
                minX[0] +=  width;
            }
        }));

        powerUpTimeline.setCycleCount(Timeline.INDEFINITE);
        powerUpTimeline.play();

        powerupImageView.setTranslateX(random.nextInt(scene.widthProperty().intValue()));
        powerupImageView.setTranslateY(random.nextInt(scene.heightProperty().intValue()));

        gameRoot.getChildren().add(powerupImageView);
        powerUps.add(powerupImageView);

        powerupImageView.addEventHandler(GameEvent.DEATH, event -> {
            powerUpTimeline.stop();

            Node powerUp = (Node) event.getTarget();

            soundPower.play();

            ScaleTransition st = new ScaleTransition(Duration.millis(1000), powerUp);
            st.setFromX(1);
            st.setFromY(1);
            st.setToX(0);
            st.setToY(0);

            st.setOnFinished(stEvent -> {
                gameRoot.getChildren().remove(powerUp);
            });

            st.play();

            playScoreAnimation(powerUp, 2000);

            event.consume();
        });
    }

    private void spawnTrace() {
        Rectangle trace = new  Rectangle(40, 40);
        trace.setTranslateX(player.getTranslateX());
        trace.setTranslateY(player.getTranslateY());

        trace.setStroke(Color.BLUE);
        trace.setFill(null);

        gameRoot.getChildren().add(trace);

        FadeTransition ft = new FadeTransition(Duration.millis(1000), trace);
        ft.setFromValue(1);
        ft.setToValue(0);
        ft.setOnFinished(event -> gameRoot.getChildren().remove(trace));
        ft.play();
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

    private void updateEnemiesPosition() {
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

        bullet.addEventHandler(GameEvent.DEATH, event -> {
            event.callback.run();

            Node deadBullet = (Node) event.getTarget();

            gameRoot.getChildren().remove(deadBullet);
        });

//        bullet.toBack();

        soundShoot.play();
    }

    private void updateBulletsPosition() {
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

    private void spawnLaser() {
        // only true after bar has grown to 100 or greater and as commented above it only happens every 5 seconds aprox
        if (laserBar.widthProperty().greaterThanOrEqualTo(100.0).not().get() || laser != null) return;

        double halfWidth = + player.getBoundsInParent().getWidth() / 2;

        double centerX = player.getTranslateX() + halfWidth;
        double centerY = player.getTranslateY() + halfWidth;

        laser = new Line(
                0,
                0,
                0,
                -(centerY + scene.widthProperty().get())
        );

        laser.setTranslateX(centerX);
        laser.setTranslateY(centerY);

        laser.setStrokeWidth(2);
        laser.setStroke(Color.YELLOW);

        gameRoot.getChildren().add(laser);

        Timeline rotateTranslate = new Timeline();

        int[] degree = {1};

        Rotate rotation = new Rotate();

        laser.getTransforms().add(rotation);

        laser.setRotationAxis(new Point3D(player.getTranslateX() + halfWidth, player.getTranslateY() + halfWidth, player.getTranslateZ()));
        rotateTranslate.getKeyFrames().add(new KeyFrame(Duration.millis(2.78), event -> {
            laser.setTranslateX(player.getTranslateX() + halfWidth);
            laser.setTranslateY(player.getTranslateY() + halfWidth);

            rotation.angleProperty().set(degree[0]);

            degree[0]++;

        }));

        rotateTranslate.setOnFinished(event -> {
            gameRoot.getChildren().remove(laser);
            laser = null;

            laserBar.widthProperty().set(0);
        });

        rotateTranslate.setCycleCount(360);

        rotateTranslate.play();

        soundLaserShoot.play();
    }

    protected void powerUpsUpdate() {
        // we update(remove) enemies here so it has the same response time as the player
        for (Node powerUp : powerUps) {
            if (player.getBoundsInParent().intersects(powerUp.getBoundsInParent())) {
                powerUp.getProperties().put("alive", false);
//                score.set(score.get() - 1000);
                powerUp.fireEvent(new GameEvent(GameEvent.DEATH, () -> {}));
            }
        }

        List<Node> powerUpsToDelete = powerUps.stream().filter(p -> !(Boolean)p.getProperties().get("alive")).toList();
        powerUps.removeAll(powerUpsToDelete);
    }

    protected void enemiesUpdate() {
        // we update(remove) enemies here so it has the same response time as the player
        for (Node enemy : enemies) {
            if (player.getBoundsInParent().intersects(enemy.getBoundsInParent())) {
                enemy.getProperties().put("alive", false);
//                score.set(score.get() - 1000);
                enemy.fireEvent(new GameEvent(GameEvent.DEATH, () -> {}));

                player.fireEvent(new GameEvent(GameEvent.DEATH, () -> {}));
            }

            if (laser != null && laser.getBoundsInParent().intersects(enemy.getBoundsInParent())) {
                enemy.getProperties().put("alive", false);
                enemy.fireEvent(new GameEvent(GameEvent.DEATH, () -> {
                    playScoreAnimation(enemy, 100);
                    playDeathAnimation(enemy);
                }));
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

//        gameRoot.getChildren().removeAll(bulletsToDelete);
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

    private void playerUpdate() {
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
            spawnLaser();
        }

        if (isLeftMouseButtonPressed && canShoot) {
            canShoot = false;
            spawnBullet();
        }
    }

    private void playDeathAnimation(Node enemy) {
        // we don't do all angles to avoid loading too many particles
        for (int i = 0; i < 360; i = i + random.nextInt(17, 22)) {
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

        // thread updating powerups
        Timeline powerTimeLine =  new Timeline(new KeyFrame(Duration.millis(slider.getValue()*5), e -> {
            powerUpsUpdate();
        }));
        powerTimeLine.setCycleCount(Timeline.INDEFINITE);
        powerTimeLine.play();

        // thread updating nodes(player, bullets) that collision with enemies
        Timeline enemiesTimeLine =  new Timeline(new KeyFrame(Duration.millis(slider.getValue()*5), e -> {
            enemiesUpdate();
        }));
        enemiesTimeLine.setCycleCount(Timeline.INDEFINITE);
        enemiesTimeLine.play();

        // thread updating player position
        Timeline playerTimeLine =  new Timeline(new KeyFrame(Duration.millis(slider.getValue()*5), e -> {
            playerUpdate();
        }));
        playerTimeLine.setCycleCount(Timeline.INDEFINITE);
        playerTimeLine.play();

        // thread updating enemies position
        Timeline enemiesPositionTimeLine =  new Timeline(new KeyFrame(Duration.millis(slider.getValue()*5), e -> {
            updateEnemiesPosition();
        }));
        enemiesPositionTimeLine.setCycleCount(Timeline.INDEFINITE);
        enemiesPositionTimeLine.play();

        // thread updating bullets position
        Timeline bulletsPositionTimeLine =  new Timeline(new KeyFrame(Duration.millis(slider.getValue()*5), e -> {
            updateBulletsPosition();
        }));
        bulletsPositionTimeLine.setCycleCount(Timeline.INDEFINITE);
        bulletsPositionTimeLine.play();

        Timeline particlesTimeLine = new Timeline(new KeyFrame(Duration.millis(slider.getValue()*5), e -> {
            updateParticles();
        }));
        particlesTimeLine.setCycleCount(Timeline.INDEFINITE);
        particlesTimeLine.play();

        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            // reconfigure powerups timer
            powerTimeLine.stop();
            powerTimeLine.getKeyFrames().clear();
            powerTimeLine.getKeyFrames().add(new KeyFrame(Duration.millis(newValue.doubleValue() * 5), e -> {
                powerUpsUpdate();
            }));
            powerTimeLine.play();

            // reconfigure player timer
            playerTimeLine.stop();
            playerTimeLine.getKeyFrames().clear();
            playerTimeLine.getKeyFrames().add(new KeyFrame(Duration.millis(newValue.doubleValue() * 5), e -> {
                playerUpdate();
            }));
            playerTimeLine.play();

            // reconfigure enemies timer
            enemiesPositionTimeLine.stop();
            enemiesPositionTimeLine.getKeyFrames().clear();
            enemiesPositionTimeLine.getKeyFrames().add(new KeyFrame(Duration.millis(newValue.doubleValue() * 5), e -> {
                updateEnemiesPosition();
            }));
            enemiesPositionTimeLine.play();

            // reconfigure bullets timer
            bulletsPositionTimeLine.stop();
            bulletsPositionTimeLine.getKeyFrames().clear();
            bulletsPositionTimeLine.getKeyFrames().add(new KeyFrame(Duration.millis(newValue.doubleValue() * 5), e -> {
                updateBulletsPosition();
            }));
            bulletsPositionTimeLine.play();

            // reconfigure main timer
            enemiesTimeLine.stop();
            enemiesTimeLine.getKeyFrames().clear();
            enemiesTimeLine.getKeyFrames().add(new KeyFrame(Duration.millis(newValue.doubleValue() * 5), e -> {
                enemiesUpdate();
            }));
            enemiesTimeLine.play();

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
            enemiesTimeLine.stop();
            bulletsPositionTimeLine.stop();
            enemiesPositionTimeLine.stop();
            particlesTimeLine.stop();
            powerTimeLine.stop();
            playerTimeLine.stop();

            spanwEnemyTimeline.stop();
            shootBulletTimeLine.stop();
            powerupSTimeLine.stop();
            laserTimeLine.stop();
            traceTimeLine.stop();

            runnable.run();
        });
    }
}
