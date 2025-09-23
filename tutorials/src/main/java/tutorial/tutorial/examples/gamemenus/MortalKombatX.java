package tutorial.tutorial.examples.gamemenus;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MortalKombatX implements GameMenu {

    private static final Font FONT = Font.font("", FontWeight.BOLD, 18);

    private static VBox menuBox;;
    private static int messages;
    private int currentItem = 0;

    private static ScheduledExecutorService bgThread = Executors.newSingleThreadScheduledExecutor();

    private static class ContentFrame extends StackPane {
        public ContentFrame(Node content) {
            setAlignment(Pos.CENTER);

            Rectangle frame = new Rectangle(200, 200);
            frame.setArcWidth(25);
            frame.setArcHeight(25);
            frame.setStroke(Color.WHITESMOKE);

            getChildren().addAll(frame, content);
        }
    }

    private static class MenuItem extends HBox {

        public MenuItem(String title) {
        }

        public void setOnActivate(Runnable callback) {
        }

        public void setActive(boolean b) {
        }
    }

    private static class TriCircle extends Parent {

    }

    private static void initMenu() {
        Pane root = new Pane();
        root.setPrefSize(900, 600);
        root.setStyle("-fx-background-color: #000000");

        ContentFrame frame1 = new ContentFrame(createLeftContent());
        ContentFrame frame2 = new ContentFrame(createMiddleContent());
        ContentFrame frame3 = new ContentFrame(createRightContent());
        
        HBox hbox = new HBox(15, frame1, frame2, frame3);
        hbox.setTranslateX(120);
        hbox.setTranslateY(50);
        
        MenuItem itemExit = new MenuItem("EXIT");
        itemExit.setOnActivate(GameMenus::clearScreen);
        
        menuBox = new VBox(
                10, 
                new MenuItem("ONE PLAYER"),
                new MenuItem("TWO PLAYER"),
                new MenuItem("ONLINE"),
                new MenuItem("FACTION"),
                new MenuItem("KRYPT"),
                new MenuItem("OPTIONS"),
                new MenuItem("EXTRAS"),
                itemExit
        );
        
        menuBox.setAlignment(Pos.TOP_CENTER);
        menuBox.setTranslateX(360);
        menuBox.setTranslateY(360);

        getMenuItem(0).setActive(true);

        root.getChildren().addAll(hbox, menuBox);

        return root;
    }

    private static MenuItem getMenuItem(int i) {
    }

    private static Node createRightContent() {
        String title = "Check For Updates";
        HBox letters = new HBox(0);
        letters.setAlignment(Pos.CENTER);

        for (int i = 0; i < title.length(); i++) {
            Text letter = new Text(String.valueOf(title.charAt(i)));
            letter.setFont(FONT);
            letter.setFill(Color.WHITE);
            letter.setOpacity(0);
            letters.getChildren().add(letter);

            FadeTransition ft = new FadeTransition(Duration.seconds(2), letter);
            ft.setDelay( Duration.millis(50 * i ) );
            ft.setToValue(1);
            ft.setAutoReverse(true);
            ft.setInterpolator(Interpolator.LINEAR);
            ft.play();
        }
    }

    private static Node createMiddleContent() {
        String title = "MKX Menu App";
        HBox letters = new HBox(0);
        letters.setAlignment(Pos.CENTER);

        for (int i = 0; i < title.length(); i++) {
            Text letter = new Text(String.valueOf(title.charAt(i)));
            letter.setFont(FONT);
            letter.setFill(Color.WHITE);
            letters.getChildren().add(letter);

            TranslateTransition tt = new TranslateTransition(Duration.seconds(2), letter);
            tt.setDelay( Duration.millis(50 * i ) );
            tt.setToY(-25);
            tt.setInterpolator(Interpolator.LINEAR);
            tt.setAutoReverse(true);
            tt.setCycleCount(TranslateTransition.INDEFINITE);
            tt.play();
        }

        return letters;
    }

    private static Node createLeftContent() {
        final Text inbox = new Text("You have " + messages + " new message(-s)");

        var service = new Service<String>() {

            @Override
            protected Task<String> createTask() {
                return null;
            }
        };

        // using a JavaFx concurrent Task object will avoid us the need to
        // explicitly use the "Platform.runLater" method inside the runnable
        bgThread.scheduleAtFixedRate(new Task<Void>() {
            @Override
            protected Void call() {
                TranslateTransition tt = new TranslateTransition(Duration.seconds(0.5), inbox);
                tt.setToY(150);

                FadeTransition ft = new FadeTransition(Duration.seconds(0.5), inbox);
                ft.setToValue(0);

                ParallelTransition pt = new ParallelTransition(tt, ft);
                pt.setOnFinished(_ -> {
                    inbox.setTranslateY(-150);
                    inbox.setText("You have " + messages + " new message(-s");

                    TranslateTransition tt2 = new TranslateTransition(Duration.seconds(0.5), inbox);
                    tt.setToY(0);

                    FadeTransition ft2 = new FadeTransition(Duration.seconds(0.5), inbox);
                    ft.setToValue(1);

                    ParallelTransition pt2 = new ParallelTransition(tt2, ft2);
                    pt2.play();
                });

                pt.play();

                return null;
            }
        }, 2, 5, TimeUnit.SECONDS);

        return inbox;
    }

    @Override
    public Region getMenu(Scene scene) {
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.UP) {
                if (currentItem > 0) {
                    getMenuItem(currentItem).setActive(false);
                    getMenuItem(--currentItem).setActive(true);
                }
            }

            if (event.getCode() == KeyCode.DOWN) {
                if (currentItem < menuBox.getChildren().size() - 1) {
                    getMenuItem(currentItem).setActive(false);
                    getMenuItem(++currentItem).setActive(true);
                }
            }

            if (event.getCode() == KeyCode.ENTER) {
                getMenuItem(currentItem).activate();
            }
        });

        initMenu();
        return null;
    }

    @Override
    public void clearResources() {
        bgThread.shutdownNow();
        menuBox = null;
    }
}
