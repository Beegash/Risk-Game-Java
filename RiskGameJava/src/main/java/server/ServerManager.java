package server;

import common.*;
import java.net.Socket;
import java.util.*;

public class ServerManager {
    private static final Queue<Socket> waitingPlayers = new LinkedList<>();
    private static final int STARTING_ARMIES = 20;

    public static synchronized void addPlayer(Socket socket) {
        waitingPlayers.add(socket);
        System.out.println("Player added to matchmaking queue. Current size: " + waitingPlayers.size());

        if (waitingPlayers.size() >= 2) {
            Socket player1 = waitingPlayers.poll();
            Socket player2 = waitingPlayers.poll();
            System.out.println("Matching 2 players and starting a session...");
            new Thread(() -> ServerSession.startSession(player1, player2)).start();
        }
    }

    public static CommonState initializeGame(String player1Name, String player2Name) {
        CommonState gameState = new CommonState();

        Map<String, CommonTerritory> map = CommonMapCreation.generateMap();
        for (CommonTerritory t : map.values()) {
            gameState.addTerritory(t);
        }

        CommonPlayer player1 = new CommonPlayer(player1Name, "RED");
        CommonPlayer player2 = new CommonPlayer(player2Name, "BLUE");

        player1.setAvailableArmies(STARTING_ARMIES);
        player2.setAvailableArmies(STARTING_ARMIES);

        gameState.addPlayer(player1);
        gameState.addPlayer(player2);

        List<CommonTerritory> territories = new ArrayList<>(map.values());
        Collections.shuffle(territories);

        boolean assignToPlayer1 = true;
        for (CommonTerritory t : territories) {
            String owner = assignToPlayer1 ? player1Name : player2Name;
            t.setOwner(owner);
            t.setArmies(1);
            CommonPlayer player = gameState.getPlayers().get(owner);
            player.addTerritory(t.getName());
            assignToPlayer1 = !assignToPlayer1;
        }

        String firstTurn = Math.random() < 0.5 ? player1Name : player2Name;
        gameState.setCurrentTurnPlayer(firstTurn);
        System.out.println("🎲 First turn: " + firstTurn);

        return gameState;
    }
}
