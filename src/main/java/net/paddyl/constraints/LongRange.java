package net.paddyl.constraints;

/**
 * Inclusive range of longs.
 */
public class LongRange {

    public static final LongRange EMPTY = new LongRange(0, -1);
    public static final LongRange ALL = new LongRange(Long.MIN_VALUE, Long.MAX_VALUE);

    public final long min;
    public final long max;

    public LongRange(long min, long max) {
        if (max < min) {
            min = 0;
            max = -1;
        }
        this.min = min;
        this.max = max;
    }

    public long count() {
        return max - min + 1;
    }

    public boolean isEmpty() {
        return max < min;
    }

    public boolean contains(long value) {
        return min <= value && value <= max;
    }

    public boolean isUnboundedLow() {
        return min == Long.MIN_VALUE;
    }

    public boolean isUnboundedHigh() {
        return max == Long.MAX_VALUE;
    }

    public LongRange union(LongRange other) {
        if (isEmpty())
            return other;
        if (other.isEmpty())
            return this;

        return new LongRange(
                Math.min(min, other.min),
                Math.max(max, other.max)
        );
    }

    public LongRange intersection(LongRange other) {
        if (isEmpty() || other.isEmpty() || max < other.min || min > other.max)
            return EMPTY;

        return new LongRange(
                Math.max(min, other.min),
                Math.min(max, other.max)
        );
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !getClass().equals(obj.getClass()))
            return false;
        LongRange other = (LongRange) obj;
        return min == other.min && max == other.max;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(min) ^ (47 * Long.hashCode(max));
    }

    @Override
    public String toString() {
        if (isEmpty())
            return "LongRange{<empty>}";
        if (min == max)
            return "LongRange{" + min + "}";
        if (isUnboundedLow() && isUnboundedHigh())
            return "LongRange{ALL}";
        if (isUnboundedLow())
            return "LongRange{ -> " + max + "}";
        if (isUnboundedHigh())
            return "LongRange{" + min + " -> }";
        return "LongRange{" + min + " -> " + max + "}";
    }

    public static LongRange of(long from, long to) {
        return new LongRange(from, to);
    }

    public static LongRange single(long value) {
        return new LongRange(value, value);
    }

    /**
     * Below {@param value} inclusive.
     */
    public static LongRange below(long value) {
        return new LongRange(Long.MIN_VALUE, value);
    }

    /**
     * Above {@param value} inclusive.
     */
    public static LongRange above(long value) {
        return new LongRange(value, Long.MAX_VALUE);
    }

    /**
     * Below {@param value} exclusive.
     */
    public static LongRange belowNotIncl(long value) {
        if (value == Long.MIN_VALUE)
            return EMPTY;
        return new LongRange(Long.MIN_VALUE, value - 1);
    }

    /**
     * Above {@param value} exclusive.
     */
    public static LongRange aboveNotIncl(long value) {
        if (value == Long.MAX_VALUE)
            return EMPTY;
        return new LongRange(value + 1, Long.MAX_VALUE);
    }
}
