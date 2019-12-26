package net.paddyl.data.value;

import net.paddyl.util.Cast;
import net.paddyl.util.Checks;

/**
 * An immutable value used in an expression.
 *
 * @author Paddy Lamont
 */
public abstract class Value<K> {

    public double asDouble() {
        throw new UnsupportedOperationException(getClass().getSimpleName() + " cannot be represented as a double");
    }

    public float asFloat() {
        throw new UnsupportedOperationException(getClass().getSimpleName() + " cannot be represented as a float");
    }

    public int asInt() {
        throw new UnsupportedOperationException(getClass().getSimpleName() + " cannot be represented as an int");
    }

    public long asLong() {
        throw new UnsupportedOperationException(getClass().getSimpleName() + " cannot be represented as a long");
    }

    public Tensor asTensor() {
        throw new UnsupportedOperationException(getClass().getSimpleName() + " cannot be represented as a tensor");
    }

    public <T extends Value> Tensor<T> asTensor(Class<T> elementType) {
        Tensor tensor = asTensor();

        Checks.assertThat(tensor.getElementType().equals(elementType),
                "this is not a tensor of element type " + elementType);

        return Cast.cast(tensor);
    }

    public K value() {
        return Cast.cast(this);
    }

    public Class<?> getDataType() {
        return getClass();
    }

    public boolean equals(Object obj) {
        if (obj == null || !getClass().equals(obj.getClass()))
            return false;

        Value other = Cast.cast(obj);
        if(!getDataType().equals(other.getDataType()))
            return false;

        K value = Cast.cast(other.value());
        return isEqualTo(value);

    }

    public abstract boolean isEqualTo(K value);

    public abstract String toString();
}
