package expressivo;

import java.util.Map;

/**
 * An immutable data type part of the ADT Expression. 
 * Represents an addition between terms, where each term is an Expression.
 */
public class Plus implements Expression {
    // rep
    public final Expression leftTerm;
    public final Expression rightTerm;
    
    // Rep invariant:
    //  none
    // Abstraction function:
    //  Represents an addition between expressions,
    //  where leftTerm is the left expression and rightTerm is the right expression.
    // Safety from rep exposure:
    //  All fields are final and immutable.
    
    public Plus(Expression left, Expression right) {
        this.leftTerm = left;
        this.rightTerm = right;
    }
    
    @Override 
    public String toString() {
        String plusString = "(" + leftTerm.toString() + " + " + rightTerm.toString() + ")";
        return plusString;
    }

    @Override
    public boolean equals(Object thatObject) {
        if (!(thatObject instanceof Plus)) return false;
        
        Plus thatPlus = (Plus) thatObject;
        if (this.leftTerm.equals(thatPlus.leftTerm) && 
                this.rightTerm.equals(thatPlus.rightTerm)) return true;
        else return false;
    }

    @Override
    public int hashCode() {
        return leftTerm.hashCode() + rightTerm.hashCode();
    }

    @Override
    public Expression differentiate(Variable var) {
        Expression leftDerivative = leftTerm.differentiate(var);
        Expression rightDerivative = rightTerm.differentiate(var);
        
        // if both derivatives are constants, add them and return the resulting constant
        if (leftDerivative instanceof Constant && rightDerivative instanceof Constant) {
            Constant leftDerivative_constant = (Constant)leftDerivative;
            Constant rightDerivative_constant = (Constant)rightDerivative;
            return new Constant(leftDerivative_constant.constant + rightDerivative_constant.constant);
        }
        // if the left term equals 0, return the right term
        else if (leftDerivative.equals(new Constant(0))) {
            return rightDerivative;
        }
        // if the right term equals 0, return the left term
        else if (rightDerivative.equals(new Constant(0))) {
            return leftDerivative;
        } 
        // In all other cases return a new plus expression between the terms
        else {
            return new Plus(leftDerivative, rightDerivative);
        }
    }

    @Override
    public Expression simplify(Map<String, Double> environment) {
        Expression left = leftTerm.simplify(environment);
        Expression right = rightTerm.simplify(environment);
        
        // if both derivatives are constants, add them and return the resulting constant
        if (left instanceof Constant && right instanceof Constant) {
            Constant left_constant = (Constant)left;
            Constant right_constant = (Constant)right;
            return new Constant(left_constant.constant + right_constant.constant);
        }
        // if the left term equals 0, return the right term
        else if (left.equals(new Constant(0))) {
            return right;
        }
        // if the right term equals 0, return the left term
        else if (right.equals(new Constant(0))) {
            return left;
        } 
        // In all other cases return a new plus expression between the terms
        else {
            return new Plus(left, right);
        }
    }  
}
