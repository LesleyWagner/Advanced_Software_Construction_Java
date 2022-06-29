package minesweeper.server;

import java.util.concurrent.BlockingQueue;

/**
 * An immutable data type.
 * Represents a request to add a new player to the game.
 */
public class PlayerRequest implements RequestMessage {
    private final Message action;
    private final BlockingQueue<ReplyMessage> queue;
    
    public PlayerRequest(Message action, BlockingQueue<ReplyMessage> queue) {
        this.action = action;
        this.queue = queue;
    }
    
    @Override
    public Message getMessage() {
        return action;
    }
    
    public BlockingQueue<ReplyMessage> getQueue() {
        return queue;
    }

    @Override
    public int getId() {
        throw new UnsupportedOperationException();
    }
}
