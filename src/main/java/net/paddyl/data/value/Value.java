package net.paddyl.data.value;

import net.paddyl.util.Cast;

/**
 * An immutable value used in an expression.
 *
 * @author Paddy Lamont
 */
public abstract class Value<K> {

    public <T> T as(Class<T> clazz) {
        throw new UnsupportedOperationException(getClass().getSimpleName() + " cannot be represented as a " + clazz);
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
