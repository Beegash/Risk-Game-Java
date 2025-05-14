package com.izzettinozmen.riskgame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class GameLobbyPlayer extends JFrame {
    private final Client client;
    private final DefaultListModel<String> playerListModel;
    private final JList<String> playerList;
    private final JButton leaveLobbyButton;

    public GameLobbyPlayer(Client client) {
        this.client = client;
        this.playerListModel = new DefaultListModel<>();
        this.playerList = new JList<>(playerListModel);
        this.leaveLobbyButton = new JButton("Leave Lobby");
        initUI();
        client.setLobbyGUI(this);
        client.startListening();
    }

    private void initUI() {
        setTitle("Game Lobby - Player");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel label = new JLabel("Players in Lobby:");
        mainPanel.add(label, BorderLayout.NORTH);

        playerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(playerList);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttonPanel.add(leaveLobbyButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        leaveLobbyButton.addActionListener(this::onLeaveLobby);
    }

    public void updatePlayerList(List<String> names) {
        SwingUtilities.invokeLater(() -> {
            playerListModel.clear();
            for (String name : names) {
                playerListModel.addElement(name);
            }
        });
    }

    private void onLeaveLobby(ActionEvent e) {
        client.disconnect();
        this.dispose();
        StartScreen startScreen = new StartScreen(client.getPlayerName());
        startScreen.setVisible(true);
    }
} 
