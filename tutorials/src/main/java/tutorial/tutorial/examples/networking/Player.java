package tutorial.tutorial.examples.networking;

import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.io.Serializable;

public class Player extends StackPane implements Serializable {
    private final short playerId;
    private final String color;

    public Player(short playerId, String color, double initialX, double initialY) {
        super();
        this.playerId = playerId;
        this.color = color;

        var rectangle = new Rectangle(25, 25, Color.valueOf(color));

        var idText = new Text(String.valueOf(playerId));

        getChildren().addAll(rectangle, idText);

        setAlignment(Pos.CENTER);

        setTranslateX(initialX);
        setTranslateY(initialY);
    }

    public PlayerFactory toFactory() {
        return new PlayerFactory(playerId, color, getTranslateX(), getTranslateY());
    }

    public short getPlayerId() {
        return playerId;
    }
}
