package net.paddyl.constraints.set;

import net.paddyl.util.NumberTypes;

/**
 * Inclusive range of longs.
 */
public class LongRange extends NumberRange<LongRange, Long> {

    public static final LongRange EMPTY = new LongRange(0L, -1L, null);
    public static final LongRange ALL = new LongRange(Long.MIN_VALUE, Long.MAX_VALUE, null);

    public LongRange(Long min, Long max, Long step) {
        super(NumberTypes.LONG, min, max, step);
    }

    /**
     * A factory for creating and manipulating LongRanges.
     */
    public static class LongRangeFactory extends NumberRangeFactory<LongRange, Long> {

        public LongRangeFactory() {
            super(NumberTypes.LONG, LongRange.class);
        }

        @Override
        public LongRange steppedRange(Long min, Long max, Long step) {
            return new LongRange(min, max, step);
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
