package tutorial.tutorial.examples.networking.events;

import java.io.Serializable;

public record DropClient(short playerId) implements Serializable { }
