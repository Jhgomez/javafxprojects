package tutorial.tutorial.examples.gamemenus;

import javafx.animation.*;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class MortalKombatX implements GameMenu {

    private static final Font FONT = Font.font("", FontWeight.BOLD, 18);

    private static Pane root;
    private static VBox menuBox;;
    private static int messages = 1;
    private final Scene containerScene;
    private int currentItem = 0;

    static SequentialTransition st = new SequentialTransition();

    public MortalKombatX(Scene containerScene) {
        this.containerScene = containerScene;
    }

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
        private TriCircle c1 = new TriCircle(), c2 = new TriCircle();
        private Text text;
        private Runnable script;

        public MenuItem(String title) {
            super(15);
            setAlignment(Pos.CENTER);

            text = new Text(title);
            text.setFont(FONT);
            text.setEffect(new GaussianBlur(2));

            getChildren().addAll(c1, text, c2);
            setActive(false);
            setOnActivate(() -> System.out.printf("%s activated\n", title));
        }

        public void setOnActivate(Runnable r) {
            script = r;
        }

        public void setActive(boolean b) {
            c1.setVisible(b);
            c2.setVisible(b);
            text.setFill(b ? Color.WHITE : Color.GREY);
        }

        public void activate() {
            if (script != null) {
                script.run();
            }
        }
    }

    private static class TriCircle extends Parent {
        public TriCircle() {
            Shape shape1 = Shape.subtract(new Circle(5), new Circle(2));
            shape1.setFill(Color.WHITE);

            Shape shape2 = Shape.subtract(new Circle(5), new Circle(2));
            shape2.setFill(Color.WHITE);
            shape2.setTranslateX(5);

            Shape shape3 = Shape.subtract(new Circle(5), new Circle(2));
            shape3.setFill(Color.WHITE);
            shape3.setTranslateX(2.5);
            shape3.setTranslateY(-5);

            getChildren().addAll(shape1, shape2, shape3);

            setEffect(new GaussianBlur(2));
        }
    }

    private static MenuItem getMenuItem(int index) {
        return (MenuItem) menuBox.getChildren().get(index);
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
            ft.setInterpolator(Interpolator.EASE_IN);
            ft.setCycleCount(FadeTransition.INDEFINITE);
            ft.play();
        }

        return letters;
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
        inbox.setFill(Color.WHITE);
        inbox.setTranslateY(-120);
        inbox.setOpacity(0);

        TranslateTransition tt2 = new TranslateTransition(Duration.seconds(0.4), inbox);
        tt2.setToY(0);

        FadeTransition ft2 = new FadeTransition(Duration.seconds(0.4), inbox);
        ft2.setToValue(1);

        ParallelTransition pt2 = new ParallelTransition(tt2, ft2);

        PauseTransition pauset = new PauseTransition(Duration.seconds(3));



        TranslateTransition tt = new TranslateTransition(Duration.seconds(0.4), inbox);
        tt.setToY(120);

        FadeTransition ft = new FadeTransition(Duration.seconds(0.4), inbox);
        ft.setToValue(0);

        ParallelTransition pt3 = new ParallelTransition(tt, ft);


        TranslateTransition tt1 = new TranslateTransition(Duration.millis(10), inbox);
        tt1.setToY(-120);

        st.getChildren().addAll(pt2, pauset, pt3, tt1);
        st.setCycleCount(SequentialTransition.INDEFINITE);

        return inbox;
    }

    @Override
    public Pane getMenu() {
        root = new Pane();
        root.setPrefSize(900, 700);
        root.setStyle("-fx-background-color: #000000");

        ContentFrame frame1 = new ContentFrame(createLeftContent());
        ContentFrame frame2 = new ContentFrame(createMiddleContent());
        ContentFrame frame3 = new ContentFrame(createRightContent());

        HBox hbox = new HBox(15, frame1, frame2, frame3);
        hbox.setTranslateX(120);
        hbox.setTranslateY(50);

        MenuItem itemExit = new MenuItem("EXIT");
        itemExit.setOnActivate(() -> {
            clearResources();
            GameMenus.clearScreen();
        });


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

        containerScene.getFocusOwner().setOnKeyPressed(event -> {
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

        st.play();

        IO.println("1WP" + root.widthProperty().get());
        IO.println("1HP" + root.heightProperty().get());

        IO.println("1W" + root.getPrefWidth());
        IO.println("1H" + root.getPrefHeight());

        return root;
    }

    @Override
    public void clearResources() {
        st.stop();
        st.getChildren().clear();

        containerScene.getFocusOwner().setOnKeyPressed(null);
    }
}
