/**
 * CommonPlayer.java
 * This class represents a player in the Risk game.
 * Each player has a name, color, list of owned territories,
 * available armies for placement, and turn status.
 * The class is serializable to allow transmission between client and server.
 */

package common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CommonPlayer implements Serializable {
    /** Player's name */
    private String name;
    /** Player's color for UI representation */
    private String color;
    /** List of territory names owned by this player */
    private List<String> territories;
    /** Number of armies available for placement */
    private int availableArmies;
    /** Whether it is currently this player's turn */
    private boolean isTurn;
    /** Serial version UID for serialization compatibility */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor for CommonPlayer
     * 
     * @param name Player's name
     * @param color Player's color
     */
    public CommonPlayer(String name, String color) {
        this.name = name;
        this.color = color;
        this.territories = new ArrayList<>();
        this.availableArmies = 20;
        this.isTurn = false;
    }

    /**
     * Gets the player's name
     * 
     * @return Player name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the player's color
     * 
     * @return Player color
     */
    public String getColor() {
        return color;
    }

    /**
     * Gets the list of territories owned by this player
     * 
     * @return List of territory names
     */
    public List<String> getTerritories() {
        return territories;
    }

    /**
     * Gets the number of armies available for placement
     * 
     * @return Number of available armies
     */
    public int getAvailableArmies() {
        return availableArmies;
    }

    /**
     * Sets the number of armies available for placement
     * 
     * @param availableArmies New number of available armies
     */
    public void setAvailableArmies(int availableArmies) {
        this.availableArmies = availableArmies;
    }

    /**
     * Adds a territory to the player's owned territories
     * 
     * @param territory Name of the territory to add
     */
    public void addTerritory(String territory) {
        territories.add(territory);
    }

    /**
     * Removes a territory from the player's owned territories
     * 
     * @param territory Name of the territory to remove
     */
    public void removeTerritory(String territory) {
        territories.remove(territory);
    }

    /**
     * Checks if it is currently this player's turn
     * 
     * @return true if it is this player's turn
     */
    public boolean isTurn() {
        return isTurn;
    }

    /**
     * Sets whether it is this player's turn
     * 
     * @param isTurn New turn status
     */
    public void setTurn(boolean isTurn) {
        this.isTurn = isTurn;
    }
}
