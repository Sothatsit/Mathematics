package net.paddyl.constraints.set;

import net.paddyl.util.NumberType;

/**
 * Inclusive range of bytes.
 */
public class ByteRange extends NumberRange<ByteRange, Byte> {

    public static final ByteRange EMPTY = new ByteRange((byte) 0, (byte) -1);
    public static final ByteRange ALL = new ByteRange(Byte.MIN_VALUE, Byte.MAX_VALUE);

    public ByteRange(byte min, byte max) {
        super(NumberType.BYTE, min, max);
    }

    /**
     * A factory for creating and manipulating ByteRanges.
     */
    public static class ByteRangeFactory extends NumberRangeFactory<ByteRange, Byte> {

        public ByteRangeFactory() {
            super(NumberType.BYTE, ByteRange.class);
        }

        @Override
        public ByteRange createRange(Byte min, Byte max) {
            return new ByteRange(min, max);
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
