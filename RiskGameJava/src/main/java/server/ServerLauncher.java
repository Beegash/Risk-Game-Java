/**
 * ServerMain.java
 * This is the main entry point for the Risk game server.
 * It initializes the server socket, handles incoming connections,
 * and manages the server's logging system.
 */

package server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.*;

public class ServerLauncher {
    /** Default port number for the game server */
    public static final int PORT = 3131;

    /**
     * Main method that starts the game server
     * Sets up logging and continuously accepts new player connections
     * 
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        // Configure server logging
        try {
            Logger rootLogger = Logger.getLogger("");
            for (Handler h : rootLogger.getHandlers()) {
                h.setFormatter(new ServerLogFormatter());
            }
        } catch (Exception e) {
            System.err.println("Failed to configure log formatter: " + e);
        }
        System.out.println("Server is running on port " + PORT);

        // Start server and handle incoming connections
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                // Accept new player connection
                Socket incoming = serverSocket.accept();
                System.out.println("Player connected: " + incoming.getInetAddress());
                // Add new player to the game manager
                ServerManager.addPlayer(incoming);
            }
        } catch (Exception e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }
}
