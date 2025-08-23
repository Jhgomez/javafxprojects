package tutorial.tutorial.examples;

import javafx.animation.AnimationTimer;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;


public class Vector {
    private Arrow bullet = new Arrow();

    // https://www.youtube.com/watch?v=ypFIoPaTwJQ
    private Parent createContent() {
        Pane root = new Pane();
        root.setPrefSize(800,800);
        root.getChildren().addAll(
                new Line(400, 0, 400, 400), // y axis
                new Line(400, 400, 800, 400), // x axis
                bullet
        );
        return root;
    }

    private class Arrow extends Parent {
        public Point2D velocity = new Point2D(0, 0);

        public Arrow() {
            Line line = new Line(10, 5, 20, 10);
            Line line2 = new Line(10, 15, 20, 10);

            line.setStrokeWidth(3);
            line2.setStrokeWidth(3);
            line.setStroke(Color.RED);
            line2.setStroke(Color.BLUE);

            getChildren().addAll(new Rectangle(0, 9, 20, 2), line, line2);
        }

        public void setTarget(double x, double y) {
            setTranslateX(400);
            setTranslateY(400);

            velocity = new Point2D(x, y).subtract(getTranslateX(), getTranslateY()).normalize().multiply(7);
//            velocity = new Point2D(x, y).subtract(getTranslateX(), getTranslateY()).multiply(0.08);

            System.out.println("X " + velocity.getX());
            System.out.println("Y " + velocity.getY());

            double angle = calculateAngleAuto(velocity.getX(), velocity.getY());
            // double angle = calculateAngleManual(velocity.getX(), velocity.getY());
            // double angle = calculateAngleManual2(velocity.getX(), velocity.getY());

            getTransforms().clear();
            getTransforms().add(new Rotate(angle, 0, 0));
        }

        public void move() {
            setTranslateX(getTranslateX() + velocity.getX());
            setTranslateY(getTranslateY() + velocity.getY());
        }
    }

    private double calculateAngleAuto(double vecX, double vecY) {
        double angle = new Point2D(vecX, vecY).angle(1, 0);
        return vecY > 0 ? angle : -angle;
    }

    private double calculateAngleManual(double vecX, double vecY) {
        double angle = Math.toDegrees(Math.atan(vecY / vecX));
        return vecX > 0 ? angle : 180 + angle;
    }

    private double calculateAngleManual2(double vecX, double vecY) {
        double hyp = Math.sqrt(vecX * vecX + vecY * vecY);
        double angle = Math.toDegrees(Math.atan(vecY / hyp));
        return vecX > 0 ? angle : 180 - angle;
    }

    public void displayScreen(Runnable runnable) {
//        Timeline timeline =  new Timeline(new KeyFrame(Duration.millis(100), e -> {}));
//        Point2D point = new Point2D(0, 0);

        Scene scene = new Scene(createContent());
        scene.setOnMouseClicked(event -> {
            bullet.setTarget(event.getSceneX(), event.getSceneY());
        });


        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                bullet.move();
            }
        };

        timer.start();

        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();

        stage.setOnCloseRequest(e -> runnable.run());
    }
}
