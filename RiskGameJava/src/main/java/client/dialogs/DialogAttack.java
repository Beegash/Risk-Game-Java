/**
 * DialogAttack.java
 * This class manages the attack dialog in the Risk game.
 * It provides a user interface for selecting source and target territories,
 * choosing the number of dice to roll, and initiating attacks.
 */

package client.dialogs;

import common.CommonState;
import common.CommonMessages;
import common.CommonPlayer;
import common.CommonTerritory;
import client.ClientGame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.stream.Collectors;

public class DialogAttack {
    /**
     * Shows the attack dialog for initiating an attack between territories
     * 
     * @param gameState Current state of the game
     * @param currentPlayer Name of the current player
     * @param client Client game instance for sending moves
     */
    public static void show(CommonState gameState, String currentPlayer, ClientGame client) {
        // Create and configure the dialog window
        JDialog dialog = new JDialog((Frame) null, "🎯 Attack Territory", true);
        dialog.setSize(440, 400);
        dialog.setLocationRelativeTo(null);
        dialog.setUndecorated(true);

        // Create and configure the main card panel
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(new Color(245, 245, 245));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(67, 181, 129), 3, true),
                BorderFactory.createEmptyBorder(22, 32, 22, 32)));

        // Create and configure the title label
        JLabel title = new JLabel(
                "<html><div style='text-align:center;font-size:22px;'><span style='font-size:32px;'>⚔️</span><br><b>Attack Territory</b></div></html>",
                SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(title);
        card.add(Box.createVerticalStrut(18));

        // Create and configure the source territory panel
        JPanel fromPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        fromPanel.setOpaque(false);
        JLabel fromLabel = new JLabel("From:");
        fromLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        JComboBox<String> fromBox = new JComboBox<>();
        fromBox.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        fromBox.setPreferredSize(new Dimension(160, 32));
        fromPanel.add(fromLabel);
        fromPanel.add(fromBox);
        JLabel fromInfo = new JLabel();
        fromInfo.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        fromPanel.add(fromInfo);
        card.add(fromPanel);

        // Create and configure the target territory panel
        JPanel toPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        toPanel.setOpaque(false);
        JLabel toLabel = new JLabel("To:");
        toLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        JComboBox<String> toBox = new JComboBox<>();
        toBox.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        toBox.setPreferredSize(new Dimension(160, 32));
        toPanel.add(toLabel);
        toPanel.add(toBox);
        JLabel toInfo = new JLabel();
        toInfo.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        toPanel.add(toInfo);
        card.add(toPanel);

        card.add(Box.createVerticalStrut(14));

        // Create and configure the dice selection panel
        JPanel dicePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        dicePanel.setOpaque(false);
        JLabel diceLabel = new JLabel("Dice:");
        diceLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        JSlider diceSlider = new JSlider(1, 3, 1);
        diceSlider.setMajorTickSpacing(1);
        diceSlider.setPaintTicks(true);
        diceSlider.setPaintLabels(true);
        diceSlider.setSnapToTicks(true);
        diceSlider.setPreferredSize(new Dimension(140, 40));
        JLabel diceIcon = new JLabel("🎲");
        diceIcon.setFont(new Font("Segoe UI", Font.PLAIN, 26));
        dicePanel.add(diceLabel);
        dicePanel.add(diceSlider);
        dicePanel.add(diceIcon);
        card.add(dicePanel);

        card.add(Box.createVerticalStrut(12));

        // Create and configure the error label
        JLabel errorLabel = new JLabel("");
        errorLabel.setForeground(new Color(200, 40, 40));
        errorLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(errorLabel);
        card.add(Box.createVerticalStrut(10));

        // Create and configure the button panel
        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.X_AXIS));
        JButton attackBtn = new JButton("Attack");
        attackBtn.setFont(new Font("Segoe UI", Font.BOLD, 19));
        attackBtn.setBackground(Color.WHITE);
        attackBtn.setForeground(new Color(67, 181, 129));
        attackBtn.setFocusPainted(false);
        attackBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        attackBtn.setBorder(BorderFactory.createLineBorder(new Color(67, 181, 129), 2, true));
        attackBtn.setPreferredSize(new Dimension(120, 38));
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        cancelBtn.setBackground(Color.WHITE);
        cancelBtn.setForeground(new Color(220, 53, 69));
        cancelBtn.setFocusPainted(false);
        cancelBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cancelBtn.setBorder(BorderFactory.createLineBorder(new Color(220, 53, 69), 2, true));
        cancelBtn.setPreferredSize(new Dimension(120, 38));
        btnPanel.add(Box.createHorizontalGlue());
        btnPanel.add(attackBtn);
        btnPanel.add(Box.createHorizontalStrut(18));
        btnPanel.add(cancelBtn);
        btnPanel.add(Box.createHorizontalGlue());
        card.add(btnPanel);

        // Populate source territory combo box with valid territories
        CommonPlayer player = gameState.getPlayers().get(currentPlayer);
        List<String> playerTerritories = player.getTerritories();
        for (String territory : playerTerritories) {
            CommonTerritory t = gameState.getTerritories().get(territory);
            if (t.getArmies() > 1) {
                fromBox.addItem(territory);
            }
        }

        // Add action listener for source territory selection
        fromBox.addActionListener((ActionEvent e) -> {
            String from = (String) fromBox.getSelectedItem();
            toBox.removeAllItems();
            if (from != null) {
                // Update source territory info
                CommonTerritory fromTerritory = gameState.getTerritories().get(from);
                fromInfo.setText(" (" + fromTerritory.getArmies() + " armies)");
                
                // Populate target territory combo box with enemy neighbors
                List<String> neighbors = fromTerritory.getAdjacentTerritories();
                List<String> enemyNeighbors = neighbors.stream()
                        .filter(name -> {
                            CommonTerritory t = gameState.getTerritories().get(name);
                            return t.getOwner() != null && !t.getOwner().equals(currentPlayer);
                        })
                        .collect(Collectors.toList());
                for (String enemy : enemyNeighbors) {
                    toBox.addItem(enemy);
                }
                toInfo.setText("");
            } else {
                fromInfo.setText("");
                toInfo.setText("");
            }
        });

        // Select first source territory if available
        if (fromBox.getItemCount() > 0) {
            fromBox.setSelectedIndex(0);
        }

        // Add action listener for the Attack button
        attackBtn.addActionListener((ActionEvent e) -> {
            String from = (String) fromBox.getSelectedItem();
            String to = (String) toBox.getSelectedItem();
            int dice = diceSlider.getValue();
            
            // Validate attack parameters
            if (from == null || to == null) {
                errorLabel.setText("⚠️ Please select both territories.");
                return;
            }
            CommonTerritory fromTerritory = gameState.getTerritories().get(from);
            if (fromTerritory.getArmies() <= 1) {
                errorLabel.setText("⚠️ Not enough armies to attack.");
                return;
            }
            if (dice > fromTerritory.getArmies() - 1) {
                errorLabel.setText("⚠️ Not enough armies for this many dice.");
                return;
            }
            
            // Send attack move to server
            CommonMessages attack = new CommonMessages(CommonMessages.Type.ATTACK, from, to, dice);
            client.sendMove(attack);
            dialog.dispose();
        });

        // Add action listener for the Cancel button
        cancelBtn.addActionListener(e -> dialog.dispose());

        card.setAlignmentX(Component.CENTER_ALIGNMENT);
        dialog.setContentPane(card);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }
}
