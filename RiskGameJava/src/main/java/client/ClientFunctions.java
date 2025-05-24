/**
 * ClientFunctions.java
 * This class serves as the main controller for the Risk game client.
 * It manages the game UI, handles user interactions, and coordinates communication with the server.
 * Implements ClientMessageListener to receive game updates and messages from the server.
 */

package client;

import common.CommonState;
import common.CommonMessages;
import common.CommonPlayer;
import common.CommonTerritory;
import client.ui.*;
import client.dialogs.*;

import javax.swing.*;
import java.awt.*;

public class ClientFunctions implements ClientMessageListener {
    // Core game components
    private ClientGame client;                          // Server communication handler
    private ClientBoard boardPanel;                     // Game board visualization
    private UIChatPanel chatPanel;                      // Chat interface
    private CommonState currentGameState;               // Current game state
    private String currentPlayerName;                   // Current player's name
    
    // UI components
    private UIActionButtonsPanel actionButtonsPanel;    // Game action buttons
    private UIPlayerInfoPanel playerInfoPanel;          // Player information display
    private JFrame mainFrame;                           // Main game window
    private DialogWaiting waitingDialog;                // Waiting dialog
    private UIHeaderPanel headerPanel;                  // Game header

    /**
     * Constructor for ClientFunctions
     * Initializes the game with the player's name and sets up the UI
     * 
     * @param playerName The name of the current player
     */
    public ClientFunctions(String playerName) {
        this.currentPlayerName = (playerName == null || playerName.trim().isEmpty()) ? "Player" : playerName;
        initUI();
        connectToServer();
    }

    /**
     * Initializes the game's user interface
     * Creates and configures all UI components including the game board,
     * player info panel, action buttons, and chat panel
     */
    private void initUI() {
        // Configure main window
        mainFrame = new JFrame("Risk - " + currentPlayerName);
        mainFrame.setSize(UIConstants.Dimensions.MAIN_WINDOW);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setResizable(false);
        mainFrame.getContentPane().setBackground(UIConstants.Colors.BACKGROUND);

        // Add header panel
        headerPanel = new UIHeaderPanel();
        mainFrame.add(headerPanel, BorderLayout.NORTH);

        // Create and configure game board panel
        JPanel mapCard = new JPanel(new BorderLayout());
        mapCard.setBackground(UIConstants.Colors.CARD_BACKGROUND);
        mapCard.setBorder(UIConstants.Borders.CARD_BORDER);
        boardPanel = new ClientBoard(this::handleCountryClick);
        boardPanel.setBackground(UIConstants.Colors.CARD_BACKGROUND);
        mapCard.add(boardPanel, BorderLayout.CENTER);

        // Create and configure right panel with player info and actions
        JPanel rightCard = new JPanel(new BorderLayout());
        rightCard.setBackground(UIConstants.Colors.CARD_BACKGROUND);
        rightCard.setBorder(UIConstants.Borders.CARD_BORDER);

        playerInfoPanel = new UIPlayerInfoPanel();
        rightCard.add(playerInfoPanel, BorderLayout.NORTH);

        // Add action buttons
        actionButtonsPanel = new UIActionButtonsPanel(
                e -> handleAttack(),
                e -> handleEndTurn(),
                e -> handleFortify());
        rightCard.add(actionButtonsPanel, BorderLayout.CENTER);

        // Add exit button
        JButton exitBtn = createExitButton();
        JPanel exitPanel = new JPanel();
        exitPanel.setBackground(UIConstants.Colors.CARD_BACKGROUND);
        exitPanel.setBorder(UIConstants.Borders.EMPTY_20);
        exitPanel.add(exitBtn);
        rightCard.add(exitPanel, BorderLayout.SOUTH);

        // Configure center panel layout
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(UIConstants.Colors.BACKGROUND);
        centerPanel.setBorder(UIConstants.Borders.EMPTY_20);
        centerPanel.add(mapCard, BorderLayout.CENTER);
        centerPanel.add(rightCard, BorderLayout.EAST);

        // Add chat panel
        chatPanel = new UIChatPanel();

        mainFrame.add(centerPanel, BorderLayout.CENTER);
        mainFrame.add(chatPanel, BorderLayout.SOUTH);
        mainFrame.setVisible(true);
    }

    /**
     * Creates and configures the exit button
     * 
     * @return Configured JButton for exiting the game
     */
    private JButton createExitButton() {
        JButton exitBtn = new JButton("Exit");
        exitBtn.setFont(UIConstants.Fonts.BUTTON);
        exitBtn.setBackground(UIConstants.Colors.BUTTON_EXIT);
        exitBtn.setForeground(UIConstants.Colors.TEXT_PRIMARY);
        exitBtn.setFocusPainted(false);
        exitBtn.setMaximumSize(UIConstants.Dimensions.BUTTON_SIZE);
        exitBtn.setPreferredSize(UIConstants.Dimensions.BUTTON_SIZE);
        exitBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.Colors.BORDER, 2),
                BorderFactory.createEmptyBorder(8, 20, 8, 20)));
        exitBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        exitBtn.addActionListener(e -> System.exit(0));
        return exitBtn;
    }

    /**
     * Handles the attack action
     * Shows the attack dialog if it's the player's turn
     */
    private void handleAttack() {
        if (currentGameState != null && currentGameState.getCurrentTurnPlayer().equals(currentPlayerName)) {
            DialogAttack.show(currentGameState, currentPlayerName, client);
        }
    }

    /**
     * Handles the end turn action
     * Sends end turn message to server if it's the player's turn
     */
    private void handleEndTurn() {
        if (currentGameState != null && currentGameState.getCurrentTurnPlayer().equals(currentPlayerName)) {
            CommonMessages endTurnMove = new CommonMessages(CommonMessages.Type.END_TURN, null, null, 0);
            client.sendMove(endTurnMove);
        }
    }

    /**
     * Handles the fortify action
     * Shows the fortify dialog if it's the player's turn
     */
    private void handleFortify() {
        if (currentGameState != null && currentGameState.getCurrentTurnPlayer().equals(currentPlayerName)) {
            DialogFortify.show(currentGameState, currentPlayerName, client);
        }
    }

    /**
     * Establishes connection to the game server
     * Creates a waiting dialog while connecting
     */
    private void connectToServer() {
        try {
            client = new ClientGame("13.51.109.85", 3131, this);
            //client = new ClientGame("localhost", 3131, this);

            CommonMessages joinMove = new CommonMessages(CommonMessages.Type.JOIN, currentPlayerName);
            client.sendMove(joinMove);

            waitingDialog = new DialogWaiting(mainFrame, currentPlayerName, e -> {
                try {
                    client = null;
                } catch (Exception ignored) {
                }
                mainFrame.dispose();
                System.exit(0);
            });
            waitingDialog.setVisible(true);
        } catch (Exception e) {
            UIGameMessageManager.showError(mainFrame, "Connection failed!");
        }
    }

    /**
     * Handles territory clicks on the game board
     * Validates if the player can place armies and shows the army placement dialog
     * 
     * @param countryName Name of the clicked territory
     */
    private void handleCountryClick(String countryName) {
        if (currentGameState == null) {
            return;
        }

        if (!currentGameState.getCurrentTurnPlayer().equals(currentPlayerName)) {
            UIGameMessageManager.showWarning(mainFrame, " It's not your turn!");
            return;
        }

        CommonPlayer currentPlayerObj = currentGameState.getPlayers().get(currentPlayerName);
        if (currentPlayerObj.getAvailableArmies() <= 0) {
            UIGameMessageManager.showInfo(mainFrame,
                    " You've already placed all your armies.\nYou may now Attack, Fortify or End Turn.");
            return;
        }

        CommonTerritory t = currentGameState.getTerritories().get(countryName);
        if (t == null || !t.getOwner().equals(currentPlayerName)) {
            UIGameMessageManager.showError(mainFrame, "You can only place armies on your own territory!");
            return;
        }

        DialogArmyPlace.show(currentGameState, currentPlayerName, countryName, client);
    }

    /**
     * Handles game state updates from the server
     * Updates UI components with new game state information
     * 
     * @param gameState The new game state received from the server
     */
    @Override
    public void onGameStateReceived(CommonState gameState) {
        if (waitingDialog != null && waitingDialog.isVisible()) {
            waitingDialog.setVisible(false);
            waitingDialog.dispose();
            waitingDialog = null;
        }
        System.out.println("Game state updated.");
        this.currentGameState = gameState;
        boardPanel.updateGameState(gameState);
        String currentTurn = gameState.getCurrentTurnPlayer();
        CommonPlayer me = gameState.getPlayers().get(currentPlayerName);
        boolean isMyTurn = currentPlayerName.equals(currentTurn);
        int myRemainingArmies = me.getAvailableArmies();
        int myTerritories = me.getTerritories().size();

        headerPanel.updateTurnPlayer(currentTurn, isMyTurn);
        actionButtonsPanel.updateButtonStates(isMyTurn && myRemainingArmies == 0);
        playerInfoPanel.updatePlayerInfo(me, myRemainingArmies, myTerritories);
    }

    /**
     * Handles chat messages from the server
     * 
     * @param message The chat message to display
     */
    @Override
    public void onChatMessage(String message) {
        chatPanel.appendMessage(message);
    }

    /**
     * Handles victory notification
     * 
     * @param message Victory message to display
     */
    public void onVictory(String message) {
        UIGameMessageManager.showGameOver(mainFrame, message, UIGameMessageManager.GameOverType.WIN, client);
    }

    /**
     * Handles defeat notification
     * 
     * @param message Defeat message to display
     */
    public void onDefeat(String message) {
        UIGameMessageManager.showGameOver(mainFrame, message, UIGameMessageManager.GameOverType.LOSE, client);
    }

    /**
     * Handles player left notification
     * 
     * @param message Message about player leaving
     */
    public void onPlayerLeft(String message) {
        UIGameMessageManager.showGameOver(mainFrame, message, UIGameMessageManager.GameOverType.PLAYER_LEFT, client);
    }

    /**
     * Launches a new game instance
     * 
     * @param playerName Name of the player starting the game
     */
    public static void launchGame(String playerName) {
        SwingUtilities.invokeLater(() -> new ClientFunctions(playerName));
    }
}
