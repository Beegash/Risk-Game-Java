/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.izzettinozmen.riskgame;

/**
 *
 * @author ifozmen
 */
public class GameRules {
    // Oyun sabitleri
    public static final int MIN_PLAYERS = 2;
    public static final int MAX_PLAYERS = 6;
    public static final int INITIAL_SOLDIERS_PER_PLAYER = 40;
    public static final int MIN_SOLDIERS_TO_ATTACK = 2;
    public static final int MAX_DICE_PER_ATTACK = 3;
    public static final int MAX_DICE_PER_DEFENSE = 2;

    // Bölge kontrolü için gerekli asker sayısını hesaplama
    public static int calculateRequiredSoldiers(Territory territory) {
        return Math.max(1, territory.getSoldierCount() / 2);
    }

    // Oyuncunun yeni asker kazanma sayısını hesaplama
    public static int calculateNewSoldiers(Player player) {
        int territoryCount = player.getTerritories().size();
        int continentBonus = calculateContinentBonus(player);
        return Math.max(3, territoryCount / 3) + continentBonus;
    }

    // Kıta bonusu hesaplama
    private static int calculateContinentBonus(Player player) {
        // Bu metod daha sonra kıta kontrolü eklendiğinde implement edilecek
        return 0;
    }
}
