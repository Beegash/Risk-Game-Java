/**
 * DialogFortify.java
 * This class manages the fortification dialog in the Risk game.
 * It provides a user interface for moving armies between owned territories
 * during the fortification phase of a turn.
 */

package client.dialogs;

import common.CommonState;
import common.CommonMessages;
import common.CommonPlayer;
import common.CommonTerritory;
import client.ClientGame;
import client.ui.UIGameMessageManager;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class DialogFortify {
    /**
     * Shows the fortification dialog for moving armies between territories
     * 
     * @param gameState Current state of the game
     * @param playerName Name of the current player
     * @param client Client game instance for sending moves
     */
    public static void show(CommonState gameState, String playerName, ClientGame client) {
        // Get player's territories and filter valid source territories
        CommonPlayer player = gameState.getPlayers().get(playerName);
        List<String> ownedTerritories = player.getTerritories();

        List<String> fromOptions = ownedTerritories.stream()
                .filter(name -> gameState.getTerritories().get(name).getArmies() > 1)
                .collect(Collectors.toList());

        // Check if player has any valid territories to fortify from
        if (fromOptions.isEmpty()) {
            UIGameMessageManager.showWarning(null, "You have no territory with more than 1 army to fortify from.");
            return;
        }

        // Create and configure the dialog window
        JDialog dialog = new JDialog((Frame) null, "🔄 Fortify", true);
        dialog.setSize(440, 340);
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
                "<html><div style='text-align:center;font-size:22px;'><span style='font-size:32px;'>🔄</span><br><b>Fortify</b></div></html>",
                SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(title);
        card.add(Box.createVerticalStrut(16));

        // Create and configure the source territory panel
        JPanel fromPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        fromPanel.setOpaque(false);
        JLabel fromLabel = new JLabel("From:");
        fromLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        JComboBox<String> fromBox = new JComboBox<>(fromOptions.toArray(new String[0]));
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

        // Create and configure the army count slider panel
        JPanel sliderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        sliderPanel.setOpaque(false);
        JLabel countLabel = new JLabel("Count:");
        countLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        JSlider armySlider = new JSlider(1, 1, 1);
        armySlider.setMajorTickSpacing(1);
        armySlider.setPaintTicks(true);
        armySlider.setSnapToTicks(true);
        armySlider.setPreferredSize(new Dimension(180, 40));
        JLabel selectedCountLabel = new JLabel("1");
        selectedCountLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        selectedCountLabel.setForeground(new Color(67, 181, 129));
        JLabel armyIcon = new JLabel("🪖");
        armyIcon.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        sliderPanel.add(countLabel);
        sliderPanel.add(armySlider);
        sliderPanel.add(selectedCountLabel);
        sliderPanel.add(armyIcon);
        card.add(sliderPanel);

        card.add(Box.createVerticalStrut(10));

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
        JButton fortifyBtn = new JButton("Fortify");
        fortifyBtn.setFont(new Font("Segoe UI", Font.BOLD, 19));
        fortifyBtn.setBackground(Color.WHITE);
        fortifyBtn.setForeground(new Color(67, 181, 129));
        fortifyBtn.setFocusPainted(false);
        fortifyBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        fortifyBtn.setBorder(BorderFactory.createLineBorder(new Color(67, 181, 129), 2, true));
        fortifyBtn.setPreferredSize(new Dimension(120, 38));
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        cancelBtn.setBackground(Color.WHITE);
        cancelBtn.setForeground(new Color(220, 53, 69));
        cancelBtn.setFocusPainted(false);
        cancelBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cancelBtn.setBorder(BorderFactory.createLineBorder(new Color(220, 53, 69), 2, true));
        cancelBtn.setPreferredSize(new Dimension(120, 38));
        btnPanel.add(Box.createHorizontalGlue());
        btnPanel.add(fortifyBtn);
        btnPanel.add(Box.createHorizontalStrut(18));
        btnPanel.add(cancelBtn);
        btnPanel.add(Box.createHorizontalGlue());
        card.add(btnPanel);

        // Define the update logic for target territory and slider
        Runnable updateToBoxAndSlider = () -> {
            String from = (String) fromBox.getSelectedItem();
            toBox.removeAllItems();
            if (from == null) {
                fromInfo.setText("");
                toInfo.setText("");
                armySlider.setMinimum(1);
                armySlider.setMaximum(1);
                armySlider.setValue(1);
                selectedCountLabel.setText("1");
                toBox.setEnabled(false);
                fortifyBtn.setEnabled(false);
                return;
            }

            // Update source territory info
            CommonTerritory fromTerritory = gameState.getTerritories().get(from);
            fromInfo.setText(" (" + fromTerritory.getArmies() + " armies)");

            // Filter and populate valid target territories
            List<String> validTargets = fromTerritory.getAdjacentTerritories().stream()
                    .filter(tName -> {
                        CommonTerritory t = gameState.getTerritories().get(tName);
                        return t.getOwner().equals(playerName);
                    }).collect(Collectors.toList());

            // Update UI based on available targets
            if (validTargets.isEmpty()) {
                toBox.setEnabled(false);
                toInfo.setText(" (No valid target)");
                fortifyBtn.setEnabled(false);
            } else {
                for (String s : validTargets)
                    toBox.addItem(s);
                toBox.setEnabled(true);
                fortifyBtn.setEnabled(true);

                String to = (String) toBox.getSelectedItem();
                if (to != null) {
                    CommonTerritory toTerritory = gameState.getTerritories().get(to);
                    toInfo.setText(" (" + toTerritory.getArmies() + " armies)");
                } else {
                    toInfo.setText("");
                }
            }

            // Update army slider based on available armies
            int maxMove = fromTerritory.getArmies() - 1;
            armySlider.setMinimum(1);
            armySlider.setMaximum(maxMove);
            armySlider.setValue(1);
            selectedCountLabel.setText("1");

            // Configure slider labels based on maximum value
            if (maxMove <= 10) {
                armySlider.setPaintLabels(true);
            } else {
                java.util.Hashtable<Integer, JLabel> labelTable = new java.util.Hashtable<>();
                labelTable.put(1, new JLabel("1"));
                int mid = 1 + (maxMove - 1) / 2;
                labelTable.put(mid, new JLabel("..."));
                labelTable.put(maxMove, new JLabel(String.valueOf(maxMove)));
                armySlider.setLabelTable(labelTable);
                armySlider.setPaintLabels(true);
            }
        };

        // Add action listeners for UI components
        fromBox.addActionListener(e -> updateToBoxAndSlider.run());
        toBox.addActionListener(e -> {
            String to = (String) toBox.getSelectedItem();
            if (to != null) {
                CommonTerritory toTerritory = gameState.getTerritories().get(to);
                toInfo.setText(" (" + toTerritory.getArmies() + " armies)");
            } else {
                toInfo.setText("");
            }
        });
        armySlider.addChangeListener(e -> selectedCountLabel.setText(String.valueOf(armySlider.getValue())));

        // Initialize the UI state
        updateToBoxAndSlider.run();

        // Add action listener for the Fortify button
        fortifyBtn.addActionListener(e -> {
            String from = (String) fromBox.getSelectedItem();
            String to = (String) toBox.getSelectedItem();
            int count = armySlider.getValue();

            // Validate fortification parameters
            if (from == null || to == null) {
                errorLabel.setText("⚠️ Please select both territories.");
                return;
            }
            CommonTerritory fromTerritory = gameState.getTerritories().get(from);
            if (count < 1 || count >= fromTerritory.getArmies()) {
                errorLabel.setText("⚠️ Invalid army count. You must leave at least 1 army behind.");
                return;
            }

            // Send fortification move to server
            CommonMessages move = new CommonMessages(CommonMessages.Type.FORTIFY, from, to, count);
            client.sendMove(move);
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
