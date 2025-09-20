package tutorial.tutorial.examples.pacman;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;
import java.util.*;

public class Pacman {
    private Random random = new Random();

    private enum Type { WALL, PLAYER, ENEMY }

    private final List<Node> enemies = new ArrayList<>();
    private final List<Node> walls = new ArrayList<>();

    private enum Action {
        UP(0, -1),
        DOWN(0, 1),
        LEFT(-1, 0),
        RIGHT(1, 0),
        NONE(0, 0);

        final int dx, dy;

        Action (int dx, int dy) {
            this.dx = dx;
            this.dy = dy;
        }
    }

    private static final int BLOCK_SIZE = 40;
    private static final int ENTITY_SIZE = BLOCK_SIZE - 10;

    private List<String> levelData = new ArrayList<>();
    // represents the width and height of the level, width 15 tiles and height 15 tiles
    // we will give this array to the AI that helps us manage the enemies
    private AStarNode[][] aiGrid = new AStarNode[21][21];
    private final AStarLogic aStarLogic = new AStarLogic();

    // this class gives the path between AStarStart to AStarTarget
    private static class AStarLogic {}

    // This class help us mark the AStart path start and AStart path target
    private static class AStarNode {
        int x, y, hCost, value;

        public AStarNode(int x, int y, int hCost, int value) {
            this.x = x;
            this.y = y;
            this.hCost = hCost;
            this.value = value;
        }
    }

    private AStarNode start, target;

    private Scene scene;
    private Pane appRoot = new Pane();
    private Pane gameRoot = new Pane();

    private Node player, enemy1;

    private Action action = Action.NONE;
    private Action prevAction = Action.NONE;

    private Action randomAIAction = Action.NONE;
    // uiRoot
    protected void initUI() throws IOException {
        InputStream leveUrl = Objects.requireNonNull(getClass().getResourceAsStream("/tutorial/tutorial/pacman_levels.txt"));
        BufferedReader reader = new BufferedReader(new InputStreamReader(leveUrl));

        reader.lines().forEach(line -> levelData.add(line));

        appRoot.setStyle("-fx-background-color: black;");

        initLevel();
        initPlayer();
        initEnemies();

        appRoot.getChildren().addAll(gameRoot);
    }

    private void initPlayer() {
        player = new Rectangle(ENTITY_SIZE, ENTITY_SIZE, Color.BLUE);

        player.setLayoutX((10*BLOCK_SIZE) + 5);
        player.setLayoutY((8*BLOCK_SIZE) + 5);

        gameRoot.getChildren().add(player);
    }

    private void initLevel() {
        // this is the "width" in all lines of the level
        int w = levelData.getFirst().length();

        for (int i = 0; i < levelData.size(); i++) {
            String line = levelData.get(i);

            for (int j = 0; j < w; j++) {
                char c = line.charAt(j);

                if (c == '1') {
                    Node wall = new Rectangle(BLOCK_SIZE, BLOCK_SIZE, Color.GRAY);
                    wall.setTranslateX(j * BLOCK_SIZE);
                    wall.setTranslateY(i * BLOCK_SIZE);

                    walls.add(wall);
                    gameRoot.getChildren().add(wall);
                }

                // we give the AI a model of the world
                aiGrid[i][j] = new AStarNode(j, i, 0, c == '1' ? 1 : 0);
            }
        }
    }

    private void initEnemies() {
        enemy1 = new Rectangle(30, 30, Color.RED);
        enemy1.setTranslateX(45);
        enemy1.setTranslateY(45);

        Rectangle enemy2 = new Rectangle(30, 30, Color.GREEN);
        enemy2.setTranslateX(45);
        enemy2.setTranslateY(45);

        gameRoot.getChildren().addAll(enemy1, enemy2);
    }

    protected void onUpdate() {
        movePlayerX();
    }

    private void movePlayerX() {
        if (action == Action.NONE) {
            return;
        }

        double x = player.getTranslateX();
        double y = player.getTranslateY();

        player.setTranslateX(x + action.dx/10.0);
        player.setTranslateY(y + action.dy/10.0);

        boolean collision = walls.stream().anyMatch(node -> player.getBoundsInParent().intersects(node.getBoundsInParent()));

        if (collision) {
            player.setTranslateX(x);
            player.setTranslateY(y);

            action = prevAction;
            prevAction = Action.NONE;
        }
    }

    private void updateRandomAI() {
        if (randomAIAction == Action.NONE) {
            return;
        }

        double x = enemy1.getTranslateX();
        double y = enemy1.getTranslateY();

        enemy1.setTranslateX(x + randomAIAction.dx);
        enemy1.setTranslateY(y + randomAIAction.dy);

        boolean collision = walls.stream().anyMatch(wall -> enemy1.getBoundsInParent().intersects(wall.getBoundsInParent()));

        if (collision) {
            enemy1.setTranslateX(x);
            enemy1.setTranslateY(y);

            randomAIAction = Action.values()[random.nextInt(5)];

            while (randomAIAction == Action.NONE) {
                randomAIAction = Action.values()[random.nextInt(5)];
            }
        }
    }

    public void displayScreen(Runnable runnable) {
        // 600 because level is 21x21 tiles, 40 is the size of each tile
        int size = 21*BLOCK_SIZE;
        scene = new Scene(appRoot, size, size);
        scene.setOnKeyPressed(event -> {
            prevAction = action;

            if (event.getCode() == KeyCode.W) {
                action = Action.UP;
            } else if (event.getCode() == KeyCode.S) {
                action = Action.DOWN;
            } else if (event.getCode() == KeyCode.A) {
                action = Action.LEFT;
            } else if (event.getCode() == KeyCode.D) {
                action = Action.RIGHT;
            } else {
                action = Action.NONE;
            }
        });

//        scene.setOnKeyReleased(event -> action = Action.NONE);

        try {
            initUI();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();

        // by default gives us 60 frames per second, means this update method
        // will be called 60 times every second
        Timeline timer =  new Timeline(new KeyFrame(Duration.millis(1), e -> {
            onUpdate();
        }));

        timer.setCycleCount(Timeline.INDEFINITE);

        timer.play();

        Timeline randomAITimer =  new Timeline(new KeyFrame(Duration.millis(10), e -> {
            updateRandomAI();
        }));

        randomAITimer.setCycleCount(Timeline.INDEFINITE);

        randomAITimer.play();

        Timeline randomAIAction =  new Timeline(new KeyFrame(Duration.millis(1000), e -> {
            updateRandomAction();
        }));

        randomAIAction.setCycleCount(Timeline.INDEFINITE);

        randomAIAction.play();

        stage.setOnCloseRequest(e -> {
            timer.stop();
            randomAITimer.stop();
            randomAIAction.stop();

            runnable.run();
        });
    }

    private void updateRandomAction() {
        randomAIAction = Action.values()[random.nextInt(5)];

        while (randomAIAction == Action.NONE) {
            randomAIAction = Action.values()[random.nextInt(5)];
        }
    }
}
