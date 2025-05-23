/**
 * CommonTerritory.java
 * This class represents a territory in the Risk game.
 * Each territory has a name, continent, owner, number of armies,
 * and a list of adjacent territories for movement and attacks.
 * The class is serializable to allow transmission between client and server.
 */

package common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CommonTerritory implements Serializable {
    /** Name of the territory */
    private String name;
    /** Continent this territory belongs to */
    private String continent;
    /** Name of the player who owns this territory */
    private String owner;
    /** Number of armies stationed in this territory */
    private int armies;
    /** List of names of territories that are adjacent to this one */
    private List<String> adjacentTerritories;
    /** Serial version UID for serialization compatibility */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor for CommonTerritory
     * 
     * @param name Name of the territory
     * @param continent Continent this territory belongs to
     */
    public CommonTerritory(String name, String continent) {
        this.name = name;
        this.continent = continent;
        this.armies = 0;
        this.adjacentTerritories = new ArrayList<>();
    }

    /**
     * Adds an adjacent territory to this territory's list of neighbors
     * 
     * @param neighbor Name of the adjacent territory
     */
    public void addAdjacent(String neighbor) {
        adjacentTerritories.add(neighbor);
    }

    /**
     * Gets the name of this territory
     * 
     * @return Territory name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the continent this territory belongs to
     * 
     * @return Continent name
     */
    public String getContinent() {
        return continent;
    }

    /**
     * Gets the name of the player who owns this territory
     * 
     * @return Owner's name
     */
    public String getOwner() {
        return owner;
    }

    /**
     * Sets the owner of this territory
     * 
     * @param owner Name of the new owner
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }

    /**
     * Gets the number of armies in this territory
     * 
     * @return Number of armies
     */
    public int getArmies() {
        return armies;
    }

    /**
     * Sets the number of armies in this territory
     * 
     * @param armies New number of armies
     */
    public void setArmies(int armies) {
        this.armies = armies;
    }

    /**
     * Adds armies to this territory
     * 
     * @param count Number of armies to add
     */
    public void addArmies(int count) {
        this.armies += count;
    }

    /**
     * Removes armies from this territory
     * 
     * @param count Number of armies to remove
     */
    public void removeArmies(int count) {
        this.armies -= count;
    }

    /**
     * Gets the list of adjacent territories
     * 
     * @return List of names of adjacent territories
     */
    public List<String> getAdjacentTerritories() {
        return adjacentTerritories;
    }
}
