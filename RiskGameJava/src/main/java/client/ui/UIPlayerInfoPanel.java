package client.ui;

import common.CommonPlayer;
import javax.swing.*;
import java.awt.*;

public class UIPlayerInfoPanel extends JPanel {
    private final JLabel playerNameLabel;
    private final JLabel armiesLabel;
    private final JLabel territoriesLabel;

    public UIPlayerInfoPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(UIConstants.Colors.PANEL_BACKGROUND);
        setBorder(UIConstants.Borders.EMPTY_20);
        setAlignmentX(Component.CENTER_ALIGNMENT);

        playerNameLabel = new JLabel("Current Player: -");
        playerNameLabel.setFont(UIConstants.Fonts.HEADER);
        playerNameLabel.setForeground(UIConstants.Colors.TEXT_PRIMARY);

        armiesLabel = new JLabel("Remaining Armies: -");
        armiesLabel.setFont(UIConstants.Fonts.TEXT);
        armiesLabel.setForeground(UIConstants.Colors.TEXT_SECONDARY);

        territoriesLabel = new JLabel("Territories: -");
        territoriesLabel.setFont(UIConstants.Fonts.TEXT);
        territoriesLabel.setForeground(UIConstants.Colors.TEXT_SECONDARY);

        add(playerNameLabel);
        add(Box.createVerticalStrut(8));
        add(armiesLabel);
        add(Box.createVerticalStrut(4));
        add(territoriesLabel);
    }

    public void updatePlayerInfo(CommonPlayer player, int remainingArmies, int territories) {
        Color nameColor = switch (player.getColor().toUpperCase()) {
            case "RED" -> Color.RED;
            case "BLUE" -> Color.BLUE;
            default -> UIConstants.Colors.TEXT_PRIMARY;
        };
        playerNameLabel.setText(player.getName());
        playerNameLabel.setForeground(nameColor);
        armiesLabel.setText("Remaining Armies: " + remainingArmies);
        territoriesLabel.setText("Territories: " + territories);
    }
}
