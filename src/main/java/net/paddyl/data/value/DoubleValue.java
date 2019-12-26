package net.paddyl.data.value;

/**
 * A 64-bit floating point value.
 *
 * @author Paddy Lamont
 */
public class DoubleValue extends Value<Double> {

    private final double value;

    public DoubleValue(double value) {
        this.value = value;
    }

    @Override
    public double asDouble() {
        return value;
    }

    @Override
    public Double value() {
        return value;
    }

    @Override
    public Class<?> getDataType() {
        return double.class;
    }

    @Override
    public boolean isEqualTo(Double value) {
        return this.value == value;
    }

    @Override
    public String toString() {
        return Double.toString(value);
    }
}
