/**
 * UIConstants.java
 * This class defines constant values used throughout the Risk game's user interface.
 * It contains color schemes, fonts, dimensions, and border styles to maintain
 * a consistent look and feel across the application.
 */

package client.ui;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class UIConstants {
    /**
     * Inner class defining color constants used in the UI
     * Includes background colors, button colors, text colors, and border colors
     */
    public static final class Colors {
        /** Main background color for the application */
        public static final Color BACKGROUND = new Color(24, 26, 27);
        /** Background color for card-like components */
        public static final Color CARD_BACKGROUND = new Color(35, 39, 42);
        /** Background color for panels */
        public static final Color PANEL_BACKGROUND = new Color(44, 47, 51);

        /** Color for attack action buttons */
        public static final Color BUTTON_ATTACK = new Color(114, 137, 218);
        /** Color for fortify action buttons */
        public static final Color BUTTON_FORTIFY = new Color(250, 166, 26);
        /** Color for end turn buttons */
        public static final Color BUTTON_END_TURN = new Color(67, 181, 129);
        /** Color for disabled buttons */
        public static final Color BUTTON_DISABLED = new Color(60, 60, 60);
        /** Color for active/selected buttons */
        public static final Color BUTTON_ACTIVE = new Color(67, 181, 129);
        /** Color for exit/quit buttons */
        public static final Color BUTTON_EXIT = new Color(200, 60, 60);

        /** Primary text color (white) */
        public static final Color TEXT_PRIMARY = Color.WHITE;
        /** Secondary text color for less important text */
        public static final Color TEXT_SECONDARY = new Color(185, 187, 190);
        /** Color for rules text */
        public static final Color TEXT_RULES = new Color(160, 100, 40);
        /** Color for rules text when hovered */
        public static final Color TEXT_RULES_HOVER = new Color(200, 120, 40);

        /** Color for borders around components */
        public static final Color BORDER = new Color(54, 57, 63);
    }

    /**
     * Inner class defining font constants used in the UI
     * Includes fonts for headers, buttons, regular text, and rules
     */
    public static final class Fonts {
        /** Font for header text */
        public static final Font HEADER = new Font("Segoe UI", Font.BOLD, 17);
        /** Font for button text */
        public static final Font BUTTON = new Font("Segoe UI", Font.BOLD, 16);
        /** Font for regular text */
        public static final Font TEXT = new Font("Segoe UI", Font.PLAIN, 15);
        /** Font for rules text */
        public static final Font RULES = new Font("Segoe UI", Font.BOLD, 18);
    }

    /**
     * Inner class defining dimension constants used in the UI
     * Includes sizes for buttons, windows, and chat areas
     */
    public static final class Dimensions {
        /** Standard size for buttons */
        public static final Dimension BUTTON_SIZE = new Dimension(180, 45);
        /** Size of the main game window */
        public static final Dimension MAIN_WINDOW = new Dimension(1280, 800);
        /** Size of the chat area */
        public static final Dimension COMMUNUCATON_AREA = new Dimension(1000, 150);
    }

    /**
     * Inner class defining border constants used in the UI
     * Includes empty borders and compound borders for cards
     */
    public static final class Borders {
        /** Empty border with 10 pixel padding on all sides */
        public static final Border EMPTY_10 = BorderFactory.createEmptyBorder(10, 10, 10, 10);
        /** Empty border with 20 pixel padding on all sides */
        public static final Border EMPTY_20 = BorderFactory.createEmptyBorder(20, 20, 20, 20);
        /** Compound border for card-like components with rounded corners */
        public static final Border CARD_BORDER = BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(24, 24, 24, 24),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Colors.BORDER, 2, true),
                        BorderFactory.createEmptyBorder(16, 16, 16, 16)));
    }
}
