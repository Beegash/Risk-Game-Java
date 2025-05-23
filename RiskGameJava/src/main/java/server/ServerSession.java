package server;

import common.CommonState;

import java.io.IOException;
import java.net.Socket;

public class ServerSession {

    public static void startSession(Socket s1, Socket s2) {
        ServerHandler player1 = null;
        ServerHandler player2 = null;
        Thread player1Thread = null;
        Thread player2Thread = null;

        try {
            System.out.println("🧩 New match found! Starting session...");

            player1 = new ServerHandler(s1, null);
            player2 = new ServerHandler(s2, null);

            player1.setOpponent(player2);
            player2.setOpponent(player1);

            player1Thread = new Thread(player1);
            player2Thread = new Thread(player2);
            player1Thread.start();
            player2Thread.start();

            long startTime = System.currentTimeMillis();
            long timeoutMillis = 30000;

            while ((player1.playerName == null || player2.playerName == null)) {
                if (!player1Thread.isAlive() && player1.playerName == null) {
                    System.out.println("Player 1 disconnected before sending name. Session terminated.");
                    closeSocket(s2);
                    return;
                }
                if (!player2Thread.isAlive() && player2.playerName == null) {
                    System.out.println("Player 2 disconnected before sending name. Session terminated.");
                    closeSocket(s1);
                    return;
                }
                if (System.currentTimeMillis() - startTime > timeoutMillis) {
                    System.out.println("Timeout waiting for player names. Session terminated.");
                    closeSocket(s1);
                    closeSocket(s2);
                    return;
                }
                System.out.println("Waiting for player names...");
                Thread.sleep(200);
            }

            CommonState gameState = ServerManager.initializeGame(player1.playerName, player2.playerName);
            player1.setGameState(gameState);
            player2.setGameState(gameState);

            System.out.println("Game started between " + player1.playerName + " and " + player2.playerName);
            System.out.println("First turn: " + gameState.getCurrentTurnPlayer());

        } catch (Exception e) {
            System.err.println("Error starting session: " + e.getMessage());

            closeSocket(s1);
            closeSocket(s2);
        }
    }

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
