package minesweeper.server;

/**
 * An immutable data type.
 * Represents a request to perform some operation on a Minesweeper board 
 * on a tile with coordinates x, y and requested by the client with id 'id'.
 * 
 */
public class BoardRequest implements RequestMessage {
    private final Message action;
    private final int x;
    private final int y;
    private final int id;

    public BoardRequest(Message action, int x, int y, int id) {
        this.action = action;
        this.x = x;
        this.y = y;
        this.id = id;
    }
    
    @Override
    public Message getMessage() {
        return action;
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    @Override
    public int getId() {
        return id;
    }
}
