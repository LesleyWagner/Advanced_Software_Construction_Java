package expressivo;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import lib6005.parser.*;

/**
 * An immutable data type representing a polynomial expression of:
 *   + 
 *   and *
 *   nonnegative integers and floating-point numbers
 *   variables (case-sensitive nonempty strings of letters)
 * 
 * <p>PS1 instructions: this is a required ADT interface.
 * You MUST NOT change its name or package or the names or type signatures of existing methods.
 * You may, however, add additional methods, or strengthen the specs of existing methods.
 * Declare concrete variants of Expression in their own Java source files.
 */
public interface Expression {
    
    // Datatype definition
    //   Expression = Constant(constant:double) + Variable(name:String) +
    //     Multiplication(left:Expression, right:Expression) + Plus(left:Expression, right:Expression)
    
    // Grammar used by the parser to parse an expression.
    enum ExpressionGrammar {ROOT, EXPRESSION, PRODUCT, SUM, TERM, VARIABLE, CONSTANT, WHITESPACE};
    
    /**
     * Parse an expression.
     * @param input expression to parse, as defined in the PS1 handout.
     * @return expression AST for the input
     * @throws IllegalArgumentException if the expression is invalid
     */
    public static Expression parse(String input) {
        try {
            Parser<ExpressionGrammar> parser = GrammarCompiler.compile(new File("Expression.g"), ExpressionGrammar.ROOT);
            ParseTree<ExpressionGrammar> tree = parser.parse(input);
            return buildExpression(tree);
        }
        catch (IOException e){
            e.printStackTrace();
            throw new IllegalArgumentException("Parse error: invalid expression");
        }
        catch (UnableToParseException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Parse error: invalid expression");
        }     
    }
    
    public static Expression buildExpression(ParseTree<ExpressionGrammar> tree) {
        // The parsetree corresponds to one of the symbols in expressiongrammar.
        switch(tree.getName()) {
        
        // A constant, positive number (terminal)
        case CONSTANT:
            return new Constant(Double.parseDouble(tree.getContents()));
        // A variable (terminal)
        case VARIABLE:
            return new Variable(tree.getContents());
        // A term contains either a variable or a constant as child.
        case TERM:
            if (tree.childrenByName(ExpressionGrammar.CONSTANT).isEmpty()) {
                if (tree.childrenByName(ExpressionGrammar.EXPRESSION).isEmpty()) {
                    return buildExpression(tree.childrenByName(ExpressionGrammar.VARIABLE).get(0));
                }
                else {
                    return buildExpression(tree.childrenByName(ExpressionGrammar.EXPRESSION).get(0));
                }
            }
            else {
                return buildExpression(tree.childrenByName(ExpressionGrammar.CONSTANT).get(0));
            }
        // A term or one or more products between terms
        case PRODUCT:        
            boolean first1 = true;
            Expression result1 = null;
            for (ParseTree<ExpressionGrammar> child : tree.childrenByName(ExpressionGrammar.TERM)) {
                if (first1) {
                    result1 = buildExpression(child);
                    first1 = false;
                }
                else {
                    Expression term = buildExpression(child);
                    // if both terms in sum are constants, add them and return the resulting constant
                    if (result1 instanceof Constant && term instanceof Constant) {
                        Constant result1_copy = (Constant)result1;
                        Constant term_copy = (Constant)term;
                        result1 = new Constant(result1_copy.constant * term_copy.constant);
                    }
                    // if the left term or the right term equals 0, return a new constant 0
                    else if (result1.equals(new Constant(0)) || term.equals(new Constant(0))) {
                        result1 = new Constant(0);
                    }
                    // if the left term equals 1, return the right term
                    else if (result1.equals(new Constant(1))) {
                        result1 = term;
                    }
                    // In all other cases return a new plus expression between the terms,
                    // except when the right term equals 1, the result remains the same
                    else if (!term.equals(new Constant(1))) {
                        result1 = new Multiplication(result1, term);
                    } 
                }
            }
            return result1;
        // A product or one or more sums between products
        case SUM:
            boolean first2 = true;
            Expression result2 = null;
            for (ParseTree<ExpressionGrammar> child : tree.childrenByName(ExpressionGrammar.PRODUCT)) {
                if (first2) {
                    result2 = buildExpression(child);
                    first2 = false;
                }
                else {
                    Expression product = buildExpression(child);
                    // if both terms in sum are constants, add them and return the resulting constant
                    if (result2 instanceof Constant && product instanceof Constant) {
                        Constant result2_copy = (Constant)result2;
                        Constant product_copy = (Constant)product;
                        result2 = new Constant(result2_copy.constant + product_copy.constant);
                    }
                    // if the left term equals 0, return the right term
                    else if (result2.equals(new Constant(0))) {
                        result2 = product;
                    }
                    // In all other cases return a new plus expression between the terms,
                    // except when the right term equals 0, the result remains the same
                    else if (!product.equals(new Constant(0))) {
                        result2 = new Plus(result2, product);
                    }                   
                }
            }
            return result2;
        // An expression with a sum as child
        case EXPRESSION:
            return buildExpression(tree.childrenByName(ExpressionGrammar.SUM).get(0));
        // Root node with expression as child
        case ROOT:
            return buildExpression(tree.childrenByName(ExpressionGrammar.EXPRESSION).get(0));
        // Since we always avoid calling buildExpression with whitespace, this code should never run.
        case WHITESPACE:
            throw new RuntimeException("Called buildExpression with whitespace.");
        }
        // There are no other cases, so the program should not reach this.
        throw new RuntimeException("You shouldn't reach this code.");
    }
    
    /**
     * Performs differentiation on this expression with respect to the given variable.
     * @param var - a Variable
     * @return Expression which is the result of differentiation with respect to var (dExpression/dvar).
     */
    public Expression differentiate(Variable var);
    
    /**
     * Simplifies the expression. If a variable in the expression is found in the given environment,
     * the variable is substituted for the value for that variable. If a variable in the environment
     * is not found in the expression, that variable is ignored. 
     * After substitution, operations between constants are simplified to a single constant.
     * @param environment - maps variables to their values
     * @return the resulting expression after simplification.
     */
    public Expression simplify(Map<String, Double> environment);
    
    /**
     * Returns string representation of this expression.
     * Parentheses are only used around plus expressions and whitespace is used only around the plus symbol to improve readability.
     * A constant is presented as an integer if at least the first 4 decimal digits are 0; 
     * otherwise it is presented as a floating point number with 4 decimal digits.
     * @return a parsable representation of this expression, such that
     * for all e:Expression, e.equals(Expression.parse(e.toString())).
     */
    @Override 
    public String toString();

    /**
     * @param thatObject any object
     * @return true if and only if this and thatObject are structurally-equal Expressions, as defined in the PS1 handout. 
     * Constants that are equal up to at least 4 decimal digits in accuracy are considered completely equal.
     */
    @Override
    public boolean equals(Object thatObject);
    
    /**
     * @return hash code value consistent with the equals() definition of structural
     * equality, such that for all e1,e2:Expression,
     *     e1.equals(e2) implies e1.hashCode() == e2.hashCode()
     */
    @Override
    public int hashCode();
    
    /**
     * Creates new Constant expression.
     * @param constant, >= 0
     * @return new Constant expression.
     */
    public static Constant number(double constant) {
        return new Constant(constant);
    }
    
    /**
     * Creates new Variable expression.
     * @param name, non-empty and contains only letters
     * @return new Variable expression.
     */
    public static Variable variable(String name) {
        return new Variable(name);
    }
    
    /**
     * Creates new Multiplication Expression.
     * @param left
     * @param right
     * @return new Multiplication Expression.
     */
    public static Multiplication multiplication(Expression left, Expression right) {
        return new Multiplication(left, right);
    }
    
    /**
     * Creates new Plus expression.
     * @param left
     * @param right
     * @return new Plus expression.
     */
    public static Plus plus(Expression left, Expression right) {
        return new Plus(left, right);
    }
    
    /* Copyright (c) 2015-2017 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires permission of course staff.
     */
}
