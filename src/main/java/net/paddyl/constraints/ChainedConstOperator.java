package net.paddyl.constraints;

/**
 * Chained const operations, applied consecutively.
 */
public class ChainedConstOperator implements ConstOperator {

    private final ConstOperator[] operators;

    public ChainedConstOperator(ConstOperator... operators) {
        this.operators = operators;
    }

    @Override
    public LongRange forward(LongRange range) {
        for (ConstOperator operator : operators) {
            range = operator.forward(range);
        }
        return range;
    }

    @Override
    public LongRange backward(LongRange range) {
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
