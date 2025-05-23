package common;

import java.io.Serializable;

public class CommonMessages implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Type {
        JOIN,
        PLACE_ARMY,
        ATTACK,
        FORTIFY,
        END_TURN,
        CHAT,
        WIN,
        LOSE,
        REMATCH_REQUEST,
        PLAY_AGAIN,
        EXIT_GAME
    }

    private final Type type;
    private final String message;
    private final String from;
    private final String to;
    private final int armyCount;

    public CommonMessages(Type type) {
        this(type, "", "", "", 0);
    }

    public CommonMessages(Type type, String message) {
        this(type, message, "", "", 0);
    }

    public CommonMessages(Type type, String from, String to, int armyCount) {
        this(type, "", from, to, armyCount);
    }

    public CommonMessages(Type type, String message, String from, String to, int armyCount) {
        this.type = type;
        this.message = message;
        this.from = from;
        this.to = to;
        this.armyCount = armyCount;
    }

    public Type getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public int getArmyCount() {
        return armyCount;
    }
}
