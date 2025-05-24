package client;

import javax.swing.SwingUtilities;

/**
 * ClientLauncher.java
 * This class provides a way to launch the Risk game client from anywhere in the application.
 */
public class ClientLauncher {
    
    /**
     * Launches the Risk game client
     */
    public static void launchClient() {
        SwingUtilities.invokeLater(ClientMain::new);
    }
    
    /**
     * Main method for direct launching
     */
    public static void main(String[] args) {
        launchClient();
    }
} 
