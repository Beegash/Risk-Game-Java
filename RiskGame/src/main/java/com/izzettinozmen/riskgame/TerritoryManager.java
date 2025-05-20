package com.izzettinozmen.riskgame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TerritoryManager {
    private static TerritoryManager instance;
    private Map<String, Territory> territories;
    private Map<Territory.Continent, Integer> continentBonuses;
    
    private TerritoryManager() {
        territories = new HashMap<>();
        initializeContinentBonuses();
        initializeTerritories();
    }
    
    public static TerritoryManager getInstance() {
        if (instance == null) {
            instance = new TerritoryManager();
        }
        return instance;
    }
    
    private void initializeContinentBonuses() {
        continentBonuses = new HashMap<>();
        continentBonuses.put(Territory.Continent.NORTH_AMERICA, 5);  // 9 territory
        continentBonuses.put(Territory.Continent.SOUTH_AMERICA, 2);  // 4 territory
        continentBonuses.put(Territory.Continent.EUROPE, 5);         // 7 territory
        continentBonuses.put(Territory.Continent.AFRICA, 3);         // 6 territory
        continentBonuses.put(Territory.Continent.ASIA, 7);           // 12 territory
        continentBonuses.put(Territory.Continent.AUSTRALIA, 2);      // 4 territory
        continentBonuses.put(Territory.Continent.CUSTOM, 0);         // Custom territories have no bonus
    }
    
    private void initializeTerritories() {
        // North America
        territories.put("Alaska", new Territory("Alaska", Territory.Continent.NORTH_AMERICA));
        territories.put("NorthWestTerritory", new Territory("NorthWestTerritory", Territory.Continent.NORTH_AMERICA));
        territories.put("Alberta", new Territory("Alberta", Territory.Continent.NORTH_AMERICA));
        territories.put("Ontario", new Territory("Ontario", Territory.Continent.NORTH_AMERICA));
        territories.put("Quebec", new Territory("Quebec", Territory.Continent.NORTH_AMERICA));
        territories.put("Greenland", new Territory("Greenland", Territory.Continent.NORTH_AMERICA));
        territories.put("WesternUnitedStates", new Territory("WesternUnitedStates", Territory.Continent.NORTH_AMERICA));
        territories.put("EasternUnitedStates", new Territory("EasternUnitedStates", Territory.Continent.NORTH_AMERICA));
        territories.put("CentralAmerica", new Territory("CentralAmerica", Territory.Continent.NORTH_AMERICA));
        
        // South America
        territories.put("Venezuela", new Territory("Venezuela", Territory.Continent.SOUTH_AMERICA));
        territories.put("Peru", new Territory("Peru", Territory.Continent.SOUTH_AMERICA));
        territories.put("Brazil", new Territory("Brazil", Territory.Continent.SOUTH_AMERICA));
        territories.put("Argentina", new Territory("Argentina", Territory.Continent.SOUTH_AMERICA));
        
        // Europe
        territories.put("Iceland", new Territory("Iceland", Territory.Continent.EUROPE));
        territories.put("Scandinavia", new Territory("Scandinavia", Territory.Continent.EUROPE));
        territories.put("GreatBritain", new Territory("GreatBritain", Territory.Continent.EUROPE));
        territories.put("NorthernEurope", new Territory("NorthernEurope", Territory.Continent.EUROPE));
        territories.put("WesternEurope", new Territory("WesternEurope", Territory.Continent.EUROPE));
        territories.put("SouthernEurope", new Territory("SouthernEurope", Territory.Continent.EUROPE));
        territories.put("Ukranie", new Territory("Ukranie", Territory.Continent.EUROPE));
        
        // Africa
        territories.put("NorthAfrica", new Territory("NorthAfrica", Territory.Continent.AFRICA));
        territories.put("Egypt", new Territory("Egypt", Territory.Continent.AFRICA));
        territories.put("EastAfrica", new Territory("EastAfrica", Territory.Continent.AFRICA));
        territories.put("Congo", new Territory("Congo", Territory.Continent.AFRICA));
        territories.put("SouthAfrica", new Territory("SouthAfrica", Territory.Continent.AFRICA));
        
        // Asia
        territories.put("Ural", new Territory("Ural", Territory.Continent.ASIA));
        territories.put("Siberia", new Territory("Siberia", Territory.Continent.ASIA));
        territories.put("Yakutsk", new Territory("Yakutsk", Territory.Continent.ASIA));
        territories.put("Kamchatka", new Territory("Kamchatka", Territory.Continent.ASIA));
        territories.put("Irkutsk", new Territory("Irkutsk", Territory.Continent.ASIA));
        territories.put("Mongolia", new Territory("Mongolia", Territory.Continent.ASIA));
        territories.put("Japan", new Territory("Japan", Territory.Continent.ASIA));
        territories.put("Afghanistan", new Territory("Afghanistan", Territory.Continent.ASIA));
        territories.put("China", new Territory("China", Territory.Continent.ASIA));
        territories.put("India", new Territory("India", Territory.Continent.ASIA));
        territories.put("MiddleEast", new Territory("MiddleEast", Territory.Continent.ASIA));
        territories.put("Slam", new Territory("Slam", Territory.Continent.ASIA));
        territories.put("Sametistan", new Territory("Sametistan", Territory.Continent.ASIA));
        
        // Australia
        territories.put("Indonesia", new Territory("Indonesia", Territory.Continent.AUSTRALIA));
        territories.put("NewGuinea", new Territory("NewGuinea", Territory.Continent.AUSTRALIA));
        territories.put("WesternAustralia", new Territory("WesternAustralia", Territory.Continent.AUSTRALIA));
        territories.put("EasternAustralia", new Territory("EasternAustralia", Territory.Continent.AUSTRALIA));
        
        // Custom Territories
        territories.put("Aratopia", new Territory("Aratopia", Territory.Continent.CUSTOM));
        territories.put("Cihanland", new Territory("Cihanland", Territory.Continent.CUSTOM));
        territories.put("Izzettinia", new Territory("Izzettinia", Territory.Continent.CUSTOM));
        territories.put("Latifland", new Territory("Latifland", Territory.Continent.CUSTOM));
        
        // Initialize adjacency relationships
        initializeAdjacencyRelationships();
    }
    
    private void initializeAdjacencyRelationships() {
        // North America
        Territory alaska = territories.get("Alaska");
        Territory northWestTerritory = territories.get("NorthWestTerritory");
        Territory alberta = territories.get("Alberta");
        Territory ontario = territories.get("Ontario");
        Territory quebec = territories.get("Quebec");
        Territory greenland = territories.get("Greenland");
        Territory westernUnitedStates = territories.get("WesternUnitedStates");
        Territory easternUnitedStates = territories.get("EasternUnitedStates");
        Territory centralAmerica = territories.get("CentralAmerica");
        
        alaska.addAdjacentTerritory(northWestTerritory);
        alaska.addAdjacentTerritory(alberta);
        
        northWestTerritory.addAdjacentTerritory(alaska);
        northWestTerritory.addAdjacentTerritory(alberta);
        northWestTerritory.addAdjacentTerritory(ontario);
        northWestTerritory.addAdjacentTerritory(greenland);
        
        alberta.addAdjacentTerritory(alaska);
        alberta.addAdjacentTerritory(northWestTerritory);
        alberta.addAdjacentTerritory(ontario);
        alberta.addAdjacentTerritory(westernUnitedStates);
        
        ontario.addAdjacentTerritory(northWestTerritory);
        ontario.addAdjacentTerritory(alberta);
        ontario.addAdjacentTerritory(quebec);
        ontario.addAdjacentTerritory(westernUnitedStates);
        ontario.addAdjacentTerritory(easternUnitedStates);
        
        quebec.addAdjacentTerritory(ontario);
        quebec.addAdjacentTerritory(easternUnitedStates);
        quebec.addAdjacentTerritory(greenland);
        
        greenland.addAdjacentTerritory(northWestTerritory);
        greenland.addAdjacentTerritory(quebec);
        
        westernUnitedStates.addAdjacentTerritory(alberta);
        westernUnitedStates.addAdjacentTerritory(ontario);
        westernUnitedStates.addAdjacentTerritory(easternUnitedStates);
        westernUnitedStates.addAdjacentTerritory(centralAmerica);
        
        easternUnitedStates.addAdjacentTerritory(ontario);
        easternUnitedStates.addAdjacentTerritory(quebec);
        easternUnitedStates.addAdjacentTerritory(westernUnitedStates);
        easternUnitedStates.addAdjacentTerritory(centralAmerica);
        
        centralAmerica.addAdjacentTerritory(westernUnitedStates);
        centralAmerica.addAdjacentTerritory(easternUnitedStates);
        
        // TODO: Add adjacency relationships for other continents
    }
    
    public Territory getTerritory(String name) {
        return territories.get(name);
    }
    
    public void setTerritoryOwner(String territoryName, Player player) {
        Territory territory = territories.get(territoryName);
        if (territory != null) {
            territory.setOwner(player);
        }
    }
    
    public void setTerritorySoldiers(String territoryName, int soldierCount) {
        Territory territory = territories.get(territoryName);
        if (territory != null) {
            territory.setSoldierCount(soldierCount);
        }
    }
    
    public int getTerritorySoldiers(String territoryName) {
        Territory territory = territories.get(territoryName);
        return territory != null ? territory.getSoldierCount() : 0;
    }
    
    public Player getTerritoryOwner(String territoryName) {
        Territory territory = territories.get(territoryName);
        return territory != null ? territory.getOwner() : null;
    }
    
    public int getPlayerTerritoryCount(Player player) {
        return (int) territories.values().stream()
                .filter(t -> player.equals(t.getOwner()))
                .count();
    }
    
    public int getPlayerTotalSoldiers(Player player) {
        return territories.values().stream()
                .filter(t -> player.equals(t.getOwner()))
                .mapToInt(Territory::getSoldierCount)
                .sum();
    }
    
    public boolean isTerritoryAdjacent(String territory1, String territory2) {
        Territory t1 = territories.get(territory1);
        Territory t2 = territories.get(territory2);
        return t1 != null && t2 != null && t1.isAdjacentTo(t2);
    }
    
    public int getContinentTerritoryCount(Territory.Continent continent) {
        return (int) territories.values().stream()
                .filter(t -> t.getContinent() == continent)
                .count();
    }
    
    public boolean playerControlsContinent(Player player, Territory.Continent continent) {
        long playerTerritoriesInContinent = territories.values().stream()
                .filter(t -> t.getContinent() == continent && player.equals(t.getOwner()))
                .count();
        return playerTerritoriesInContinent == getContinentTerritoryCount(continent);
    }
    
    public int getContinentBonus(Territory.Continent continent) {
        return continentBonuses.getOrDefault(continent, 0);
    }
    
    public int getPlayerTotalBonus(Player player) {
        int totalBonus = 0;
        for (Territory.Continent continent : Territory.Continent.values()) {
            if (playerControlsContinent(player, continent)) {
                totalBonus += getContinentBonus(continent);
            }
        }
        return totalBonus;
    }
    
    public Map<Territory.Continent, Integer> getPlayerContinentBonuses(Player player) {
        Map<Territory.Continent, Integer> playerBonuses = new HashMap<>();
        for (Territory.Continent continent : Territory.Continent.values()) {
            if (playerControlsContinent(player, continent)) {
                playerBonuses.put(continent, getContinentBonus(continent));
            }
        }
        return playerBonuses;
    }
    
    public String getContinentStatus(Player player) {
        StringBuilder status = new StringBuilder();
        for (Territory.Continent continent : Territory.Continent.values()) {
            if (continent == Territory.Continent.CUSTOM) continue; // Skip custom territories
            
            int totalTerritories = getContinentTerritoryCount(continent);
            int playerTerritories = (int) territories.values().stream()
                    .filter(t -> t.getContinent() == continent && player.equals(t.getOwner()))
                    .count();
            
            status.append(continent.name())
                  .append(": ")
                  .append(playerTerritories)
                  .append("/")
                  .append(totalTerritories)
                  .append(" territories");
            
            if (playerControlsContinent(player, continent)) {
                status.append(" (Bonus: +")
                      .append(getContinentBonus(continent))
                      .append(")");
            }
            status.append("\n");
        }
        return status.toString();
    }
    
    public List<Territory> getAllTerritories() {
        return new ArrayList<>(territories.values());
    }
    
    public Map<String, Territory> getTerritories() {
        return new HashMap<>(territories);
    }
} 
