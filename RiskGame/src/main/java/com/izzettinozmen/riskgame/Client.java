/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.izzettinozmen.riskgame;

import java.io.*;
import java.net.*;
import java.util.List;
import javax.swing.JFrame;

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
    private Object lobbyGUI; // Can be GameLobbyHost or GameLobbyPlayer

    public Client(String playerName) {
        this.playerName = playerName;
        this.isConnected = false;
    }

    public void setLobbyGUI(Object lobbyGUI) {
        this.lobbyGUI = lobbyGUI;
    }

    public void startListening() {
        Thread listener = new Thread(() -> {
            try {
                while (isConnected) {
                    Object obj = in.readObject();
                    if (obj instanceof GameMessage) {
                        GameMessage msg = (GameMessage) obj;
                        switch (msg.getType()) {
                            case PLAYER_JOINED:
                                if (lobbyGUI instanceof GameLobbyHost) {
                                    ((GameLobbyHost) lobbyGUI).updatePlayerList((List<String>) msg.getData());
                                } else if (lobbyGUI instanceof GameLobbyPlayer) {
                                    ((GameLobbyPlayer) lobbyGUI).updatePlayerList((List<String>) msg.getData());
                                }
                                break;
                            case LOBBY_CLOSED:
                                if (lobbyGUI instanceof JFrame) {
                                    ((JFrame) lobbyGUI).dispose();
                                }
                                StartScreen startScreen = new StartScreen(playerName);
                                startScreen.setVisible(true);
                                isConnected = false;
                                break;
                            case PLAYER_LEFT:
                                if (msg.getData() instanceof String) {
                                    javax.swing.SwingUtilities.invokeLater(() -> {
                                        javax.swing.JOptionPane.showMessageDialog(null, (String) msg.getData(), "Lobby Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                                        if (lobbyGUI instanceof JFrame) {
                                            ((JFrame) lobbyGUI).dispose();
                                        }
                                        StartScreen startScreen2 = new StartScreen(playerName);
                                        startScreen2.setVisible(true);
                                        isConnected = false;
                                    });
                                }
                                break;
                            case PLAYER_KICKED:
                                javax.swing.SwingUtilities.invokeLater(() -> {
                                    javax.swing.JOptionPane.showMessageDialog(null, "You have been kicked from the lobby.", "Kicked", javax.swing.JOptionPane.WARNING_MESSAGE);
                                    if (lobbyGUI instanceof JFrame) {
                                        ((JFrame) lobbyGUI).dispose();
                                    }
                                    StartScreen startScreen3 = new StartScreen(playerName);
                                    startScreen3.setVisible(true);
                                    isConnected = false;
                                });
                                break;
                            case PLAYER_BANNED:
                                javax.swing.SwingUtilities.invokeLater(() -> {
                                    javax.swing.JOptionPane.showMessageDialog(null, "You have been banned from the lobby.", "Banned", javax.swing.JOptionPane.ERROR_MESSAGE);
                                    if (lobbyGUI instanceof JFrame) {
                                        ((JFrame) lobbyGUI).dispose();
                                    }
                                    StartScreen startScreen4 = new StartScreen(playerName);
                                    startScreen4.setVisible(true);
                                    isConnected = false;
                                });
                                break;
                            case GAME_STARTED:
                                javax.swing.SwingUtilities.invokeLater(() -> {
                                    if (lobbyGUI instanceof JFrame) {
                                        ((JFrame) lobbyGUI).dispose();
                                    }
                                    ClientGUI clientGUI = new ClientGUI();
                                    clientGUI.setVisible(true);
                                });
                                break;
                        }
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
}
