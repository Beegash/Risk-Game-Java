/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.izzettinozmen.riskgame;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author ifozmen
 */
public class GameState implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String gameId;
    private List<Player> players;
    private List<Territory> territories;
    private Player currentPlayer;
    private int currentTurn;
    private GamePhase currentPhase;
    private boolean isGameOver;
    private Player winner;

    public enum GamePhase {
        SETUP,
        REINFORCEMENT,
        ATTACK,
        FORTIFICATION
    }

    public GameState() {
        this.gameId = UUID.randomUUID().toString();
        this.players = new ArrayList<>();
        this.territories = new ArrayList<>();
        this.currentTurn = 1;
        this.currentPhase = GamePhase.SETUP;
        this.isGameOver = false;
        this.winner = null;
    }

    // Custom deserialization
    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        // Restore adjacent territories for each territory
        for (Territory territory : territories) {
            territory.restoreAdjacentTerritories(territories);
        }
    }

    public String getGameId() {
        return gameId;
    }

    public void addPlayer(Player player) {
        if (!players.contains(player)) {
            players.add(player);
            if (currentPlayer == null) {
                currentPlayer = player;
            }
        }
    }

    public void addTerritory(Territory territory) {
        if (!territories.contains(territory)) {
            territories.add(territory);
        }
    }

    public List<Player> getPlayers() {
        return players;
    }

    public List<Territory> getTerritories() {
        return territories;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(Player player) {
        this.currentPlayer = player;
    }

    public int getCurrentTurn() {
        return currentTurn;
    }

    public void nextTurn() {
        int currentIndex = players.indexOf(currentPlayer);
        int nextIndex = (currentIndex + 1) % players.size();
        currentPlayer = players.get(nextIndex);
    }

    public GamePhase getCurrentPhase() {
        return currentPhase;
    }

    public void setCurrentPhase(GamePhase phase) {
        this.currentPhase = phase;
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public Player getWinner() {
        return winner;
    }

    public void checkGameEnd() {
        // Eğer oyun zaten bitmişse tekrar kontrol etme
        if (isGameOver) {
            return;
        }

        // Tüm bölgelerin sahibini kontrol et
        Player firstOwner = null;
        boolean allTerritoriesOwnedBySamePlayer = true;

        for (Territory territory : territories) {
            if (territory.getOwner() == null) {
                allTerritoriesOwnedBySamePlayer = false;
                break;
            }

            if (firstOwner == null) {
                firstOwner = territory.getOwner();
            } else if (territory.getOwner() != firstOwner) {
                allTerritoriesOwnedBySamePlayer = false;
                break;
            }
        }

        // Eğer tüm bölgeler aynı oyuncuya aitse oyun biter
        if (allTerritoriesOwnedBySamePlayer && firstOwner != null) {
            isGameOver = true;
            winner = firstOwner;
        }
    }

    public void initializeGame() {
        // Her oyuncuya başlangıç askerlerini ver
        for (Player player : players) {
            player.addSoldiers(GameRules.INITIAL_SOLDIERS_PER_PLAYER);
        }
        
        // İlk oyuncuyu belirle
        currentPlayer = players.get(0);
        currentPhase = GamePhase.SETUP;
    }
}
