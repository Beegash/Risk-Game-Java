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
                    this.playerName = (String) gameMessage.getData();
                    System.out.println("Player " + this.playerName + " is creating a lobby");
                    boolean created = server.createGameLobby(this);
                    System.out.println("Lobby creation result: " + created);
                    sendMessage(new GameMessage(GameMessage.GameMessageType.LOBBY_CREATED, created));
                    if (!created) {
                        sendMessage(new GameMessage(GameMessage.GameMessageType.PLAYER_LEFT, "Lobby could not be created. (Maybe a lobby already exists or name is in use.)"));
                    }
                    break;
                case JOIN_LOBBY:
                    this.playerName = (String) gameMessage.getData();
                    System.out.println("Player " + this.playerName + " is joining the lobby");
                    boolean joined = server.joinGameLobby(this);
                    System.out.println("Lobby join result: " + joined);
                    sendMessage(new GameMessage(GameMessage.GameMessageType.LOBBY_JOINED, joined));
                    if (!joined) {
                        sendMessage(new GameMessage(GameMessage.GameMessageType.PLAYER_LEFT, "Could not join lobby. (Name already in use or lobby full.)"));
                    }
                    break;
                case LOBBY_CLOSED:
                    System.out.println("Lobby is being closed by host");
                    server.closeLobby();
                    break;
                case KICK_PLAYER:
                    String playerToKick = (String) gameMessage.getData();
                    System.out.println("Host is kicking player: " + playerToKick);
                    server.kickPlayer(playerToKick);
                    break;
                case BAN_PLAYER:
                    String playerToBan = (String) gameMessage.getData();
                    System.out.println("Host is banning player: " + playerToBan);
                    server.banPlayer(playerToBan);
                    break;
                case START_GAME:
                    System.out.println("Host started the game.");
                    server.startGame();
                    break;
            }
        }
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
