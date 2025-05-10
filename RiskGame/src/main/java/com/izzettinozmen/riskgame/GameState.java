/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.izzettinozmen.riskgame;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ifozmen
 */
public class GameState {
    private List<Player> players;
    private List<Territory> territories;
    private Player currentPlayer;
    private int currentTurn;
    private GamePhase currentPhase;

    public enum GamePhase {
        SETUP,
        REINFORCEMENT,
        ATTACK,
        FORTIFICATION
    }

    public GameState() {
        this.players = new ArrayList<>();
        this.territories = new ArrayList<>();
        this.currentTurn = 1;
        this.currentPhase = GamePhase.SETUP;
    }

    public void addPlayer(Player player) {
        if (players.size() < GameRules.MAX_PLAYERS) {
            players.add(player);
        }
    }

    public void addTerritory(Territory territory) {
        territories.add(territory);
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
        this.currentTurn++;
        int currentPlayerIndex = players.indexOf(currentPlayer);
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        currentPlayer = players.get(currentPlayerIndex);
    }

    public GamePhase getCurrentPhase() {
        return currentPhase;
    }

    public void setCurrentPhase(GamePhase phase) {
        this.currentPhase = phase;
    }

    public boolean isGameOver() {
        return players.size() == 1;
    }

    public Player getWinner() {
        if (isGameOver()) {
            return players.get(0);
        }
        return null;
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
