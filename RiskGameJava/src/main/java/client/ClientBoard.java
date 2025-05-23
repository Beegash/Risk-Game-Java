package client;

import common.CommonState;
import common.CommonTerritory;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ClientBoard extends JPanel {

    private BufferedImage mapImage;
    private Map<String, Point> countryPositions;
    private Map<String, String> countryOwners;
    private Map<String, Integer> countryArmies;
    private Consumer<String> clickHandler;

    public ClientBoard(Consumer<String> clickHandler) {
        this.clickHandler = clickHandler;
        this.setLayout(null);

        try {
            mapImage = ImageIO.read(getClass().getResource("/images/RiskMap.jpg"));

            int newWidth = (int) (mapImage.getWidth() * 1.2);
            int newHeight = (int) (mapImage.getHeight() * 1.2);
            this.setPreferredSize(new Dimension(newWidth, newHeight));
        } catch (Exception e) {
            System.err.println("Map could not be loaded!");
            e.printStackTrace();
        }

        countryPositions = new HashMap<>();
        countryOwners = new HashMap<>();
        countryArmies = new HashMap<>();

        initializeCountries();

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (mapImage == null) {
                    return;
                }

                double scaleX = getWidth() / (double) mapImage.getWidth();
                double scaleY = getHeight() / (double) mapImage.getHeight();

                for (Map.Entry<String, Point> entry : countryPositions.entrySet()) {
                    Point original = entry.getValue();
                    int x = (int) (original.x * scaleX);
                    int y = (int) (original.y * scaleY);

                    int dx = e.getX() - x;
                    int dy = e.getY() - y;

                    if (dx * dx + dy * dy <= 100) {
                        System.out.println("Clicked on: " + entry.getKey());
                        if (clickHandler != null) {
                            clickHandler.accept(entry.getKey());
                        }
                        break;
                    }
                }
            }
        });

    }

    private void initializeCountries() {

        addCountry("Afghanistan", 810, 260);
        addCountry("Alaska", 100, 130);
        addCountry("Alberta", 220, 190);
        addCountry("Aratopia", 310, 120);
        addCountry("Argentina", 300, 590);
        addCountry("Brazil", 370, 480);
        addCountry("CentralAmerica", 220, 330);
        addCountry("China", 950, 310);
        addCountry("Cihanland", 395, 110);
        addCountry("Congo", 620, 520);
        addCountry("EastAfrica", 680, 460);
        addCountry("EasternAustralia", 1090, 580);
        addCountry("EasternUnitedStates", 305, 280);
        addCountry("Egypt", 630, 380);
        addCountry("GreatBritain", 530, 210);
        addCountry("Greenland", 490, 95);
        addCountry("Iceland", 545, 150);
        addCountry("India", 860, 380);
        addCountry("Indonesia", 990, 470);
        addCountry("Irkutsk", 1010, 190);
        addCountry("Izzettinia", 380, 355);
        addCountry("Japan", 1120, 270);
        addCountry("Kamchatka", 1160, 130);
        addCountry("Latifland", 1180, 600);
        addCountry("MiddleEast", 720, 340);
        addCountry("Mongolia", 1050, 260);
        addCountry("NewGuinea", 1120, 490);
        addCountry("NorthAfrica", 530, 410);
        addCountry("NorthWestTerritory", 205, 135);
        addCountry("NorthernEurope", 600, 230);
        addCountry("Ontario", 290, 190);
        addCountry("Peru", 290, 510);
        addCountry("Quebec", 380, 190);
        addCountry("Sametistan", 1070, 390);
        addCountry("Scandinavia", 620, 150);
        addCountry("Siberia", 920, 160);
        addCountry("Slam", 960, 390);
        addCountry("SouthAfrica", 630, 600);
        addCountry("SouthernEurope", 645, 280);
        addCountry("Ukraine", 720, 180);
        addCountry("Ural", 840, 180);
        addCountry("Venezuela", 270, 410);
        addCountry("WesternAustralia", 990, 580);
        addCountry("WesternEurope", 530, 300);
        addCountry("WesternUnitedStates", 220, 250);
        addCountry("Yakutsk", 1040, 130);
    }

    private void addCountry(String name, int x, int y) {
        countryPositions.put(name, new Point(x, y));
        countryOwners.put(name, null);
        countryArmies.put(name, 0);
    }

    public void updateGameState(CommonState gameState) {
        for (CommonTerritory t : gameState.getTerritories().values()) {
            String name = t.getName();
            String owner = t.getOwner();
            int armies = t.getArmies();

            if (countryPositions.containsKey(name)) {
                String color = "GRAY";
                if (owner != null && gameState.getPlayers().containsKey(owner)) {
                    color = gameState.getPlayers().get(owner).getColor();
                }
                countryOwners.put(name, color);
                countryArmies.put(name, armies);
            }
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (mapImage != null) {
            g.drawImage(mapImage, 0, 0, getWidth(), getHeight(), this);
        }

        double scaleX = getWidth() / (double) mapImage.getWidth();
        double scaleY = getHeight() / (double) mapImage.getHeight();

        Graphics2D g2 = (Graphics2D) g;
        g2.setFont(new Font("Arial", Font.BOLD, 13));

        for (String name : countryPositions.keySet()) {
            Point original = countryPositions.get(name);

            int x = (int) (original.x * scaleX);
            int y = (int) (original.y * scaleY);

            String ownerColor = countryOwners.get(name);
            Color color = switch (ownerColor != null ? ownerColor.toUpperCase() : "") {
                case "RED" ->
                    Color.RED;
                case "BLUE" ->
                    Color.BLUE;
                default ->
                    Color.GRAY;
            };

            g2.setColor(color);
            g2.fillOval(x - 10, y - 10, 20, 20);
            g2.setColor(Color.BLACK);
            g2.drawOval(x - 10, y - 10, 20, 20);

            g2.setColor(Color.WHITE);
            String armyText = String.valueOf(countryArmies.get(name));
            FontMetrics fm = g2.getFontMetrics();
            int textWidth = fm.stringWidth(armyText);
            int textHeight = fm.getAscent();
            g2.drawString(armyText, x - textWidth / 2, y + textHeight / 3);

            g2.setFont(new Font("Arial", Font.PLAIN, 11));
            FontMetrics nameFm = g2.getFontMetrics();
            int nameWidth = nameFm.stringWidth(name);
            g2.setColor(Color.WHITE);
            g2.drawString(name, x - nameWidth / 2, y + 22);
            g2.setFont(new Font("Arial", Font.BOLD, 13));
        }
    }

}
