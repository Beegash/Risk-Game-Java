/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.izzettinozmen.riskgame;

import java.io.*;
import java.net.*;
import java.util.List;
import javax.swing.JFrame;
import java.util.Map;
import java.util.HashMap;

/**
 *
 * @author ifozmen
 */
public class Client {
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 3131;
    
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private boolean isConnected;
    private ClientGUI gui;
    private String playerName;
    private String currentGameId;
    private JFrame lobbyGUI; // Can be GameLobbyHost or GameLobbyPlayer
    private boolean isCreatingGUI = false; // Flag to track GUI creation
    private GameState currentGameState; // Mevcut oyun durumunu saklamak için

    public Client(String playerName) {
        this.playerName = playerName;
        this.isConnected = false;
    }

    // Mevcut GameState'i döndüren getter metodu
    public GameState getCurrentGameState() {
        return currentGameState;
    }

    public void setLobbyGUI(JFrame lobbyGUI) {
        this.lobbyGUI = lobbyGUI;
    }

    public void startListening() {
        Thread listener = new Thread(() -> {
            try {
                while (isConnected) {
                    Object obj = in.readObject();
                    if (obj instanceof GameMessage) {
                        GameMessage msg = (GameMessage) obj;
                        handleMessage(msg);
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                // Connection closed or error
            }
        });
        listener.start();
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getPlayerName() {
        return playerName;
    }

    public boolean connect() {
        try {
            socket = new Socket(SERVER_IP, SERVER_PORT);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            isConnected = true;
            return true;
        } catch (IOException e) {
            System.out.println("Error connecting to server: " + e.getMessage());
            return false;
        }
    }

    public void disconnect() {
        isConnected = false;
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            System.out.println("Error disconnecting: " + e.getMessage());
        }
    }

    public boolean createLobby() {
        if (!isConnected) {
            System.out.println("Cannot create lobby: Not connected to server");
            return false;
        }
        try {
            System.out.println("Sending create lobby request...");
            sendMessage(new GameMessage(GameMessage.GameMessageType.CREATE_LOBBY, playerName));
            GameMessage response = (GameMessage) in.readObject();
            System.out.println("Received response for create lobby: " + response.getType());
            
            if (response.getType() == GameMessage.GameMessageType.PLAYER_JOINED) {
                // Lobi başarıyla oluşturuldu ve oyuncu listesi geldi
                return true;
            } else if (response.getType() == GameMessage.GameMessageType.PLAYER_LEFT) {
                // Lobi oluşturulamadı
                return false;
            }
            return false;
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error creating lobby: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean joinLobby() {
        if (!isConnected) {
            System.out.println("Cannot join lobby: Not connected to server");
            return false;
        }
        try {
            System.out.println("Sending join lobby request...");
            sendMessage(new GameMessage(GameMessage.GameMessageType.JOIN_LOBBY, playerName));
            GameMessage response = (GameMessage) in.readObject();
            System.out.println("Received response for join lobby: " + response.getType());
            
            if (response.getType() == GameMessage.GameMessageType.PLAYER_JOINED) {
                // Lobiye başarıyla katıldı ve oyuncu listesi geldi
                return true;
            } else if (response.getType() == GameMessage.GameMessageType.PLAYER_LEFT) {
                // Lobiye katılamadı
                return false;
            }
            return false;
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error joining lobby: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public void sendLobbyCloseRequest() {
        try {
            sendMessage(new GameMessage(GameMessage.GameMessageType.LOBBY_CLOSED, null));
        } catch (IOException e) {
            System.out.println("Error sending lobby close request: " + e.getMessage());
        }
    }

    public void sendMessage(Object message) throws IOException {
        out.writeObject(message);
        out.flush();
    }

    public void setGUI(ClientGUI gui) {
        this.gui = gui;
    }

    private void handleMessage(Object message) {
        if (message instanceof GameMessage) {
            GameMessage gameMessage = (GameMessage) message;
            System.out.println("Received message type: " + gameMessage.getType());
            
            switch (gameMessage.getType()) {
                case LOBBY_CREATED:
                    handleLobbyCreated(gameMessage);
                    break;
                case LOBBY_JOINED:
                    handleLobbyJoined(gameMessage);
                    break;
                case PLAYER_JOINED:
                    handlePlayerJoined(gameMessage);
                    break;
                case PLAYER_LEFT:
                    handlePlayerLeft(gameMessage);
                    break;
                case PLAYER_KICKED:
                    handlePlayerKicked(gameMessage);
                    break;
                case PLAYER_BANNED:
                    handlePlayerBanned(gameMessage);
                    break;
                case LOBBY_CLOSED:
                    handleLobbyClosed(gameMessage);
                    break;
                case GAME_STARTED:
                    handleGameStarted(gameMessage);
                    break;
                case GAME_STATE_UPDATE:
                    handleGameStateUpdate(gameMessage);
                    break;
                case TURN_STARTED:
                    handleTurnStarted(gameMessage);
                    break;
                case TURN_ENDED:
                    handleTurnEnded(gameMessage);
                    break;
                case TERRITORY_SELECTED:
                    handleTerritorySelected(gameMessage);
                    break;
                case SOLDIERS_PLACED:
                    handleSoldiersPlaced(gameMessage);
                    break;
                case ATTACK_MADE:
                    handleAttackMade(gameMessage);
                    break;
                case FORTIFICATION_MADE:
                    handleFortificationMade(gameMessage);
                    break;
                case GAME_OVER:
                    handleGameOver(gameMessage);
                    break;
                case ERROR_MESSAGE:
                case INVALID_MOVE:
                case NOT_YOUR_TURN:
                    handleErrorMessage(gameMessage);
                    break;
                case PHASE_CHANGED:
                    handlePhaseChanged(gameMessage);
                    break;
            }
        }
    }

    private void handleLobbyCreated(GameMessage message) {
        boolean success = (boolean) message.getData();
        if (success) {
            System.out.println("Lobby created successfully");
            // Player list will be received in a separate PLAYER_JOINED message
        } else {
            System.out.println("Failed to create lobby");
            // Update UI to show lobby creation failure
        }
    }

    private void handleLobbyJoined(GameMessage message) {
        boolean success = (boolean) message.getData();
        if (success) {
            System.out.println("Joined lobby successfully");
            // Player list will be received in a separate PLAYER_JOINED message
        } else {
            System.out.println("Failed to join lobby");
            // Update UI to show lobby join failure
        }
    }

    private void handlePlayerJoined(GameMessage message) {
        try {
            List<String> playerList = (List<String>) message.getData();
            System.out.println("Player list updated: " + playerList);
            
            // Update UI based on lobby type
            if (lobbyGUI instanceof GameLobbyHost) {
                ((GameLobbyHost) lobbyGUI).updatePlayerList(playerList);
            } else if (lobbyGUI instanceof GameLobbyPlayer) {
                ((GameLobbyPlayer) lobbyGUI).updatePlayerList(playerList);
            }
        } catch (ClassCastException e) {
            System.out.println("Error updating player list: " + e.getMessage());
        }
    }

    private void handlePlayerLeft(GameMessage message) {
        try {
            List<String> playerList = (List<String>) message.getData();
            System.out.println("Player list updated after player left: " + playerList);
            
            // Update UI based on lobby type
            if (lobbyGUI instanceof GameLobbyHost) {
                ((GameLobbyHost) lobbyGUI).updatePlayerList(playerList);
            } else if (lobbyGUI instanceof GameLobbyPlayer) {
                ((GameLobbyPlayer) lobbyGUI).updatePlayerList(playerList);
            }
        } catch (ClassCastException e) {
            System.out.println("Error updating player list: " + e.getMessage());
        }
    }

    private void handlePlayerKicked(GameMessage message) {
        String reason = (String) message.getData();
        System.out.println("You have been kicked: " + reason);
        // Update UI to show kick message and return to main menu
    }

    private void handlePlayerBanned(GameMessage message) {
        String reason = (String) message.getData();
        System.out.println("You have been banned: " + reason);
        // Update UI to show ban message and return to main menu
    }

    private void handleLobbyClosed(GameMessage message) {
        System.out.println("Lobby has been closed");
        // Update UI to return to main menu
    }

    private void handleGameStarted(GameMessage message) {
        String gameId = (String) message.getData();
        System.out.println("Game started with ID: " + gameId);
        this.currentGameId = gameId;
        
        // Only create a new GUI if one doesn't already exist and we're not already creating one
        if (gui == null && lobbyGUI != null && !isCreatingGUI) {
            // Set the flag to indicate we're starting to create a GUI
            isCreatingGUI = true;
            
            // Hide the lobby GUI
            lobbyGUI.setVisible(false);
            
            // Create the game GUI
            java.awt.EventQueue.invokeLater(() -> {
                ClientGUI gameGUI = new ClientGUI();
                gameGUI.setClient(this);
                gameGUI.setThisPlayer(new Player(playerName));
                gameGUI.setVisible(true);
                gameGUI.addLogMessage("Game started with ID: " + gameId);
                
                // Set the game GUI reference
                this.gui = gameGUI;
                
                // GUI creation is complete
                isCreatingGUI = false;
            });
        } else if (gui != null) {
            gui.addLogMessage("Game started with ID: " + gameId);
        }
    }

    private void handleGameStateUpdate(GameMessage message) {
        try {
            GameState gameState = (GameState) message.getData();
            this.currentGameState = gameState; // Mevcut oyun durumunu güncelle
            System.out.println("Game state updated");
            
            // Debug bilgisi yazdır
            System.out.println("Territory count in GameState: " + gameState.getTerritories().size());
            int territoriesWithOwner = 0;
            for (Territory t : gameState.getTerritories()) {
                if (t.getOwner() != null) {
                    territoriesWithOwner++;
                }
            }
            System.out.println("Territories with owner: " + territoriesWithOwner);
            
            // If we're already creating a GUI, queue this update to be processed when the GUI is ready
            if (isCreatingGUI) {
                java.awt.EventQueue.invokeLater(() -> {
                    // By the time this runs, the GUI should be created
                    if (gui != null) {
                        gui.updateGameState(gameState);
                    }
                });
                return;
            }
            
            // Ensure the game GUI exists when we get a game state update
            if (gui == null && !isCreatingGUI) {
                // Set the flag to indicate we're starting to create a GUI
                isCreatingGUI = true;
                
                // If the GUI doesn't exist yet, create it
                java.awt.EventQueue.invokeLater(() -> {
                    ClientGUI gameGUI = new ClientGUI();
                    gameGUI.setClient(this);
                    // Find our player in the game state
                    for (Player player : gameState.getPlayers()) {
                        if (player.getName().equals(playerName)) {
                            gameGUI.setThisPlayer(player);
                            break;
                        }
                    }
                    gameGUI.setVisible(true);
                    gameGUI.addLogMessage("Received initial game state");
                    
                    // Set the game GUI reference
                    this.gui = gameGUI;
                    
                    // Close lobby GUI if it exists
                    if (lobbyGUI != null) {
                        lobbyGUI.setVisible(false);
                    }
                    
                    // Update state after GUI is created
                    gameGUI.updateGameState(gameState);
                    
                    // GUI creation is complete
                    isCreatingGUI = false;
                });
            } else {
                gui.updateGameState(gameState);
            }
        } catch (Exception e) {
            System.err.println("Error in handleGameStateUpdate: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleTurnStarted(GameMessage message) {
        String turnMessage = (String) message.getData();
        System.out.println("Turn started: " + turnMessage);
        
        if (gui != null) {
            gui.addLogMessage(turnMessage);
        }
    }

    private void handleTurnEnded(GameMessage message) {
        System.out.println("Turn ended");
        
        if (gui != null) {
            gui.addLogMessage("Turn ended");
        }
    }

    private void handleTerritorySelected(GameMessage message) {
        String territoryName = (String) message.getData();
        String playerName = message.getSender();
        System.out.println("Territory selected: " + territoryName + " by " + playerName);
        
        if (gui != null) {
            gui.highlightTerritory(territoryName, true);
            gui.addLogMessage(playerName + " selected " + territoryName);
        }
    }

    private void handleSoldiersPlaced(GameMessage message) {
        String placementMessage = (String) message.getData();
        System.out.println("Soldiers placed: " + placementMessage);
        
        if (gui != null) {
            gui.addLogMessage(placementMessage);
            
            // Kullanıcı arayüzünde asker yerleştirme mesajını belirgin hale getirmek için
            gui.addLogMessage("Waiting for updated game state...");
        }
    }

    private void handleAttackMade(GameMessage message) {
        String attackMessage = (String) message.getData();
        System.out.println("Attack made: " + attackMessage);
        
        if (gui != null) {
            gui.addLogMessage(attackMessage);
        }
    }

    private void handleFortificationMade(GameMessage message) {
        String fortificationMessage = (String) message.getData();
        System.out.println("Fortification made: " + fortificationMessage);
        
        if (gui != null) {
            gui.addLogMessage(fortificationMessage);
        }
    }

    private void handleGameOver(GameMessage message) {
        String winnerMessage = (String) message.getData();
        System.out.println("Game Over: " + winnerMessage);
        
        if (gui != null) {
            gui.addLogMessage("GAME OVER: " + winnerMessage);
            gui.showGameOverMessage(winnerMessage);
        }
    }

    private void handleErrorMessage(GameMessage message) {
        String errorMessage = (String) message.getData();
        System.out.println("Error: " + errorMessage);
        
        if (gui != null) {
            gui.addLogMessage("ERROR: " + errorMessage);
            gui.showErrorMessage(errorMessage);
        }
    }
    
    private void handlePhaseChanged(GameMessage message) {
        String phaseMessage = (String) message.getData();
        System.out.println("Phase changed: " + phaseMessage);
        
        if (gui != null) {
            gui.addLogMessage(phaseMessage);
        }
    }
}
