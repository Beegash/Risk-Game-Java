package com.izzettinozmen.riskgame;

import java.util.ArrayList;
import java.util.List;

public class GameLobby {
    private static final int MAX_PLAYERS = 6;
    private ClientHandler host;
    private List<ClientHandler> players;

    public GameLobby(ClientHandler host) {
        this.host = host;
        this.players = new ArrayList<>();
        this.players.add(host);
        // Host'a anında güncel listeyi gönder
        host.sendMessage(new GameMessage(GameMessage.GameMessageType.PLAYER_JOINED, getPlayerNames()));
        broadcastPlayerList(); // Broadcast immediately after host creation
    }

    public boolean addPlayer(ClientHandler player) {
        if (players.size() >= MAX_PLAYERS) {
            return false;
        }
        // Tüm oyuncular için aynı isimle giriş engellensin (host dahil)
        for (ClientHandler ch : players) {
            if (ch.getPlayerName() != null && ch.getPlayerName().equals(player.getPlayerName())) {
                return false;
            }
        }
        players.add(player);
        player.sendMessage(new GameMessage(GameMessage.GameMessageType.PLAYER_JOINED, getPlayerNames()));
        broadcastPlayerList();
        return true;
    }

    public void removePlayer(ClientHandler player) {
        players.remove(player);
        if (player == host && !players.isEmpty()) {
            host = players.get(0);
        }
        broadcastPlayerList();
    }

    public ClientHandler getHost() {
        return host;
    }

    public List<ClientHandler> getPlayers() {
        return players;
    }

    public int getPlayerCount() {
        return players.size();
    }

    public List<String> getPlayerNames() {
        List<String> names = new ArrayList<>();
        for (ClientHandler ch : players) {
            String name = ch.getPlayerName();
            if (ch == host) {
                name += " (host)";
            }
            names.add(name);
        }
        return names;
    }

    private void broadcastPlayerList() {
        for (ClientHandler ch : players) {
            List<String> names = getPlayerNamesFor(ch);
            ch.sendMessage(new GameMessage(GameMessage.GameMessageType.PLAYER_JOINED, names));
        }
    }

    // Her oyuncu için, kendi isminin yanında (you) etiketi olacak şekilde liste döndür
    private List<String> getPlayerNamesFor(ClientHandler current) {
        List<String> names = new ArrayList<>();
        for (ClientHandler ch : players) {
            String name = ch.getPlayerName();
            if (ch == host) {
                name += " (host)";
            }
            if (ch == current) {
                name += " (you)";
            }
            names.add(name);
        }
        return names;
    }
} 
