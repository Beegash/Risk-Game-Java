/**
 * UIHeaderPanel.java
 * This class represents the header panel of the Risk game interface.
 * It displays the game logo, current turn information, and a rules link.
 * The panel is divided into left and right sections for different components.
 */

package client.ui;

import client.dialogs.DialogRules;
import javax.swing.*;
import java.awt.*;

public class UIHeaderPanel extends JPanel {
    /** Label displaying the Risk game logo */
    private final JLabel logoLabel;
    /** Label for accessing game rules */
    private final JLabel rulesLabel;
    /** Label showing current turn information */
    private final JLabel turnLabel;

    /**
     * Constructor for UIHeaderPanel
     * Initializes the panel with logo, turn information, and rules link
     * Sets up the layout and styling according to UI constants
     */
    public UIHeaderPanel() {
        setLayout(new BorderLayout());
        setBorder(UIConstants.Borders.EMPTY_10);
        setBackground(UIConstants.Colors.BACKGROUND);

        // Create and configure the left panel with logo and turn info
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

        // Create and configure the right panel with rules link
        JPanel rulesPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rulesPanel.setOpaque(false);
        rulesLabel = new JLabel("Rules");
        rulesLabel.setFont(UIConstants.Fonts.RULES);
        rulesLabel.setForeground(UIConstants.Colors.TEXT_RULES);
        rulesLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        rulesLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                rulesLabel.setForeground(UIConstants.Colors.TEXT_RULES_HOVER);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                rulesLabel.setForeground(UIConstants.Colors.TEXT_RULES);
            }

            public void mouseClicked(java.awt.event.MouseEvent evt) {
                DialogRules.show();
            }
        });
        rulesPanel.add(rulesLabel);
        add(rulesPanel, BorderLayout.EAST);
    }

    /**
     * Creates and configures the logo label
     * 
     * @return Configured JLabel with the Risk logo
     */
    private JLabel createLogoLabel() {
        JLabel label = new JLabel("Risk");
        label.setFont(new Font("Segoe UI", Font.BOLD, 24));
        label.setForeground(UIConstants.Colors.TEXT_PRIMARY);
        return label;
    }

    /**
     * Updates the turn information display
     * 
     * @param playerName Name of the player whose turn it is
     * @param isMyTurn Whether it is the current player's turn
     */
    public void updateTurnPlayer(String playerName, boolean isMyTurn) {
        turnLabel.setText(isMyTurn ? "Your Turn" : playerName + "'s Turn");
    }
}
