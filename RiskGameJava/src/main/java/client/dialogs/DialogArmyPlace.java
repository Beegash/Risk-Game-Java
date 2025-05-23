/**
 * DialogArmyPlace.java
 * This class manages the dialog for placing armies on territories during the game.
 * It provides a slider interface for selecting the number of armies to place
 * and handles the validation and submission of army placement moves.
 */

package client.dialogs;

import common.CommonState;
import common.CommonMessages;
import common.CommonPlayer;
import common.CommonTerritory;
import client.ClientGame;

import javax.swing.*;
import java.awt.*;

public class DialogArmyPlace {
    /**
     * Shows the army placement dialog for a specific territory
     * 
     * @param gameState Current state of the game
     * @param currentPlayer Name of the current player
     * @param countryName Name of the territory to place armies on
     * @param client Client game instance for sending moves
     */
    public static void show(CommonState gameState, String currentPlayer, String countryName, ClientGame client) {
        // Get player and check available armies
        CommonPlayer player = gameState.getPlayers().get(currentPlayer);
        int availableArmies = player.getAvailableArmies();
        if (availableArmies <= 0) {
            JOptionPane.showMessageDialog(null, "You have no armies left to place.", "Info",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Validate territory
        CommonTerritory territory = gameState.getTerritories().get(countryName);
        if (territory == null) {
            JOptionPane.showMessageDialog(null, "Invalid territory.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create and configure the dialog window
        JDialog dialog = new JDialog((Frame) null, "🪖 Place Armies", true);
        dialog.setSize(420, 300);
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
                "<html><div style='text-align:center;font-size:22px;'><span style='font-size:32px;'>🪖</span><br><b>Place Armies</b></div></html>",
                SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(title);
        card.add(Box.createVerticalStrut(16));

        // Create and configure the country information panel
        JPanel countryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        countryPanel.setOpaque(false);
        JLabel countryLabel = new JLabel("Country:");
        countryLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        JLabel countryNameLabel = new JLabel(countryName);
        countryNameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        countryNameLabel.setForeground(new Color(67, 100, 180));
        JLabel countryInfo = new JLabel(" (" + territory.getArmies() + " armies)");
        countryInfo.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        countryPanel.add(countryLabel);
        countryPanel.add(countryNameLabel);
        countryPanel.add(countryInfo);
        card.add(countryPanel);

        // Create and configure the available armies label
        JLabel availableLabel = new JLabel("Available armies: " + availableArmies);
        availableLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        availableLabel.setForeground(new Color(67, 181, 129));
        availableLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(availableLabel);
        card.add(Box.createVerticalStrut(10));

        // Create and configure the army count slider panel
        JPanel sliderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        sliderPanel.setOpaque(false);
        JLabel countLabel = new JLabel("Count:");
        countLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        JSlider armySlider = new JSlider(1, availableArmies, 1);
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

        // Configure slider labels based on available armies
        if (availableArmies <= 10) {
            armySlider.setPaintLabels(true);
        } else {
            java.util.Hashtable<Integer, JLabel> labelTable = new java.util.Hashtable<>();
            labelTable.put(1, new JLabel("1"));
            int mid = 1 + (availableArmies - 1) / 2;
            labelTable.put(mid, new JLabel("..."));
            labelTable.put(availableArmies, new JLabel(String.valueOf(availableArmies)));
            armySlider.setLabelTable(labelTable);
            armySlider.setPaintLabels(true);
        }

        // Add change listener to update selected count label
        armySlider.addChangeListener(e -> {
            selectedCountLabel.setText(String.valueOf(armySlider.getValue()));
        });

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
        JButton okBtn = new JButton("Place");
        okBtn.setFont(new Font("Segoe UI", Font.BOLD, 19));
        okBtn.setBackground(Color.WHITE);
        okBtn.setForeground(new Color(67, 181, 129));
        okBtn.setFocusPainted(false);
        okBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        okBtn.setBorder(BorderFactory.createLineBorder(new Color(67, 181, 129), 2, true));
        okBtn.setPreferredSize(new Dimension(120, 38));
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        cancelBtn.setBackground(Color.WHITE);
        cancelBtn.setForeground(new Color(220, 53, 69));
        cancelBtn.setFocusPainted(false);
        cancelBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cancelBtn.setBorder(BorderFactory.createLineBorder(new Color(220, 53, 69), 2, true));
        cancelBtn.setPreferredSize(new Dimension(120, 38));
        btnPanel.add(Box.createHorizontalGlue());
        btnPanel.add(okBtn);
        btnPanel.add(Box.createHorizontalStrut(18));
        btnPanel.add(cancelBtn);
        btnPanel.add(Box.createHorizontalGlue());
        card.add(btnPanel);

        // Add action listener for the Place button
        okBtn.addActionListener(e -> {
            int count = armySlider.getValue();
            if (count < 1 || count > availableArmies) {
                errorLabel.setText("⚠️ Invalid army count.");
                return;
            }
            CommonMessages move = new CommonMessages(CommonMessages.Type.PLACE_ARMY, null, countryName, count);
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
