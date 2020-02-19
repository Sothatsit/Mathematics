package net.paddyl.constraints.set;

import net.paddyl.util.NumberType;

/**
 * Inclusive range of numbers.
 */
public abstract class NumberRange<R extends NumberRange<R, N>, N extends Number> implements ValueSet<R, N> {

    public final NumberType<N> type;
    public final N min;
    public final N max;

    public NumberRange(NumberType<N> type, N min, N max) {
        if (type.ltImpl(max, min)) {
            min = type.coerce(0);
            max = type.coerce(-1);
        }
        this.type = type;
        this.min = min;
        this.max = max;
    }

    @Override
    public boolean isEmpty() {
        return type.compare(max, min) < 0;
    }

    @Override
    public boolean contains(N value) {
        return type.lteImpl(min, value) && type.lteImpl(value, max);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !getClass().equals(obj.getClass()))
            return false;
        NumberRange other = (NumberRange) obj;
        return type.eq(min, other.min) && type.eq(max, other.max);
    }

    @Override
    public int hashCode() {
        return min.hashCode() ^ (47 * max.hashCode());
    }

    @Override
    public String toString() {
        String className = getClass().getSimpleName();

        if (isEmpty())
            return className + "{EMPTY}";
        if (type.compare(min, max) == 0)
            return className + "{" + min + "}";

        boolean minLowest = type.eq(min, type.getMinValue());
        boolean maxHighest = type.eq(max, type.getMaxValue());
        if (minLowest && maxHighest)
            return className + "{ALL}";
        if (minLowest)
            return className + "{ -> " + max + "}";
        if (maxHighest)
            return className + "{" + min + " -> }";

        return className + "{" + min + " -> " + max + "}";
    }
}
