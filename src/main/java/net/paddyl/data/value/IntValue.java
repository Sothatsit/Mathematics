package net.paddyl.data.value;

/**
 * A 32-bit integer value.
 *
 * @author Paddy Lamont
 */
public class IntValue extends Value<Integer> {

    private final int value;

    public IntValue(int value) {
        this.value = value;
    }

    @Override
    public int asInt() {
        return value;
    }

    @Override
    public long asLong() {
        return value;
    }

    @Override
    public Integer value() {
        return value;
    }

    @Override
    public Class<?> getDataType() {
        return int.class;
    }

    @Override
    public boolean isEqualTo(Integer value) {
        return this.value == value;
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }
}
