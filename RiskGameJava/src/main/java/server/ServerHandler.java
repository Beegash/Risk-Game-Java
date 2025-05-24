/**
 * ServerHandler.java
 * This class handles individual player connections and game logic on the server side.
 * It manages player communication, processes game moves, and enforces game rules.
 */

package server;

import common.*;

import java.io.*;
import java.net.Socket;
import java.util.logging.Logger;
import java.util.logging.Level;

public class ServerHandler implements Runnable {
    /** Logger for server-side logging */
    private static final Logger LOGGER = Logger.getLogger(ServerHandler.class.getName());

    // Player connection and game state
    private final Socket socket;                    // Player's socket connection
    private ObjectInputStream in;                   // Input stream for receiving messages
    private ObjectOutputStream out;                 // Output stream for sending messages
    volatile String playerName;                     // Player's name
    private ServerHandler opponent;                 // Reference to opponent's handler
    private CommonState gameState;                  // Current game state
    private boolean rematchRequested = false;       // Rematch request status
    private boolean gameEnded = false;              // Game end status

    /**
     * Constructor for ServerHandler
     * 
     * @param socket Player's socket connection
     * @param playerName Player's name (can be null initially)
     */
    public ServerHandler(Socket socket, String playerName) {
        this.socket = socket;
        this.playerName = playerName;
    }

    /**
     * Sets the opponent handler for this player
     * 
     * @param opponent Opponent's ServerHandler instance
     */
    public void setOpponent(ServerHandler opponent) {
        this.opponent = opponent;
    }

    /**
     * Sets the game state for this player
     * 
     * @param gameState Current game state
     */
    public void setGameState(CommonState gameState) {
        this.gameState = gameState;
    }

    /**
     * Main run method for the player handler thread
     * Manages the player's game session lifecycle
     */
    @Override
    public void run() {
        try {
            initializeStreams();
            waitForPlayerName();
            waitForGameState();
            sendInitialGameState();
            handleGameLoop();
        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            String errorMessage = e.getMessage();
            if (errorMessage == null) {
                errorMessage = "Player left the game";
            }
            LOGGER.log(Level.SEVERE, "Player " + (playerName != null ? playerName : "unknown") + " disconnected: " + errorMessage);
            handleDisconnection();
        }
    }

    /**
     * Initializes input and output streams for player communication
     */
    private void initializeStreams() throws IOException {
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
    }

    /**
     * Waits for the player to send their name
     */
    private void waitForPlayerName() throws IOException, ClassNotFoundException {
        while (playerName == null) {
            Object obj = in.readObject();
            if (obj instanceof CommonMessages move && move.getType() == CommonMessages.Type.JOIN) {
                this.playerName = move.getMessage();
                LOGGER.info("Player JOIN received: " + playerName);
            }
        }
    }

    /**
     * Waits for the game state to be initialized
     */
    private void waitForGameState() throws InterruptedException {
        while (gameState == null) {
            Thread.sleep(50);
        }
    }

    /**
     * Sends the initial game state to the player
     */
    private void sendInitialGameState() {
        LOGGER.info("Sending initial GameState to " + playerName);
        sendGameState();
    }

    /**
     * Main game loop that processes player moves
     */
    private void handleGameLoop() throws IOException, ClassNotFoundException {
        while (true) {
            CommonMessages move = (CommonMessages) in.readObject();
            processMove(move);
            checkWinner();
        }
    }

    /**
     * Processes different types of player moves
     * 
     * @param move The move to process
     */
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

    /**
     * Handles player join request
     * 
     * @param move Join message containing player name
     */
    private void handleJoin(CommonMessages move) {
        this.playerName = move.getMessage();
        LOGGER.info("🔗 Player joined: " + playerName);
    }

    /**
     * Handles army placement move
     * Validates and processes placing armies on territories
     * 
     * @param move Army placement message
     */
    private void handlePlaceArmy(CommonMessages move) {
        if (!gameState.getCurrentTurnPlayer().equals(playerName)) {
            sendMessage(new CommonMessages(CommonMessages.Type.COMMUNUCATON, "It's not your turn!"));
            return;
        }

        CommonPlayer p = gameState.getPlayers().get(playerName);
        int available = p.getAvailableArmies();
        int requested = move.getArmyCount();
        CommonTerritory t1 = gameState.getTerritories().get(move.getTo());

        if (available <= 0) {
            sendMessage(new CommonMessages(CommonMessages.Type.COMMUNUCATON, "You have no armies left to place."));
            return;
        }

        if (!t1.getOwner().equals(playerName)) {
            sendMessage(new CommonMessages(CommonMessages.Type.COMMUNUCATON, "You can only place armies on your own territory!"));
            return;
        }

        int toPlace = Math.min(available, requested);
        t1.addArmies(toPlace);
        p.setAvailableArmies(available - toPlace);

        sendMessage(new CommonMessages(CommonMessages.Type.COMMUNUCATON, "🪖 " + playerName + " placed " + toPlace
                + " armies on " + t1.getName() + ". Remaining: " + p.getAvailableArmies()));

        if (p.getAvailableArmies() == 0) {
            sendMessage(new CommonMessages(CommonMessages.Type.COMMUNUCATON, "All armies placed. Now you can Attack, Fortify or End Turn."));
        }
    }

    /**
     * Handles attack move
     * Processes territory attacks including dice rolling and army losses
     * 
     * @param move Attack message
     */
    private void handleAttack(CommonMessages move) {
        CommonTerritory from = gameState.getTerritories().get(move.getFrom());
        CommonTerritory to = gameState.getTerritories().get(move.getTo());

        if (!isValidAttack(from, to)) {
            sendMessage(new CommonMessages(CommonMessages.Type.COMMUNUCATON, "Invalid attack!"));
            return;
        }

        // Calculate number of dice for attacker and defender
        int maxAttackerDice = Math.min(3, from.getArmies() - 1);
        int attackerDice = Math.min(move.getArmyCount(), maxAttackerDice);
        int defenderDice = Math.min(2, to.getArmies());

        // Roll dice and calculate losses
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

        sendMessage(new CommonMessages(CommonMessages.Type.COMMUNUCATON, "Dice | Attacker: " + arrayToString(attackerRolls)
                + " | Defender: " + arrayToString(defenderRolls)));
        sendMessage(new CommonMessages(CommonMessages.Type.COMMUNUCATON, "Losses | Attacker: " + attackerLoss + ", Defender: " + defenderLoss));
    }

    /**
     * Validates if an attack is legal
     * 
     * @param from Attacking territory
     * @param to Defending territory
     * @return true if attack is valid
     */
    private boolean isValidAttack(CommonTerritory from, CommonTerritory to) {
        return from.getOwner().equals(playerName)
                && !to.getOwner().equals(playerName)
                && from.getArmies() > 1
                && from.getAdjacentTerritories().contains(to.getName());
    }

    /**
     * Handles territory capture after successful attack
     * 
     * @param from Attacking territory
     * @param to Captured territory
     * @param armyCount Number of armies to move
     * @param attackerLoss Number of armies lost in attack
     */
    private void handleTerritoryCapture(CommonTerritory from, CommonTerritory to, int armyCount, int attackerLoss) {
        String previousOwner = to.getOwner();
        to.setOwner(playerName);
        int moveIn = Math.min(from.getArmies() - 1, armyCount - attackerLoss);
        from.removeArmies(moveIn);
        to.setArmies(moveIn);

        gameState.getPlayers().get(playerName).addTerritory(to.getName());
        gameState.getPlayers().get(previousOwner).removeTerritory(to.getName());
    }

    /**
     * Handles fortify move
     * Processes moving armies between owned territories
     * 
     * @param move Fortify message
     */
    private void handleFortify(CommonMessages move) {
        CommonTerritory fromF = gameState.getTerritories().get(move.getFrom());
        CommonTerritory toF = gameState.getTerritories().get(move.getTo());
        int count = move.getArmyCount();

        if (!isValidFortify(fromF, toF, count)) {
            return;
        }

        fromF.removeArmies(count);
        toF.addArmies(count);

        sendMessage(new CommonMessages(CommonMessages.Type.COMMUNUCATON, "Moved " + count + " armies from " + fromF.getName() + " to " + toF.getName()));
        opponent.sendMessage(new CommonMessages(CommonMessages.Type.COMMUNUCATON, playerName + " fortified positions."));
    }

    /**
     * Validates if a fortify move is legal
     * 
     * @param from Source territory
     * @param to Destination territory
     * @param count Number of armies to move
     * @return true if fortify is valid
     */
    private boolean isValidFortify(CommonTerritory from, CommonTerritory to, int count) {
        if (!playerName.equals(from.getOwner()) || !playerName.equals(to.getOwner())) {
            sendMessage(new CommonMessages(CommonMessages.Type.COMMUNUCATON, "You must own both territories!"));
            return false;
        }
        if (!from.getAdjacentTerritories().contains(to.getName())) {
            sendMessage(new CommonMessages(CommonMessages.Type.COMMUNUCATON, "Territories are not adjacent!"));
            return false;
        }
        if (from.getArmies() <= count) {
            sendMessage(new CommonMessages(CommonMessages.Type.COMMUNUCATON, "Not enough armies to move (leave at least 1)!"));
            return false;
        }
        return true;
    }

    /**
     * Handles rematch request
     * Processes player's request for a new game
     */
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
            sendMessage(new CommonMessages(CommonMessages.Type.COMMUNUCATON, "Waiting for opponent to accept rematch..."));
        }
    }

    /**
     * Handles the end of a player's turn
     * Updates the current turn player, calculates reinforcements for the next player,
     * and notifies both players about the turn change
     */
    private void handleEndTurn() {
        if (!gameState.getCurrentTurnPlayer().equals(playerName)) {
            return;
        }

        gameState.setCurrentTurnPlayer(opponent.playerName);
        LOGGER.info("Turn passed to: " + opponent.playerName);

        CommonPlayer nextPlayer = gameState.getPlayers().get(opponent.playerName);
        int reinforcement = gameState.calculateReinforcements(opponent.playerName);
        nextPlayer.setAvailableArmies(nextPlayer.getAvailableArmies() + reinforcement);

        opponent.sendMessage(new CommonMessages(CommonMessages.Type.COMMUNUCATON, "You received " + reinforcement + " reinforcement armies!"));
        sendMessage(new CommonMessages(CommonMessages.Type.COMMUNUCATON, "Turn passed to " + opponent.playerName + "."));
    }

    /**
     * Handles player's request to play another game
     * If the current game has ended, adds the player back to the matchmaking queue
     */
    private void handlePlayAgain() {
        if (gameEnded) {
            LOGGER.info(playerName + " wants to play again");
            ServerManager.addPlayer(socket);
            cleanup();
        }
    }

    /**
     * Handles player's request to exit the game
     * Notifies the opponent of the win and cleans up the game session
     */
    private void handleExitGame() {
        LOGGER.info(playerName + " is exiting the game");
        if (opponent != null) {
            opponent.sendMessage(new CommonMessages(CommonMessages.Type.COMMUNUCATON, "Opponent has left the game. You win!"));
            opponent.sendMessage(new CommonMessages(CommonMessages.Type.WIN, "🏆 You won the game!"));
            opponent.gameEnded = true;
        }
        cleanup();
    }

    /**
     * Handles player disconnection
     * Notifies the opponent of the win if the game hasn't ended
     */
    private void handleDisconnection() {
        if (opponent != null && !gameEnded) {
            opponent.sendMessage(new CommonMessages(CommonMessages.Type.COMMUNUCATON, "Opponent disconnected. You win!"));
            opponent.sendMessage(new CommonMessages(CommonMessages.Type.WIN, "🏆 You won the game!"));
            opponent.gameEnded = true;
        }
        cleanup();
    }

    /**
     * Cleans up the game session
     * Marks the game as ended and closes the socket connection
     */
    private void cleanup() {
        gameEnded = true;
        closeSocket();
    }

    /**
     * Checks if there is a winner in the game
     * A player wins when their opponent has no territories left
     */
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

    /**
     * Sends the current game state to the player
     * Resets the output stream to ensure clean state transmission
     */
    private void sendGameState() {
        try {
            out.reset();
            out.writeObject(gameState);
            out.flush();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error sending game state to " + playerName + ": " + e.getMessage());
        }
    }

    /**
     * Sends a message to the player
     * 
     * @param move The message to send
     */
    public void sendMessage(CommonMessages move) {
        try {
            out.writeObject(move);
            out.flush();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error sending message to " + playerName + ": " + e.getMessage());
        }
    }

    /**
     * Rolls a specified number of dice and returns the results in descending order
     * 
     * @param count Number of dice to roll
     * @return Array of dice roll results in descending order
     */
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

    /**
     * Converts an array of integers to a space-separated string
     * 
     * @param arr Array to convert
     * @return Space-separated string representation of the array
     */
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

    /**
     * Safely closes the player's socket connection
     */
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
