package com.izzettinozmen.riskgame;

import java.io.*;
import java.net.*;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private String clientAddress;
    private boolean isRunning;
    private Server server;
    private String playerName;
    private String currentGameId;

    public ClientHandler(Socket socket, Server server) {
        this.clientSocket = socket;
        this.server = server;
        this.clientAddress = socket.getInetAddress().getHostAddress();
        this.isRunning = true;
        try {
            this.out = new ObjectOutputStream(socket.getOutputStream());
            this.in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.out.println("Error creating client handler: " + e.getMessage());
        }
    }

    public String getClientAddress() {
        return clientAddress;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setCurrentGameId(String gameId) {
        this.currentGameId = gameId;
    }

    @Override
    public void run() {
        try {
            while (isRunning) {
                Object message = in.readObject();
                handleMessage(message);
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Client disconnected: " + (playerName != null ? playerName : clientAddress));
            server.removeClient(this);
        }
    }

    private void handleMessage(Object message) {
        if (message instanceof GameMessage) {
            GameMessage gameMessage = (GameMessage) message;
            System.out.println("Handling message type: " + gameMessage.getType());
            
            switch (gameMessage.getType()) {
                case CREATE_LOBBY:
                    handleCreateLobby(gameMessage);
                    break;
                case JOIN_LOBBY:
                    handleJoinLobby(gameMessage);
                    break;
                case LOBBY_CLOSED:
                    handleLobbyClosed();
                    break;
                case KICK_PLAYER:
                    handleKickPlayer(gameMessage);
                    break;
                case BAN_PLAYER:
                    handleBanPlayer(gameMessage);
                    break;
                case START_GAME:
                    handleStartGame();
                    break;
                // Game action messages
                case TERRITORY_SELECTED:
                case SOLDIERS_PLACED:
                case ATTACK_MADE:
                case FORTIFICATION_MADE:
                case TURN_ENDED:
                    if (currentGameId != null) {
                        server.handleGameAction(this, gameMessage);
                    }
                    break;
            }
        }
    }

    private void handleCreateLobby(GameMessage message) {
        this.playerName = (String) message.getData();
        System.out.println("Player " + this.playerName + " is creating a lobby");
        boolean created = server.createGameLobby(this);
        System.out.println("Lobby creation result: " + created);
        sendMessage(new GameMessage(GameMessage.GameMessageType.LOBBY_CREATED, created));
        if (!created) {
            sendMessage(new GameMessage(GameMessage.GameMessageType.PLAYER_LEFT, 
                "Lobby could not be created. (Maybe a lobby already exists or name is in use.)"));
        }
    }

    private void handleJoinLobby(GameMessage message) {
        this.playerName = (String) message.getData();
        System.out.println("Player " + this.playerName + " is joining the lobby");
        boolean joined = server.joinGameLobby(this);
        System.out.println("Lobby join result: " + joined);
        sendMessage(new GameMessage(GameMessage.GameMessageType.LOBBY_JOINED, joined));
        if (!joined) {
            sendMessage(new GameMessage(GameMessage.GameMessageType.PLAYER_LEFT, 
                "Could not join lobby. (Name already in use or lobby full.)"));
        }
    }

    private void handleLobbyClosed() {
        System.out.println("Lobby is being closed by host");
        server.closeLobby();
    }

    private void handleKickPlayer(GameMessage message) {
        String playerToKick = (String) message.getData();
        System.out.println("Host is kicking player: " + playerToKick);
        server.kickPlayer(playerToKick);
    }

    private void handleBanPlayer(GameMessage message) {
        String playerToBan = (String) message.getData();
        System.out.println("Host is banning player: " + playerToBan);
        server.banPlayer(playerToBan);
    }

    private void handleStartGame() {
        System.out.println("Host started the game.");
        server.startGame();
    }

    public void sendMessage(Object message) {
        try {
            out.writeObject(message);
            out.flush();
        } catch (IOException e) {
            System.out.println("Error sending message to client: " + e.getMessage());
        }
    }

    public void close() {
        isRunning = false;
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (clientSocket != null) clientSocket.close();
        } catch (IOException e) {
            System.out.println("Error closing client connection: " + e.getMessage());
        }
    }
} 
