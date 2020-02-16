package net.paddyl.constraints.set;

/**
 * Inclusive range of longs.
 */
public class LongRange implements ValueSet<LongRange, Long> {

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

    @Override
    public boolean isEmpty() {
        return max < min;
    }

    @Override
    public boolean contains(Long value) {
        return min <= value && value <= max;
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
        if (min == Long.MIN_VALUE && max == Long.MAX_VALUE)
            return "LongRange{ALL}";
        if (min == Long.MIN_VALUE)
            return "LongRange{ -> " + max + "}";
        if (max == Long.MAX_VALUE)
            return "LongRange{" + min + " -> }";
        return "LongRange{" + min + " -> " + max + "}";
    }
}
