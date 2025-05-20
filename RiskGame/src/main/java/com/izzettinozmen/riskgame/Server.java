/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.izzettinozmen.riskgame;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.awt.Color;

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
    private GameManager gameManager;
    private Map<String, GameState> activeGames;

    public Server() {
        this.clients = new ArrayList<>();
        this.clientThreadPool = Executors.newCachedThreadPool();
        this.gameLobby = null;
        this.bannedPlayers = new HashSet<>();
        this.activeGames = new HashMap<>();
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
        // Send initial player list to host
        broadcastPlayerList();
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
        boolean success = gameLobby.addPlayer(player);
        if (success) {
            // Send updated player list to all players
            broadcastPlayerList();
        }
        return success;
    }

    public void removeClient(ClientHandler client) {
        clients.remove(client);
        if (gameLobby != null) {
            gameLobby.removePlayer(client);
            // Send updated player list to remaining players
            broadcastPlayerList();
        }
        client.close();
        System.out.println("Client removed: " + client.getPlayerName());
    }

    private void broadcastPlayerList() {
        if (gameLobby != null) {
            List<String> playerList = gameLobby.getPlayers().stream()
                .map(player -> {
                    String name = player.getPlayerName();
                    // Add host indicator
                    if (player.equals(gameLobby.getHost())) {
                        name += " (host)";
                    }
                    return name;
                })
                .collect(java.util.stream.Collectors.toList());
            
            // Send to each player with their own "you" indicator
            for (ClientHandler player : gameLobby.getPlayers()) {
                List<String> personalizedList = new ArrayList<>(playerList);
                // Add "you" indicator to the current player's name
                String playerName = player.getPlayerName();
                int index = personalizedList.indexOf(playerName);
                if (index != -1) {
                    personalizedList.set(index, playerName + " (you)");
                }
                
                GameMessage message = new GameMessage(
                    GameMessage.GameMessageType.PLAYER_JOINED,
                    personalizedList
                );
                
                player.sendMessage(message);
            }
        }
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
            int playerCount = gameLobby.getPlayers().size();
            if (playerCount < GameRules.MIN_PLAYERS) {
                // Not enough players, send error to host
                ClientHandler host = gameLobby.getHost();
                if (host != null) {
                    host.sendMessage(new GameMessage(
                        GameMessage.GameMessageType.ERROR_MESSAGE,
                        "At least " + GameRules.MIN_PLAYERS + " players are required to start the game."
                    ));
                }
                return;
            }
            
            // Create game state
            GameState gameState = new GameState();
            String gameId = UUID.randomUUID().toString();
            
            // Define player colors
            Color[] playerColors = {
                Color.RED,
                Color.BLUE,
                Color.GREEN,
                Color.YELLOW,
                Color.MAGENTA,
                Color.CYAN,
                Color.ORANGE,
                new Color(150, 75, 0)  // Brown
            };
            
            // Add players to game state with assigned colors
            List<ClientHandler> lobbyPlayers = gameLobby.getPlayers();
            for (int i = 0; i < lobbyPlayers.size(); i++) {
                ClientHandler clientHandler = lobbyPlayers.get(i);
                Color playerColor = playerColors[i % playerColors.length]; // Cycle colors if more players than colors
                Player newPlayer = new Player(clientHandler.getPlayerName(), playerColor, i);
                gameState.addPlayer(newPlayer);
            }
            
            // Store game state before initializing
            activeGames.put(gameId, gameState);
            
            // Create and setup the game manager
            gameManager = new GameManager();
            gameManager.setServer(this);
            gameManager.setGameState(gameState); // Set the initialized gameState with players
            
            // Initialize game
            gameManager.startGame();
            
            // Get first player and start their turn
            Player firstPlayer = gameState.getCurrentPlayer();
            if (firstPlayer != null) {
                firstPlayer.startTurn();
            
                // First notify players that game has started
                broadcastToLobby(new GameMessage(GameMessage.GameMessageType.GAME_STARTED, gameId));
                
                // Then send initial game state to all players
                broadcastGameState(gameId);
                
                // Notify players about first turn
                for (ClientHandler ch : gameLobby.getPlayers()) {
                    if (ch.getPlayerName().equals(firstPlayer.getName())) {
                        ch.sendMessage(new GameMessage(
                            GameMessage.GameMessageType.TURN_STARTED,
                            "Your turn has started"
                        ));
                    } else {
                        ch.sendMessage(new GameMessage(
                            GameMessage.GameMessageType.TURN_STARTED,
                            firstPlayer.getName() + "'s turn has started"
                        ));
                    }
                }
                
                System.out.println("Game started with ID: " + gameId);
            } else {
                // Something went wrong, notify host
                ClientHandler host = gameLobby.getHost();
                if (host != null) {
                    host.sendMessage(new GameMessage(
                        GameMessage.GameMessageType.ERROR_MESSAGE,
                        "Failed to start the game - could not determine first player."
                    ));
                }
            }
        }
    }

    private void broadcastGameState(String gameId) {
        GameState gameState = activeGames.get(gameId);
        if (gameState != null) {
            GameMessage message = new GameMessage(
                GameMessage.GameMessageType.GAME_STATE_UPDATE,
                gameState
            );
            broadcastToLobby(message);
        }
    }

    public void handleGameAction(ClientHandler client, GameMessage message) {
        String gameId = (String) message.getData();
        GameState gameState = activeGames.get(gameId);
        
        if (gameState == null) {
            client.sendMessage(new GameMessage(
                GameMessage.GameMessageType.ERROR_MESSAGE,
                "Game not found"
            ));
            return;
        }

        // Check if it's the player's turn
        if (!gameState.getCurrentPlayer().getName().equals(client.getPlayerName())) {
            client.sendMessage(new GameMessage(
                GameMessage.GameMessageType.NOT_YOUR_TURN,
                "It's not your turn"
            ));
            return;
        }

        // Handle different game actions
        switch (message.getType()) {
            case TERRITORY_SELECTED:
                handleTerritorySelection(client, gameState, message);
                break;
            case SOLDIERS_PLACED:
                handleSoldierPlacement(client, gameState, message);
                break;
            case ATTACK_MADE:
                handleAttack(client, gameState, message);
                break;
            case FORTIFICATION_MADE:
                handleFortification(client, gameState, message);
                break;
            case TURN_ENDED:
                handleTurnEnd(client, gameState);
                break;
        }
    }

    private void handleTerritorySelection(ClientHandler client, GameState gameState, GameMessage message) {
        String territoryName = (String) message.getData();
        Territory territory = TerritoryManager.getInstance().getTerritory(territoryName);
        
        if (territory == null) {
            client.sendMessage(new GameMessage(
                GameMessage.GameMessageType.ERROR_MESSAGE,
                "Territory not found: " + territoryName
            ));
            return;
        }

        // Validate territory selection based on game phase
        switch (gameState.getCurrentPhase()) {
            case SETUP:
                if (territory.getOwner() != null) {
                    client.sendMessage(new GameMessage(
                        GameMessage.GameMessageType.INVALID_MOVE,
                        "Territory is already owned"
                    ));
                    return;
                }
                break;
            case REINFORCEMENT:
            case ATTACK:
            case FORTIFICATION:
                if (!territory.getOwner().getName().equals(client.getPlayerName())) {
                    client.sendMessage(new GameMessage(
                        GameMessage.GameMessageType.INVALID_MOVE,
                        "You don't own this territory"
                    ));
                    return;
                }
                break;
        }

        // Broadcast territory selection to all players
        broadcastToLobby(new GameMessage(
            GameMessage.GameMessageType.TERRITORY_SELECTED,
            territoryName,
            client.getPlayerName()
        ));
    }

    private void handleSoldierPlacement(ClientHandler client, GameState gameState, GameMessage message) {
        Map<String, Object> data = (Map<String, Object>) message.getData();
        String territoryName = (String) data.get("territory");
        Integer soldierCount = (Integer) data.get("count");
        
        Territory territory = TerritoryManager.getInstance().getTerritory(territoryName);
        Player currentPlayer = gameState.getCurrentPlayer();
        
        if (territory == null || soldierCount == null) {
            client.sendMessage(new GameMessage(
                GameMessage.GameMessageType.ERROR_MESSAGE,
                "Invalid territory or soldier count"
            ));
            return;
        }

        // Kontrol: Bölge mevcut oyuncuya mı ait?
        if (territory.getOwner() == null || !territory.getOwner().getName().equals(currentPlayer.getName())) {
            client.sendMessage(new GameMessage(
                GameMessage.GameMessageType.INVALID_MOVE,
                "You can only place soldiers in your own territories."
            ));
            return;
        }

        System.out.println("[DEBUG] Server - Player " + currentPlayer.getName() + 
                         " is placing " + soldierCount + " soldiers in " + territoryName + 
                         ". Currently has " + currentPlayer.getRemainingSoldiers() + " soldiers available.");

        // Validate soldier placement
        if (soldierCount <= 0 || soldierCount > currentPlayer.getRemainingSoldiers()) {
            client.sendMessage(new GameMessage(
                GameMessage.GameMessageType.INVALID_MOVE,
                "Invalid number of soldiers"
            ));
            return;
        }

        // Place soldiers
        if (gameManager.placeSoldiers(territory, soldierCount)) {
            // Log başarılı yerleştirme
            System.out.println("[DEBUG] Server - Successfully placed " + soldierCount + 
                            " soldiers in " + territoryName + ". Player now has " + 
                            currentPlayer.getRemainingSoldiers() + " soldiers remaining.");
            
            // Tüm oyunculara asker yerleştirilme mesajını gönder
            broadcastToLobby(new GameMessage(
                GameMessage.GameMessageType.SOLDIERS_PLACED,
                currentPlayer.getName() + " placed " + soldierCount + " soldiers in " + territoryName
            ));
            
            // Hemen ardından güncel oyun durumunu da gönder
            broadcastGameState(gameState.getGameId());
        } else {
            client.sendMessage(new GameMessage(
                GameMessage.GameMessageType.INVALID_MOVE,
                "Cannot place soldiers in this territory"
            ));
        }
    }

    private void handleAttack(ClientHandler client, GameState gameState, GameMessage message) {
        Map<String, Object> data = (Map<String, Object>) message.getData();
        String attackerName = (String) data.get("attacker");
        String defenderName = (String) data.get("defender");
        Integer attackingSoldiers = (Integer) data.get("soldiers");
        
        Territory attacker = TerritoryManager.getInstance().getTerritory(attackerName);
        Territory defender = TerritoryManager.getInstance().getTerritory(defenderName);
        
        if (attacker == null || defender == null || attackingSoldiers == null) {
            client.sendMessage(new GameMessage(
                GameMessage.GameMessageType.ERROR_MESSAGE,
                "Invalid territories or soldier count"
            ));
            return;
        }

        // Validate attack
        if (!attacker.isAdjacentTo(defender)) {
            client.sendMessage(new GameMessage(
                GameMessage.GameMessageType.INVALID_MOVE,
                "Territories are not adjacent"
            ));
            return;
        }

        // Perform attack
        if (gameManager.attack(attacker, defender, attackingSoldiers)) {
            // Broadcast the update
            broadcastGameState(gameState.getGameId());
            
            // Check if game is over
            if (gameState.isGameOver()) {
                Player winner = gameState.getWinner();
                broadcastToLobby(new GameMessage(
                    GameMessage.GameMessageType.GAME_OVER,
                    winner.getName() + " has won the game!"
                ));
            }
        } else {
            client.sendMessage(new GameMessage(
                GameMessage.GameMessageType.INVALID_MOVE,
                "Attack failed"
            ));
        }
    }

    private void handleFortification(ClientHandler client, GameState gameState, GameMessage message) {
        Map<String, Object> data = (Map<String, Object>) message.getData();
        String fromName = (String) data.get("from");
        String toName = (String) data.get("to");
        Integer soldierCount = (Integer) data.get("count");
        
        Territory from = TerritoryManager.getInstance().getTerritory(fromName);
        Territory to = TerritoryManager.getInstance().getTerritory(toName);
        
        if (from == null || to == null || soldierCount == null) {
            client.sendMessage(new GameMessage(
                GameMessage.GameMessageType.ERROR_MESSAGE,
                "Invalid territories or soldier count"
            ));
            return;
        }

        // Validate fortification
        if (!from.isAdjacentTo(to)) {
            client.sendMessage(new GameMessage(
                GameMessage.GameMessageType.INVALID_MOVE,
                "Territories are not adjacent"
            ));
            return;
        }

        // Perform fortification
        if (gameManager.fortify(from, to, soldierCount)) {
            // Broadcast the update
            broadcastGameState(gameState.getGameId());
        } else {
            client.sendMessage(new GameMessage(
                GameMessage.GameMessageType.INVALID_MOVE,
                "Fortification failed"
            ));
        }
    }

    private void handleTurnEnd(ClientHandler client, GameState gameState) {
        // Validate turn end
        if (!gameState.getCurrentPlayer().getName().equals(client.getPlayerName())) {
            client.sendMessage(new GameMessage(
                GameMessage.GameMessageType.INVALID_MOVE,
                "It's not your turn"
            ));
            return;
        }

        // End current player's turn
        gameState.getCurrentPlayer().endTurn();
        
        // Move to next turn
        gameState.nextTurn();
        
        // Start next player's turn
        gameState.getCurrentPlayer().startTurn();
        
        // Broadcast the update
        broadcastGameState(gameState.getGameId());
        
        // Notify next player
        Player nextPlayer = gameState.getCurrentPlayer();
        for (ClientHandler ch : gameLobby.getPlayers()) {
            if (ch.getPlayerName().equals(nextPlayer.getName())) {
                ch.sendMessage(new GameMessage(
                    GameMessage.GameMessageType.TURN_STARTED,
                    "Your turn has started"
                ));
                break;
            }
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }
}
