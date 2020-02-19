package net.paddyl.constraints.set;

import net.paddyl.util.NumberType;

/**
 * Inclusive range of shorts.
 */
public class ShortRange extends NumberRange<ShortRange, Short> {

    public static final ShortRange EMPTY = new ShortRange((short) 0, (short) -1);
    public static final ShortRange ALL = new ShortRange(Short.MIN_VALUE, Short.MAX_VALUE);

    public ShortRange(short min, short max) {
        super(NumberType.SHORT, min, max);
    }

    /**
     * A factory for creating and manipulating ShortRanges.
     */
    public static class ShortRangeFactory extends NumberRangeFactory<ShortRange, Short> {

        public ShortRangeFactory() {
            super(NumberType.SHORT, ShortRange.class);
        }

        @Override
        public ShortRange createRange(Short min, Short max) {
            return new ShortRange(min, max);
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
