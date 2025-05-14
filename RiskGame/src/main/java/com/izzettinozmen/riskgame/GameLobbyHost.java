package com.izzettinozmen.riskgame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.List;

public class GameLobbyHost extends JFrame {
    private final Client client;
    private final DefaultListModel<String> playerListModel;
    private final JList<String> playerList;
    private final JButton startGameButton;
    private final JButton kickPlayerButton;
    private final JButton banPlayerButton;
    private final JButton closeLobbyButton;

    public GameLobbyHost(Client client) {
        this.client = client;
        this.playerListModel = new DefaultListModel<>();
        this.playerList = new JList<>(playerListModel);
        this.startGameButton = new JButton("Start Game");
        this.kickPlayerButton = new JButton("Kick Player");
        this.banPlayerButton = new JButton("Ban Player");
        this.closeLobbyButton = new JButton("Close Lobby");
        initUI();
        client.setLobbyGUI(this);
        client.startListening();
    }

    private void initUI() {
        setTitle("Game Lobby - Host");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 420);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel label = new JLabel("Players in Lobby:");
        mainPanel.add(label, BorderLayout.NORTH);

        playerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(playerList);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.setPreferredSize(new Dimension(380, 50));
        buttonPanel.add(startGameButton);
        buttonPanel.add(kickPlayerButton);
        buttonPanel.add(banPlayerButton);
        buttonPanel.add(closeLobbyButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Action listeners
        startGameButton.addActionListener(this::onStartGame);
        kickPlayerButton.addActionListener(this::onKickPlayer);
        banPlayerButton.addActionListener(this::onBanPlayer);
        closeLobbyButton.addActionListener(this::onCloseLobby);
    }

    public void updatePlayerList(List<String> names) {
        SwingUtilities.invokeLater(() -> {
            playerListModel.clear();
            for (String name : names) {
                playerListModel.addElement(name);
            }
        });
    }

    private void onStartGame(ActionEvent e) {
        try {
            client.sendMessage(new GameMessage(GameMessage.GameMessageType.START_GAME, null));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error starting game: " + ex.getMessage());
        }
    }

    private void onKickPlayer(ActionEvent e) {
        int selected = playerList.getSelectedIndex();
        if (selected == -1) {
            JOptionPane.showMessageDialog(this, "Please select a player to kick.");
            return;
        }
        String selectedName = playerListModel.getElementAt(selected);
        if (selectedName.contains("(host)")) {
            JOptionPane.showMessageDialog(this, "You cannot kick yourself (host).");
            return;
        }
        // Remove "(host)" suffix if present
        selectedName = selectedName.replace(" (host)", "");
        try {
            client.sendMessage(new GameMessage(GameMessage.GameMessageType.KICK_PLAYER, selectedName));
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error kicking player: " + ex.getMessage());
        }
    }

    private void onBanPlayer(ActionEvent e) {
        int selected = playerList.getSelectedIndex();
        if (selected == -1) {
            JOptionPane.showMessageDialog(this, "Please select a player to ban.");
            return;
        }
        String selectedName = playerListModel.getElementAt(selected);
        if (selectedName.contains("(host)")) {
            JOptionPane.showMessageDialog(this, "You cannot ban yourself (host).");
            return;
        }
        // Remove "(host)" suffix if present
        selectedName = selectedName.replace(" (host)", "");
        try {
            client.sendMessage(new GameMessage(GameMessage.GameMessageType.BAN_PLAYER, selectedName));
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error banning player: " + ex.getMessage());
        }
    }

    private void onCloseLobby(ActionEvent e) {
        client.sendLobbyCloseRequest();
    }
} 
