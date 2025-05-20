package com.izzettinozmen.riskgame;

import java.io.Serializable;

public class GameMessage implements Serializable {
    public enum GameMessageType {
        // Lobby messages
        CREATE_LOBBY,
        JOIN_LOBBY,
        LOBBY_CREATED,
        LOBBY_JOINED,
        LOBBY_CLOSED,
        PLAYER_JOINED,
        PLAYER_LEFT,
        KICK_PLAYER,
        BAN_PLAYER,
        PLAYER_KICKED,
        PLAYER_BANNED,
        
        // Game control messages
        START_GAME,
        GAME_STARTED,
        GAME_STATE_UPDATE,
        TURN_STARTED,
        TURN_ENDED,
        GAME_OVER,
        PHASE_CHANGED,
        PHASE_CHANGE_FAILED,
        
        // Game action messages
        TERRITORY_SELECTED,
        SOLDIERS_PLACED,
        ATTACK_MADE,
        FORTIFICATION_MADE,
        
        // Error messages
        ERROR_MESSAGE,
        INVALID_MOVE,
        NOT_YOUR_TURN
    }

    private final GameMessageType type;
    private final Object data;
    private final String sender;

    public GameMessage(GameMessageType type, Object data) {
        this.type = type;
        this.data = data;
        this.sender = null;
    }

    public GameMessage(GameMessageType type, Object data, String sender) {
        this.type = type;
        this.data = data;
        this.sender = sender;
    }

    public GameMessageType getType() {
        return type;
    }

    public Object getData() {
        return data;
    }

    public String getSender() {
        return sender;
    }
} 
