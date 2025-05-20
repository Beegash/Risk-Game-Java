/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.izzettinozmen.riskgame;

import java.util.List;

/**
 *
 * @author ifozmen
 */
public class GameManager {
    private GameState gameState;
    private Territory selectedTerritory;
    private Territory targetTerritory;
    private TerritoryDistributor territoryDistributor;
    private Server server; // Server reference for notifications

    public GameManager() {
        this.gameState = new GameState();
        this.territoryDistributor = TerritoryDistributor.getInstance();
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public void startGame() {
        // Oyunu başlat
        gameState.initializeGame();
        
        // Bölgeleri dağıt (this modifies Territory instances from TerritoryManager)
        boolean distributionSuccess = territoryDistributor.distributeTerritories(gameState.getPlayers());
        if (!distributionSuccess) {
            throw new IllegalStateException("Territory distribution failed");
        }
        
        // Başlangıç askerlerini dağıt
        territoryDistributor.distributeInitialSoldiers(gameState.getPlayers());
        
        // Dağıtımı doğrula
        if (!territoryDistributor.validateDistribution(gameState.getPlayers())) {
            throw new IllegalStateException("Invalid territory distribution");
        }

        // GameState'in ana bölge listesini TerritoryManager'daki tüm bölgelerle doldur.
        // Bunlar TerritoryDistributor tarafından değiştirilen (sahip ve asker sayıları olan) aynı örneklerdir.
        List<Territory> allTerritoriesFromManager = TerritoryManager.getInstance().getAllTerritories();
        gameState.getTerritories().clear(); // Temiz olduğundan emin ol
        for (Territory territory : allTerritoriesFromManager) {
            gameState.addTerritory(territory); // Değiştirilmiş bölge örneklerini ekler
        }
        System.out.println("[GameManager] Populated gameState.territories with " + gameState.getTerritories().size() + " territories from TerritoryManager.");
        
        // İlk oyuncunun turunu başlat
        gameState.getCurrentPlayer().startTurn();
        
        // SETUP fazını başlat
        gameState.setCurrentPhase(GameState.GamePhase.SETUP);
        notifyPhaseChange(GameState.GamePhase.SETUP);
    }

    private boolean canAdvanceFromSetup() {
        // Tüm bölgelerin dolu olup olmadığını kontrol et
        if (!allTerritoriesOccupied()) {
            return false;
        }

        // Her oyuncunun başlangıç askerlerini yerleştirdiğini kontrol et
        for (Player player : gameState.getPlayers()) {
            if (player.getRemainingSoldiers() > 0) {
                return false;
            }
        }

        return true;
    }

    private boolean canAdvanceFromReinforcement() {
        Player currentPlayer = gameState.getCurrentPlayer();
        
        // Oyuncunun kalan askeri var mı kontrol et
        if (currentPlayer.getRemainingSoldiers() > 0) {
            if (server != null) {
                server.broadcastToLobby(new GameMessage(
                    GameMessage.GameMessageType.PHASE_CHANGE_FAILED,
                    String.format("Cannot advance from REINFORCEMENT phase: %s still has %d soldiers to place.",
                        currentPlayer.getName(),
                        currentPlayer.getRemainingSoldiers())
                ));
            }
            return false;
        }
        
        return true;
    }

    private boolean canAdvanceFromAttack() {
        // ATTACK fazından çıkmak için özel bir kontrol yok
        // Oyuncu istediği zaman ATTACK fazından çıkabilir
        return true;
    }

    private boolean canAdvanceFromFortification() {
        // FORTIFICATION fazından çıkmak için özel bir kontrol yok
        // Oyuncu istediği zaman FORTIFICATION fazından çıkabilir
        return true;
    }

    private void notifyPhaseChange(GameState.GamePhase newPhase) {
        if (server != null) {
            String message = String.format("Game phase changed to: %s", newPhase);
            server.broadcastToLobby(new GameMessage(
                GameMessage.GameMessageType.PHASE_CHANGED,
                message
            ));
        }
    }

    public boolean nextPhase() {
        GameState.GamePhase currentPhase = gameState.getCurrentPhase();
        GameState.GamePhase nextPhase;

        // Oyun bittiyse faz değişimine izin verme
        if (gameState.isGameOver()) {
            if (server != null) {
                server.broadcastToLobby(new GameMessage(
                    GameMessage.GameMessageType.INVALID_MOVE,
                    "Game is over. No more phase changes allowed."
                ));
            }
            return false;
        }

        switch (currentPhase) {
            case SETUP:
                if (!canAdvanceFromSetup()) {
                    if (server != null) {
                        server.broadcastToLobby(new GameMessage(
                            GameMessage.GameMessageType.PHASE_CHANGE_FAILED,
                            "Cannot advance from SETUP phase: All territories must be occupied and all initial soldiers must be placed."
                        ));
                    }
                    return false;
                }
                nextPhase = GameState.GamePhase.REINFORCEMENT;
                break;
            case REINFORCEMENT:
                if (!canAdvanceFromReinforcement()) {
                    return false;
                }
                nextPhase = GameState.GamePhase.ATTACK;
                break;
            case ATTACK:
                if (!canAdvanceFromAttack()) {
                    return false;
                }
                nextPhase = GameState.GamePhase.FORTIFICATION;
                break;
            case FORTIFICATION:
                if (!canAdvanceFromFortification()) {
                    return false;
                }
                Player currentPlayer = gameState.getCurrentPlayer();
                currentPlayer.endTurn();
                gameState.nextTurn();
                currentPlayer = gameState.getCurrentPlayer();
                currentPlayer.startTurn();
                nextPhase = GameState.GamePhase.REINFORCEMENT;
                break;
            default:
                return false;
        }

        gameState.setCurrentPhase(nextPhase);
        notifyPhaseChange(nextPhase);
        return true;
    }

    public boolean placeSoldiers(Territory territory, int count) {
        if (gameState.getCurrentPhase() != GameState.GamePhase.SETUP && 
            gameState.getCurrentPhase() != GameState.GamePhase.REINFORCEMENT) {
            if (server != null) {
                server.broadcastToLobby(new GameMessage(
                    GameMessage.GameMessageType.INVALID_MOVE,
                    "Soldiers can only be placed during SETUP or REINFORCEMENT phases."
                ));
            }
            return false;
        }

        Player currentPlayer = gameState.getCurrentPlayer();
        
        // Asker sayısı kontrolü
        if (count <= 0) {
            if (server != null) {
                server.broadcastToLobby(new GameMessage(
                    GameMessage.GameMessageType.INVALID_MOVE,
                    "Must place at least 1 soldier."
                ));
            }
            return false;
        }
        
        if (count > currentPlayer.getRemainingSoldiers()) {
            if (server != null) {
                server.broadcastToLobby(new GameMessage(
                    GameMessage.GameMessageType.INVALID_MOVE,
                    String.format("Cannot place %d soldiers. Only %d soldiers remaining.",
                        count,
                        currentPlayer.getRemainingSoldiers())
                ));
            }
            return false;
        }

        // Bölge sahipliği kontrolü
        if (gameState.getCurrentPhase() == GameState.GamePhase.SETUP) {
            if (territory.getOwner() != null) {
                if (server != null) {
                    server.broadcastToLobby(new GameMessage(
                        GameMessage.GameMessageType.INVALID_MOVE,
                        "Territory is already owned during SETUP phase."
                    ));
                }
                return false;
            }
            territory.setOwner(currentPlayer);
            currentPlayer.addTerritory(territory);
        } else if (territory.getOwner() != currentPlayer) {
            if (server != null) {
                server.broadcastToLobby(new GameMessage(
                    GameMessage.GameMessageType.INVALID_MOVE,
                    "Can only place soldiers in your own territories during REINFORCEMENT phase."
                ));
            }
            return false;
        }

        // Askerleri yerleştir
        territory.addSoldiers(count);
        currentPlayer.removeSoldiers(count);
        
        // Başarılı yerleştirme bildirimi
        if (server != null) {
            server.broadcastToLobby(new GameMessage(
                GameMessage.GameMessageType.SOLDIERS_PLACED,
                String.format("%s placed %d soldiers in %s",
                    currentPlayer.getName(),
                    count,
                    territory.getName())
            ));
        }
        
        return true;
    }

    public boolean attack(Territory attacker, Territory defender, int attackingSoldiers) {
        if (gameState.getCurrentPhase() != GameState.GamePhase.ATTACK) {
            if (server != null) {
                server.broadcastToLobby(new GameMessage(
                    GameMessage.GameMessageType.INVALID_MOVE,
                    "Attacks can only be made during ATTACK phase."
                ));
            }
            return false;
        }

        Player currentPlayer = gameState.getCurrentPlayer();

        // Saldıran bölge kontrolü
        if (attacker.getOwner() != currentPlayer) {
            if (server != null) {
                server.broadcastToLobby(new GameMessage(
                    GameMessage.GameMessageType.INVALID_MOVE,
                    "You can only attack from your own territories."
                ));
            }
            return false;
        }

        // Savunan bölge kontrolü
        if (defender.getOwner() == currentPlayer) {
            if (server != null) {
                server.broadcastToLobby(new GameMessage(
                    GameMessage.GameMessageType.INVALID_MOVE,
                    "You cannot attack your own territories."
                ));
            }
            return false;
        }

        // Komşuluk kontrolü
        if (!attacker.isAdjacentTo(defender)) {
            if (server != null) {
                server.broadcastToLobby(new GameMessage(
                    GameMessage.GameMessageType.INVALID_MOVE,
                    "You can only attack adjacent territories."
                ));
            }
            return false;
        }

        // Asker sayısı kontrolleri
        if (attackingSoldiers <= 0) {
            if (server != null) {
                server.broadcastToLobby(new GameMessage(
                    GameMessage.GameMessageType.INVALID_MOVE,
                    "You must attack with at least 1 soldier."
                ));
            }
            return false;
        }

        if (attackingSoldiers >= attacker.getSoldierCount()) {
            if (server != null) {
                server.broadcastToLobby(new GameMessage(
                    GameMessage.GameMessageType.INVALID_MOVE,
                    "You must leave at least 1 soldier in the attacking territory."
                ));
            }
            return false;
        }

        if (attackingSoldiers > GameRules.MAX_DICE_PER_ATTACK) {
            if (server != null) {
                server.broadcastToLobby(new GameMessage(
                    GameMessage.GameMessageType.INVALID_MOVE,
                    String.format("You can only attack with a maximum of %d soldiers.",
                        GameRules.MAX_DICE_PER_ATTACK)
                ));
            }
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

        // Savaş sonucu bildirimi
        if (server != null) {
            server.broadcastToLobby(new GameMessage(
                GameMessage.GameMessageType.ATTACK_MADE,
                String.format("%s attacked %s with %d soldiers. Attacker lost %d, defender lost %d.",
                    attacker.getName(),
                    defender.getName(),
                    attackingSoldiers,
                    result.getAttackerLosses(),
                    result.getDefenderLosses())
            ));
        }

        // Eğer savunucu kaybetti ise bölgeyi ele geçir
        if (defender.getSoldierCount() == 0) {
            Player oldOwner = defender.getOwner();
            if (oldOwner != null) {
                oldOwner.removeTerritory(defender);
            }
            defender.setOwner(currentPlayer);
            currentPlayer.addTerritory(defender);
            
            // En az bir asker bırak
            int remainingSoldiers = attackingSoldiers - result.getAttackerLosses();
            defender.addSoldiers(remainingSoldiers);
            attacker.removeSoldiers(remainingSoldiers);

            // Bölge ele geçirme bildirimi
            if (server != null) {
                server.broadcastToLobby(new GameMessage(
                    GameMessage.GameMessageType.TERRITORY_SELECTED,
                    String.format("%s conquered %s!",
                        currentPlayer.getName(),
                        defender.getName())
                ));
            }

            // Oyun sonu kontrolü
            gameState.checkGameEnd();
            if (gameState.isGameOver()) {
                Player winner = gameState.getWinner();
                if (server != null) {
                    server.broadcastToLobby(new GameMessage(
                        GameMessage.GameMessageType.GAME_OVER,
                        String.format("Game Over! %s has won the game by conquering all territories!",
                            winner.getName())
                    ));
                }
            }
            return true;
        }

        return false;
    }

    public boolean fortify(Territory from, Territory to, int soldierCount) {
        if (gameState.getCurrentPhase() != GameState.GamePhase.FORTIFICATION) {
            if (server != null) {
                server.broadcastToLobby(new GameMessage(
                    GameMessage.GameMessageType.INVALID_MOVE,
                    "Fortification can only be done during FORTIFICATION phase."
                ));
            }
            return false;
        }

        Player currentPlayer = gameState.getCurrentPlayer();

        // Bölge sahipliği kontrolü
        if (from.getOwner() != currentPlayer || to.getOwner() != currentPlayer) {
            if (server != null) {
                server.broadcastToLobby(new GameMessage(
                    GameMessage.GameMessageType.INVALID_MOVE,
                    "You can only fortify between your own territories."
                ));
            }
            return false;
        }

        // Aynı bölge kontrolü
        if (from.equals(to)) {
            if (server != null) {
                server.broadcastToLobby(new GameMessage(
                    GameMessage.GameMessageType.INVALID_MOVE,
                    "You cannot fortify a territory with itself."
                ));
            }
            return false;
        }

        // Komşuluk kontrolü
        if (!from.isAdjacentTo(to)) {
            if (server != null) {
                server.broadcastToLobby(new GameMessage(
                    GameMessage.GameMessageType.INVALID_MOVE,
                    "You can only fortify between adjacent territories."
                ));
            }
            return false;
        }

        // Asker sayısı kontrolleri
        if (soldierCount <= 0) {
            if (server != null) {
                server.broadcastToLobby(new GameMessage(
                    GameMessage.GameMessageType.INVALID_MOVE,
                    "You must move at least 1 soldier."
                ));
            }
            return false;
        }

        if (soldierCount >= from.getSoldierCount()) {
            if (server != null) {
                server.broadcastToLobby(new GameMessage(
                    GameMessage.GameMessageType.INVALID_MOVE,
                    "You must leave at least 1 soldier in the source territory."
                ));
            }
            return false;
        }

        // Askerleri taşı
        from.removeSoldiers(soldierCount);
        to.addSoldiers(soldierCount);

        // Başarılı güçlendirme bildirimi
        if (server != null) {
            server.broadcastToLobby(new GameMessage(
                GameMessage.GameMessageType.FORTIFICATION_MADE,
                String.format("%s moved %d soldiers from %s to %s",
                    currentPlayer.getName(),
                    soldierCount,
                    from.getName(),
                    to.getName())
            ));
        }

        return true;
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
