package tutorial.tutorial.examples.networking;

import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.io.Serializable;

public record PlayerFactory(short playerId, String color, double initialX, double initialY) implements Serializable {
//    private final short playerId;
//    private final String color;
//    private final double initialX;
//    private final double initialY;
//
//    public PlayerFactory(short playerId, String color, double initialX, double initialY) {
//        super();
//
//        this.playerId = playerId;
//        this.color = color;
//        this.initialX = initialX;
//        this.initialY = initialY;
//    }

    public Player getPlayer() {
//        var rectangle = new Rectangle(25, 25, Color.valueOf(color));
//
//        var idText = new Text(String.valueOf(playerId));
//
//        StackPane spane = new StackPane(rectangle, idText);
//
//        spane.setAlignment(Pos.CENTER);
//
//        spane.setTranslateX(initialX);
//        spane.setTranslateY(initialY);

        return new Player(playerId, color, initialX, initialY);
    }

    public short getPlayerId() {
        return playerId;
    }

    public String getColor() {
        return color;
    }

    public double getInitialX() {
        return initialX;
    }

    public double getInitialY() {
        return initialY;
    }
}
