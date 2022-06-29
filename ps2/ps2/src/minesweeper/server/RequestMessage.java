package minesweeper.server;

/**
 * An immutable data type.
 * Represents a request to perform some operation.
 */
public interface RequestMessage {
    public static enum Message { DIG, FLAG, DEFLAG, LOOK, BYE, NEWPLAYER };
    
    /**
     * @return type of message
     */
    public Message getMessage();
    
    /**
     * @return id of client from which the request comes.
     */
    public int getId();
}
