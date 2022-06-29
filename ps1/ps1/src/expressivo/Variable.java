package expressivo;

import java.util.Map;

/**
 * An immutable data type part of the ADT Expression. 
 * Represents a variable, i.e. a case sensitive non-empty sequence of letters.
 */
public class Variable implements Expression {
    // rep
    public final String name;
    
    // Rep invariant:
    //  name must be a non-empty sequence of letters
    // Abstraction function:
    //  Represents a variable, with name this.name and without a value assigned to it.
    // Safety from rep exposure:
    //  All fields are final and immutable.
    
    public Variable(String name) {
        this.name = name;
        checkRep();
    }
    
    private void checkRep() {
        assert !name.isEmpty();
        assert name.matches("^[a-zA-Z]+$");
    }
    
    @Override 
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object thatObject) {
        if (!(thatObject instanceof Variable)) return false;
        
        Variable thatVariable = (Variable) thatObject;
        if (this.name.equals(thatVariable.name)) return true;
        else return false;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public Expression differentiate(Variable var) {
        if (var.equals(this)) return new Constant(1);
        else return new Constant(0);
    }

    @Override
    public Expression simplify(Map<String, Double> environment) {
        if (environment.containsKey(this.name)) {
            return new Constant(environment.get(this.name));
        }
        else {
            return this;
        }
    }
}
