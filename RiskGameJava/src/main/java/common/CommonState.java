/**
 * CommonState.java
 * This class represents the core game state of the Risk game.
 * It manages territories, players, turn order, and calculates reinforcements.
 * The class is serializable to allow transmission between client and server.
 */

package common;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class CommonState implements Serializable {
    /** Map of all territories in the game, keyed by territory name */
    private Map<String, CommonTerritory> territories;
    /** Map of all players in the game, keyed by player name */
    private Map<String, CommonPlayer> players;
    /** Name of the player whose turn it currently is */
    private String currentTurnPlayer;
    /** Serial version UID for serialization compatibility */
    private static final long serialVersionUID = 1L;

    /** Map of continent bonuses for controlling entire continents */
    private final static Map<String, Integer> CONTINENT_BONUSES = Map.of(
            "Asia", 7,
            "North America", 5,
            "Europe", 5,
            "Africa", 3,
            "South America", 2,
            "Oceania", 2);

    /**
     * Constructor for CommonState
     * Initializes empty maps for territories and players
     */
    public CommonState() {
        territories = new HashMap<>();
        players = new HashMap<>();
    }

    /**
     * Adds a territory to the game state
     * 
     * @param t The territory to add
     */
    public void addTerritory(CommonTerritory t) {
        territories.put(t.getName(), t);
    }

    /**
     * Adds a player to the game state
     * 
     * @param p The player to add
     */
    public void addPlayer(CommonPlayer p) {
        players.put(p.getName(), p);
    }

    /**
     * Gets all territories in the game
     * 
     * @return Map of territories keyed by name
     */
    public Map<String, CommonTerritory> getTerritories() {
        return territories;
    }

    /**
     * Gets all players in the game
     * 
     * @return Map of players keyed by name
     */
    public Map<String, CommonPlayer> getPlayers() {
        return players;
    }

    /**
     * Gets the name of the player whose turn it is
     * 
     * @return Name of current turn player
     */
    public String getCurrentTurnPlayer() {
        return currentTurnPlayer;
    }

    /**
     * Sets the current turn player
     * 
     * @param currentTurnPlayer Name of the player whose turn it is
     */
    public void setCurrentTurnPlayer(String currentTurnPlayer) {
        this.currentTurnPlayer = currentTurnPlayer;
    }

    /**
     * Calculates the number of reinforcement armies a player receives at the start of their turn
     * Base reinforcement is max(3, territories/3) plus any continent control bonuses
     * 
     * @param playerName Name of the player to calculate reinforcements for
     * @return Number of reinforcement armies
     */
    public int calculateReinforcements(String playerName) {
        CommonPlayer player = players.get(playerName);
        int base = Math.max(3, player.getTerritories().size() / 3);
        int bonus = calculateContinentBonus(playerName);
        return base + bonus;
    }

    /**
     * Calculates the continent control bonus for a player
     * A player receives bonus armies for controlling all territories in a continent
     * 
     * @param playerName Name of the player to calculate bonus for
     * @return Total continent control bonus
     */
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
