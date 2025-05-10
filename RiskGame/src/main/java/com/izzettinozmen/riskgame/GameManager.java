/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.izzettinozmen.riskgame;

/**
 *
 * @author ifozmen
 */
public class GameManager {
    private GameState gameState;
    private Territory selectedTerritory;
    private Territory targetTerritory;

    public GameManager() {
        this.gameState = new GameState();
    }

    public void startGame() {
        gameState.initializeGame();
    }

    public boolean placeSoldiers(Territory territory, int count) {
        if (gameState.getCurrentPhase() != GameState.GamePhase.SETUP && 
            gameState.getCurrentPhase() != GameState.GamePhase.REINFORCEMENT) {
            return false;
        }

        Player currentPlayer = gameState.getCurrentPlayer();
        if (count > currentPlayer.getRemainingSoldiers()) {
            return false;
        }

        if (gameState.getCurrentPhase() == GameState.GamePhase.SETUP) {
            if (territory.getOwner() != null) {
                return false;
            }
            territory.setOwner(currentPlayer);
        } else if (territory.getOwner() != currentPlayer) {
            return false;
        }

        territory.addSoldiers(count);
        currentPlayer.removeSoldiers(count);
        return true;
    }

    public boolean attack(Territory attacker, Territory defender, int attackingSoldiers) {
        if (gameState.getCurrentPhase() != GameState.GamePhase.ATTACK) {
            return false;
        }

        Player currentPlayer = gameState.getCurrentPlayer();
        if (attacker.getOwner() != currentPlayer || defender.getOwner() == currentPlayer) {
            return false;
        }

        if (!attacker.isAdjacentTo(defender)) {
            return false;
        }

        if (attackingSoldiers >= attacker.getSoldierCount() || 
            attackingSoldiers > GameRules.MAX_DICE_PER_ATTACK) {
            return false;
        }

        // Savaş hesaplaması
        int[] attackerDice = Dice.rollDice(attackingSoldiers);
        int[] defenderDice = Dice.rollDice(Math.min(defender.getSoldierCount(), 
                                                   GameRules.MAX_DICE_PER_DEFENSE));

        Dice.BattleResult result = Dice.calculateBattleResult(attackerDice, defenderDice);

        // Kayıpları uygula
        attacker.removeSoldiers(result.getAttackerLosses());
        defender.removeSoldiers(result.getDefenderLosses());

        // Eğer savunucu kaybetti ise bölgeyi ele geçir
        if (defender.getSoldierCount() == 0) {
            defender.setOwner(currentPlayer);
            // En az bir asker bırak
            int remainingSoldiers = attackingSoldiers - result.getAttackerLosses();
            defender.addSoldiers(remainingSoldiers);
            attacker.removeSoldiers(remainingSoldiers);
            return true;
        }

        return false;
    }

    public boolean fortify(Territory from, Territory to, int soldierCount) {
        if (gameState.getCurrentPhase() != GameState.GamePhase.FORTIFICATION) {
            return false;
        }

        Player currentPlayer = gameState.getCurrentPlayer();
        if (from.getOwner() != currentPlayer || to.getOwner() != currentPlayer) {
            return false;
        }

        if (soldierCount >= from.getSoldierCount()) {
            return false;
        }

        from.removeSoldiers(soldierCount);
        to.addSoldiers(soldierCount);
        return true;
    }

    public void nextPhase() {
        GameState.GamePhase currentPhase = gameState.getCurrentPhase();
        switch (currentPhase) {
            case SETUP:
                if (allTerritoriesOccupied()) {
                    gameState.setCurrentPhase(GameState.GamePhase.REINFORCEMENT);
                }
                break;
            case REINFORCEMENT:
                gameState.setCurrentPhase(GameState.GamePhase.ATTACK);
                break;
            case ATTACK:
                gameState.setCurrentPhase(GameState.GamePhase.FORTIFICATION);
                break;
            case FORTIFICATION:
                gameState.nextTurn();
                gameState.setCurrentPhase(GameState.GamePhase.REINFORCEMENT);
                break;
        }
    }

    private boolean allTerritoriesOccupied() {
        return gameState.getTerritories().stream()
                .allMatch(territory -> territory.getOwner() != null);
    }

    public GameState getGameState() {
        return gameState;
    }

    public Territory getSelectedTerritory() {
        return selectedTerritory;
    }

    public void setSelectedTerritory(Territory territory) {
        this.selectedTerritory = territory;
    }

    public Territory getTargetTerritory() {
        return targetTerritory;
    }

    public void setTargetTerritory(Territory territory) {
        this.targetTerritory = territory;
    }
}
