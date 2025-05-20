/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.izzettinozmen.riskgame;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ifozmen
 */
public class Territory implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public enum Continent {
        NORTH_AMERICA,
        SOUTH_AMERICA,
        EUROPE,
        AFRICA,
        ASIA,
        AUSTRALIA,
        CUSTOM
    }

    private String name;
    private Player owner;
    private int soldierCount;
    private List<Territory> adjacentTerritories;
    private final Continent continent;
    private transient List<String> adjacentTerritoryNames; // For serialization

    public Territory(String name, Continent continent) {
        this.name = name;
        this.owner = null;
        this.soldierCount = 0;
        this.adjacentTerritories = new ArrayList<>();
        this.continent = continent;
        this.adjacentTerritoryNames = new ArrayList<>();
    }

    // Custom serialization
    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        // Save adjacent territory names instead of references
        adjacentTerritoryNames = new ArrayList<>();
        for (Territory territory : adjacentTerritories) {
            adjacentTerritoryNames.add(territory.getName());
        }
        out.writeObject(adjacentTerritoryNames);
    }

    // Custom deserialization
    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        adjacentTerritories = new ArrayList<>();
        adjacentTerritoryNames = (List<String>) in.readObject();
    }

    // Method to restore adjacent territories after deserialization
    public void restoreAdjacentTerritories(List<Territory> allTerritories) {
        adjacentTerritories.clear();
        for (String territoryName : adjacentTerritoryNames) {
            for (Territory territory : allTerritories) {
                if (territory.getName().equals(territoryName)) {
                    adjacentTerritories.add(territory);
                    break;
                }
            }
        }
    }

    public String getName() {
        return name;
    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public int getSoldierCount() {
        return soldierCount;
    }

    public void setSoldierCount(int soldierCount) {
        this.soldierCount = soldierCount;
    }

    public void addSoldiers(int count) {
        this.soldierCount += count;
    }

    public void removeSoldiers(int count) {
        if (count <= this.soldierCount) {
            this.soldierCount -= count;
        }
    }

    public List<Territory> getAdjacentTerritories() {
        return adjacentTerritories;
    }

    public void addAdjacentTerritory(Territory territory) {
        if (!adjacentTerritories.contains(territory)) {
            adjacentTerritories.add(territory);
            // Karşılıklı komşuluk ilişkisi
            if (!territory.getAdjacentTerritories().contains(this)) {
                territory.addAdjacentTerritory(this);
            }
        }
    }

    public boolean isAdjacentTo(Territory territory) {
        return adjacentTerritories.contains(territory);
    }

    public Continent getContinent() {
        return continent;
    }
}
