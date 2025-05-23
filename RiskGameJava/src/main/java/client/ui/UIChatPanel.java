package client.ui;

import javax.swing.*;
import java.awt.*;

public class UIChatPanel extends JPanel {
    private final JTextArea chatArea;

    public UIChatPanel() {
        setLayout(new BorderLayout());
        setBorder(UIConstants.Borders.EMPTY_10);
        setBackground(UIConstants.Colors.CARD_BACKGROUND);

        JLabel warLogsLabel = new JLabel("Risk Logs");
        warLogsLabel.setFont(UIConstants.Fonts.HEADER);
        warLogsLabel.setForeground(UIConstants.Colors.TEXT_PRIMARY);
        add(warLogsLabel, BorderLayout.NORTH);

        chatArea = new JTextArea(8, 30);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setEditable(false);
        chatArea.setBackground(UIConstants.Colors.CARD_BACKGROUND);
        chatArea.setForeground(UIConstants.Colors.TEXT_PRIMARY);
        chatArea.setFont(UIConstants.Fonts.TEXT);

        JScrollPane chatScroll = new JScrollPane(chatArea);
        chatScroll.setPreferredSize(UIConstants.Dimensions.CHAT_AREA);
        chatScroll.getViewport().setBackground(UIConstants.Colors.CARD_BACKGROUND);
        add(chatScroll, BorderLayout.CENTER);
    }

    public void appendMessage(String message) {
        chatArea.append(message + "\n");
    }
} 
