package net.paddyl.constraints;

import net.paddyl.constraints.set.ValueSet;

/**
 * Chained const operations, applied consecutively.
 */
public class ChainedConstOperator<S extends ValueSet<S, V>, V> implements ConstOperator<S, V> {

    private final ConstOperator<S, V>[] operators;

    public ChainedConstOperator(ConstOperator<S, V>[] operators) {
        this.operators = operators;
    }

    @Override
    public S forward(S range) {
        for (ConstOperator<S, V> operator : operators) {
            range = operator.forward(range);
        }
        return range;
    }

    @Override
    public S backward(S range) {
        for (int index = operators.length - 1; index >= 0; --index) {
            range = operators[index].backward(range);
        }
        return range;
    }

    @Override
    public String toString() {
        if (operators.length == 0)
            return "Identity";

        String result = operators[0].toString();
        for (int index = 1; index < operators.length; ++index) {
            result = "(" + result + ") " + operators[index].toString();
        }
        return result;
    }
}
