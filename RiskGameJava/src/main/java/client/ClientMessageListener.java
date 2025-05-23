package client;

import common.CommonState;

public interface ClientMessageListener {

    void onChatMessage(String message);

    void onGameStateReceived(CommonState gameState);

    void onVictory(String message);

    void onDefeat(String message);
}
