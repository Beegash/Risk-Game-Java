package com.izzettinozmen.riskgame;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerStats implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String name;
    private Color color;
    private int turnOrder;
    private int totalSoldiers;
    private int availableSoldiers;
    private int soldiersPerTurn;
    private List<Territory> territories;
    private Map<Territory.Continent, Integer> continentControl;
    private int totalTerritories;
    private boolean isActive;
    private int turnCount;

    public PlayerStats(String name, Color color, int turnOrder) {
        this.name = name;
        this.color = color;
        this.turnOrder = turnOrder;
        this.totalSoldiers = 0;
        this.availableSoldiers = 0;
        this.soldiersPerTurn = 0;
        this.territories = new ArrayList<>();
        this.continentControl = new HashMap<>();
        this.totalTerritories = 0;
        this.isActive = true;
        this.turnCount = 0;
    }

    // Getters
    public String getName() { return name; }
    public Color getColor() { return color; }
    public int getTurnOrder() { return turnOrder; }
    public int getTotalSoldiers() { return totalSoldiers; }
    public int getAvailableSoldiers() { return availableSoldiers; }
    public int getSoldiersPerTurn() { return soldiersPerTurn; }
    public List<Territory> getTerritories() { return new ArrayList<>(territories); }
    public Map<Territory.Continent, Integer> getContinentControl() { return new HashMap<>(continentControl); }
    public int getTotalTerritories() { return totalTerritories; }
    public boolean isActive() { return isActive; }
    public int getTurnCount() { return turnCount; }

    // Setters
    public void setTotalSoldiers(int totalSoldiers) { this.totalSoldiers = totalSoldiers; }
    public void setAvailableSoldiers(int availableSoldiers) { this.availableSoldiers = availableSoldiers; }
    public void setSoldiersPerTurn(int soldiersPerTurn) { this.soldiersPerTurn = soldiersPerTurn; }
    public void setActive(boolean active) { isActive = active; }

    // Territory management
    public void addTerritory(Territory territory) {
        if (!territories.contains(territory)) {
            territories.add(territory);
            totalTerritories++;
            updateContinentControl(territory.getContinent());
        }
    }

    public void removeTerritory(Territory territory) {
        if (territories.remove(territory)) {
            totalTerritories--;
            updateContinentControl(territory.getContinent());
        }
    }

    // Soldier management
    public void addSoldiers(int count) {
        totalSoldiers += count;
        availableSoldiers += count;
    }

    public void removeSoldiers(int count) {
        // Sadece mevcut askerleri kadar çıkar
        int actualRemoved = Math.min(count, availableSoldiers);
        
        if (actualRemoved > 0) {
            totalSoldiers -= actualRemoved;
            availableSoldiers -= actualRemoved;
            System.out.println("[DEBUG] PlayerStats - Removed " + actualRemoved + " soldiers. Remaining: " + availableSoldiers);
        } else {
            System.out.println("[DEBUG] PlayerStats - Not enough soldiers! Requested: " + count + ", Available: " + availableSoldiers);
        }
    }

    // Turn management
    public void startTurn() {
        turnCount++;
        calculateSoldiersPerTurn();
    }

    public void endTurn() {
        availableSoldiers = 0;
    }

    // Helper methods
    private void updateContinentControl(Territory.Continent continent) {
        int count = (int) territories.stream()
                .filter(t -> t.getContinent() == continent)
                .count();
        continentControl.put(continent, count);
    }

    private void calculateSoldiersPerTurn() {
        // Base soldiers from territories
        soldiersPerTurn = Math.max(3, totalTerritories / 3);
        
        // Add continent bonuses
        for (Map.Entry<Territory.Continent, Integer> entry : continentControl.entrySet()) {
            Territory.Continent continent = entry.getKey();
            int territoriesInContinent = entry.getValue();
            int totalTerritoriesInContinent = TerritoryManager.getInstance().getContinentTerritoryCount(continent);
            
            if (territoriesInContinent == totalTerritoriesInContinent) {
                soldiersPerTurn += TerritoryManager.getInstance().getContinentBonus(continent);
            }
        }
    }

    @Override
    public String toString() {
        return String.format("Player: %s\n" +
                           "Total Soldiers: %d\n" +
                           "Available Soldiers: %d\n" +
                           "Soldiers Per Turn: %d\n" +
                           "Total Territories: %d\n" +
                           "Turn Count: %d",
                           name, totalSoldiers, availableSoldiers, 
                           soldiersPerTurn, totalTerritories, turnCount);
    }
} 
