/* Copyright (c) 2015-2017 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package expressivo;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Tests for the static methods of Commands.
 */
public class CommandsTest {

    // Testing strategy for Commands
    // 
    // differentiate()
    // variable: valid variable, invalid variable
    // expression: variable in expression, variable not in expression, variable used in all types of differentiation rules
    //
    // simplify()
    // environment: contains variables in the expression, contains variables not in the expression,
    //  expression after substituting variables consists of only constants
    
    
    @Rule
    public final ExpectedException exception = ExpectedException.none();
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    // Covers invalid variable in differentiate()
    @Test
    public void testDifferentiateInvalidVariable() {
        exception.expect(AssertionError.class); 
        Commands.differentiate("x*x", "x1");           
    }
    
    // Covers invalid expression in differentiate()
    @Test
    public void testDifferentiateInvalidExpression() {
        exception.expect(IllegalArgumentException.class);
        Commands.differentiate("x*", "x");
    }
    
    // Covers valid expressions in differentiate()
    @Test
    public void testDifferentiateValidExpressions() {
        String constant = "4";
        String variable1 = "x";
        String variable2 = "y";
        String varNotIn = "4*y+z";
        String product1 = "4*x";
        String product2 = "4*x*(4+x)";
        String product3 = "4*x*x";
        String sum = "4*x+4*x";
        
        assertTrue("0".equals(Commands.differentiate(constant, variable1)));
        assertTrue("1".equals(Commands.differentiate(variable1, variable1)));
        assertTrue("0".equals(Commands.differentiate(variable2, variable1)));
        assertTrue("0".equals(Commands.differentiate(varNotIn, variable1)));
        assertTrue("4".equals(Commands.differentiate(product1, variable1)));
        assertTrue("(4*x + 4*(4 + x))".equals(Commands.differentiate(product2, variable1)));
        assertTrue("(4*x + 4*x)".equals(Commands.differentiate(product3, variable1)));
        assertTrue("8".equals(Commands.differentiate(sum, variable1)));
    }
    
    // Covers all parts in simplify()
    @Test 
    public void testSimplify() {
        Map<String, Double> environment = new HashMap<>();
        environment.put("x", 1.0);
        environment.put("y", 2.0);
        String constant = "4+4";
        String variable1 = "x";
        String variable2 = "y";
        String variable3 = "z";
        String varsNotIn = "z+z";
        String expression1 = "4*x*x + z";
        String expression2 = "(x+y)*(x*y)";
        
        assertTrue("8".equals(Commands.simplify(constant, environment)));
        assertTrue("1".equals(Commands.simplify(variable1, environment)));
        assertTrue("2".equals(Commands.simplify(variable2, environment)));
        assertTrue("z".equals(Commands.simplify(variable3, environment)));
        assertTrue("(z + z)".equals(Commands.simplify(varsNotIn, environment)));
        assertTrue("(4 + z)".equals(Commands.simplify(expression1, environment)));
        assertTrue("6".equals(Commands.simplify(expression2, environment)));
    }
}
