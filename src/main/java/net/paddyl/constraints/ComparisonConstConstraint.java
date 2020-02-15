package net.paddyl.constraints;

/**
 * Contains the basic comparison with constant constraints.
 */
public class ComparisonConstConstraint implements ConstConstraint {

    public final long value;
    public final LongRange validRange;

    private ComparisonConstConstraint(long value, LongRange validRange) {
        this.value = value;
        this.validRange = validRange;
    }

    @Override
    public LongRange tryReduce(LongRange range) {
        return range.intersection(validRange);
    }

    /**
     * range >= minValue.
     */
    public static class GTEConstConstraint extends ComparisonConstConstraint {

        public GTEConstConstraint(long minValue) {
            super(minValue, LongRange.above(minValue));
        }

        @Override
        public String toString() {
            return ">= " + value;
        }
    }

    /**
     * range > minValue.
     */
    public static class GTConstConstraint extends ComparisonConstConstraint {

        public GTConstConstraint(long minValue) {
            super(minValue, LongRange.aboveNotIncl(minValue));
        }

        @Override
        public String toString() {
            return "> " + value;
        }
    }

    /**
     * range <= maxValue.
     */
    public static class LTEConstConstraint extends ComparisonConstConstraint {

        public LTEConstConstraint(long maxValue) {
            super(maxValue, LongRange.below(maxValue));
        }

        @Override
        public String toString() {
            return "<= " + value;
        }
    }

    /**
     * range < maxValue.
     */
    public static class LTConstConstraint extends ComparisonConstConstraint {

        public LTConstConstraint(long maxValue) {
            super(maxValue, LongRange.belowNotIncl(maxValue));
        }

        @Override
        public String toString() {
            return "< " + value;
        }
    }

    /**
     * range == value.
     */
    public static class EqualsConstConstraint extends ComparisonConstConstraint {

        public EqualsConstConstraint(long value) {
            super(value, LongRange.single(value));
        }

        @Override
        public String toString() {
            return "== " + value;
        }
    }
}
