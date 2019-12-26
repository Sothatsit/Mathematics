package net.paddyl.data.value;

/**
 * A 64-bit integer value.
 *
 * @author Paddy Lamont
 */
public class LongValue extends Value<Long> {

    private final long value;

    public LongValue(long value) {
        this.value = value;
    }

    @Override
    public long asLong() {
        return value;
    }

    @Override
    public Class<?> getDataType() {
        return long.class;
    }

    @Override
    public boolean isEqualTo(Long value) {
        return this.value == value;
    }

    @Override
    public String toString() {
        return Long.toString(value);
    }
}
