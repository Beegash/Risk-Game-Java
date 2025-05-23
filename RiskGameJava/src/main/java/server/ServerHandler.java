package server;

import common.*;

import java.io.*;
import java.net.Socket;
import java.util.logging.Logger;
import java.util.logging.Level;

public class ServerHandler implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(ServerHandler.class.getName());
    private static final int MAX_RETRY_ATTEMPTS = 3;
    private static final long RETRY_DELAY_MS = 1000;

    private final Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    volatile String playerName;
    private ServerHandler opponent;
    private CommonState gameState;
    private boolean rematchRequested = false;
    private boolean gameEnded = false;

    public ServerHandler(Socket socket, String playerName) {
        this.socket = socket;
        this.playerName = playerName;
    }

    public void setOpponent(ServerHandler opponent) {
        this.opponent = opponent;
    }

    public void setGameState(CommonState gameState) {
        this.gameState = gameState;
    }

    @Override
    public void run() {
        try {
            initializeStreams();
            waitForPlayerName();
            waitForGameState();
            sendInitialGameState();
            handleGameLoop();
        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            LOGGER.log(Level.SEVERE, "Player " + (playerName != null ? playerName : "unknown") + " disconnected: " + e.getMessage());
            handleDisconnection();
        }
    }

    private void initializeStreams() throws IOException {
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
    }

    private void waitForPlayerName() throws IOException, ClassNotFoundException {
        while (playerName == null) {
            Object obj = in.readObject();
            if (obj instanceof CommonMessages move && move.getType() == CommonMessages.Type.JOIN) {
                this.playerName = move.getMessage();
                LOGGER.info("Player JOIN received: " + playerName);
            }
        }
    }

    private void waitForGameState() throws InterruptedException {
        while (gameState == null) {
            Thread.sleep(50);
        }
    }

    private void sendInitialGameState() {
        LOGGER.info("Sending initial GameState to " + playerName);
        sendGameState();
    }

    private void handleGameLoop() throws IOException, ClassNotFoundException {
        while (true) {
            CommonMessages move = (CommonMessages) in.readObject();
            processMove(move);
            checkWinner();
        }
    }

    private void processMove(CommonMessages move) {
        LOGGER.info("🎯 Move from " + playerName + ": " + move.getType());

        switch (move.getType()) {
            case JOIN -> handleJoin(move);
            case PLACE_ARMY -> handlePlaceArmy(move);
            case ATTACK -> handleAttack(move);
            case FORTIFY -> handleFortify(move);
            case REMATCH_REQUEST -> handleRematchRequest();
            case END_TURN -> handleEndTurn();
            case PLAY_AGAIN -> handlePlayAgain();
            case EXIT_GAME -> handleExitGame();
            default -> LOGGER.warning("Unknown move type: " + move.getType());
        }

        if (!gameEnded) {
            sendGameState();
            opponent.sendGameState();
        }
    }

    private void handleJoin(CommonMessages move) {
        this.playerName = move.getMessage();
        LOGGER.info("🔗 Player joined: " + playerName);
    }

    private void handlePlaceArmy(CommonMessages move) {
        if (!gameState.getCurrentTurnPlayer().equals(playerName)) {
            sendMessage(new CommonMessages(CommonMessages.Type.CHAT, "It's not your turn!"));
            return;
        }

        CommonPlayer p = gameState.getPlayers().get(playerName);
        int available = p.getAvailableArmies();
        int requested = move.getArmyCount();
        CommonTerritory t1 = gameState.getTerritories().get(move.getTo());

        if (available <= 0) {
            sendMessage(new CommonMessages(CommonMessages.Type.CHAT, "You have no armies left to place."));
            return;
        }

        if (!t1.getOwner().equals(playerName)) {
            sendMessage(new CommonMessages(CommonMessages.Type.CHAT, "You can only place armies on your own territory!"));
            return;
        }

        int toPlace = Math.min(available, requested);
        t1.addArmies(toPlace);
        p.setAvailableArmies(available - toPlace);

        sendMessage(new CommonMessages(CommonMessages.Type.CHAT, "🪖 " + playerName + " placed " + toPlace
                + " armies on " + t1.getName() + ". Remaining: " + p.getAvailableArmies()));

        if (p.getAvailableArmies() == 0) {
            sendMessage(new CommonMessages(CommonMessages.Type.CHAT, "All armies placed. Now you can Attack, Fortify or End Turn."));
        }
    }

    private void handleAttack(CommonMessages move) {
        CommonTerritory from = gameState.getTerritories().get(move.getFrom());
        CommonTerritory to = gameState.getTerritories().get(move.getTo());

        if (!isValidAttack(from, to)) {
            sendMessage(new CommonMessages(CommonMessages.Type.CHAT, "Invalid attack!"));
            return;
        }

        int maxAttackerDice = Math.min(3, from.getArmies() - 1);
        int attackerDice = Math.min(move.getArmyCount(), maxAttackerDice);
        int defenderDice = Math.min(2, to.getArmies());

        int[] attackerRolls = rollDice(attackerDice);
        int[] defenderRolls = rollDice(defenderDice);

        int attackerLoss = 0;
        int defenderLoss = 0;

        for (int i = 0; i < Math.min(attackerDice, defenderDice); i++) {
            if (attackerRolls[i] > defenderRolls[i]) {
                defenderLoss++;
            } else {
                attackerLoss++;
            }
        }

        from.removeArmies(attackerLoss);
        to.removeArmies(defenderLoss);

        if (to.getArmies() <= 0) {
            handleTerritoryCapture(from, to, move.getArmyCount(), attackerLoss);
        }

        sendMessage(new CommonMessages(CommonMessages.Type.CHAT, "Dice | Attacker: " + arrayToString(attackerRolls)
                + " | Defender: " + arrayToString(defenderRolls)));
        sendMessage(new CommonMessages(CommonMessages.Type.CHAT, "Losses | Attacker: " + attackerLoss + ", Defender: " + defenderLoss));
    }

    private boolean isValidAttack(CommonTerritory from, CommonTerritory to) {
        return from.getOwner().equals(playerName)
                && !to.getOwner().equals(playerName)
                && from.getArmies() > 1
                && from.getAdjacentTerritories().contains(to.getName());
    }

    private void handleTerritoryCapture(CommonTerritory from, CommonTerritory to, int armyCount, int attackerLoss) {
        String previousOwner = to.getOwner();
        to.setOwner(playerName);
        int moveIn = Math.min(from.getArmies() - 1, armyCount - attackerLoss);
        from.removeArmies(moveIn);
        to.setArmies(moveIn);

        gameState.getPlayers().get(playerName).addTerritory(to.getName());
        gameState.getPlayers().get(previousOwner).removeTerritory(to.getName());
    }

    private void handleFortify(CommonMessages move) {
        CommonTerritory fromF = gameState.getTerritories().get(move.getFrom());
        CommonTerritory toF = gameState.getTerritories().get(move.getTo());
        int count = move.getArmyCount();

        if (!isValidFortify(fromF, toF, count)) {
            return;
        }

        fromF.removeArmies(count);
        toF.addArmies(count);

        sendMessage(new CommonMessages(CommonMessages.Type.CHAT, "Moved " + count + " armies from " + fromF.getName() + " to " + toF.getName()));
        opponent.sendMessage(new CommonMessages(CommonMessages.Type.CHAT, playerName + " fortified positions."));
    }

    private boolean isValidFortify(CommonTerritory from, CommonTerritory to, int count) {
        if (!playerName.equals(from.getOwner()) || !playerName.equals(to.getOwner())) {
            sendMessage(new CommonMessages(CommonMessages.Type.CHAT, "You must own both territories!"));
            return false;
        }
        if (!from.getAdjacentTerritories().contains(to.getName())) {
            sendMessage(new CommonMessages(CommonMessages.Type.CHAT, "Territories are not adjacent!"));
            return false;
        }
        if (from.getArmies() <= count) {
            sendMessage(new CommonMessages(CommonMessages.Type.CHAT, "Not enough armies to move (leave at least 1)!"));
            return false;
        }
        return true;
    }

    private void handleRematchRequest() {
        rematchRequested = true;
        LOGGER.info(playerName + " wants a rematch!");

        if (opponent != null && opponent.rematchRequested) {
            LOGGER.info("Both players requested rematch. Restarting game...");
            gameState = ServerManager.initializeGame(playerName, opponent.playerName);
            this.setGameState(gameState);
            opponent.setGameState(gameState);
            sendGameState();
            opponent.sendGameState();

            this.rematchRequested = false;
            opponent.rematchRequested = false;
        } else {
            sendMessage(new CommonMessages(CommonMessages.Type.CHAT, "Waiting for opponent to accept rematch..."));
        }
    }

    private void handleEndTurn() {
        if (!gameState.getCurrentTurnPlayer().equals(playerName)) {
            return;
        }

        gameState.setCurrentTurnPlayer(opponent.playerName);
        LOGGER.info("Turn passed to: " + opponent.playerName);

        CommonPlayer nextPlayer = gameState.getPlayers().get(opponent.playerName);
        int reinforcement = gameState.calculateReinforcements(opponent.playerName);
        nextPlayer.setAvailableArmies(nextPlayer.getAvailableArmies() + reinforcement);

        opponent.sendMessage(new CommonMessages(CommonMessages.Type.CHAT, "You received " + reinforcement + " reinforcement armies!"));
        sendMessage(new CommonMessages(CommonMessages.Type.CHAT, "Turn passed to " + opponent.playerName + "."));
    }

    private void handlePlayAgain() {
        if (gameEnded) {
            LOGGER.info(playerName + " wants to play again");
            ServerManager.addPlayer(socket);
            cleanup();
        }
    }

    private void handleExitGame() {
        LOGGER.info(playerName + " is exiting the game");
        if (opponent != null) {
            opponent.sendMessage(new CommonMessages(CommonMessages.Type.CHAT, "Opponent has left the game. You win!"));
            opponent.sendMessage(new CommonMessages(CommonMessages.Type.WIN, "🏆 You won the game!"));
            opponent.gameEnded = true;
        }
        cleanup();
    }

    private void handleDisconnection() {
        if (opponent != null && !gameEnded) {
            opponent.sendMessage(new CommonMessages(CommonMessages.Type.CHAT, "Opponent disconnected. You win!"));
            opponent.sendMessage(new CommonMessages(CommonMessages.Type.WIN, "🏆 You won the game!"));
            opponent.gameEnded = true;
        }
        cleanup();
    }

    private void cleanup() {
        gameEnded = true;
        closeSocket();
    }

    private void checkWinner() {
        for (CommonPlayer p : gameState.getPlayers().values()) {
            if (p.getTerritories().isEmpty()) {
                String winner = !playerName.equals(p.getName()) ? playerName : opponent.playerName;
                LOGGER.info("🏆 " + winner + " wins the game!");
                gameEnded = true;

                if (playerName.equals(winner)) {
                    sendMessage(new CommonMessages(CommonMessages.Type.WIN, "🏆 You won the game!"));
                    opponent.sendMessage(new CommonMessages(CommonMessages.Type.LOSE, "❌ You lost the game."));
                } else {
                    sendMessage(new CommonMessages(CommonMessages.Type.LOSE, "❌ You lost the game."));
                    opponent.sendMessage(new CommonMessages(CommonMessages.Type.WIN, "🏆 You won the game!"));
                }
            }
        }
    }

    private void sendGameState() {
        try {
            out.reset();
            out.writeObject(gameState);
            out.flush();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error sending game state to " + playerName + ": " + e.getMessage());
        }
    }

    public void sendMessage(CommonMessages move) {
        try {
            out.writeObject(move);
            out.flush();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error sending message to " + playerName + ": " + e.getMessage());
        }
    }

    private int[] rollDice(int count) {
        int[] rolls = new int[count];
        for (int i = 0; i < count; i++) {
            rolls[i] = (int) (Math.random() * 6) + 1;
        }
        java.util.Arrays.sort(rolls);
        for (int i = 0; i < count / 2; i++) {
            int tmp = rolls[i];
            rolls[i] = rolls[count - 1 - i];
            rolls[count - 1 - i] = tmp;
        }
        return rolls;
    }

    private String arrayToString(int[] arr) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            sb.append(arr[i]);
            if (i < arr.length - 1) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    private void closeSocket() {
        if (socket != null && !socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error closing socket for " + playerName + ": " + e.getMessage());
            }
        }
    }
}
