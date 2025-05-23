package common;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class CommonState implements Serializable {

    private Map<String, CommonTerritory> territories;
    private Map<String, CommonPlayer> players;
    private String currentTurnPlayer;
    private static final long serialVersionUID = 1L;

    private final static Map<String, Integer> CONTINENT_BONUSES = Map.of(
            "Asia", 7,
            "North America", 5,
            "Europe", 5,
            "Africa", 3,
            "South America", 2,
            "Oceania", 2);

    public CommonState() {
        territories = new HashMap<>();
        players = new HashMap<>();
    }

    public void addTerritory(CommonTerritory t) {
        territories.put(t.getName(), t);
    }

    public void addPlayer(CommonPlayer p) {
        players.put(p.getName(), p);
    }

    public Map<String, CommonTerritory> getTerritories() {
        return territories;
    }

    public Map<String, CommonPlayer> getPlayers() {
        return players;
    }

    public String getCurrentTurnPlayer() {
        return currentTurnPlayer;
    }

    public void setCurrentTurnPlayer(String currentTurnPlayer) {
        this.currentTurnPlayer = currentTurnPlayer;
    }

    public int calculateReinforcements(String playerName) {
        CommonPlayer player = players.get(playerName);
        int base = Math.max(3, player.getTerritories().size() / 3);
        int bonus = calculateContinentBonus(playerName);
        return base + bonus;
    }

    private int calculateContinentBonus(String playerName) {
        int totalBonus = 0;
        for (String continent : CONTINENT_BONUSES.keySet()) {
            boolean ownsAll = territories.values().stream()
                    .filter(t -> t.getContinent().equals(continent))
                    .allMatch(t -> playerName.equals(t.getOwner()));
            if (ownsAll) {
                totalBonus += CONTINENT_BONUSES.get(continent);
            }
        }
        return totalBonus;
    }
}
