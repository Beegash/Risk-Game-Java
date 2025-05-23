package common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CommonPlayer implements Serializable {

    private String name;
    private String color;
    private List<String> territories;
    private int availableArmies;
    private boolean isTurn;
    private static final long serialVersionUID = 1L;

    public CommonPlayer(String name, String color) {
        this.name = name;
        this.color = color;
        this.territories = new ArrayList<>();
        this.availableArmies = 20;
        this.isTurn = false;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public List<String> getTerritories() {
        return territories;
    }

    public int getAvailableArmies() {
        return availableArmies;
    }

    public void setAvailableArmies(int availableArmies) {
        this.availableArmies = availableArmies;
    }

    public void addTerritory(String territory) {
        territories.add(territory);
    }

    public void removeTerritory(String territory) {
        territories.remove(territory);
    }

    public boolean isTurn() {
        return isTurn;
    }

    public void setTurn(boolean isTurn) {
        this.isTurn = isTurn;
    }
}
