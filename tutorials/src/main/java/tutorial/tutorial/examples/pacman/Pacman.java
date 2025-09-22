package tutorial.tutorial.examples.pacman;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.geom.Point2D;
import java.io.*;
import java.util.*;

public class Pacman {
    private Random random = new Random();
    private AStarNode path;

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
    // represents the width and height of the level, width 21 tiles and height 21 tiles
    // we will give this map to the AI that helps us manage the enemies
    private final HashMap<Integer, Heuristic> aiGrid = new HashMap<>();
    private final AStarLogic ai = new AStarLogic();

    // this class gives the path between AStarStart to AStarTarget
    private class AStarLogic {

        ///  The idea of this method is to bring the enemy to the player through the shortest path. Means the start is the
        /// player and the target is the enemy, do it this way will help us walk the tree while saving the computation of
        /// having to go up the tree again and walk it down again as this is what we would have to do if the enemy would be
        /// the start and the player the target
        public AStarNode getPath(HashMap<Integer, Heuristic> heuristicMap, int startX, int startY, int targetX, int targetY) {
            Queue<AStarNode> succerors = new PriorityQueue<>();

            HashMap<Integer, AStarNode> visited = new HashMap<>();

            List<AStarNode> path = new ArrayList<>();

            AStarNode current = new AStarNode(
                    startY * 100 + startX,
                    startX,
                    startY,
                    0,
                    0,
                    null
            );

            while (current != null) {
                visited.put(current.id, current);

                if (current.x == targetX && current.y == targetY) {
                    return current;
                }

                generateSuccesors(succerors, current);

                current = succerors.poll();
            }

            return null;
        }

        private void generateSuccesors(Queue<AStarNode> succerors, AStarNode current) {
            var movementLevel = current.movementLevel + 1;

            // we don't allow more than 63 movements, that should not be possible as our grid is 21x21
            if (movementLevel >= 50) {
                throw new IllegalStateException("Tree might be looping indefinitely");
            }

            var rightId = current.y * 100 + (current.x + 1);
            var rightHeuristic = aiGrid.get(rightId);

            if (!rightHeuristic.isWall) {
                var right = new AStarNode(
                        rightId,
                        current.x + 1,
                        current.y,
                        rightHeuristic.hCost + movementLevel,
                        movementLevel,
                        current
                );

                succerors.add(right);
            }

            var leftId = current.y * 100 + (current.x - 1);
            var leftHeuristic = aiGrid.get(leftId);

            if (!leftHeuristic.isWall) {
                var left = new AStarNode(
                        leftId,
                        current.x - 1,
                        current.y,
                        leftHeuristic.hCost + movementLevel,
                        movementLevel,
                        current
                );

                succerors.add(left);
            }

            var topId = (current.y - 1) * 100 + current.x;
            var topHeuristic = aiGrid.get(topId);

            if (!topHeuristic.isWall) {
                var top = new AStarNode(
                        topId,
                        current.x,
                        current.y - 1,
                        topHeuristic.hCost + movementLevel,
                        movementLevel,
                        current
                );

                succerors.add(top);
            }

            var bottomId = (current.y + 1) * 100 + current.x;
            var bottomHeuristic = aiGrid.get(topId);

            if (!bottomHeuristic.isWall) {
                var bottom = new AStarNode(
                        bottomId,
                        current.x,
                        current.y + 1,
                        bottomHeuristic.hCost + movementLevel,
                        movementLevel,
                        current
                );

                succerors.add(bottom);
            }


        }
    }

    // This class help us mark the AStart path start and AStart path target
    private static class AStarNode implements Comparable<AStarNode> {
        int id, x, y, movementLevel;

        // in this A* algorithm we will use the heuristic(distance(x, y) from node to target) plus the number of movements
        // to calculate the cost of each node
        int cost;

        AStarNode parent;

        public AStarNode(int id, int x, int y, int cost, int movementLevel, AStarNode parent) {
            this.id = id;
            this.x = x;
            this.y = y;
            this.cost = cost;
            this.movementLevel = movementLevel;
            this.parent = parent;
        }

        @Override
        public int compareTo(AStarNode o) {
            return o.cost - this.cost;
        }
    }

    /// This class could have been created just as a map but to make it more readable we will make it an object
    /// and wrap it inside a map to be able to search in it easily and efficiently. This class basically represents
    /// a map of the world and just identifies the tiles/blocks in the world that are walls and also free space
    private static class Heuristic {
        int id;
        boolean isWall;
        int hCost; // heuristic will be "Manhattan distance"

        /// @param id       this id is calculated by the line and column values of the characters that draw the world
        /// @param isWall   when the map finds a '1' it represents a wall, and '0' represents space to move around
        public Heuristic(int id, boolean isWall) {
            this.id = id;
            this.isWall = isWall;
        }
    }

//    private int startX, startY, targetX, targetY;
    private boolean shouldWalkPath = true;

    private Scene scene;
    private Pane appRoot = new Pane();
    private Pane gameRoot = new Pane();

    private Node player, enemy1, enemy2;

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

        gameRoot.getChildren().add(player);

        player.setTranslateX((10*BLOCK_SIZE) + 5);
        player.setTranslateY((8*BLOCK_SIZE) + 5);
    }

    private void initLevel() {
        // this is the "width" in all lines of the level
        int w = levelData.getFirst().length();

        for (int i = 0; i < levelData.size(); i++) {
            String line = levelData.get(i);
            int levelIDBase = i * 100;

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
                int id = levelIDBase + j;
                aiGrid.put(id, new Heuristic(id, c == '1'));
            }
        }
    }

    private void initEnemies() {
        enemy1 = new Rectangle(30, 30, Color.RED);
        enemy1.setTranslateX(45);
        enemy1.setTranslateY(45);

        enemy2 = new Rectangle(30, 30, Color.GREEN);
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

        player.setTranslateX(x + action.dx*40);
        player.setTranslateY(y + action.dy*40);

        boolean collision = walls.stream().anyMatch(node -> player.getBoundsInParent().intersects(node.getBoundsInParent()));

        if (collision) {
            player.setTranslateX(x);
            player.setTranslateY(y);

            action = prevAction;
            prevAction = Action.NONE;
        }

        if (player.getTranslateX() <= -40) {
            player.setTranslateX(player.getTranslateX() + 22*BLOCK_SIZE);
        }

        if (player.getTranslateX() >= 21*BLOCK_SIZE) {
            player.setTranslateX(-40);
        }
    }

    private void updateRandomAI() {
        if (randomAIAction == Action.NONE) {
            return;
        }

        double x = enemy1.getTranslateX();
        double y = enemy1.getTranslateY();

        enemy1.setTranslateX(x + randomAIAction.dx*40);
        enemy1.setTranslateY(y + randomAIAction.dy*40);

        boolean collision = walls.stream().anyMatch(wall -> enemy1.getBoundsInParent().intersects(wall.getBoundsInParent()));

        if (!collision) {
            collision = enemy1.getTranslateX() < 0 || enemy1.getTranslateX() > 20*BLOCK_SIZE;
        }

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
        Timeline timer =  new Timeline(new KeyFrame(Duration.millis(100), e -> {
            onUpdate();
        }));

        timer.setCycleCount(Timeline.INDEFINITE);

        timer.play();

        Timeline randomAITimer =  new Timeline(new KeyFrame(Duration.millis(80), e -> {
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
