package net.paddyl.constraints.set;

import net.paddyl.util.NumberType;

/**
 * Inclusive range of bytes.
 */
public class ByteRange extends NumberRange<ByteRange, Byte> {

    public static final ByteRange EMPTY = new ByteRange((byte) 0, (byte) -1, null);
    public static final ByteRange ALL = new ByteRange(Byte.MIN_VALUE, Byte.MAX_VALUE, null);

    public ByteRange(Byte min, Byte max, Byte step) {
        super(NumberType.BYTE, min, max, step);
    }

    /**
     * A factory for creating and manipulating ByteRanges.
     */
    public static class ByteRangeFactory extends NumberRangeFactory<ByteRange, Byte> {

        public ByteRangeFactory() {
            super(NumberType.BYTE, ByteRange.class);
        }

        @Override
        public ByteRange createRange(Byte min, Byte max, Byte step) {
            return new ByteRange(min, max, step);
        }

        @Override
        public ByteRange empty() {
            return EMPTY;
        }

        @Override
        public ByteRange all() {
            return ALL;
        }
    }
}
