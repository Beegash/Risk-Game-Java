/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.izzettinozmen.riskgame;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

/**
 *
 * @author ifozmen
 */
public class Server {
    private static final int PORT = 3131;
    private ServerSocket serverSocket;
    private boolean isRunning;
    private List<ClientHandler> clients;
    private GameLobby gameLobby;
    private ExecutorService clientThreadPool;
    private Set<String> bannedPlayers;

    public Server() {
        this.clients = new ArrayList<>();
        this.clientThreadPool = Executors.newCachedThreadPool();
        this.gameLobby = null;
        this.bannedPlayers = new HashSet<>();
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(PORT);
            isRunning = true;
            System.out.println("Server started on port: " + PORT);
            System.out.println("Waiting for clients...");

            while (isRunning) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                    clients.add(clientHandler);
                    clientThreadPool.execute(clientHandler);
                    System.out.println("New client connected: " + clientSocket.getInetAddress().getHostAddress());
                } catch (IOException e) {
                    if (isRunning) {
                        System.out.println("Error accepting client connection: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
        }
    }

    public void stop() {
        isRunning = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            clientThreadPool.shutdown();
            System.out.println("Server stopped");
        } catch (IOException e) {
            System.out.println("Error stopping server: " + e.getMessage());
        }
    }

    public boolean createGameLobby(ClientHandler host) {
        if (gameLobby != null) {
            return false;
        }
        gameLobby = new GameLobby(host);
        System.out.println("New game lobby created by: " + host.getPlayerName());
        return true;
    }

    public boolean joinGameLobby(ClientHandler player) {
        if (gameLobby == null) {
            return false;
        }
        if (bannedPlayers.contains(player.getPlayerName())) {
            System.out.println("Player " + player.getPlayerName() + " is banned and cannot join the lobby.");
            return false;
        }
        return gameLobby.addPlayer(player);
    }

    public void removeClient(ClientHandler client) {
        clients.remove(client);
        if (gameLobby != null) {
            gameLobby.removePlayer(client);
        }
        client.close();
        System.out.println("Client removed: " + client.getPlayerName());
    }

    public void closeLobby() {
        if (gameLobby != null) {
            broadcastToLobby(new GameMessage(GameMessage.GameMessageType.LOBBY_CLOSED, null));
            for (ClientHandler ch : new ArrayList<>(gameLobby.getPlayers())) {
                ch.close();
            }
            gameLobby = null;
            System.out.println("Lobby closed by host.");
        }
    }

    public void broadcastToLobby(GameMessage message) {
        if (gameLobby != null) {
            for (ClientHandler ch : gameLobby.getPlayers()) {
                ch.sendMessage(message);
            }
        }
    }

    public void kickPlayer(String playerName) {
        if (gameLobby != null) {
            for (ClientHandler ch : gameLobby.getPlayers()) {
                if (ch.getPlayerName().equals(playerName)) {
                    System.out.println("Player " + playerName + " has been kicked from the lobby.");
                    ch.sendMessage(new GameMessage(GameMessage.GameMessageType.PLAYER_KICKED, "You have been kicked from the lobby."));
                    removeClient(ch);
                    break;
                }
            }
        }
    }

    public void banPlayer(String playerName) {
        if (gameLobby != null) {
            for (ClientHandler ch : gameLobby.getPlayers()) {
                if (ch.getPlayerName().equals(playerName)) {
                    System.out.println("Player " + playerName + " has been banned from the lobby.");
                    bannedPlayers.add(playerName);
                    ch.sendMessage(new GameMessage(GameMessage.GameMessageType.PLAYER_BANNED, "You have been banned from the lobby."));
                    removeClient(ch);
                    break;
                }
            }
        }
    }

    public void startGame() {
        if (gameLobby != null) {
            broadcastToLobby(new GameMessage(GameMessage.GameMessageType.GAME_STARTED, null));
            System.out.println("Game started! Notified all players.");
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }
}
