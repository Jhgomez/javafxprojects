package tutorial.tutorial.examples.geometrywars;

import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.input.KeyEvent;


class GameEvent extends Event {
    Runnable callback;
    /**
     * Common supertype for all game event types.
     */
    public static final EventType<GameEvent> ANY = new EventType<>(Event.ANY, "GAME_EVENT");

    public static final EventType<GameEvent> DEATH = new EventType<>(GameEvent.ANY, "DEATH");

    public GameEvent(EventType<GameEvent> eventType, Runnable callback) {
        super(eventType);

        this.callback = callback;
    }
}
