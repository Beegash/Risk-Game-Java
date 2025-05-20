/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.izzettinozmen.riskgame;

import java.awt.Color;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author ifozmen
 */
public class Player implements Serializable {
    private static final long serialVersionUID = 1L;
    private PlayerStats stats;
    
    public Player(String name) {
        this.stats = new PlayerStats(name, Color.BLACK, 0); // Default color and turn order
    }
    
    public Player(String name, Color color, int turnOrder) {
        this.stats = new PlayerStats(name, color, turnOrder);
    }
    
    public String getName() {
        return stats.getName();
    }
    
    public Color getColor() {
        return stats.getColor();
    }
    
    public List<Territory> getTerritories() {
        return stats.getTerritories();
    }
    
    public Territory getTerritory(String name) {
        return stats.getTerritories().stream()
                .filter(t -> t.getName().equals(name))
                .findFirst()
                .orElse(null);
    }
    
    public void addTerritory(Territory territory) {
        stats.addTerritory(territory);
    }
    
    public void removeTerritory(Territory territory) {
        stats.removeTerritory(territory);
    }
    
    public int getRemainingSoldiers() {
        return stats.getAvailableSoldiers();
    }
     
    public void addSoldiers(int count) {
        stats.addSoldiers(count);
    }
    
    public void removeSoldiers(int count) {
        stats.removeSoldiers(count);
    }
    
    public boolean hasTerritory(Territory territory) {
        return stats.getTerritories().contains(territory);
    }
    
    public int getTerritoryCount() {
        return stats.getTotalTerritories();
    }
    
    public int getContinentControlCount(Territory.Continent continent) {
        return stats.getContinentControl().getOrDefault(continent, 0);
    }
    
    public boolean controlsContinent(Territory.Continent continent) {
        int territoriesInContinent = stats.getContinentControl().getOrDefault(continent, 0);
        int totalTerritoriesInContinent = TerritoryManager.getInstance().getContinentTerritoryCount(continent);
        return territoriesInContinent == totalTerritoriesInContinent;
    }

    public void startTurn() {
        stats.startTurn();
    }

    public void endTurn() {
        stats.endTurn();
    }

    public int getSoldiersPerTurn() {
        return stats.getSoldiersPerTurn();
    }

    public int getTotalSoldiers() {
        return stats.getTotalSoldiers();
    }

    public boolean isActive() {
        return stats.isActive();
    }

    public void setActive(boolean active) {
        stats.setActive(active);
    }

    public int getTurnCount() {
        return stats.getTurnCount();
    }

    public int getTurnOrder() {
        return stats.getTurnOrder();
    }

    @Override
    public String toString() {
        return stats.toString();
    }
}
