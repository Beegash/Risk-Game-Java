/**
 * UIChatPanel.java
 * This class represents the chat panel component of the Risk game interface.
 * It displays game logs and messages in a scrollable text area.
 * The panel includes a header label and a non-editable text area for messages.
 */

package client.ui;

import javax.swing.*;
import java.awt.*;

public class UIChatPanel extends JPanel {
    /** Text area component that displays chat messages and game logs */
    private final JTextArea chatArea;

    /**
     * Constructor for UIChatPanel
     * Initializes the panel with a header label and scrollable text area
     * Sets up the layout, colors, and fonts according to UI constants
     */
    public UIChatPanel() {
        setLayout(new BorderLayout());
        setBorder(UIConstants.Borders.EMPTY_10);
        setBackground(UIConstants.Colors.CARD_BACKGROUND);

        // Create and configure the header label
        JLabel warLogsLabel = new JLabel("Risk Logs");
        warLogsLabel.setFont(UIConstants.Fonts.HEADER);
        warLogsLabel.setForeground(UIConstants.Colors.TEXT_PRIMARY);
        add(warLogsLabel, BorderLayout.NORTH);

        // Create and configure the chat text area
        chatArea = new JTextArea(8, 30);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setEditable(false);
        chatArea.setBackground(UIConstants.Colors.CARD_BACKGROUND);
        chatArea.setForeground(UIConstants.Colors.TEXT_PRIMARY);
        chatArea.setFont(UIConstants.Fonts.TEXT);

        // Create and configure the scroll pane
        JScrollPane chatScroll = new JScrollPane(chatArea);
        chatScroll.setPreferredSize(UIConstants.Dimensions.COMMUNUCATON_AREA);
        chatScroll.getViewport().setBackground(UIConstants.Colors.CARD_BACKGROUND);
        add(chatScroll, BorderLayout.CENTER);
    }

    /**
     * Appends a new message to the chat area
     * Formats dice roll messages and battle results with special styling
     * 
     * @param message The message to be added to the chat
     */
    public void appendMessage(String message) {
        if (message.startsWith("Dice |")) {
            // Format dice roll message
            String[] parts = message.split("\\|");
            String attackerPart = parts[1].trim();
            String defenderPart = parts[2].trim();
            
            String formattedMessage = String.format(
                "🎲 DICE ROLL\n" +
                "⚔️ Attacker: %s\n" +
                "🛡️ Defender: %s\n",
                attackerPart.replace("Attacker:", "").trim(),
                defenderPart.replace("Defender:", "").trim()
            );
            chatArea.append(formattedMessage + "\n");
        } else if (message.startsWith("Losses |")) {
            // Format battle results message
            String[] parts = message.split("\\|")[1].split(",");
            String attackerLoss = parts[0].replace("Attacker:", "").trim();
            String defenderLoss = parts[1].replace("Defender:", "").trim();
            
            String formattedMessage = String.format(
                "💥 BATTLE RESULTS\n" +
                "⚔️ Attacker lost: %s armies\n" +
                "🛡️ Defender lost: %s armies\n",
                attackerLoss,
                defenderLoss
            );
            chatArea.append(formattedMessage + "\n");
        } else {
            // Regular message
            chatArea.append(message + "\n");
        }
        
        // Scroll to bottom
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }
} 
