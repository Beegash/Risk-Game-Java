package client.dialogs;

import javax.swing.*;
import java.awt.*;

public class DialogRules {
    public static void showRules(Component parent) {
        String rules = """
            🛡 RISK GAME RULES:

            1. Players take turns placing armies only on their own territories.
            2. Each player starts with 20 armies.
            3. During attack, attacker can roll up to 3 dice (must leave 1 army behind).
            4. Defender can roll up to 2 dice.
            5. Dice are compared from highest to lowest; higher roll wins each pair.
            6. After an attack, turn does not automatically pass. Use 'End Turn'.
            7. Only the active player can interact with buttons.
            """;

        JTextArea textArea = new JTextArea(rules);
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        textArea.setEditable(false);
        textArea.setCaretPosition(0);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));

        JOptionPane.showMessageDialog(parent, scrollPane, "🎯 Risk Game Rules", JOptionPane.INFORMATION_MESSAGE);
    }
 public static JLabel createHoverableLabel() {
        JLabel label = new JLabel("Rules");
        label.setForeground(Color.BLUE.darker());
        label.setFont(new Font("Arial", Font.PLAIN, 12));
        label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        label.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                showRules(label);
            }
        });

        return label;
    }
}

