/**
 * UIPlayerInfoPanel.java
 * This class represents the panel displaying player information in the Risk game interface.
 * It shows the current player's name, remaining armies, and number of territories.
 * The panel uses color coding to distinguish between different players.
 */

package client.ui;

import common.CommonPlayer;
import javax.swing.*;
import java.awt.*;

public class UIPlayerInfoPanel extends JPanel {
    /** Label displaying the current player's name */
    private final JLabel playerNameLabel;
    /** Label showing the number of remaining armies */
    private final JLabel armiesLabel;
    /** Label showing the number of territories owned */
    private final JLabel territoriesLabel;

    /**
     * Constructor for UIPlayerInfoPanel
     * Initializes the panel with labels for player information
     * Sets up the layout and styling according to UI constants
     */
    public UIPlayerInfoPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(UIConstants.Colors.PANEL_BACKGROUND);
        setBorder(UIConstants.Borders.EMPTY_20);
        setAlignmentX(Component.CENTER_ALIGNMENT);

        // Create and configure the player name label
        playerNameLabel = new JLabel("Current Player: -");
        playerNameLabel.setFont(UIConstants.Fonts.HEADER);
        playerNameLabel.setForeground(UIConstants.Colors.TEXT_PRIMARY);

        // Create and configure the armies label
        armiesLabel = new JLabel("Remaining Armies: -");
        armiesLabel.setFont(UIConstants.Fonts.TEXT);
        armiesLabel.setForeground(UIConstants.Colors.TEXT_SECONDARY);

        // Create and configure the territories label
        territoriesLabel = new JLabel("Territories: -");
        territoriesLabel.setFont(UIConstants.Fonts.TEXT);
        territoriesLabel.setForeground(UIConstants.Colors.TEXT_SECONDARY);

        // Add labels to panel with spacing
        add(playerNameLabel);
        add(Box.createVerticalStrut(8));
        add(armiesLabel);
        add(Box.createVerticalStrut(4));
        add(territoriesLabel);
    }

    /**
     * Updates the panel with current player information
     * Sets the player name color based on their assigned color
     * Updates the display of remaining armies and territories
     * 
     * @param player The current player object
     * @param remainingArmies Number of armies available for placement
     * @param territories Number of territories owned by the player
     */
    public void updatePlayerInfo(CommonPlayer player, int remainingArmies, int territories) {
        // Set player name color based on their assigned color
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
