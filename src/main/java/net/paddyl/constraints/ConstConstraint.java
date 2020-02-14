package net.paddyl.constraints;

/**
 * A constraint that takes a single input.
 */
public interface ConstConstraint {

    public LongRange tryReduce(LongRange range);

    /**
     * Repeatedly try to reduce {@param range} until it no longer changes.
     */
    public default LongRange bruteReduce(LongRange range) {
        LongRange last;
        do {
            last = range;
            range = tryReduce(last);
        } while (!last.equals(range));

        return range;
    }

    /**
     * Greater than equals. range >= value.
     */
    public static ConstConstraint gte(long value) {
        return new ComparisonConstConstraint.GTEConstConstraint(value);
    }

    /**
     * Greater than. range > value.
     */
    public static ConstConstraint gt(long value) {
        return new ComparisonConstConstraint.GTConstConstraint(value);
    }

    /**
     * Less than equals. range <= value.
     */
    public static ConstConstraint lte(long value) {
        return new ComparisonConstConstraint.LTEConstConstraint(value);
    }

    /**
     * Less than. range < value.
     */
    public static ConstConstraint lt(long value) {
        return new ComparisonConstConstraint.LTConstConstraint(value);
    }

    /**
     * Equals. range == value.
     */
    public static ConstConstraint eq(long value) {
        return new ComparisonConstConstraint.EqualsConstConstraint(value);
    }
}
