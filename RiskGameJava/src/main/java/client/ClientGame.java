/**
 * ClientGame.java
 * This class handles the client-side networking and communication with the game server.
 * It manages the socket connection, message sending/receiving, and game state updates.
 */

package client;

import common.CommonState;
import common.CommonMessages;

import java.io.*;
import java.net.Socket;

public class ClientGame {
    // Network communication components
    private Socket socket;              // Socket connection to the server
    private ObjectOutputStream out;     // Stream for sending objects to server
    private ObjectInputStream in;       // Stream for receiving objects from server

    private ClientMessageListener listener;  // Callback interface for game events

    /**
     * Constructor for ClientGame
     * Establishes connection to the game server and initializes communication streams
     * 
     * @param serverIP The IP address of the game server
     * @param port The port number to connect to
     * @param listener The callback interface for game events
     */
    public ClientGame(String serverIP, int port, ClientMessageListener listener) {
        this.listener = listener;
        try {
            // Establish connection to server
            socket = new Socket(serverIP, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            // Start listening for server messages in a separate thread
            new Thread(this::listen).start();

        } catch (IOException e) {
            System.err.println("Cannot connect to server: " + e.getMessage());
        }
    }

    /**
     * Sends a game move or message to the server
     * 
     * @param move The move or message to send to the server
     */
    public void sendMove(CommonMessages move) {
        try {
            out.writeObject(move);
            out.flush();
        } catch (IOException e) {
            System.err.println("Failed to send move: " + e.getMessage());
        }
    }

    /**
     * Continuously listens for messages from the server
     * Handles different types of messages:
     * - Game state updates
     * - Chat messages
     * - Victory/Defeat notifications
     */
    private void listen() {
        try {
            while (true) {
                Object obj = in.readObject();

                if (obj instanceof CommonState gameState) {
                    // Handle game state updates
                    listener.onGameStateReceived(gameState);
                } else if (obj instanceof CommonMessages move) {
                    // Handle different types of messages
                    switch (move.getType()) {
                        case COMMUNUCATON ->
                            listener.onChatMessage(move.getMessage());
                        case WIN ->
                            listener.onVictory(move.getMessage());
                        case LOSE ->
                            listener.onDefeat(move.getMessage());
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Connection closed: " + e.getMessage());
        }
    }
}
