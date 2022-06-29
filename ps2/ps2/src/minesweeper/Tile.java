package minesweeper;

/**
 * A mutable data type, not thread safe.
 * Represents a tile in a Minesweeper board.
 */
public class Tile {
    public static enum State { UNTOUCHED, FLAGGED, DUG }
    // rep
    private State state;
    private boolean hasBomb;
    private int neighbors;
    
    // Rep invariant:
    //  none
    // Abstraction function:
    //  Represents a tile in a Minesweeper board with a particular state,
    //  untouched, flagged or dug. Tile contains a bomb if hasBomb is true.
    // Safety from rep exposure:
    //  All fields are private and immutable.
    
    public Tile(boolean isBomb) {
        this.state = State.UNTOUCHED;
        this.hasBomb = isBomb;
        this.neighbors = 0;
    }
    
    /**
     * @return state of this tile.
     */
    public State getState() {
        return state;
    }

    /**
     * Set state of this tile to newState.
     * @param newState - new state for the tile
     */
    public void setState(State newState) {
        state = newState;
    }
    
    /**
     * @return whether this tile is a bomb.
     */
    public boolean hasBomb() {
        return hasBomb;
    }
    
    /**
     * Remove bomb from the tile.
     */
    public void removeBomb() {
        hasBomb = false;
    }
    
    /**
     * Add 1 to amount of neighbor tiles with a bomb.
     */
    public void addNeighbor() {
        neighbors += 1;
    }
    
    /**
     * Subtract 1 from amount of neighbor tiles with a bomb.
     */
    public void removeNeighbor() {
        neighbors -= 1;
    }
    
    /**
     * @return amount of neighbor tiles with a bomb.
     */
    public int getNeighbors() {
        return neighbors;
    }
    
    /*  
     * !!! Unit test only 
     * Adds bomb to the tile.
     */     
    protected void add_bomb() {
        hasBomb = true;
    }
}
