/**
 * DialogRules.java
 * This class manages the rules dialog in the Risk game.
 * It provides a modern and user-friendly interface for displaying game rules.
 */

package client.dialogs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class DialogRules {
    /**
     * Shows the rules dialog with game rules and instructions
     */
    public static void show() {
        // Create and configure the dialog window
        JDialog dialog = new JDialog((Frame) null, "📖 Game Rules", true);
        dialog.setSize(600, 700);
        dialog.setLocationRelativeTo(null);
        dialog.setUndecorated(true);

        // Create and configure the main card panel
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(new Color(245, 245, 245));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(67, 181, 129), 3, true),
                BorderFactory.createEmptyBorder(20, 25, 20, 25)));

        // Create and configure the title label
        JLabel title = new JLabel(
                "<html><div style='text-align:center;font-size:22px;'><span style='font-size:32px;'>📖</span><br><b>Risk Game Rules</b></div></html>",
                SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(title);
        card.add(Box.createVerticalStrut(20));

        // Create scrollable content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(245, 245, 245));

        // Add rules sections
        addSection(contentPanel, "🎮 Game Overview", 
            "Risk is a strategy board game where two players compete to conquer territories and eliminate their opponent. " +
            "The game is played on a world map divided into territories across six continents.");

        addSection(contentPanel, "🎲 Turn Structure", 
            "Each turn consists of three phases:\n" +
            "1. Place Armies\n" +
            "2. Attack\n" +
            "3. Fortify");

        addSection(contentPanel, "🪖 Army Placement", 
            "At the start of your turn, you receive armies based on:\n" +
            "• Number of territories you control (minimum 3)\n" +
            "• Continent control bonuses:\n" +
            "  - Asia: 7 armies\n" +
            "  - North America: 5 armies\n" +
            "  - Europe: 5 armies\n" +
            "  - Africa: 3 armies\n" +
            "  - South America: 2 armies\n" +
            "  - Oceania: 2 armies\n\n" +
            "You can only place armies on territories you control.");

        addSection(contentPanel, "⚔️ Attacking", 
            "• Attack only adjacent territories\n" +
            "• Attacking territory must have at least 2 armies\n" +
            "• Attacker can roll up to 3 dice (territory armies - 1)\n" +
            "• Defender can roll up to 2 dice\n" +
            "• Highest dice wins, defender wins ties\n" +
            "• If defender loses all armies, territory is captured\n" +
            "• Must move at least 1 army to captured territory");

        addSection(contentPanel, "🛡️ Fortification", 
            "• Move armies between your territories\n" +
            "• Territories must be adjacent\n" +
            "• Must leave at least 1 army behind\n" +
            "• Optional phase");

        addSection(contentPanel, "🏆 Victory", 
            "The game is won by eliminating your opponent's armies and capturing all territories.");

        // Create scroll pane
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setPreferredSize(new Dimension(520, 480));
        scrollPane.setBorder(null);
        card.add(scrollPane);

        // Create close button
        JButton closeBtn = new JButton("Close");
        closeBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        closeBtn.setBackground(new Color(67, 181, 129));
        closeBtn.setForeground(Color.BLACK);
        closeBtn.setFocusPainted(false);
        closeBtn.setBorderPainted(false);
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeBtn.setPreferredSize(new Dimension(120, 38));

        // Add button panel
        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.X_AXIS));
        btnPanel.setBackground(new Color(245, 245, 245));
        btnPanel.add(Box.createHorizontalGlue());
        btnPanel.add(closeBtn);
        btnPanel.add(Box.createHorizontalGlue());
        card.add(Box.createVerticalStrut(20));
        card.add(btnPanel);

        // Add action listener for close button
        closeBtn.addActionListener((ActionEvent e) -> dialog.dispose());

        // Configure and show dialog
        card.setAlignmentX(Component.CENTER_ALIGNMENT);
        dialog.setContentPane(card);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    /**
     * Adds a section to the rules panel
     * 
     * @param panel Panel to add section to
     * @param title Section title
     * @param content Section content
     */
    private static void addSection(JPanel panel, String title, String content) {
        // Create section panel
        JPanel sectionPanel = new JPanel();
        sectionPanel.setLayout(new BoxLayout(sectionPanel, BoxLayout.Y_AXIS));
        sectionPanel.setBackground(new Color(245, 245, 245));
        sectionPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Add title
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(new Color(67, 181, 129));
        sectionPanel.add(titleLabel);
        sectionPanel.add(Box.createVerticalStrut(10));

        // Add content
        JTextArea contentArea = new JTextArea(content);
        contentArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentArea.setBackground(new Color(245, 245, 245));
        contentArea.setEditable(false);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentArea.setRows(content.split("\n").length);
        contentArea.setMargin(new Insets(0, 0, 0, 0));
        sectionPanel.add(contentArea);

        panel.add(sectionPanel);
    }
}

