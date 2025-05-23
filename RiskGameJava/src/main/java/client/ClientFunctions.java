package client;

import common.CommonState;
import common.CommonMessages;
import common.CommonPlayer;
import common.CommonTerritory;
import client.ui.*;
import client.dialogs.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ClientFunctions implements ClientMessageListener {
    private ClientGame client;
    private ClientBoard boardPanel;
    private UIChatPanel chatPanel;
    private CommonState currentGameState;
    private String currentPlayerName;
    private UIActionButtonsPanel actionButtonsPanel;
    private UIPlayerInfoPanel playerInfoPanel;
    private JFrame mainFrame;
    private DialogWaiting waitingDialog;
    private UIHeaderPanel headerPanel;

    public ClientFunctions(String playerName) {
        this.currentPlayerName = (playerName == null || playerName.trim().isEmpty()) ? "Player" : playerName;
        initUI();
        connectToServer();
    }

    private void initUI() {
        mainFrame = new JFrame("Risk - " + currentPlayerName);
        mainFrame.setSize(UIConstants.Dimensions.MAIN_WINDOW);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setResizable(false);
        mainFrame.getContentPane().setBackground(UIConstants.Colors.BACKGROUND);

        headerPanel = new UIHeaderPanel();
        mainFrame.add(headerPanel, BorderLayout.NORTH);

        JPanel mapCard = new JPanel(new BorderLayout());
        mapCard.setBackground(UIConstants.Colors.CARD_BACKGROUND);
        mapCard.setBorder(UIConstants.Borders.CARD_BORDER);
        boardPanel = new ClientBoard(this::handleCountryClick);
        boardPanel.setBackground(UIConstants.Colors.CARD_BACKGROUND);
        mapCard.add(boardPanel, BorderLayout.CENTER);

        JPanel rightCard = new JPanel(new BorderLayout());
        rightCard.setBackground(UIConstants.Colors.CARD_BACKGROUND);
        rightCard.setBorder(UIConstants.Borders.CARD_BORDER);

        playerInfoPanel = new UIPlayerInfoPanel();
        rightCard.add(playerInfoPanel, BorderLayout.NORTH);

        actionButtonsPanel = new UIActionButtonsPanel(
                e -> handleAttack(),
                e -> handleEndTurn(),
                e -> handleFortify());
        rightCard.add(actionButtonsPanel, BorderLayout.CENTER);

        JButton exitBtn = createExitButton();
        JPanel exitPanel = new JPanel();
        exitPanel.setBackground(UIConstants.Colors.CARD_BACKGROUND);
        exitPanel.setBorder(UIConstants.Borders.EMPTY_20);
        exitPanel.add(exitBtn);
        rightCard.add(exitPanel, BorderLayout.SOUTH);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(UIConstants.Colors.BACKGROUND);
        centerPanel.setBorder(UIConstants.Borders.EMPTY_20);
        centerPanel.add(mapCard, BorderLayout.CENTER);
        centerPanel.add(rightCard, BorderLayout.EAST);

        chatPanel = new UIChatPanel();

        mainFrame.add(centerPanel, BorderLayout.CENTER);
        mainFrame.add(chatPanel, BorderLayout.SOUTH);
        mainFrame.setVisible(true);
    }

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

    private void handleAttack() {
        if (currentGameState != null && currentGameState.getCurrentTurnPlayer().equals(currentPlayerName)) {
            DialogAttack.show(currentGameState, currentPlayerName, client);
        }
    }

    private void handleEndTurn() {
        if (currentGameState != null && currentGameState.getCurrentTurnPlayer().equals(currentPlayerName)) {
            CommonMessages endTurnMove = new CommonMessages(CommonMessages.Type.END_TURN, null, null, 0);
            client.sendMove(endTurnMove);
        }
    }

    private void handleFortify() {
        if (currentGameState != null && currentGameState.getCurrentTurnPlayer().equals(currentPlayerName)) {
            DialogFortify.show(currentGameState, currentPlayerName, client);
        }
    }

    private void connectToServer() {
        try {
            //client = new ClientGame("localhost", 3131, this);
            client = new ClientGame("13.51.109.85", 3131, this);
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
        CommonPlayer currentPlayer = gameState.getPlayers().get(currentTurn);
        CommonPlayer me = gameState.getPlayers().get(currentPlayerName);
        boolean isMyTurn = currentPlayerName.equals(currentTurn);
        int myRemainingArmies = me.getAvailableArmies();
        int myTerritories = me.getTerritories().size();

        headerPanel.updateTurnPlayer(currentTurn, isMyTurn);

        actionButtonsPanel.updateButtonStates(isMyTurn && myRemainingArmies == 0);

        playerInfoPanel.updatePlayerInfo(me, myRemainingArmies, myTerritories);
    }

    @Override
    public void onChatMessage(String message) {
        chatPanel.appendMessage(message);
    }

    public void onVictory(String message) {
        UIGameMessageManager.showGameOver(mainFrame, message, UIGameMessageManager.GameOverType.WIN, client);
    }

    public void onDefeat(String message) {
        UIGameMessageManager.showGameOver(mainFrame, message, UIGameMessageManager.GameOverType.LOSE, client);
    }

    public void onPlayerLeft(String message) {
        UIGameMessageManager.showGameOver(mainFrame, message, UIGameMessageManager.GameOverType.PLAYER_LEFT, client);
    }

    private void restartApp() {
        SwingUtilities.invokeLater(() -> new ClientFunctions(currentPlayerName));
    }

    public static void launchGame(String playerName) {
        SwingUtilities.invokeLater(() -> new ClientFunctions(playerName));
    }
}
