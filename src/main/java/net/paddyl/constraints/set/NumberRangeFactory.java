package net.paddyl.constraints.set;

import net.paddyl.util.Checks;
import net.paddyl.util.NumberType;

/**
 * A factory for constructing and manipulating NumberRanges.
 */
public abstract class NumberRangeFactory<R extends NumberRange<R, N>, N extends Number> extends ValueSetFactory<R, N> {

    public final NumberType<N> type;

    public NumberRangeFactory(NumberType<N> type, Class<R> rangeClass) {
        super(rangeClass, type.boxClass);

        Checks.assertThat(type.isBounded, "Only bounded NumberTypes are supported");

        this.type = type;
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
        if (type.eq(maxExclusive, type.minValue))
            return empty();
        return createRange(type.minValue, type.subtract(maxExclusive, type.one));
    }

    @Override
    public R belowIncl(N max) {
        return createRange(type.minValue, max);
    }

    @Override
    public R above(N minExclusive) {
        if (type.eq(minExclusive, type.maxValue))
            return empty();
        return createRange(type.add(minExclusive, type.one), type.maxValue);
    }

    @Override
    public R aboveIncl(N min) {
        return createRange(min, type.maxValue);
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
        if (type.lte(newStep, type.zero)) {
            // The LCM overflowed
            return createRange(naiveMin, naiveMax);
        }

        // The smallest number above two.min that is potentially included in both one and two
        N offsetLCM = type.offsetLCM(one.stepOr1, two.stepOr1, oneAdvantage);
        if (type.lt(offsetLCM, type.zero)) {
            // The offsetLCM Overflowed
            return createRange(naiveMin, naiveMax);
        }

        // Find the smallest number that is included in both sets
        N newMin = type.add(two.min, offsetLCM);
        if (type.gte(two.min, type.zero) && type.lte(newMin, type.zero)) {
            // Add overflowed
            return createRange(naiveMin, naiveMax);
        }

        N newLength = type.div(type.subtract(naiveMax, newMin), newStep);
        if (type.lte(newLength, type.zero))
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
        if (!type.eq(type.zero, type.positiveRemainder(one.stepOr1, two.stepOr1)))
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
        if (type.compare(newMin, type.zero) != type.compare(type.zero, range.max))
            return all();
        if (type.compare(newMax, type.zero) != type.compare(type.zero, range.min))
            return all();

        return createRange(newMin, newMax, range.stepOr1);
    }

    @Override
    public R add(R range, N shift) {
        if (range.isEmpty() || type.eq(shift, type.zero))
            return range;

        // Check for overflow
        if (type.gt(shift, type.zero)) {
            N maxNoOverflow = type.subtract(type.maxValue, shift);

            // We don't mind if both min and max overflow
            if (type.gt(range.max, maxNoOverflow) && !type.gt(range.min, maxNoOverflow))
                return all();
        } else {
            N minNoOverflow = type.subtract(type.minValue, shift);

            // We don't mind if both min and max overflow
            if (type.lt(range.min, minNoOverflow) && !type.lt(range.max, minNoOverflow))
                return all();
        }

        return createRange(type.add(range.min, shift), type.add(range.max, shift), range.stepOr1);
    }

    @Override
    public R multiply(R range, N multiplicand) {
        // Special case of 0
        if (type.eq(multiplicand, type.zero))
            return single(type.zero);

        // Canonicalize to a positive multiplicand
        if (type.lt(multiplicand, type.zero)) {
            multiplicand = type.negate(multiplicand);
            range = negate(range);

            // Check for overflow
            if (type.lt(multiplicand, type.zero))  {
                if (!type.eq(multiplicand, type.minValue))
                    throw new IllegalStateException("negation overflow while multiplicand not equal to the minimum value");

                return union(single(type.minValue), single(type.zero));
            }
        }

        // TODO : If multiplicand is a power of 2, we could just do a bit shift even if there is overflow.

        // Check for overflow
        if (type.isBounded) {
            N noOverflowMin = type.div(type.minValue, multiplicand);
            N noOverflowMax = type.div(type.maxValue, multiplicand);
            if (type.lt(range.min, noOverflowMin) || type.gt(range.max, noOverflowMax))
                return all();
            if (!type.inRange(range.stepOr1, noOverflowMin, noOverflowMax))
                return all();
        }

        // Perform scaling
        N newMin = type.mul(range.min, multiplicand);
        N newMax = type.mul(range.max, multiplicand);
        N newStep = type.mul(range.stepOr1, multiplicand);
        return createRange(newMin, newMax, newStep);
    }

    @Override
    public R reverseMultiply(R range, N multiplicand) {
        // If the min, max, and step aren't all a multiple of range, there's not much we can do...
        if (!type.eq(type.zero, type.positiveRemainder(range.stepOr1, multiplicand)))
            return all();

        // Inverse scaling
        N newMin = type.div(range.min, multiplicand);
        N newMax = type.div(range.max, multiplicand);
        N newStep = type.div(range.stepOr1, multiplicand);
        return createRange(newMin, newMax, newStep);
    }

    @Override
    public R divide(R range, N divisor) {
        // Canonicalize to a positive divisor
        if (type.lt(divisor, type.zero)) {
            divisor = type.negate(divisor);
            range = negate(range);

            // Check for overflow
            if (type.lt(divisor, type.zero))  {
                if (type.minValue == null || !type.eq(divisor, type.minValue))
                    throw new IllegalStateException("negation overflow while divisor not equal to the minimum value");

                return range.contains(type.minValue) ? createRange(type.zero, type.one) : single(type.zero);
            }
        }

        N newMin = type.div(range.min, divisor);
        N newMax = type.div(range.max, divisor);

        N newStep;
        if (type.isMultiple(range.stepOr1, divisor)) {
            newStep = type.div(range.stepOr1, divisor);
        } else {
            newStep = type.one;
        }

        return createRange(newMin, newMax, newStep);
    }

    @Override
    public R reverseDivide(R range, N divisor) {
        // Canonicalize to a positive divisor
        if (type.lt(divisor, type.zero)) {
            divisor = type.negate(divisor);
            range = negate(range);

            // Check for overflow
            if (type.lt(divisor, type.zero))  {
                if (type.minValue == null || !type.eq(divisor, type.minValue))
                    throw new IllegalStateException("negation overflow while divisor not equal to the minimum value");

                return range.contains(type.minValue) ? createRange(type.zero, type.one) : single(type.zero);
            }
        }

        // Check for overflow with min/max
        if (type.isBounded) {
            N noOverflowMin = type.div(type.minValue, divisor);
            N noOverflowMax = type.div(type.maxValue, divisor);
            if (type.lt(range.min, noOverflowMin) || type.gt(range.max, noOverflowMax))
                return all();
            if (!type.inRange(range.stepOr1, noOverflowMin, noOverflowMax))
                return all();
        }

        N newMin = type.mul(range.min, divisor);
        N newMax = type.mul(range.max, divisor);

        // Calculate if there was an original step to uphold
        N newStep;
        if (range.step != null) {
            newStep = type.mul(range.step, divisor);
        } else {
            newStep = type.one;
        }

        // Extend min/max to include values that would round down to range.min or range.max
        if (range.step == null) {
            N extension = type.subtract(divisor, type.one);

            // Extend min
            if (type.lt(newMin, type.zero)) {
                N extendedMin = type.subtract(newMin, extension);
                if (type.gt(extendedMin, type.zero)) {
                    extendedMin = type.minValue;
                }
                newMin = extendedMin;
            }

            // Extend max
            if (type.gt(newMax, type.zero)) {
                N extendedMax = type.add(newMax, extension);
                if (type.lt(extendedMax, type.zero)) {
                    extendedMax = type.maxValue;
                }
                newMax = extendedMax;
            }
        }

        return createRange(newMin, newMax, newStep);
    }
}
