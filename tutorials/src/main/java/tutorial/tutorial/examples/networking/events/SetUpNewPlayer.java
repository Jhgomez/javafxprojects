package tutorial.tutorial.examples.networking.events;

import tutorial.tutorial.examples.networking.PlayerFactory;

import java.io.Serializable;

public record SetUpNewPlayer(PlayerFactory playerFactory) implements Serializable { }
