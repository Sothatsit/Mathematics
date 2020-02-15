package net.paddyl.constraints;

/**
 * Basic arithmetic const operator.
 */
public abstract class ArithmeticConstOperator implements ConstOperator {

    protected final long value;

    public ArithmeticConstOperator(long value) {
        this.value = value;
    }

    /**
     * range + value.
     */
    public static class AddConstOperator extends ArithmeticConstOperator {

        public AddConstOperator(long value) {
            super(value);
        }

        @Override
        public LongRange forward(LongRange range) {
            if (value == 0)
                return range;

            // Check for overflow
            if (value > 0) {
                long maxNoOverflow = Long.MAX_VALUE - value;
                if (range.min > maxNoOverflow || range.max > maxNoOverflow)
                    return LongRange.ALL;
            } else {
                long minNoOverflow = Long.MIN_VALUE - value;
                if (range.min < minNoOverflow || range.max < minNoOverflow)
                    return LongRange.ALL;
            }

            return new LongRange(range.min + value, range.max + value);
        }

        @Override
        public LongRange backward(LongRange range) {
            return new LongRange(range.min - value, range.max - value);
        }

        @Override
        public String toString() {
            if (value < 0)
                return "- " + Long.toString(value).substring(1);
            return "+ " + value;
        }
    }
}
