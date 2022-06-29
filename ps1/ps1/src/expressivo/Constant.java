package expressivo;

import java.util.Locale;
import java.util.Map;

/**
 * An immutable data type part of the ADT Expression. 
 * Represents a non-negative constant number.
 * Two Constants are considered equal if they are equal up to at least 4 decimal digits.
 */
public class Constant implements Expression {
    // rep
    public final double constant;
    
    // Rep invariant:
    //  constant must be non-negative
    // Abstraction function:
    //  Represents a non-negative constant number, with value this.constant.
    // Safety from rep exposure:
    //  All fields are final and immutable.
    
    public Constant(double constant) {
        this.constant = constant;
        checkRep();
    }
    
    private void checkRep() {
        assert constant >= 0;
    }
    
    @Override 
    public String toString() {
        String result;
        if ((constant - (long)constant) < 0.0001) {
            result = String.valueOf((long)constant);
        }
        else {
            result = String.format(Locale.US, "%.4f", constant);
        }
        
        return result;
    }

    @Override
    public boolean equals(Object thatObject) {
        if (!(thatObject instanceof Constant)) return false;
        
        Constant thatConstant = (Constant) thatObject;
        if (Math.abs(this.constant - thatConstant.constant) < 0.0001) return true;
        else return false;
    }

    @Override
    public int hashCode() {
        return (int)this.constant;
    }

    @Override
    public Expression differentiate(Variable var) {
        return new Constant(0);
    }

    @Override
    public Expression simplify(Map<String, Double> environment) {
        return this;
    }
}
