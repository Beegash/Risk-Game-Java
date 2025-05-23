package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClientPlayerNameDialog extends JDialog {
    private JTextField nameField;
    private JButton okButton;
    private JButton cancelButton;
    private String playerName = null;

    public ClientPlayerNameDialog(Frame parent) {
        super(parent, "Enter Your Player Name", true);
        setSize(350, 180);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(250, 245, 230));

        JLabel promptLabel = new JLabel("Please enter your player name:");
        promptLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        promptLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(promptLabel);
        mainPanel.add(Box.createVerticalStrut(10));

        nameField = new JTextField(20);
        nameField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        nameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, nameField.getPreferredSize().height));
        mainPanel.add(nameField);
        mainPanel.add(Box.createVerticalStrut(20));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setOpaque(false);

        okButton = new JButton("OK");
        styleButton(okButton, new Color(76, 175, 80), Color.WHITE);

        cancelButton = new JButton("Cancel");
        styleButton(cancelButton, new Color(220, 53, 69), Color.WHITE);

        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String input = nameField.getText().trim();
                if (!input.isEmpty()) {
                    playerName = input;
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(ClientPlayerNameDialog.this,
                            "Name cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                playerName = null;
                dispose();
            }
        });

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                playerName = null;
            }
        });

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void styleButton(JButton button, Color bgColor, Color fgColor) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public String getPlayerName() {

        return playerName;
    }

    public static String showDialog(Frame parent) {
        ClientPlayerNameDialog dialog = new ClientPlayerNameDialog(parent);
        dialog.setVisible(true);
        return dialog.getPlayerName();
    }
}
