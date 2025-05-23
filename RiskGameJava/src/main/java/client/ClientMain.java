/**
 * ClientMain.java
 * This is the main entry point for the Risk game client application.
 * It creates the initial game window with start screen, handles user interactions,
 * and manages the connection to the game server.
 */

package client;

import javax.swing.*;
import java.awt.*;
import client.dialogs.*;

public class ClientMain extends JFrame {
    /**
     * Constructor for the main client window
     * Sets up the initial game interface with background, logo, and control buttons
     */
    public ClientMain() {
        // Configure the main window properties
        setTitle("Risk - Start Screen");
        setSize(500, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Create a custom panel with background image
        JPanel backgroundPanel = new JPanel() {
            Image bg = new ImageIcon(getClass().getResource("/images/RiskBackground.jpg")).getImage();

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
            }
        };
        backgroundPanel.setLayout(new BoxLayout(backgroundPanel, BoxLayout.Y_AXIS));

        // Load and display the Risk logo
        JLabel logoLabel;
        try {
            ImageIcon logoIcon = new ImageIcon(getClass().getResource("/images/RiskLogo.png"));
            Image scaledLogo = logoIcon.getImage().getScaledInstance(260, -1, Image.SCALE_SMOOTH);
            logoLabel = new JLabel(new ImageIcon(scaledLogo));
        } catch (Exception e) {
            // Fallback to text if image loading fails
            logoLabel = new JLabel("Risk");
            logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 40));
            logoLabel.setForeground(Color.RED);
        }
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        backgroundPanel.add(Box.createVerticalStrut(30));
        backgroundPanel.add(logoLabel);
        backgroundPanel.add(Box.createVerticalStrut(30));

        // Create the main control panel
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setMaximumSize(new Dimension(300, 200));

        // Create and style the Start Game button
        JButton startButton = new JButton("Start Game");

        Font btnFont = new Font("Segoe UI", Font.BOLD, 24);
        startButton.setFont(btnFont);

        Color startBg = new Color(76, 175, 80);
        Color startBgHover = new Color(56, 142, 60);
        startButton.setBackground(startBg);
        startButton.setForeground(Color.WHITE);
        startButton.setOpaque(true);
        startButton.setContentAreaFilled(true);
        startButton.setFocusPainted(false);
        startButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(56, 142, 60), 2, true),
                BorderFactory.createEmptyBorder(16, 48, 16, 48)));
        startButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                startButton.setBackground(startBgHover);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                startButton.setBackground(startBg);
            }
        });

        // Add action listener for the Start Game button
        startButton.addActionListener(e -> {
            // Show player name input dialog
            String name = ClientPlayerNameDialog.showDialog(this);

            if (name != null && !name.trim().isEmpty()) {
                this.setVisible(false);
                // Create and show waiting dialog while connecting to server
                final DialogWaiting[] waitingDialog = new DialogWaiting[1];
                waitingDialog[0] = new DialogWaiting(null, name.trim(), exitEvent -> {
                    waitingDialog[0].dispose();
                    System.exit(0);
                });
                
                // Start connection process in a new thread
                new Thread(() -> {
                    try {
                        // Initialize game client and connect to server
                        //ClientGame tempClient = new ClientGame("13.51.109.85", 3131, new ClientMessageListener() {
                        ClientGame tempClient = new ClientGame("localhost", 3131, new ClientMessageListener() {
                            @Override
                            public void onGameStateReceived(common.CommonState gameState) {
                                // Launch the game when server responds
                                SwingUtilities.invokeLater(() -> {
                                    waitingDialog[0].dispose();
                                    ClientFunctions.launchGame(name.trim());
                                });
                            }

                            @Override
                            public void onChatMessage(String message) {
                            }

                            @Override
                            public void onVictory(String message) {
                            }

                            @Override
                            public void onDefeat(String message) {
                            }
                        });
                        // Send join request to server
                        tempClient.sendMove(new common.CommonMessages(common.CommonMessages.Type.JOIN, name.trim()));
                    } catch (Exception ex) {
                        // Handle connection errors
                        ex.printStackTrace();
                        SwingUtilities.invokeLater(() -> {
                            waitingDialog[0].dispose();
                            JOptionPane.showMessageDialog(null, "Connection failed!", "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        });
                    }
                }).start();
                waitingDialog[0].setVisible(true);
            }
        });

        // Create and style the Quit Game button
        JButton quitButton = new JButton("Quit Game");
        quitButton.setFont(btnFont);
        Color quitBg = new Color(220, 53, 69);
        Color quitBgHover = new Color(180, 40, 50);
        quitButton.setBackground(quitBg);
        quitButton.setForeground(Color.WHITE);
        quitButton.setFocusPainted(false);
        quitButton.setOpaque(true);
        quitButton.setContentAreaFilled(true);
        quitButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(quitBg.darker(), 2, true),
                BorderFactory.createEmptyBorder(16, 48, 16, 48)));
        quitButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        quitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        quitButton.setMaximumSize(new Dimension(300, 60));
        quitButton.setPreferredSize(new Dimension(300, 60));
        quitButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                quitButton.setBackground(quitBgHover);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                quitButton.setBackground(quitBg);
            }
        });
        quitButton.addActionListener(e2 -> System.exit(0));

        startButton.setMaximumSize(new Dimension(300, 60));
        startButton.setPreferredSize(new Dimension(300, 60));

        panel.add(startButton);
        panel.add(Box.createVerticalStrut(14));
        panel.add(quitButton);

        backgroundPanel.add(panel);
        backgroundPanel.add(Box.createVerticalGlue());

        // Add Rules label with click handler
        JLabel rulesLabel = new JLabel("Rules");
        rulesLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        rulesLabel.setForeground(new Color(160, 100, 40));
        rulesLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        rulesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        rulesLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                rulesLabel.setForeground(new Color(200, 120, 40));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                rulesLabel.setForeground(new Color(160, 100, 40));
            }

            public void mouseClicked(java.awt.event.MouseEvent evt) {
                DialogRules.show();
            }
        });
        backgroundPanel.add(rulesLabel);
        backgroundPanel.add(Box.createVerticalStrut(18));

        setContentPane(backgroundPanel);
        setVisible(true);
    }

    /**
     * Main method to launch the application
     * Uses SwingUtilities.invokeLater to ensure thread safety
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(ClientMain::new);
    }
}
