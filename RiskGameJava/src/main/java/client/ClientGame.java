package client;

import common.CommonState;
import common.CommonMessages;

import java.io.*;
import java.net.Socket;

public class ClientGame {

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    private ClientMessageListener listener;

    public ClientGame(String serverIP, int port, ClientMessageListener listener) {
        this.listener = listener;
        try {
            socket = new Socket(serverIP, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            new Thread(this::listen).start();

        } catch (IOException e) {
            System.err.println("Cannot connect to server: " + e.getMessage());
        }
    }

    public void sendMove(CommonMessages move) {
        try {
            out.writeObject(move);
            out.flush();
        } catch (IOException e) {
            System.err.println("Failed to send move: " + e.getMessage());
        }
    }

    private void listen() {
        try {
            while (true) {
                Object obj = in.readObject();

                if (obj instanceof CommonState gameState) {
                    listener.onGameStateReceived(gameState);
                } else if (obj instanceof CommonMessages move) {
                    switch (move.getType()) {
                        case CHAT ->
                            listener.onChatMessage(move.getMessage());
                        case WIN ->
                            listener.onVictory(move.getMessage());
                        case LOSE ->
                            listener.onDefeat(move.getMessage());
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("❌ Connection closed: " + e.getMessage());
        }
    }

}
