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
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

/**
 * Tests for the Expression abstract data type.
 */
public class ExpressionTest {
    
    /**
     * Implementation classes for the Expression ADT.
     * JUnit runs this test suite once for each class name in the returned array.
     * @return array of Java class names, including their full package prefix
     */
    @Parameters(name="{0}")
    public static Object[] allImplementationClassNames() {
        return new Object[] { 
            "expressivo.Constant", 
            "expressivo.Variable",
            "expressivo.Multiplication",
            "expressivo.Plus"
        }; 
    }

    /**
     * Implementation class being tested on this run of the test suite.
     * JUnit sets this variable automatically as it iterates through the array returned
     * by allImplementationClassNames.
     */
    @Parameter
    public String implementationClassName;    

    /**
     * @return a fresh instance of a Expression, constructed from the implementation class specified
     * by implementationClassName.
     */
    public Expression makeExpression() {
        try {
            Class<?> cls = Class.forName(implementationClassName);
            return (Expression) cls.newInstance();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    // Testing strategy for each operation in Expression
    // 
    // parse()
    // input: various valid and invalid inputs
    //
    // toString()
    // this: expression contains number, variable, plus, multiplication; 
    // expression contains all 4, expression contains extra parentheses, extra whitespace 
    // expression contains no whitespace; expression contains operation between constants
    //
    // differentiate()
    // var: var in expression, var not in expression, var used in all types of differentiation rules
    //
    // simplify()
    // environment: contains variables in the expression, contains variables not in the expression,
    //  expression after substituting variables consists of only constants
    //
    // equals()
    // thatObject: expressions differ in constants, variables, operations;
    // expressions contain the same parts, but in different order;
    // expressions are grouped differently that changes meaning;
    // expressions are grouped differently but have the same meaning;
    // expressions are equal but for a constant that differs by the 5th decimal place;
    // expressions are structurally equal
    //
    // hashCode()
    // test for the same test cases as in the equals method
    //
    // number()
    // constant: 0, >0
    //
    // variable()
    // name: any sequence of letters
    //
    // multiplication()
    // left: constant, variable, multiplication or plus
    // right: constant, variable, multiplication or plus
    //
    // plus()
    // left: left: constant, variable, multiplication or plus
    // right: right: constant, variable, multiplication or plus
    //
    // Covers all parts
    
    @Rule
    public final ExpectedException exception = ExpectedException.none();
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    // Covers all parts in parse()
    @Test
    public void testParse() {
        String input1 = "3 + 2.4";
        String input2 = "3 * x + 2.4";
        String input3 = "3 * (x + 2.4)";
        String input4 = "((3 + 4) * x * x)";
        String input5 = "foo + bar+baz";
        String input6 = "(2*x   )+    (    y*x    )";
        String input7 = "4 + 3 * x + 2 * x * x + 1 * x * x * (((x)))";
        String input8 = "3";
        String input9 = "x";        
        
        Expression number1 = Expression.number(3);
        Expression number2 = Expression.number(2.4);
        Expression variable = Expression.variable("x");
        Expression expression1 = Expression.number(5.4);
        Expression expression2 = Expression.plus(Expression.multiplication(number1, variable), number2);
        Expression expression3 = Expression.multiplication(number1, Expression.plus(variable, number2));
        Expression expression4 = Expression.multiplication(Expression.multiplication(Expression.number(7), variable), variable);
        Expression expression5 = Expression.plus(Expression.plus(Expression.variable("foo"), Expression.variable("bar")), Expression.variable("baz"));
        Expression expression6 = Expression.plus(Expression.multiplication(Expression.number(2), variable), Expression.multiplication(Expression.variable("y"), variable));
        Expression expression7 = Expression.plus(Expression.plus(Expression.plus(Expression.number(4), Expression.multiplication(number1, variable)), Expression.multiplication(Expression.multiplication(Expression.number(2), variable), variable)), Expression.multiplication(Expression.multiplication(variable, variable), variable));
        Expression expression8 = number1;
        Expression expression9 = variable;
        
        assertEquals(expression1, Expression.parse(input1));
        assertEquals(expression2, Expression.parse(input2));
        assertEquals(expression3, Expression.parse(input3));
        assertEquals(expression4, Expression.parse(input4));
        assertEquals(expression5, Expression.parse(input5));
        assertEquals(expression6, Expression.parse(input6));
        assertEquals(expression7, Expression.parse(input7));
        assertEquals(expression8, Expression.parse(input8));
        assertEquals(expression9, Expression.parse(input9));
        
    }
    
    // Covers invalid input in parse()
    @Test
    public void testParseInvalid1() {
        String input = "3 *";
        
        exception.expect(IllegalArgumentException.class);
        Expression.parse(input);
    }
    
    // Covers invalid input in parse()
    @Test
    public void testParseInvalid2() {
        String input = "( 3";
        
        exception.expect(IllegalArgumentException.class);
        Expression.parse(input);
    }
    
    // Covers invalid input in parse()
    @Test
    public void testParseInvalid3() {
        String input = "3 x";   
        
        exception.expect(IllegalArgumentException.class);
        Expression.parse(input);
    }
    
    // Covers invalid input in parse()
    @Test
    public void testParseInvalid4() {
        String input = "()";
        
        exception.expect(IllegalArgumentException.class);
        Expression.parse(input);
    }
    
    // Covers invalid input in parse()
    @Test
    public void testParseInvalid5() {
        String input = "2x";
        
        exception.expect(IllegalArgumentException.class);
        Expression.parse(input);
    }
    
    // Covers all parts in toString(), number(), variable(), multiplication() and plus().
    @Test
    public void testToString() {
        Expression number1 = Expression.number(4.0);
        Expression number2 = Expression.number(0);
        Expression variable = Expression.variable("x");
        Expression multiplication1 = Expression.multiplication(number1, number1);
        Expression multiplication2 = Expression.multiplication(number1, variable);
        Expression multiplication3 = Expression.multiplication(variable, variable);
        Expression plus1 = Expression.plus(number1, number1);
        Expression plus2 = Expression.plus(number1, variable);
        Expression plus3 = Expression.plus(variable, variable);
        Expression multiplication4 = Expression.multiplication(number1, multiplication1);
        Expression multiplication5 = Expression.multiplication(number1, plus3);
        Expression multiplication6 = Expression.multiplication(variable, multiplication2);
        Expression multiplication7 = Expression.multiplication(variable, plus2);
        Expression multiplication8 = Expression.multiplication(multiplication2, multiplication3);
        Expression multiplication9 = Expression.multiplication(multiplication2, plus2);
        Expression multiplication10 = Expression.multiplication(plus2, plus3);
        Expression plus4 = Expression.plus(number1, multiplication1);
        Expression plus5 = Expression.plus(number1, plus3);
        Expression plus6 = Expression.plus(variable, multiplication2);
        Expression plus7 = Expression.plus(variable, plus2);
        Expression plus8 = Expression.plus(multiplication2, multiplication3);
        Expression plus9 = Expression.plus(multiplication2, plus2);
        Expression plus10 = Expression.plus(plus2, plus3);
        
        assertEquals("4", number1.toString());
        assertEquals("0", number2.toString());
        assertEquals("x", variable.toString());
        assertEquals("4*4", multiplication1.toString());
        assertEquals("4*x", multiplication2.toString());
        assertEquals("x*x", multiplication3.toString());
        assertEquals("(4 + 4)", plus1.toString());
        assertEquals("(4 + x)", plus2.toString());
        assertEquals("(x + x)", plus3.toString());
        assertEquals("4*4*4", multiplication4.toString());
        assertEquals("4*(x + x)", multiplication5.toString());
        assertEquals("x*4*x", multiplication6.toString());
        assertEquals("x*(4 + x)", multiplication7.toString());
        assertEquals("4*x*x*x", multiplication8.toString());
        assertEquals("4*x*(4 + x)", multiplication9.toString());
        assertEquals("(4 + x)*(x + x)", multiplication10.toString());
        assertEquals("(4 + 4*4)", plus4.toString());
        assertEquals("(4 + (x + x))", plus5.toString());
        assertEquals("(x + 4*x)", plus6.toString());
        assertEquals("(x + (4 + x))", plus7.toString());
        assertEquals("(4*x + x*x)", plus8.toString());
        assertEquals("(4*x + (4 + x))", plus9.toString());
        assertEquals("((4 + x) + (x + x))", plus10.toString());
    }
    
    // Covers all parts in differentiate()
    @Test 
    public void testDifferentiate() {
        Expression constant = Expression.number(4);
        Variable variable1 = Expression.variable("x");
        Variable variable2 = Expression.variable("y");
        Expression varNotIn = Expression.plus(Expression.multiplication(constant, variable2), Expression.variable("z"));
        Expression product1 = Expression.multiplication(constant, variable1);
        Expression product2 = Expression.multiplication(product1, Expression.plus(constant, variable1));
        Expression product3 = Expression.multiplication(product1, variable1);
        Expression sum = Expression.plus(product1, product1);
        
        assertTrue(Expression.number(0).equals(constant.differentiate(variable1)));
        assertTrue(Expression.number(1).equals(variable1.differentiate(variable1)));
        assertTrue(Expression.number(0).equals(variable2.differentiate(variable1)));
        assertTrue(Expression.number(0).equals(varNotIn.differentiate(variable1)));
        assertTrue(Expression.number(4).equals(product1.differentiate(variable1)));
        assertTrue(Expression.plus(product1, Expression.multiplication(Expression.number(4), Expression.plus(constant, variable1))).equals(product2.differentiate(variable1)));
        assertTrue(Expression.plus(product1, product1).equals(product3.differentiate(variable1)));
        assertTrue(Expression.number(8).equals(sum.differentiate(variable1)));
    }
    
    // Covers all parts in simplify()
    @Test 
    public void testSimplify() {
        Map<String, Double> environment = new HashMap<>();
        environment.put("x", 1.0);
        environment.put("y", 2.0);
        Expression constant = Expression.parse("4+4");
        Variable variable1 = Expression.variable("x");
        Variable variable2 = Expression.variable("y");
        Variable variable3 = Expression.variable("z");
        Expression varsNotIn = Expression.parse("z+z");
        Expression expression1 = Expression.parse("4*x*x + z");
        Expression expression2 = Expression.parse("(x+y)*(x*y)");
        
        assertTrue("8".equals(constant.simplify(environment).toString()));
        assertTrue("1".equals(variable1.simplify(environment).toString()));
        assertTrue("2".equals(variable2.simplify(environment).toString()));
        assertTrue("z".equals(variable3.simplify(environment).toString()));
        assertTrue("(z + z)".equals(varsNotIn.simplify(environment).toString()));
        assertTrue("(4 + z)".equals(expression1.simplify(environment).toString()));
        assertTrue("6".equals(expression2.simplify(environment).toString()));
    }
    
    // Covers all parts in equals() and hashCode()
    @Test
    public void testEquality() {
        Expression number1 = Expression.number(4.0);
        Expression number2 = Expression.number(0);
        Expression number3 = Expression.number(4.54432);
        Expression number4 = Expression.number(4.54444);
        Expression number5 = Expression.number(4.54449);
        Expression variable1 = Expression.variable("x");
        Expression variable2 = Expression.variable("y");
        Expression multiplication1 = Expression.multiplication(number1, number3);
        Expression multiplication2 = Expression.multiplication(number1, variable1);
        Expression multiplication3 = Expression.multiplication(number2, variable1);
        Expression multiplication4 = Expression.multiplication(number2, variable2);
        Expression multiplication5 = Expression.multiplication(variable1, number1);
        Expression multiplication6 = Expression.multiplication(multiplication1, multiplication2);
        Expression multiplication7 = Expression.multiplication(multiplication2, multiplication1);
        Expression multiplication8 = Expression.multiplication(variable1, variable1);
        Expression plus1 = Expression.plus(number1, number3);
        Expression plus2 = Expression.plus(number1, variable1);
        Expression plus3 = Expression.plus(number2, variable1);
        Expression plus4 = Expression.plus(number2, variable2);
        Expression multiplication9 = Expression.multiplication(plus2, variable1);
        Expression plus5 = Expression.plus(number1, multiplication8);
        Expression plus6 = Expression.plus(number1, number2);
        Expression plus7 = Expression.plus(number1, plus3);
        Expression plus8 = Expression.plus(plus6, variable1);
        
        assertFalse("numbers are unequal", number1.equals(number2));
        assertFalse("numbers are unequal", number1.equals(number3));
        assertFalse("numbers are unequal", number3.equals(number4));
        assertTrue("numbers are equal", number1.equals(number1));
        assertTrue("numbers are equal", number1.hashCode() == number1.hashCode());
        assertTrue("numbers are equal", number4.equals(number5)); // equal up to 4 decimal digits
        assertTrue("numbers are equal", number4.hashCode() == number5.hashCode());
        assertFalse("variables are unequal", variable1.equals(variable2));
        assertTrue("variables are equal", variable2.equals(variable2));
        assertTrue("variables are equal", variable2.hashCode() == variable2.hashCode());
        
        assertFalse("numbers differ", multiplication2.equals(multiplication3));
        assertFalse("numbers differ", plus2.equals(plus3));
        assertFalse("variables differ", multiplication4.equals(multiplication3));
        assertFalse("variables differ", plus4.equals(plus3));
        assertFalse("operations differ", plus1.equals(multiplication1));
        assertTrue("operations are the same", multiplication2.equals(multiplication2));
        assertTrue("operations are the same", multiplication2.hashCode() == multiplication2.hashCode());
        
        // test order
        assertFalse("Order differs", multiplication2.equals(multiplication5));
        assertFalse("order differs", multiplication6.equals(multiplication7));
        assertFalse("grouped differently, matters", multiplication9.equals(plus5));
        assertFalse("grouped differently, matters", plus7.equals(plus8));
    }
}