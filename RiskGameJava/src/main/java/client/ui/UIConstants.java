package client.ui;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class UIConstants {
    public static final class Colors {

        public static final Color BACKGROUND = new Color(24, 26, 27);
        public static final Color CARD_BACKGROUND = new Color(35, 39, 42);
        public static final Color PANEL_BACKGROUND = new Color(44, 47, 51);

        public static final Color BUTTON_ATTACK = new Color(114, 137, 218);
        public static final Color BUTTON_FORTIFY = new Color(250, 166, 26);
        public static final Color BUTTON_END_TURN = new Color(67, 181, 129);
        public static final Color BUTTON_DISABLED = new Color(60, 60, 60);
        public static final Color BUTTON_ACTIVE = new Color(67, 181, 129);
        public static final Color BUTTON_EXIT = new Color(200, 60, 60);

        public static final Color TEXT_PRIMARY = Color.WHITE;
        public static final Color TEXT_SECONDARY = new Color(185, 187, 190);
        public static final Color TEXT_RULES = new Color(160, 100, 40);
        public static final Color TEXT_RULES_HOVER = new Color(200, 120, 40);

        public static final Color BORDER = new Color(54, 57, 63);
    }

    public static final class Fonts {
        public static final Font HEADER = new Font("Segoe UI", Font.BOLD, 17);
        public static final Font BUTTON = new Font("Segoe UI", Font.BOLD, 16);
        public static final Font TEXT = new Font("Segoe UI", Font.PLAIN, 15);
        public static final Font RULES = new Font("Segoe UI", Font.BOLD, 18);
    }

    public static final class Dimensions {
        public static final Dimension BUTTON_SIZE = new Dimension(180, 45);
        public static final Dimension MAIN_WINDOW = new Dimension(1280, 800);
        public static final Dimension CHAT_AREA = new Dimension(1000, 150);
    }

    public static final class Borders {
        public static final Border EMPTY_10 = BorderFactory.createEmptyBorder(10, 10, 10, 10);
        public static final Border EMPTY_20 = BorderFactory.createEmptyBorder(20, 20, 20, 20);
        public static final Border CARD_BORDER = BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(24, 24, 24, 24),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Colors.BORDER, 2, true),
                        BorderFactory.createEmptyBorder(16, 16, 16, 16)));
    }
}
