package server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.*;

public class ServerMain {

    public static final int PORT = 3131;

    public static void main(String[] args) {

        try {
            Logger rootLogger = Logger.getLogger("");
            for (Handler h : rootLogger.getHandlers()) {
                h.setFormatter(new ServerLogFormatter());
            }
        } catch (Exception e) {
            System.err.println("Log formatter ayarlanamadı: " + e);
        }
        System.out.println("Server is running on port " + PORT);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket incoming = serverSocket.accept();
                System.out.println("Player connected: " + incoming.getInetAddress());
                ServerManager.addPlayer(incoming);
            }
        } catch (Exception e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }
}
