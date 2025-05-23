package common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CommonTerritory implements Serializable {

    private String name;
    private String continent;
    private String owner;
    private int armies;
    private List<String> adjacentTerritories;
    private static final long serialVersionUID = 1L;

    public CommonTerritory(String name, String continent) {
        this.name = name;
        this.continent = continent;
        this.armies = 0;
        this.adjacentTerritories = new ArrayList<>();
    }

    public void addAdjacent(String neighbor) {
        adjacentTerritories.add(neighbor);
    }

    public String getName() {
        return name;
    }

    public String getContinent() {
        return continent;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public int getArmies() {
        return armies;
    }

    public void setArmies(int armies) {
        this.armies = armies;
    }

    public void addArmies(int count) {
        this.armies += count;
    }

    public void removeArmies(int count) {
        this.armies -= count;
    }

    public List<String> getAdjacentTerritories() {
        return adjacentTerritories;
    }
}
