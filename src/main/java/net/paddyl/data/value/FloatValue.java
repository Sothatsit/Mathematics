package net.paddyl.data.value;

/**
 * A 32-bit floating point value.
 *
 * @author Paddy Lamont
 */
public class FloatValue extends Value<Float> {

    private final float value;

    public FloatValue(float value) {
        this.value = value;
    }

    @Override
    public float asFloat() {
        return value;
    }

    @Override
    public double asDouble() {
        return value;
    }

    @Override
    public Float value() {
        return value;
    }

    @Override
    public Class<?> getDataType() {
        return float.class;
    }

    @Override
    public boolean isEqualTo(Float value) {
        return this.value == value;
    }

    @Override
    public String toString() {
        return Float.toString(value);
    }
}
