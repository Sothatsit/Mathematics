package net.paddyl.constraints.set;

import net.paddyl.util.NumberType;

/**
 * A factory for constructing and manipulating LongRanges.
 */
public abstract class NumberRangeFactory<R extends NumberRange<R, N>, N extends Number> extends ValueSetFactory<R, N> {

    public final NumberType<N> type;
    public final N minValue;
    public final N maxValue;
    public final N zero;
    public final N one;

    public NumberRangeFactory(NumberType<N> type, Class<R> rangeClass) {
        super(rangeClass, type.getBoxClass());

        this.type = type;
        this.minValue = type.getMinValue();
        this.maxValue = type.getMaxValue();
        this.zero = type.coerce(0);
        this.one = type.coerce(1);
        if (minValue == null || maxValue == null)
            throw new IllegalArgumentException("types that do not have min or max values are not supported");
    }

    public abstract R createRange(N min, N max);

    @Override
    public R range(N from, N to) {
        return createRange(from, to);
    }

    @Override
    public R below(N maxExclusive) {
        if (type.eqImpl(maxExclusive, minValue))
            return empty();
        return createRange(minValue, type.subtractImpl(maxExclusive, one));
    }

    @Override
    public R belowIncl(N max) {
        return createRange(minValue, max);
    }

    @Override
    public R above(N minExclusive) {
        if (type.eqImpl(minExclusive, maxValue))
            return empty();
        return createRange(type.addImpl(minExclusive, one), maxValue);
    }

    @Override
    public R aboveIncl(N min) {
        return createRange(min, maxValue);
    }

    @Override
    public R union(R one, R two) {
        if (one.isEmpty())
            return two;
        if (two.isEmpty())
            return one;
        return createRange(type.minImpl(one.min, two.min), type.maxImpl(one.max, two.max));
    }

    @Override
    public R intersection(R one, R two) {
        if (one.isEmpty() || two.isEmpty() || type.gt(one.min, two.max) || type.gt(two.min, one.max))
            return empty();

        return createRange(type.maxImpl(one.min, two.min), type.minImpl(one.max, two.max));
    }

    @Override
    public R shift(R range, N shift) {
        if (range.isEmpty() || type.eqImpl(shift, zero))
            return range;

        // Check for overflow
        if (type.gtImpl(shift, zero)) {
            N maxNoOverflow = type.subtractImpl(maxValue, shift);
            if (type.gt(range.max, maxNoOverflow))
                return all();
        } else {
            N minNoOverflow = type.subtractImpl(minValue, shift);
            if (type.lt(range.min, minNoOverflow))
                return all();
        }

        return createRange(type.addImpl(range.min, shift), type.addImpl(range.max, shift));
    }
}
