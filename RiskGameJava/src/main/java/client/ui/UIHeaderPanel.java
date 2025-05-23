package client.ui;

import client.dialogs.DialogRules;
import javax.swing.*;
import java.awt.*;

public class UIHeaderPanel extends JPanel {
    private final JLabel logoLabel;
    private final JLabel rulesLabel;
    private final JLabel turnLabel;

    public UIHeaderPanel() {
        setLayout(new BorderLayout());
        setBorder(UIConstants.Borders.EMPTY_10);
        setBackground(UIConstants.Colors.BACKGROUND);

        JPanel logoPanel = new JPanel();
        logoPanel.setOpaque(false);
        logoLabel = createLogoLabel();
        logoPanel.add(logoLabel);

        turnLabel = new JLabel("");
        turnLabel.setFont(UIConstants.Fonts.HEADER);
        turnLabel.setForeground(UIConstants.Colors.TEXT_PRIMARY);
        logoPanel.add(Box.createHorizontalStrut(18));
        logoPanel.add(turnLabel);
        add(logoPanel, BorderLayout.WEST);

        JPanel rulesPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rulesPanel.setOpaque(false);
        rulesLabel = DialogRules.createHoverableLabel();
        rulesLabel.setForeground(UIConstants.Colors.TEXT_RULES);
        rulesPanel.add(rulesLabel);
        add(rulesPanel, BorderLayout.EAST);
    }

    private JLabel createLogoLabel() {
        JLabel label;
        try {
            ImageIcon logoIcon = new ImageIcon(getClass().getResource("/images/RiskLogo.png"));
            Image scaledImage = logoIcon.getImage().getScaledInstance(160, -1, Image.SCALE_SMOOTH);
            label = new JLabel(new ImageIcon(scaledImage));
        } catch (Exception e) {
            label = new JLabel("Risk");
            label.setFont(UIConstants.Fonts.HEADER);
            label.setForeground(UIConstants.Colors.TEXT_PRIMARY);
        }
        return label;
    }

    public void updateTurnPlayer(String playerName, boolean isMyTurn) {
        if (playerName == null) {
            turnLabel.setText("");
        } else {
            String text = playerName + (isMyTurn ? " (Your Turn!)" : " is playing");
            turnLabel.setText(text);
        }
    }
}
