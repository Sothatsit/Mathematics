package net.paddyl.constraints.set;

import net.paddyl.util.NumberType;

/**
 * Inclusive range of shorts.
 */
public class ShortRange extends NumberRange<ShortRange, Short> {

    public static final ShortRange EMPTY = new ShortRange((short) 0, (short) -1, null);
    public static final ShortRange ALL = new ShortRange(Short.MIN_VALUE, Short.MAX_VALUE, null);

    public ShortRange(Short min, Short max, Short step) {
        super(NumberType.SHORT, min, max, step);
    }

    /**
     * A factory for creating and manipulating ShortRanges.
     */
    public static class ShortRangeFactory extends NumberRangeFactory<ShortRange, Short> {

        public ShortRangeFactory() {
            super(NumberType.SHORT, ShortRange.class);
        }

        @Override
        public ShortRange createRange(Short min, Short max, Short step) {
            return new ShortRange(min, max, step);
        }

        @Override
        public ShortRange empty() {
            return EMPTY;
        }

        @Override
        public ShortRange all() {
            return ALL;
        }
    }
}
