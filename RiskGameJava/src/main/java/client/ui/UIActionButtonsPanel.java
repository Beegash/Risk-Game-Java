/**
 * UIActionButtonsPanel.java
 * This class represents the panel containing action buttons in the Risk game interface.
 * It manages three main action buttons: Attack, End Turn, and Fortify.
 * The panel handles button creation, styling, and state management.
 */

package client.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class UIActionButtonsPanel extends JPanel {
    /** Button for initiating an attack action */
    private final JButton attackBtn;
    /** Button for ending the current player's turn */
    private final JButton endTurnBtn;
    /** Button for fortifying a territory */
    private final JButton fortifyBtn;

    /**
     * Constructor for UIActionButtonsPanel
     * Creates and initializes the three main action buttons with their respective listeners
     * 
     * @param attackListener Action listener for the attack button
     * @param endTurnListener Action listener for the end turn button
     * @param fortifyListener Action listener for the fortify button
     */
    public UIActionButtonsPanel(ActionListener attackListener, ActionListener endTurnListener,
            ActionListener fortifyListener) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(UIConstants.Colors.CARD_BACKGROUND);
        setBorder(UIConstants.Borders.EMPTY_20);

        // Create the three main action buttons
        attackBtn = createButton("Attack", UIConstants.Colors.BUTTON_ATTACK, attackListener);
        endTurnBtn = createButton("End Turn", UIConstants.Colors.BUTTON_END_TURN, endTurnListener);
        fortifyBtn = createButton("Fortify", UIConstants.Colors.BUTTON_FORTIFY, fortifyListener);

        // Add buttons to panel with spacing
        add(attackBtn);
        add(Box.createVerticalStrut(18));
        add(endTurnBtn);
        add(Box.createVerticalStrut(18));
        add(fortifyBtn);
    }

    /**
     * Creates a styled button with the specified text, color, and action listener
     * 
     * @param text Text to display on the button
     * @param color Background color for the button
     * @param listener Action listener for button clicks
     * @return Configured JButton instance
     */
    private JButton createButton(String text, Color color, ActionListener listener) {
        JButton button = new JButton(text);
        button.setMaximumSize(UIConstants.Dimensions.BUTTON_SIZE);
        button.setPreferredSize(UIConstants.Dimensions.BUTTON_SIZE);
        button.setFont(UIConstants.Fonts.BUTTON);
        button.setFocusPainted(false);
        button.setBackground(UIConstants.Colors.PANEL_BACKGROUND);
        button.setForeground(UIConstants.Colors.TEXT_SECONDARY);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.Colors.BORDER, 2),
                BorderFactory.createEmptyBorder(8, 20, 8, 20)));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.addActionListener(listener);

        // Add hover effects for the button
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (button.isEnabled()) {
                    button.setBackground(UIConstants.Colors.BUTTON_ACTIVE);
                }
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (button.isEnabled()) {
                    button.setBackground(color);
                }
            }
        });

        return button;
    }

    /**
     * Updates the enabled/disabled state of all action buttons
     * Also updates their background colors based on their state
     * 
     * @param isEnabled Whether the buttons should be enabled
     */
    public void updateButtonStates(boolean isEnabled) {
        attackBtn.setEnabled(isEnabled);
        attackBtn.setBackground(
                attackBtn.isEnabled() ? UIConstants.Colors.BUTTON_ATTACK : UIConstants.Colors.BUTTON_DISABLED);

        endTurnBtn.setEnabled(isEnabled);
        endTurnBtn.setBackground(
                endTurnBtn.isEnabled() ? UIConstants.Colors.BUTTON_END_TURN : UIConstants.Colors.BUTTON_DISABLED);

        fortifyBtn.setEnabled(isEnabled);
        fortifyBtn.setBackground(
                fortifyBtn.isEnabled() ? UIConstants.Colors.BUTTON_FORTIFY : UIConstants.Colors.BUTTON_DISABLED);
    }
}
