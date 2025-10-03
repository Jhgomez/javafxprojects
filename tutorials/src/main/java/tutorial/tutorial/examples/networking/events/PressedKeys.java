package tutorial.tutorial.examples.networking.events;

import javafx.scene.input.KeyCode;

import java.io.Serializable;

public record PressedKeys(
        short playerId,
        KeyCode[] keys
) implements Serializable { }
