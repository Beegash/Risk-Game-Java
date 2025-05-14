package com.izzettinozmen.riskgame;

import java.io.Serializable;

public class GameMessage implements Serializable {
    public enum GameMessageType {
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
        START_GAME,
        GAME_STARTED
    }

    private final GameMessageType type;
    private final Object data;

    public GameMessage(GameMessageType type, Object data) {
        this.type = type;
        this.data = data;
    }

    public GameMessageType getType() {
        return type;
    }

    public Object getData() {
        return data;
    }
} 
