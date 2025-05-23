/**
 * ClientMessageListener.java
 * This interface defines the callback methods for handling different types of messages
 * received from the game server. Classes implementing this interface can respond to
 * game state updates, chat messages, and game outcome notifications.
 */

package client;

import common.CommonState;

public interface ClientMessageListener {
    /**
     * Called when a chat message is received from the server
     * 
     * @param message The chat message content
     */
    void onChatMessage(String message);

    /**
     * Called when the game state is updated by the server
     * This includes changes to territories, armies, and player turns
     * 
     * @param gameState The updated game state
     */
    void onGameStateReceived(CommonState gameState);

    /**
     * Called when the player wins the game
     * 
     * @param message Victory message or announcement
     */
    void onVictory(String message);

    /**
     * Called when the player loses the game
     * 
     * @param message Defeat message or announcement
     */
    void onDefeat(String message);
}
