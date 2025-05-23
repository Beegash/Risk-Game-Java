package client.dialogs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class DialogWaiting extends JDialog {
    private JLabel infoLabel;
    private JLabel tipLabel;
    private JProgressBar progressBar;
    private JButton exitButton;

    public DialogWaiting(JFrame parent, String playerName, ActionListener onExit) {
        super(parent, "Waiting for Opponent", true);
        setSize(350, 220);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(245, 235, 210));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        infoLabel = new JLabel("Welcome, " + playerName + "!");
        infoLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(infoLabel);
        panel.add(Box.createVerticalStrut(10));

        JLabel waitingLabel = new JLabel("Waiting for another player to join...");
        waitingLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        waitingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(waitingLabel);
        panel.add(Box.createVerticalStrut(10));

        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(progressBar);
        panel.add(Box.createVerticalStrut(16));

        tipLabel = new JLabel("Tip: You can exit while waiting.");
        tipLabel.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        tipLabel.setForeground(new Color(120, 100, 60));
        tipLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(tipLabel);
        panel.add(Box.createVerticalStrut(16));

        exitButton = new JButton("Exit");
        exitButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        exitButton.setBackground(Color.WHITE);
        exitButton.setForeground(new Color(220, 53, 69));
        exitButton.setFocusPainted(false);
        exitButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        exitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitButton.addActionListener(onExit);
        panel.add(exitButton);

        setContentPane(panel);
    }
} 
 