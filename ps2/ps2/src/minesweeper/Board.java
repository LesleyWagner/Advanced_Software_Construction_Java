/* Copyright (c) 2007-2017 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package minesweeper;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import lib6005.parser.*;

/**
 * A mutable data type, not thread safe.
 * Represents a board in a game of minesweeper.
 */
public class Board {   
    // rep
    private final Tile[][] tiles;
    public int rows;
    public int columns;
    
    private enum BoardGrammar { FILE, BOARD, LINE, VAL, X, Y, SPACE, NEWLINE, INT }
    
    // Rep invariant:
    //  Elements in tiles can't be null.
    // Abstraction function:
    //  Represents a Minesweeper board with tiles as its tiles, 
    //  where the inner arrays represent rows on the board and
    //  with size tiles.length by tiles[0].length.
    // Safety from rep exposure:
    //  All fields are private and final.
    
    public Board(int width, int height) {
        this.tiles = new Tile[height][width];
        this.rows = height;
        this.columns = width;
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[0].length; j++) {
                tiles[i][j] = new Tile(Math.random() > 0.25);
            }
        }
        // add amount of neighbor bombs to tiles
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[0].length; j++) {
                if (tiles[i][j].hasBomb()) {
                    if (i > 0) {
                        if (j > 0) {
                            tiles[i-1][j-1].addNeighbor();
                        }
                        if (j < tiles[0].length-1) {
                            tiles[i-1][j+1].addNeighbor();
                        }
                        tiles[i-1][j].addNeighbor();
                    }
                    if (i < tiles.length-1) {
                        if (j > 0) {
                            tiles[i+1][j-1].addNeighbor();
                        }
                        if (j < tiles[0].length-1) {
                            tiles[i+1][j+1].addNeighbor();
                        }
                        tiles[i+1][j].addNeighbor();
                    }
                    if (j > 0) {
                        tiles[i][j-1].addNeighbor();
                    }
                    if (j < tiles[0].length-1) {
                        tiles[i][j+1].addNeighbor();
                    }
                }
            }
        }
        checkRep();
    }
    
    public Board(File file) {
        Tile[][] tilesCopy = null;
        try (BufferedReader reader = Files.newBufferedReader(file.toPath())) {
            // construct board
            Parser<BoardGrammar> parser = GrammarCompiler.compile(new File("Board.g"), BoardGrammar.FILE);
            ParseTree<BoardGrammar> tree = parser.parse(reader); 
            List<ParseTree<BoardGrammar>> size = tree.children().get(0).children();
            columns = Integer.parseInt(size.get(0).children().get(0).getContents());
            rows = Integer.parseInt(size.get(2).children().get(0).getContents());
            tilesCopy = new Tile[rows][columns];
            List<ParseTree<BoardGrammar>> lines = tree.children();
            for (int i = 0; i < tilesCopy.length; i++) {
                List<ParseTree<BoardGrammar>> line = lines.get(i+1).childrenByName(BoardGrammar.VAL);
                for (int j = 0; j < tilesCopy[0].length; j++) {
                    tilesCopy[i][j] = new Tile(Integer.parseInt(line.get(j).getContents()) == 1);
                }
            }
            // add amount of neighbor bombs to tilesCopy
            for (int i = 0; i < tilesCopy.length; i++) {
                for (int j = 0; j < tilesCopy[0].length; j++) {
                    if (tilesCopy[i][j].hasBomb()) {
                        if (i > 0) {
                            if (j > 0) {
                                tilesCopy[i-1][j-1].addNeighbor();
                            }
                            if (j < tilesCopy[0].length-1) {
                                tilesCopy[i-1][j+1].addNeighbor();
                            }
                            tilesCopy[i-1][j].addNeighbor();
                        }
                        if (i < tilesCopy.length-1) {
                            if (j > 0) {
                                tilesCopy[i+1][j-1].addNeighbor();
                            }
                            if (j < tilesCopy[0].length-1) {
                                tilesCopy[i+1][j+1].addNeighbor();
                            }
                            tilesCopy[i+1][j].addNeighbor();
                        }
                        if (j > 0) {
                            tilesCopy[i][j-1].addNeighbor();
                        }
                        if (j < tilesCopy[0].length-1) {
                            tilesCopy[i][j+1].addNeighbor();
                        }
                    }
                }
            }
        }
        catch (IOException ie) {
            ie.printStackTrace();
        }
        catch (UnableToParseException ie) {
            ie.printStackTrace();
        }
        finally {
            tiles = tilesCopy;
        }
    }
    
    // Checks rep invariant of Board
    private void checkRep() {
        for (Tile[] row : tiles) {
            for (Tile tile : row) {
                assert tile != null;
            }
        }
    }
    
    /*    
     * Digs untouched tile with x, y coordinates. Changes the state of the tile to 'dug'.
     * If the tile contains a bomb, remove the bomb from the tile and
     * update neighbors for the tiles.
     * If the tile has no neighbor tiles with bombs,
     * then for each untouched neighbor tile, change said tile to dug and 
     * repeat this step recursively for said neighbor tiles.
     * @param x, y - coordinates of a tile (row, column)
     * @return true if the tile contains a bomb, false if the tile doesn't contain a bomb or
     * if the there is no untouched tile at coordinates x, y.
     */      
    public boolean dig(int x, int y) {
        if (x < 0 || y < 0 || x >= tiles[0].length || y >= tiles.length || !(tiles[y][x].getState().equals(Tile.State.UNTOUCHED))) {
            return false;
        }
        boolean hasBomb = false;
        Tile tile = tiles[y][x];
        tile.setState(Tile.State.DUG);
        if (tile.hasBomb()) {
            hasBomb = true;
            tile.removeBomb();
            updateNeighbors(x, y);
        } 
        if (tile.getNeighbors() == 0) {
            digNeighbors(x, y);
        }
        return hasBomb;
    }
    
    /*    
     * Update neighbors after bomb is removed from the tile (x, y), helper function for dig().
     * @param x, y - coordinates of a tile (row, column)
     * x and y must be valid coordinates in tiles.
     */      
    private void updateNeighbors(int x, int y) {
        if (y > 0) {
            if (x > 0) {
                tiles[y-1][x-1].removeNeighbor();
            }
            if (x < tiles[0].length-1) {
                tiles[y-1][x+1].removeNeighbor();
            }
            tiles[y-1][x].removeNeighbor();
        }
        if (y < tiles.length-1) {
            if (x > 0) {
                tiles[y+1][x-1].removeNeighbor();
            }
            if (x < tiles[0].length-1) {
                tiles[y+1][x+1].removeNeighbor();
            }
            tiles[y+1][x].removeNeighbor();
        }
        if (x > 0) {
            tiles[y][x-1].removeNeighbor();
        }
        if (x < tiles[0].length-1) {
            tiles[y][x+1].removeNeighbor();
        }
    }
    
    /*    
     * Dig neighbor tiles for tile (x, y), helper function for dig().
     * @param x, y - coordinates of a tile (row, column)
     * x and y must be valid coordinates in tiles.
     */      
    private void digNeighbors(int x, int y) {
        Tile[] neighbors = new Tile[9];
        if (y > 0) {
            if (x > 0) {
                neighbors[0] = tiles[y-1][x-1];
            }
            if (x < tiles[0].length-1) {
                neighbors[2] = tiles[y-1][x+1];
            }
            neighbors[1] = tiles[y-1][x];
        }
        if (y < tiles.length-1) {
            if (x > 0) {
                neighbors[6] = tiles[y+1][x-1];
            }
            if (x < tiles[0].length-1) {
                neighbors[8] = tiles[y+1][x+1];
            }
            neighbors[7] = tiles[y+1][x];
        }
        if (x > 0) {
            neighbors[3] =  tiles[y][x-1];
        }
        if (x < tiles[0].length-1) {
            neighbors[5] = tiles[y][x+1];
        }
        for (int i = 0; i < neighbors.length; i++) {
            Tile tile = neighbors[i];
            if (tile != null) {
                if (tile.getState() == Tile.State.UNTOUCHED) {
                    tile.setState(Tile.State.DUG);
                    if (tile.getNeighbors() == 0) {
                        digNeighbors(x-1+(i%3), y-1+(i/3));
                    }
                }
            }
        }
    }
    
    /*    
     * Flags untouched tile with x, y coordinates.
     * @param x, y - coordinates of a tile (row, column)
     * Does nothing if there is no untouched tile at coordinates x, y.
     */      
    public void flag(int x, int y) {
        if (!(x < 0 || y < 0 || y >= tiles.length || x >= tiles[0].length) && tiles[y][x].getState().equals(Tile.State.UNTOUCHED)) {
            tiles[y][x].setState(Tile.State.FLAGGED);
        }   
    }
    
    /*    
     * Deflags flagged tile with x, y coordinates.
     * @param x, y - coordinates of a tile (row, column)
     * Does nothing if there is no flagged tile at coordinates x, y.
     */      
    public void deflag(int x, int y) {
        if (!(x < 0 || y < 0 || y >= tiles.length || x >= tiles[0].length) && tiles[y][x].getState().equals(Tile.State.FLAGGED)) {
            tiles[y][x].setState(Tile.State.UNTOUCHED);
        }
    }

    @Override
    public String toString() {
        String stringView = "";
        
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[0].length; j++) {
                if (tiles[i][j].getState() == Tile.State.UNTOUCHED) {
                    stringView += "-";
                }
                else if (tiles[i][j].getState() == Tile.State.DUG) {
                    int number_of_bombs = tiles[i][j].getNeighbors();
                    if (number_of_bombs == 0) {
                        stringView += " ";
                    }
                    else {
                        stringView += String.valueOf(number_of_bombs);
                    }
                }
                else {
                    stringView += "F";
                }
                if (j < tiles[0].length-1) {
                    stringView += " ";
                }
            }
            if (i < tiles.length-1) {
                stringView += "\n";
            } 
        }
        checkRep();
        return stringView;
    }
    
    /*  
     * !!! Unit test only 
     * Adds bomb to the tile with x, y coordinates.
     * @param x, y - coordinates of a tile (row, column)
     * x and y must be valid coordinates in tiles and
     * the tile must be untouched.
     */     
    protected void add_bomb(int x, int y) {
        tiles[y][x].add_bomb();
    }
    
    /*  
     * !!! Unit test only 
     * Add amount of neighbor bombs to tiles, called after adding all bombs.
     */     
    protected void updateNeighbors() {
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[0].length; j++) {
                if (tiles[i][j].hasBomb()) {
                    if (i > 0) {
                        if (j > 0) {
                            tiles[i-1][j-1].addNeighbor();
                        }
                        if (j < tiles[0].length-1) {
                            tiles[i-1][j+1].addNeighbor();
                        }
                        tiles[i-1][j].addNeighbor();
                    }
                    if (i < tiles.length-1) {
                        if (j > 0) {
                            tiles[i+1][j-1].addNeighbor();
                        }
                        if (j < tiles[0].length-1) {
                            tiles[i+1][j+1].addNeighbor();
                        }
                        tiles[i+1][j].addNeighbor();
                    }
                    if (j > 0) {
                        tiles[i][j-1].addNeighbor();
                    }
                    if (j < tiles[0].length-1) {
                        tiles[i][j+1].addNeighbor();
                    }
                }
            }
        }
    }
}
