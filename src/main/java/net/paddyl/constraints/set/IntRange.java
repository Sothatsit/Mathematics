package net.paddyl.constraints.set;

import net.paddyl.util.NumberType;

/**
 * Inclusive range of ints.
 */
public class IntRange extends NumberRange<IntRange, Integer> {

    public static final IntRange EMPTY = new IntRange(0, -1, null);
    public static final IntRange ALL = new IntRange(Integer.MIN_VALUE, Integer.MAX_VALUE, null);

    public IntRange(Integer min, Integer max, Integer step) {
        super(NumberType.INT, min, max, step);
    }

    /**
     * A factory for creating and manipulating IntRanges.
     */
    public static class IntRangeFactory extends NumberRangeFactory<IntRange, Integer> {

        public IntRangeFactory() {
            super(NumberType.INT, IntRange.class);
        }

        @Override
        public IntRange createRange(Integer min, Integer max, Integer step) {
            return new IntRange(min, max, step);
        }

        @Override
        public IntRange empty() {
            return EMPTY;
        }

        @Override
        public IntRange all() {
            return ALL;
        }
    }
}
