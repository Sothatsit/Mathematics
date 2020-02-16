package net.paddyl.constraints.set;

/**
 * A factory for constructing and manipulating LongRanges.
 */
public class LongRangeFactory extends ValueSetFactory<LongRange, Long> {

    public LongRangeFactory() {
        super(LongRange.class, Long.class);
    }

    @Override
    public LongRange empty() {
        return LongRange.EMPTY;
    }

    @Override
    public LongRange range(Long from, Long to) {
        return new LongRange(from, to);
    }

    @Override
    public LongRange below(Long maxExclusive) {
        if (maxExclusive == Long.MIN_VALUE)
            return LongRange.EMPTY;
        return new LongRange(Long.MIN_VALUE, maxExclusive - 1);
    }

    @Override
    public LongRange belowIncl(Long max) {
        return new LongRange(Long.MIN_VALUE, max);
    }

    @Override
    public LongRange above(Long minExclusive) {
        if (minExclusive == Long.MAX_VALUE)
            return LongRange.EMPTY;
        return new LongRange(minExclusive + 1, Long.MAX_VALUE);
    }

    @Override
    public LongRange aboveIncl(Long min) {
        return new LongRange(min, Long.MAX_VALUE);
    }

    @Override
    public LongRange union(LongRange one, LongRange two) {
        if (one.isEmpty())
            return two;
        if (two.isEmpty())
            return one;

        return new LongRange(Math.min(one.min, two.min), Math.max(one.max, two.max));
    }

    @Override
    public LongRange intersection(LongRange one, LongRange two) {
        if (one.isEmpty() || two.isEmpty() || one.min > two.max || one.max < two.min)
            return LongRange.EMPTY;

        return new LongRange(Math.max(one.min, two.min), Math.min(one.max, two.max));
    }

    @Override
    public LongRange shift(LongRange range, Long shift) {
        if (shift == 0)
            return range;

        // Check for overflow
        if (shift > 0) {
            long maxNoOverflow = Long.MAX_VALUE - shift;
            if (range.min > maxNoOverflow || range.max > maxNoOverflow)
                return LongRange.ALL;
        } else {
            long minNoOverflow = Long.MIN_VALUE - shift;
            if (range.min < minNoOverflow || range.max < minNoOverflow)
                return LongRange.ALL;
        }

        return new LongRange(range.min + shift, range.max + shift);
    }
}
