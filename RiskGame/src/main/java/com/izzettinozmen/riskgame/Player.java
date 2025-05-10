/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.izzettinozmen.riskgame;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ifozmen
 */
public class Player {
    private String name;
    private Color color;
    private List<Territory> territories;
    private int remainingSoldiers;
    
    public Player(String name, Color color, int initialSoldiers) {
        this.name = name;
        this.color = color;
        this.territories = new ArrayList<>();
        this.remainingSoldiers = initialSoldiers;
    }
    
    public String getName() {
        return name;
    }
    
    public Color getColor() {
        return color;
    }
    
    public List<Territory> getTerritories() {
        return territories;
    }
    
    public int getRemainingSoldiers() {
        return remainingSoldiers;
    }
     
    public void addSoldiers(int count) {
        remainingSoldiers += count;
    }
    
    public void removeSoldiers(int count) {
        if (count <= remainingSoldiers) {
            remainingSoldiers -= count;
        }
    }
    
    public boolean hasTerritory(Territory territory) {
        return territories.contains(territory);
    }
}
