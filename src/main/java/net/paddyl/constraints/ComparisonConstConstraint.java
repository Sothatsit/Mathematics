package net.paddyl.constraints;

/**
 * Contains the basic comparison with constant constraints.
 */
public abstract class ComparisonConstConstraint implements ConstConstraint {

    public final long value;

    public ComparisonConstConstraint(long value) {
        this.value = value;
    }

    /**
     * range >= minValue.
     */
    public static class GTEConstConstraint extends ComparisonConstConstraint {

        public GTEConstConstraint(long minValue) {
            super(minValue);
        }

        @Override
        public LongRange tryReduce(LongRange range) {
            if (range.max < value)
                return LongRange.EMPTY;
            if (range.min >= value)
                return range;
            return new LongRange(value, range.max);
        }
    }

    /**
     * range > minValue.
     */
    public static class GTConstConstraint extends ComparisonConstConstraint {

        public GTConstConstraint(long minValue) {
            super(minValue);
        }

        @Override
        public LongRange tryReduce(LongRange range) {
            if (range.max <= value)
                return LongRange.EMPTY;
            if (range.min > value)
                return range;
            return new LongRange(value + 1, range.max);
        }
    }

    /**
     * range <= maxValue.
     */
    public static class LTEConstConstraint extends ComparisonConstConstraint {

        public LTEConstConstraint(long maxValue) {
            super(maxValue);
        }

        @Override
        public LongRange tryReduce(LongRange range) {
            if (range.min > value)
                return LongRange.EMPTY;
            if (range.max <= value)
                return range;
            return new LongRange(range.min, value);
        }
    }

    /**
     * range < maxValue.
     */
    public static class LTConstConstraint extends ComparisonConstConstraint {

        public LTConstConstraint(long maxValue) {
            super(maxValue);
        }

        @Override
        public LongRange tryReduce(LongRange range) {
            if (range.min >= value)
                return LongRange.EMPTY;
            if (range.max < value)
                return range;
            return new LongRange(range.min, value - 1);
        }
    }

    /**
     * range == value.
     */
    public static class EqualsConstConstraint extends ComparisonConstConstraint {

        public EqualsConstConstraint(long value) {
            super(value);
        }

        @Override
        public LongRange tryReduce(LongRange range) {
            return range.contains(value) ? LongRange.single(value) : LongRange.EMPTY;
        }
    }
}
