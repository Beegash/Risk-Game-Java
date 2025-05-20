package com.izzettinozmen.riskgame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Map;
import java.util.HashMap;

public class TerritoryDistributor {
    private static TerritoryDistributor instance;
    private final Random random;
    private final TerritoryManager territoryManager;

    private TerritoryDistributor() {
        this.random = new Random();
        this.territoryManager = TerritoryManager.getInstance();
    }

    public static TerritoryDistributor getInstance() {
        if (instance == null) {
            instance = new TerritoryDistributor();
        }
        return instance;
    }

    /**
     * Tüm bölgeleri oyunculara eşit ve rastgele dağıtır.
     * @param players Dağıtım yapılacak oyuncular listesi
     * @return Dağıtım başarılı oldu mu?
     */
    public boolean distributeTerritories(List<Player> players) {
        if (players == null || players.isEmpty()) {
            return false;
        }

        // Tüm bölgeleri al ve karıştır
        Map<String, Territory> allTerritories = new HashMap<>(territoryManager.getTerritories());
        List<String> territoryNames = new ArrayList<>(allTerritories.keySet());
        Collections.shuffle(territoryNames, random);

        // Her oyuncuya eşit sayıda bölge dağıt
        int territoriesPerPlayer = territoryNames.size() / players.size();
        int remainingTerritories = territoryNames.size() % players.size();

        int currentIndex = 0;
        for (Player player : players) {
            // Her oyuncuya temel bölge sayısını dağıt
            for (int i = 0; i < territoriesPerPlayer; i++) {
                String territoryName = territoryNames.get(currentIndex++);
                Territory territory = allTerritories.get(territoryName);
                assignTerritoryToPlayer(territory, player);
            }

            // Kalan bölgeleri sırayla dağıt
            if (remainingTerritories > 0) {
                String territoryName = territoryNames.get(currentIndex++);
                Territory territory = allTerritories.get(territoryName);
                assignTerritoryToPlayer(territory, player);
                remainingTerritories--;
            }
        }

        return true;
    }

    /**
     * Belirli bir bölgeyi bir oyuncuya atar ve gerekli güncellemeleri yapar.
     * @param territory Atanacak bölge
     * @param player Bölgeyi alacak oyuncu
     */
    private void assignTerritoryToPlayer(Territory territory, Player player) {
        // Bölgeyi oyuncuya ata
        territory.setOwner(player);
        player.addTerritory(territory);

        // Başlangıç askerlerini yerleştir (her bölgeye 1 asker)
        territory.setSoldierCount(1);
        player.removeSoldiers(1);
    }

    /**
     * Oyunculara başlangıç askerlerini dağıtır.
     * @param players Asker dağıtılacak oyuncular listesi
     */
    public void distributeInitialSoldiers(List<Player> players) {
        int initialSoldiersPerPlayer = GameRules.INITIAL_SOLDIERS_PER_PLAYER;
        
        for (Player player : players) {
            // Oyuncunun bölgelerine yerleştirilmiş askerleri çıkar
            int placedSoldiers = player.getTerritoryCount();
            int remainingSoldiers = initialSoldiersPerPlayer - placedSoldiers;
            
            if (remainingSoldiers > 0) {
                player.addSoldiers(remainingSoldiers);
            }
        }
    }

    /**
     * Dağıtım sonrası oyuncuların durumunu kontrol eder.
     * @param players Kontrol edilecek oyuncular listesi
     * @return Tüm oyuncular en az bir bölgeye sahip mi?
     */
    public boolean validateDistribution(List<Player> players) {
        return players.stream()
                .allMatch(player -> player.getTerritoryCount() > 0);
    }
} 
