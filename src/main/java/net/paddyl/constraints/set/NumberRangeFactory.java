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
        this.zero = type.getZero();
        this.one = type.getOne();
        if (minValue == null || maxValue == null)
            throw new IllegalArgumentException("types that do not have min or max values are not supported");
    }

    public abstract R createRange(N min, N max, N step);

    public R createRange(N min, N max) {
        return createRange(min, max, null);
    }

    @Override
    public R range(N from, N to) {
        return createRange(from, to);
    }

    @Override
    public R below(N maxExclusive) {
        if (type.eq(maxExclusive, minValue))
            return empty();
        return createRange(minValue, type.subtract(maxExclusive, one));
    }

    @Override
    public R belowIncl(N max) {
        return createRange(minValue, max);
    }

    @Override
    public R above(N minExclusive) {
        if (type.eq(minExclusive, maxValue))
            return empty();
        return createRange(type.add(minExclusive, one), maxValue);
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

        N overallMin = type.min(one.min, two.min);
        N overallMax = type.max(one.max, two.max);

        if (one.step == null && two.step == null)
            return createRange(overallMin, overallMax);

        // TODO : We can do better for ranges with steps
        // Or if we're given two single values, we can create
        // a range with a step of the difference between the two values.

        N step = type.gcd(one.stepOr1, two.stepOr1);

        return createRange(overallMin, overallMax);
    }

    @Override
    public R intersection(R one, R two) {
        if (one.isEmpty() || two.isEmpty() || type.gt(one.min, two.max) || type.gt(two.min, one.max))
            return empty();

        // Doesn't take step into account
        N naiveMin = type.max(one.min, two.min);
        N naiveMax = type.min(one.max, two.max);

        if (one.step == null && two.step == null)
            return createRange(naiveMin, naiveMax);

        // Check that the two steps are compatible
        N gcd = type.gcd(one.stepOr1, two.stepOr1);
        N oneAdvantage = type.subtract(one.min, two.min);
        if (!type.isMultiple(oneAdvantage, gcd))
            return empty();

        // Find the new step for the resultant set
        N newStep = type.lcm(one.stepOr1, two.stepOr1);
        if (type.lte(newStep, zero)) {
            // The LCM overflowed
            return createRange(naiveMin, naiveMax);
        }

        // The smallest number above two.min that is potentially included in both one and two
        N offsetLCM = type.offsetLCM(one.stepOr1, two.stepOr1, oneAdvantage);
        if (type.lt(offsetLCM, zero)) {
            // The offsetLCM Overflowed
            return createRange(naiveMin, naiveMax);
        }

        // Find the smallest number that is included in both sets
        N newMin = type.add(two.min, offsetLCM);
        if (type.gte(two.min, zero) && type.lte(newMin, zero)) {
            // Add overflowed
            return createRange(naiveMin, naiveMax);
        }

        N newLength = type.div(type.subtract(naiveMax, newMin), newStep);
        if (type.lte(newLength, zero))
            return empty();

        N newMax = type.add(newMin, type.mul(newLength, newStep));
        return createRange(newMin, newMax, newStep);
    }

    @Override
    public boolean isSubset(R one, R two) {
        // Out of bounds check
        if (type.lt(one.min, two.min))
            return false;
        if (type.gt(one.max, two.max))
            return false;

        // Incompatible step check
        if (!type.eq(zero, type.positiveRemainder(one.stepOr1, two.stepOr1)))
            return false;

        // Check that steps are not offset from one another
        N oneMod = type.signedRemainder(one.min, two.stepOr1);
        N twoMod = type.signedRemainder(two.min, two.stepOr1);
        return type.eq(oneMod, twoMod);
    }

    @Override
    public R negate(R range) {
        N newMin = type.negate(range.max);
        N newMax = type.negate(range.min);

        // Check for overflow
        if (type.compare(newMin, zero) != type.compare(zero, range.max))
            return all();
        if (type.compare(newMax, zero) != type.compare(zero, range.min))
            return all();

        return createRange(newMin, newMax, range.stepOr1);
    }

    @Override
    public R shift(R range, N shift) {
        if (range.isEmpty() || type.eq(shift, zero))
            return range;

        // Check for overflow
        if (type.gt(shift, zero)) {
            N maxNoOverflow = type.subtract(maxValue, shift);

            // We don't mind if both min and max overflow
            if (type.gt(range.max, maxNoOverflow) && !type.gt(range.min, maxNoOverflow))
                return all();
        } else {
            N minNoOverflow = type.subtract(minValue, shift);

            // We don't mind if both min and max overflow
            if (type.lt(range.min, minNoOverflow) && !type.lt(range.max, minNoOverflow))
                return all();
        }

        return createRange(type.add(range.min, shift), type.add(range.max, shift), range.stepOr1);
    }

    @Override
    public R scale(R range, N scale) {
        // Special case of 0
        if (type.eq(scale, zero))
            return single(zero);

        // Canonicalize to a positive scale
        if (type.lt(scale, zero)) {
            scale = type.subtract(zero, scale);
            range = negate(range);

            // Check for overflow
            if (type.lt(scale, zero))  {
                if (!type.eq(scale, type.getMinValue()))
                    throw new IllegalStateException("negation overflow while scale not equal to the minimum value");

                return union(single(type.getMinValue()), single(zero));
            }
        }

        // TODO : If scale is a power of 2, we could just do a bit shift even if there is overflow.

        // Check for overflow
        N noOverflowMin = type.div(type.getMinValue(), scale);
        N noOverflowMax = type.div(type.getMaxValue(), scale);
        if (type.lt(range.min, noOverflowMin) || type.gt(range.max, noOverflowMax))
            return all();
        if (!type.inRange(range.stepOr1, noOverflowMin, noOverflowMax))
            return all();

        // Perform scaling
        N newMin = type.mul(range.min, scale);
        N newMax = type.mul(range.max, scale);
        N newStep = type.mul(range.stepOr1, scale);
        return createRange(newMin, newMax, newStep);
    }

    @Override
    public R unscale(R range, N scale) {
        // If the min, max, and step aren't all a multiple of range, there's not much we can do...
        if (!type.eq(zero, type.positiveRemainder(range.stepOr1, scale)))
            return all();

        // Inverse scaling
        N newMin = type.div(range.min, scale);
        N newMax = type.div(range.max, scale);
        N newStep = type.div(range.stepOr1, scale);
        return createRange(newMin, newMax, newStep);
    }
}
