package net.paddyl.constraints;

/**
 * An operation on a single range.
 */
public interface ConstOperator {

    public static final ConstOperator IDENTITY = new ChainedConstOperator();

    /**
     * Apply the operation.
     */
    public LongRange forward(LongRange range);

    /**
     * Reverse the operation.
     */
    public LongRange backward(LongRange range);

    /**
     * @return the inverse operation.
     */
    public default ConstOperator inverse() {
        ConstOperator original = this;
        return new ConstOperator() {
            @Override
            public LongRange forward(LongRange range) {
                return original.backward(range);
            }

            @Override
            public LongRange backward(LongRange range) {
                return original.forward(range);
            }

            @Override
            public String toString() {
                return "inverse{" + original.toString() + "}";
            }
        };
    }

    /**
     * Add. +.
     */
    public static ConstOperator add(long value) {
        return new ArithmeticConstOperator.AddConstOperator(value);
    }
}