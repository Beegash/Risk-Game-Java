/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.izzettinozmen.riskgame;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ifozmen
 */
public class Territory {
    private String name;
    private Player owner;
    private int soldierCount;
    private List<Territory> adjacentTerritories;

    public Territory(String name) {
        this.name = name;
        this.soldierCount = 0;
        this.adjacentTerritories = new ArrayList<>();
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
        }
    }

    public boolean isAdjacentTo(Territory territory) {
        return adjacentTerritories.contains(territory);
    }
}
