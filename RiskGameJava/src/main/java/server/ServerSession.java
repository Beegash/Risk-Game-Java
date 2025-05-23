/**
 * ServerSession.java
 * This class manages individual game sessions between two players.
 * It handles the setup of player connections, game initialization,
 * and session lifecycle management.
 */

package server;

import common.CommonState;

import java.io.IOException;
import java.net.Socket;

public class ServerSession {
    /**
     * Starts a new game session between two players
     * Sets up player handlers, waits for player names,
     * and initializes the game state
     * 
     * @param s1 Socket connection for the first player
     * @param s2 Socket connection for the second player
     */
    public static void startSession(Socket s1, Socket s2) {
        ServerHandler player1 = null;
        ServerHandler player2 = null;
        Thread player1Thread = null;
        Thread player2Thread = null;

        try {
            System.out.println("New match found! Starting session...");

            // Create handlers for both players
            player1 = new ServerHandler(s1, null);
            player2 = new ServerHandler(s2, null);

            // Set up opponent references
            player1.setOpponent(player2);
            player2.setOpponent(player1);

            // Start player handler threads
            player1Thread = new Thread(player1);
            player2Thread = new Thread(player2);
            player1Thread.start();
            player2Thread.start();

            // Set timeout for waiting player names
            long startTime = System.currentTimeMillis();
            long timeoutMillis = 30000;

            // Wait for both players to send their names
            while ((player1.playerName == null || player2.playerName == null)) {
                // Check if player 1 disconnected
                if (!player1Thread.isAlive() && player1.playerName == null) {
                    System.out.println("Player 1 disconnected before sending name. Session terminated.");
                    closeSocket(s2);
                    return;
                }
                // Check if player 2 disconnected
                if (!player2Thread.isAlive() && player2.playerName == null) {
                    System.out.println("Player 2 disconnected before sending name. Session terminated.");
                    closeSocket(s1);
                    return;
                }
                // Check for timeout
                if (System.currentTimeMillis() - startTime > timeoutMillis) {
                    System.out.println("Timeout waiting for player names. Session terminated.");
                    closeSocket(s1);
                    closeSocket(s2);
                    return;
                }
                System.out.println("Waiting for player names...");
                Thread.sleep(200);
            }

            // Initialize game state and assign to players
            CommonState gameState = ServerManager.initializeGame(player1.playerName, player2.playerName);
            player1.setGameState(gameState);
            player2.setGameState(gameState);

            System.out.println("Game started between " + player1.playerName + " and " + player2.playerName);
            System.out.println("First turn: " + gameState.getCurrentTurnPlayer());

        } catch (Exception e) {
            System.err.println("Error starting session: " + e.getMessage());

            // Clean up connections on error
            closeSocket(s1);
            closeSocket(s2);
        }
    }

    /**
     * Safely closes a socket connection
     * 
     * @param socket The socket to close
     */
    private static void closeSocket(Socket socket) {
        if (socket != null && !socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException ex) {
                System.err.println("Error closing socket: " + ex.getMessage());
            }
        }
    }
}
