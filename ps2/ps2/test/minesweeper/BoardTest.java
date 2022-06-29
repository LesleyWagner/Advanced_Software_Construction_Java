/* Copyright (c) 2007-2017 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package minesweeper;

import org.junit.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import static org.junit.Assert.assertFalse;

/**
 * Tests for every method in the ADT Board
 */
public class BoardTest {
    // Testing Strategy 
    // dig()
    // x, y - tile contains a bomb, doesn't contain a bomb
    //
    // flag()
    // x, y - valid arguments
    //
    // deflag()
    // x, y - valid arguments
    //
    // toString()
    // this - board in various states, called after dig() with bomb (changes neighbors)
    
    Board board;
    
    @Before 
    public void initialise() {
        this.board = new Board(new File("board_file2.txt"));
    }
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    // Covers dig - tile contains a bomb
    @Test
    public void test_dig_bomb() {
        assertTrue(this.board.dig(0, 0));
        assertEquals("         \n" + 
                     "         \n" +
                     "         \n" +
                     "      1 1\n" + 
                     "      1 -", this.board.toString());
    }
    
    // Covers dig - tile doesn't contain a bomb
    @Test
    public void test_dig_nobomb() {
        assertFalse(this.board.dig(0, 4));
        assertEquals("- 1      \n" + 
                     "1 1      \n" +
                     "         \n" +
                     "      1 1\n" + 
                     "      1 -", this.board.toString());
    }
    
    // Covers flag
    @Test
    public void test_flag() {
        this.board.flag(0, 0);
        assertEquals("F - - - -\n" + 
                     "- - - - -\n" +
                     "- - - - -\n" +
                     "- - - - -\n" + 
                     "- - - - -", this.board.toString());
    }
    
    // Covers deflag
    @Test
    public void test_deflag() {
        this.board.flag(0, 1);
        assertEquals("- - - - -\n" + 
                     "F - - - -\n" +
                     "- - - - -\n" +
                     "- - - - -\n" + 
                     "- - - - -", this.board.toString());
        this.board.deflag(0, 1);
        assertEquals("- - - - -\n" + 
                     "- - - - -\n" +
                     "- - - - -\n" +
                     "- - - - -\n" + 
                     "- - - - -", this.board.toString());
    }
    
    // Covers toString
    @Test
    public void test_toString() {
        assertEquals("- - - - -\n" + 
                     "- - - - -\n" +
                     "- - - - -\n" +
                     "- - - - -\n" + 
                     "- - - - -", this.board.toString());
    }
}
