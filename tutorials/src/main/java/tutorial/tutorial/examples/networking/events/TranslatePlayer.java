package tutorial.tutorial.examples.networking.events;

import java.io.Serializable;

public record TranslatePlayer(short playerId, double x, double y) implements Serializable { }
