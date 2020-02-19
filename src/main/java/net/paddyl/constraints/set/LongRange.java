package net.paddyl.constraints.set;

import net.paddyl.util.NumberType;

/**
 * Inclusive range of longs.
 */
public class LongRange extends NumberRange<LongRange, Long> {

    public static final LongRange EMPTY = new LongRange(0, -1);
    public static final LongRange ALL = new LongRange(Long.MIN_VALUE, Long.MAX_VALUE);

    public LongRange(long min, long max) {
        super(NumberType.LONG, min, max);
    }

    /**
     * A factory for creating and manipulating LongRanges.
     */
    public static class LongRangeFactory extends NumberRangeFactory<LongRange, Long> {

        public LongRangeFactory() {
            super(NumberType.LONG, LongRange.class);
        }

        @Override
        public LongRange createRange(Long min, Long max) {
            return new LongRange(min, max);
        }

        @Override
        public LongRange empty() {
            return EMPTY;
        }

        @Override
        public LongRange all() {
            return ALL;
        }
    }
}
