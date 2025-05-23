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
    public static void show(CommonState gameState, String playerName, ClientGame client) {
        CommonPlayer player = gameState.getPlayers().get(playerName);
        List<String> ownedTerritories = player.getTerritories();

        List<String> fromOptions = ownedTerritories.stream()
                .filter(name -> gameState.getTerritories().get(name).getArmies() > 1)
                .collect(Collectors.toList());

        if (fromOptions.isEmpty()) {
            UIGameMessageManager.showWarning(null, "You have no territory with more than 1 army to fortify from.");
            return;
        }

        JDialog dialog = new JDialog((Frame) null, "🔄 Fortify", true);
        dialog.setSize(440, 340);
        dialog.setLocationRelativeTo(null);
        dialog.setUndecorated(true);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(new Color(245, 245, 245));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(67, 181, 129), 3, true),
                BorderFactory.createEmptyBorder(22, 32, 22, 32)));

        JLabel title = new JLabel(
                "<html><div style='text-align:center;font-size:22px;'><span style='font-size:32px;'>🔄</span><br><b>Fortify</b></div></html>",
                SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(title);
        card.add(Box.createVerticalStrut(16));

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

        JLabel errorLabel = new JLabel("");
        errorLabel.setForeground(new Color(200, 40, 40));
        errorLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(errorLabel);
        card.add(Box.createVerticalStrut(10));

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
            CommonTerritory fromTerritory = gameState.getTerritories().get(from);
            fromInfo.setText(" (" + fromTerritory.getArmies() + " armies)");

            List<String> validTargets = fromTerritory.getAdjacentTerritories().stream()
                    .filter(tName -> {
                        CommonTerritory t = gameState.getTerritories().get(tName);
                        return t.getOwner().equals(playerName);
                    }).collect(Collectors.toList());
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

            int maxMove = fromTerritory.getArmies() - 1;
            armySlider.setMinimum(1);
            armySlider.setMaximum(maxMove);
            armySlider.setValue(1);
            selectedCountLabel.setText("1");
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

        updateToBoxAndSlider.run();

        fortifyBtn.addActionListener(e -> {
            String from = (String) fromBox.getSelectedItem();
            String to = (String) toBox.getSelectedItem();
            int count = armySlider.getValue();
            if (from == null || to == null) {
                errorLabel.setText("⚠️ Please select both territories.");
                return;
            }
            CommonTerritory fromTerritory = gameState.getTerritories().get(from);
            if (count < 1 || count >= fromTerritory.getArmies()) {
                errorLabel.setText("⚠️ Invalid army count. You must leave at least 1 army behind.");
                return;
            }
            CommonMessages move = new CommonMessages(CommonMessages.Type.FORTIFY, from, to, count);
            client.sendMove(move);
            dialog.dispose();
        });
        cancelBtn.addActionListener(e -> dialog.dispose());

        card.setAlignmentX(Component.CENTER_ALIGNMENT);
        dialog.setContentPane(card);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }
}
