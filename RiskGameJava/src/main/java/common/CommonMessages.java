/**
 * CommonMessages.java
 * This class represents messages exchanged between client and server in the Risk game.
 * It supports various types of game actions, chat messages, and system notifications.
 * The class is serializable to allow transmission between client and server.
 */

package common;

import java.io.Serializable;

public class CommonMessages implements Serializable {
    /** Serial version UID for serialization compatibility */
    private static final long serialVersionUID = 1L;

    /**
     * Enum defining all possible message types in the game
     */
    public enum Type {
        /** Player joining the game */
        JOIN,
        /** Placing armies on a territory */
        PLACE_ARMY,
        /** Attacking another territory */
        ATTACK,
        /** Moving armies between owned territories */
        FORTIFY,
        /** Ending the current turn */
        END_TURN,
        /** Chat message between players */
        COMMUNUCATON,
        /** Victory notification */
        WIN,
        /** Defeat notification */
        LOSE,
        /** Request for a rematch */
        REMATCH_REQUEST,
        /** Confirmation to play another game */
        PLAY_AGAIN,
        /** Player exiting the game */
        EXIT_GAME
    }

    /** Type of the message */
    private final Type type;
    /** Text content of the message */
    private final String message;
    /** Source territory or player name */
    private final String from;
    /** Destination territory or player name */
    private final String to;
    /** Number of armies involved in the action */
    private final int armyCount;

    /**
     * Constructor for simple message types without additional data
     * 
     * @param type Type of the message
     */
    public CommonMessages(Type type) {
        this(type, "", "", "", 0);
    }

    /**
     * Constructor for messages with text content
     * 
     * @param type Type of the message
     * @param message Text content of the message
     */
    public CommonMessages(Type type, String message) {
        this(type, message, "", "", 0);
    }

    /**
     * Constructor for game actions involving territories and armies
     * 
     * @param type Type of the message
     * @param from Source territory name
     * @param to Destination territory name
     * @param armyCount Number of armies involved
     */
    public CommonMessages(Type type, String from, String to, int armyCount) {
        this(type, "", from, to, armyCount);
    }

    /**
     * Full constructor for messages with all possible fields
     * 
     * @param type Type of the message
     * @param message Text content of the message
     * @param from Source territory or player name
     * @param to Destination territory or player name
     * @param armyCount Number of armies involved
     */
    public CommonMessages(Type type, String message, String from, String to, int armyCount) {
        this.type = type;
        this.message = message;
        this.from = from;
        this.to = to;
        this.armyCount = armyCount;
    }

    /**
     * Gets the type of the message
     * 
     * @return Message type
     */
    public Type getType() {
        return type;
    }

    /**
     * Gets the text content of the message
     * 
     * @return Message text
     */
    public String getMessage() {
        return message;
    }

    /**
     * Gets the source territory or player name
     * 
     * @return Source name
     */
    public String getFrom() {
        return from;
    }

    /**
     * Gets the destination territory or player name
     * 
     * @return Destination name
     */
    public String getTo() {
        return to;
    }

    /**
     * Gets the number of armies involved in the action
     * 
     * @return Number of armies
     */
    public int getArmyCount() {
        return armyCount;
    }
}
