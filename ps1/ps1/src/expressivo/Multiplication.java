package expressivo;

import java.util.Map;

/**
 * An immutable data type part of the ADT Expression. 
 * Represents a multiplication between terms, where each term is an Expression.
 */
public class Multiplication implements Expression {
    // rep
    public final Expression leftTerm;
    public final Expression rightTerm;
    
    // Rep invariant:
    //  none
    // Abstraction function:
    //  Represents a multiplication between expressions,
    //  where leftTerm is the left expression and rightTerm is the right expression.
    // Safety from rep exposure:
    //  All fields are final and immutable.
    
    public Multiplication(Expression left, Expression right) {
        this.leftTerm = left;
        this.rightTerm = right;
    }
    
    @Override 
    public String toString() {
        String multString = leftTerm.toString() + "*" + rightTerm.toString();
        return multString;
    }

    @Override
    public boolean equals(Object thatObject) {
        if (!(thatObject instanceof Multiplication)) return false;
        
        Multiplication thatMultiplication = (Multiplication) thatObject;
        if (this.leftTerm.equals(thatMultiplication.leftTerm) && 
                this.rightTerm.equals(thatMultiplication.rightTerm)) return true;
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
        Expression product1 = null;
        Expression product2;
        
        // if both terms of product1 are constants, multiply them and assign the resulting constant to product1
        if (leftTerm instanceof Constant && rightDerivative instanceof Constant) {
            Constant leftTerm_constant = (Constant)leftTerm;
            Constant rightDerivative_constant = (Constant)rightDerivative;
            product1 = new Constant(leftTerm_constant.constant * rightDerivative_constant.constant);
        }
        // if the left term or the right derivative equals 0, assign a new constant 0 to product1
        else if (rightDerivative.equals(new Constant(0))) {
            product1 = new Constant(0);
        }
        // if the right derivative equals 1, product1 is the left term
        else if (rightDerivative.equals(new Constant(1))) {
            product1 = leftTerm;
        }
        // In all other cases product1 is the multiplication between the left term and the right derivative
        else {
            product1 = new Multiplication(leftTerm, rightDerivative);
        } 
        // if both terms of product2 are constants, multiply them and assign the resulting constant to product2
        if (leftDerivative instanceof Constant && rightTerm instanceof Constant) {
            Constant leftDerivative_constant = (Constant)leftDerivative;
            Constant rightTerm_constant = (Constant)rightTerm;
            product2 = new Constant(leftDerivative_constant.constant * rightTerm_constant.constant);
        }  
        // if the left derivative or the right term equals 0, assign a new constant 0 to product2
        else if (leftDerivative.equals(new Constant(0))) {
            product2 = new Constant(0);
        }       
        // if the left derivative equals 1, product2 is the right term
        else if (leftDerivative.equals(new Constant(1))) {
            product2 = rightTerm;
        }
        // In all other cases product2 is the multiplication between the left derivative and the right term
        else {
            product2 = new Multiplication(leftDerivative, rightTerm);
        } 
        
        // if both products are constants, add them and return the resulting constant
        if (product1 instanceof Constant && product2 instanceof Constant) {
            Constant product1_constant = (Constant)product1;
            Constant product2_constant = (Constant)product2;
            return new Constant(product1_constant.constant + product2_constant.constant);
        }
        // if the left product equals 0, return the right product
        else if (product1.equals(new Constant(0))) {
            return product2;
        }
        // if the right product equals 0, return the left product
        else if (product2.equals(new Constant(0))) {
            return product1;
        } 
        // In all other cases return a new plus expression between the products
        else {
            return new Plus(product1, product2);
        }       
    }

    @Override
    public Expression simplify(Map<String, Double> environment) {
        Expression left = this.leftTerm.simplify(environment);
        Expression right = this.rightTerm.simplify(environment);
        
        // if both terms are constants, multiply them and return the resulting constant
        if (left instanceof Constant && right instanceof Constant) {
            Constant left_constant = (Constant)left;
            Constant right_constant = (Constant)right;
            return new Constant(left_constant.constant * right_constant.constant);
        }
        // if the left term or the right term equals 0, return a new constant 0
        else if (left.equals(new Constant(0)) || right.equals(new Constant(0))) {
            return new Constant(0);
        }
        // if the left term equals 1, return the right term
        else if (left.equals(new Constant(1))) {
            return right;
        }
        // if the right term equals 1, return the left term
        else if (right.equals(new Constant(1))) {
            return left;
        }
        // In all other cases product1 is the multiplication between the left term and the right derivative
        else {
            return new Multiplication(left, right);
        } 
    }
}
