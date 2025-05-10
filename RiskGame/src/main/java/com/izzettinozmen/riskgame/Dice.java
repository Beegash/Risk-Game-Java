/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.izzettinozmen.riskgame;

import java.util.Random;

/**
 *
 * @author ifozmen
 */
public class Dice {
    private static final Random random = new Random();
    
    public static int[] rollDice(int count) {
        int[] dice = new int[count];
        for (int i = 0; i < count; i++) {
            dice[i] = random.nextInt(6) + 1;
        }
        return dice;
    }
    
    public static void sortDice(int[] dice) {
        for (int i = 0; i < dice.length - 1; i++) {
            for (int j = 0; j < dice.length - i - 1; j++) {
                if (dice[j] < dice[j + 1]) {
                    int temp = dice[j];
                    dice[j] = dice[j + 1];
                    dice[j + 1] = temp;
                }
            }
        }
    }
    
    public static BattleResult calculateBattleResult(int[] attackerDice, int[] defenderDice) {
        int attackerLosses = 0;
        int defenderLosses = 0;

        // Zar sayılarını sırala (büyükten küçüğe)
        sortDice(attackerDice);
        sortDice(defenderDice);

        // Her tur için karşılaştırma yap
        int comparisons = Math.min(attackerDice.length, defenderDice.length);
        for (int i = 0; i < comparisons; i++) {
            if (attackerDice[i] > defenderDice[i]) {
                defenderLosses++;
            } else {
                attackerLosses++;
            }
        }

        return new BattleResult(attackerLosses, defenderLosses);
    }
    
    public static class BattleResult {
        private final int attackerLosses;
        private final int defenderLosses;

        public BattleResult(int attackerLosses, int defenderLosses) {
            this.attackerLosses = attackerLosses;
            this.defenderLosses = defenderLosses;
        }

        public int getAttackerLosses() {
            return attackerLosses;
        }

        public int getDefenderLosses() {
            return defenderLosses;
        }
    }
}
