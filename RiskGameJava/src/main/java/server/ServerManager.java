/**
 * ServerManager.java
 * This class manages the game server's matchmaking and game initialization.
 * It handles player queuing, session creation, and initial game state setup.
 */

package server;

import common.*;
import java.net.Socket;
import java.util.*;

public class ServerManager {
    /** Queue for players waiting to be matched */
    private static final Queue<Socket> waitingPlayers = new LinkedList<>();
    /** Number of armies each player starts with */
    private static final int STARTING_ARMIES = 20;

    /**
     * Adds a new player to the matchmaking queue
     * If enough players are waiting, starts a new game session
     * 
     * @param socket Socket connection of the new player
     */
    public static synchronized void addPlayer(Socket socket) {
        waitingPlayers.add(socket);
        System.out.println("Player added to matchmaking queue. Current size: " + waitingPlayers.size());

        // Start a new game session when 2 players are available
        if (waitingPlayers.size() >= 2) {
            Socket player1 = waitingPlayers.poll();
            Socket player2 = waitingPlayers.poll();
            System.out.println("Matching 2 players and starting a session...");
            new Thread(() -> ServerSession.startSession(player1, player2)).start();
        }
    }

    /**
     * Initializes a new game state for two players
     * Creates the game map, assigns territories, and sets up initial armies
     * 
     * @param player1Name Name of the first player
     * @param player2Name Name of the second player
     * @return Initialized game state
     */
    public static CommonState initializeGame(String player1Name, String player2Name) {
        CommonState gameState = new CommonState();

        // Generate and add territories to the game state
        Map<String, CommonTerritory> map = CommonMapCreation.generateMap();
        for (CommonTerritory t : map.values()) {
            gameState.addTerritory(t);
        }

        // Create player objects with their colors
        CommonPlayer player1 = new CommonPlayer(player1Name, "RED");
        CommonPlayer player2 = new CommonPlayer(player2Name, "BLUE");

        // Set initial armies for both players
        player1.setAvailableArmies(STARTING_ARMIES);
        player2.setAvailableArmies(STARTING_ARMIES);

        gameState.addPlayer(player1);
        gameState.addPlayer(player2);

        // Randomly assign territories to players
        List<CommonTerritory> territories = new ArrayList<>(map.values());
        Collections.shuffle(territories);

        // Alternate territory assignment between players
        boolean assignToPlayer1 = true;
        for (CommonTerritory t : territories) {
            String owner = assignToPlayer1 ? player1Name : player2Name;
            t.setOwner(owner);
            t.setArmies(1);
            CommonPlayer player = gameState.getPlayers().get(owner);
            player.addTerritory(t.getName());
            assignToPlayer1 = !assignToPlayer1;
        }

        // Randomly determine first player
        String firstTurn = Math.random() < 0.5 ? player1Name : player2Name;
        gameState.setCurrentTurnPlayer(firstTurn);
        System.out.println("🎲 First turn: " + firstTurn);

        return gameState;
    }
}
