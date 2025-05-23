package client.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class UIActionButtonsPanel extends JPanel {
    private final JButton attackBtn;
    private final JButton endTurnBtn;
    private final JButton fortifyBtn;

    public UIActionButtonsPanel(ActionListener attackListener, ActionListener endTurnListener,
            ActionListener fortifyListener) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(UIConstants.Colors.CARD_BACKGROUND);
        setBorder(UIConstants.Borders.EMPTY_20);

        attackBtn = createButton("Attack", UIConstants.Colors.BUTTON_ATTACK, attackListener);
        endTurnBtn = createButton("End Turn", UIConstants.Colors.BUTTON_END_TURN, endTurnListener);
        fortifyBtn = createButton("Fortify", UIConstants.Colors.BUTTON_FORTIFY, fortifyListener);

        add(attackBtn);
        add(Box.createVerticalStrut(18));
        add(endTurnBtn);
        add(Box.createVerticalStrut(18));
        add(fortifyBtn);
    }

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
