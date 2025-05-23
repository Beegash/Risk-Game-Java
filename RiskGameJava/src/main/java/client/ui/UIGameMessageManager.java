package client.ui;

import client.ClientGame;
import client.ClientMain;
import common.CommonMessages;
import javax.swing.*;
import java.awt.*;

public class UIGameMessageManager {
    private static void showModernDialog(Component parent, String message, String title, Color bgColor, Color fgColor,
            String icon) {
        Frame frame = parent instanceof Frame ? (Frame) parent : (Frame) SwingUtilities.getWindowAncestor(parent);
        JDialog dialog = new JDialog(frame, title, true);
        dialog.setLocationRelativeTo(parent);
        dialog.setResizable(false);
        dialog.setUndecorated(true);

        JPanel panel = new JPanel();
        panel.setBackground(bgColor);
        panel.setBorder(UIConstants.Borders.EMPTY_20);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel msgLabel = new JLabel(
                "<html><div style='text-align:center;font-size:18px;'><span style='font-size:28px;'>" + icon
                        + "</span><br>" + message + "</div></html>",
                SwingConstants.CENTER);
        msgLabel.setFont(UIConstants.Fonts.HEADER);
        msgLabel.setForeground(fgColor);
        msgLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(msgLabel);
        panel.add(Box.createVerticalStrut(16));
        panel.add(Box.createVerticalGlue());

        JButton okBtn = new JButton("OK");
        okBtn.setFont(UIConstants.Fonts.BUTTON);
        okBtn.setBackground(new Color(67, 181, 129));
        okBtn.setForeground(Color.BLACK);
        okBtn.setFocusPainted(false);
        okBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        okBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        okBtn.addActionListener(e -> dialog.dispose());
        panel.add(okBtn);
        panel.add(Box.createVerticalStrut(8));

        dialog.setContentPane(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    public static void showError(Component parent, String message) {
        showModernDialog(parent, message, "Error", new Color(60, 30, 30), Color.RED, "❌");
    }

    public static void showWarning(Component parent, String message) {
        showModernDialog(parent, message, "Warning", new Color(60, 60, 30), new Color(255, 200, 40), "⚠️");
    }

    public static void showInfo(Component parent, String message) {
        showModernDialog(parent, message, "Info", new Color(35, 39, 42), UIConstants.Colors.TEXT_PRIMARY, "ℹ️");
    }

    public static String showInput(Component parent, String message) {
        final String[] result = { null };
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(parent), "Input", true);
        dialog.setResizable(false);
        dialog.setUndecorated(true);
        dialog.setLocationRelativeTo(parent);

        JPanel panel = new JPanel();
        panel.setBackground(new Color(35, 39, 42));
        panel.setBorder(UIConstants.Borders.EMPTY_20);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel msgLabel = new JLabel(
                "<html><div style='text-align:center;font-size:18px;'><span style='font-size:28px;'>✏️</span><br>"
                        + message + "</div></html>",
                SwingConstants.CENTER);
        msgLabel.setFont(UIConstants.Fonts.HEADER);
        msgLabel.setForeground(UIConstants.Colors.TEXT_PRIMARY);
        msgLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(msgLabel);
        panel.add(Box.createVerticalStrut(12));

        JTextField inputField = new JTextField();
        inputField.setFont(UIConstants.Fonts.TEXT);
        inputField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        panel.add(inputField);
        panel.add(Box.createVerticalStrut(16));
        panel.add(Box.createVerticalGlue());

        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.X_AXIS));
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setFont(UIConstants.Fonts.BUTTON);
        cancelBtn.setBackground(new Color(200, 200, 200));
        cancelBtn.setForeground(new Color(40, 40, 40));
        cancelBtn.setFocusPainted(false);
        cancelBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cancelBtn.addActionListener(e -> {
            result[0] = null;
            dialog.dispose();
        });
        JButton okBtn = new JButton("OK");
        okBtn.setFont(UIConstants.Fonts.BUTTON);
        okBtn.setBackground(new Color(67, 181, 129));
        okBtn.setForeground(Color.BLACK);
        okBtn.setFocusPainted(false);
        okBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        okBtn.addActionListener(e -> {
            result[0] = inputField.getText();
            dialog.dispose();
        });
        btnPanel.add(Box.createHorizontalGlue());
        btnPanel.add(cancelBtn);
        btnPanel.add(Box.createHorizontalStrut(16));
        btnPanel.add(okBtn);
        btnPanel.add(Box.createHorizontalGlue());
        panel.add(btnPanel);
        panel.add(Box.createVerticalStrut(8));

        dialog.setContentPane(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
        return result[0];
    }

    public static boolean showConfirm(Component parent, String message) {
        final boolean[] confirmed = { false };
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(parent), "Confirm", true);
        dialog.setResizable(false);
        dialog.setUndecorated(true);
        dialog.setLocationRelativeTo(parent);

        JPanel panel = new JPanel();
        panel.setBackground(new Color(35, 39, 42));
        panel.setBorder(UIConstants.Borders.EMPTY_20);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel msgLabel = new JLabel(
                "<html><div style='text-align:center;font-size:18px;'><span style='font-size:28px;'>❓</span><br>"
                        + message + "</div></html>",
                SwingConstants.CENTER);
        msgLabel.setFont(UIConstants.Fonts.HEADER);
        msgLabel.setForeground(UIConstants.Colors.TEXT_PRIMARY);
        msgLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(msgLabel);
        panel.add(Box.createVerticalStrut(18));
        panel.add(Box.createVerticalGlue());

        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.X_AXIS));
        JButton noBtn = new JButton("No");
        noBtn.setFont(UIConstants.Fonts.BUTTON);
        noBtn.setBackground(new Color(200, 200, 200));
        noBtn.setForeground(new Color(40, 40, 40));
        noBtn.setFocusPainted(false);
        noBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        noBtn.addActionListener(e -> {
            confirmed[0] = false;
            dialog.dispose();
        });
        JButton yesBtn = new JButton("Yes");
        yesBtn.setFont(UIConstants.Fonts.BUTTON);
        yesBtn.setBackground(new Color(67, 181, 129));
        yesBtn.setForeground(Color.BLACK);
        yesBtn.setFocusPainted(false);
        yesBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        yesBtn.addActionListener(e -> {
            confirmed[0] = true;
            dialog.dispose();
        });
        btnPanel.add(Box.createHorizontalGlue());
        btnPanel.add(noBtn);
        btnPanel.add(Box.createHorizontalStrut(16));
        btnPanel.add(yesBtn);
        btnPanel.add(Box.createHorizontalGlue());
        panel.add(btnPanel);
        panel.add(Box.createVerticalStrut(8));

        dialog.setContentPane(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
        return confirmed[0];
    }

    public enum GameOverType {
        WIN, LOSE, PLAYER_LEFT
    }

    public static void showGameOver(Component parent, String message, GameOverType type, ClientGame client) {

        String icon;
        Color borderColor, titleColor;
        String title;
        switch (type) {
            case WIN -> {
                icon = "🏆";
                borderColor = new Color(67, 181, 129);
                titleColor = new Color(67, 181, 129);
                title = "You Win!";
            }
            case LOSE -> {
                icon = "❌";
                borderColor = new Color(220, 53, 69);
                titleColor = new Color(220, 53, 69);
                title = "Defeat";
            }
            case PLAYER_LEFT -> {
                icon = "🚪";
                borderColor = new Color(255, 140, 40);
                titleColor = new Color(255, 140, 40);
                title = "Game Ended";
            }
            default -> {
                icon = "🏁";
                borderColor = new Color(67, 100, 180);
                titleColor = new Color(67, 100, 180);
                title = "Game Over";
            }
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(parent), title, true);
        dialog.setUndecorated(true);
        dialog.setSize(420, 320);
        dialog.setLocationRelativeTo(parent);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(new Color(245, 245, 245));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 4, true),
                BorderFactory.createEmptyBorder(28, 36, 28, 36)));

        JLabel iconLabel = new JLabel(icon, SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 54));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(iconLabel);
        card.add(Box.createVerticalStrut(8));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(titleColor);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(10));

        JLabel msgLabel = new JLabel(
                "<html><div style='text-align:center;font-size:18px;'>" + message + "</div></html>",
                SwingConstants.CENTER);
        msgLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        msgLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(msgLabel);
        card.add(Box.createVerticalStrut(24));

        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.X_AXIS));
        JButton playAgainBtn = new JButton("🔁 Play Again");
        playAgainBtn.setFont(new Font("Segoe UI", Font.BOLD, 19));
        playAgainBtn.setBackground(new Color(67, 181, 129));
        playAgainBtn.setForeground(Color.BLACK);
        playAgainBtn.setFocusPainted(false);
        playAgainBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        playAgainBtn.setBorder(BorderFactory.createLineBorder(new Color(67, 181, 129), 2, true));
        playAgainBtn.setPreferredSize(new Dimension(140, 42));
        JButton exitBtn = new JButton("❌ Exit");
        exitBtn.setFont(new Font("Segoe UI", Font.BOLD, 17));
        exitBtn.setBackground(Color.WHITE);
        exitBtn.setForeground(new Color(220, 53, 69));
        exitBtn.setFocusPainted(false);
        exitBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        exitBtn.setBorder(BorderFactory.createLineBorder(new Color(220, 53, 69), 2, true));
        exitBtn.setPreferredSize(new Dimension(120, 42));
        btnPanel.add(Box.createHorizontalGlue());
        btnPanel.add(playAgainBtn);
        btnPanel.add(Box.createHorizontalStrut(22));
        btnPanel.add(exitBtn);
        btnPanel.add(Box.createHorizontalGlue());
        card.add(btnPanel);
        card.add(Box.createVerticalStrut(8));

        playAgainBtn.addActionListener(e -> {
            dialog.dispose();
            if (parent instanceof JFrame frame)
                frame.dispose();
            SwingUtilities.invokeLater(ClientMain::new);
        });
        exitBtn.addActionListener(e -> {
            if (client != null) {
                client.sendMove(new CommonMessages(CommonMessages.Type.EXIT_GAME));
            }
            dialog.dispose();
            if (parent instanceof JFrame frame)
                frame.dispose();
            System.exit(0);
        });

        card.setAlignmentX(Component.CENTER_ALIGNMENT);
        dialog.setContentPane(card);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    public static void showGameOver(JFrame mainFrame, String message, boolean isWinner, ClientGame client) {
        showGameOver(mainFrame, message, isWinner ? GameOverType.WIN : GameOverType.LOSE, client);
    }
}
