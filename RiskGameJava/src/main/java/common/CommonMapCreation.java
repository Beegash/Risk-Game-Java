package common;

import java.util.HashMap;
import java.util.Map;

public class CommonMapCreation {

    public static Map<String, CommonTerritory> generateMap() {
        Map<String, CommonTerritory> map = new HashMap<>();

        addTerritory(map, "Alaska", "North America", "NorthWestTerritory", "Alberta", "Kamchatka");
        addTerritory(map, "NorthWestTerritory", "North America", "Alaska", "Alberta", "Ontario", "Greenland", "Aratopia");
        addTerritory(map, "Alberta", "North America", "Alaska", "NorthWestTerritory", "Ontario", "WesternUnitedStates");
        addTerritory(map, "Ontario", "North America", "NorthWestTerritory", "Alberta", "WesternUnitedStates", "EasternUnitedStates", "Quebec", "Aratopia");
        addTerritory(map, "Quebec", "North America", "Ontario", "EasternUnitedStates", "Greenland");
        addTerritory(map, "WesternUnitedStates", "North America", "Alberta", "Ontario", "EasternUnitedStates", "CentralAmerica");
        addTerritory(map, "EasternUnitedStates", "North America", "WesternUnitedStates", "Ontario", "Quebec", "CentralAmerica", "Izzettinia");
        addTerritory(map, "CentralAmerica", "North America", "WesternUnitedStates", "EasternUnitedStates", "Venezuela");
        addTerritory(map, "Greenland", "North America", "NorthWestTerritory", "Ontario", "Quebec", "Iceland");
        addTerritory(map, "Aratopia", "North America", "NorthWestTerritory", "Ontario", "Cihanland");
        addTerritory(map, "Izzettinia", "North America", "Brazil", "NorthAfrica", "EasternUnitedStates", "Venezuela");

        addTerritory(map, "Venezuela", "South America", "CentralAmerica", "Brazil", "Peru", "Izzettinia");
        addTerritory(map, "Brazil", "South America", "Venezuela", "Peru", "Argentina", "NorthAfrica", "Izzettinia");
        addTerritory(map, "Peru", "South America", "Venezuela", "Brazil", "Argentina");
        addTerritory(map, "Argentina", "South America", "Brazil", "Peru");

        addTerritory(map, "Iceland", "Europe", "Greenland", "Scandinavia", "GreatBritain");
        addTerritory(map, "Scandinavia", "Europe", "Iceland", "NorthernEurope", "Ukraine", "GreatBritain");
        addTerritory(map, "GreatBritain", "Europe", "Iceland", "Scandinavia", "NorthernEurope", "WesternEurope");
        addTerritory(map, "NorthernEurope", "Europe", "Scandinavia", "Ukraine", "SouthernEurope", "WesternEurope", "GreatBritain");
        addTerritory(map, "WesternEurope", "Europe", "GreatBritain", "NorthernEurope", "SouthernEurope", "NorthAfrica");
        addTerritory(map, "SouthernEurope", "Europe", "WesternEurope", "NorthernEurope", "Ukraine", "Egypt", "MiddleEast");
        addTerritory(map, "Ukraine", "Europe", "Scandinavia", "NorthernEurope", "SouthernEurope", "Ural", "Afghanistan", "MiddleEast");
        addTerritory(map, "Cihanland", "Europe", "Aratopia", "Mongolia", "Irkutsk");

        addTerritory(map, "NorthAfrica", "Africa", "Brazil", "WesternEurope", "Egypt", "EastAfrica", "Congo", "Izzettinia");
        addTerritory(map, "Egypt", "Africa", "NorthAfrica", "EastAfrica", "SouthernEurope", "MiddleEast");
        addTerritory(map, "EastAfrica", "Africa", "Egypt", "NorthAfrica", "Congo", "SouthAfrica", "MiddleEast");
        addTerritory(map, "Congo", "Africa", "NorthAfrica", "EastAfrica", "SouthAfrica");
        addTerritory(map, "SouthAfrica", "Africa", "Congo", "EastAfrica");

        addTerritory(map, "Ural", "Asia", "Ukraine", "Siberia", "China", "Afghanistan");
        addTerritory(map, "Siberia", "Asia", "Ural", "Yakutsk", "Irkutsk", "Mongolia", "China");
        addTerritory(map, "Yakutsk", "Asia", "Siberia", "Irkutsk", "Kamchatka");
        addTerritory(map, "Kamchatka", "Asia", "Yakutsk", "Irkutsk", "Mongolia", "Japan", "Alaska");
        addTerritory(map, "Irkutsk", "Asia", "Siberia", "Yakutsk", "Kamchatka", "Mongolia", "Cihanland");
        addTerritory(map, "Mongolia", "Asia", "Siberia", "Irkutsk", "Kamchatka", "Japan", "China", "Cihanland");
        addTerritory(map, "Japan", "Asia", "Kamchatka", "Mongolia");
        addTerritory(map, "China", "Asia", "Ural", "Siberia", "Mongolia", "India", "Slam");
        addTerritory(map, "India", "Asia", "China", "MiddleEast", "Slam");
        addTerritory(map, "Afghanistan", "Asia", "Ukraine", "Ural", "China", "MiddleEast");
        addTerritory(map, "MiddleEast", "Asia", "Afghanistan", "India", "Egypt", "EastAfrica", "SouthernEurope");
        addTerritory(map, "Slam", "Asia", "China", "India", "Sametistan");
        addTerritory(map, "Sametistan", "Asia", "Slam", "Indonesia", "NewGuinea");

        addTerritory(map, "Indonesia", "Oceania", "Sametistan", "NewGuinea", "WesternAustralia", "EasternAustralia");
        addTerritory(map, "NewGuinea", "Oceania", "Sametistan", "Indonesia", "EasternAustralia", "Latifland");
        addTerritory(map, "WesternAustralia", "Oceania", "Indonesia", "EasternAustralia");
        addTerritory(map, "EasternAustralia", "Oceania", "Indonesia", "WesternAustralia", "NewGuinea", "Latifland");
        addTerritory(map, "Latifland", "Oceania", "EasternAustralia", "NewGuinea");

        return map;
    }

    private static void addTerritory(Map<String, CommonTerritory> map, String name, String continent, String... neighbors) {
        CommonTerritory territory = new CommonTerritory(name, continent);
        for (String neighbor : neighbors) {
            territory.addAdjacent(neighbor);
        }
        map.put(name, territory);
    }
}
