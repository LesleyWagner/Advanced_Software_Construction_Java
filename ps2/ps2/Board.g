/* Copyright (c) 2015-2017 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */

// grammar Expression;

/*
 *
 * You should make sure you have one rule that describes the entire input.
 * This is the "start rule". Below, "root" is the start rule.
 *
 * For more information, see the parsers reading.
 */
file ::= board line+;
board ::= x space y newline;
line ::= (val space)* val newline;
val ::= "0" | "1";
x ::= int;
y ::= int;
space ::= " ";
newline ::= "\n" | "\r" "\n"?;
int ::= [0-9]+;