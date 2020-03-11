package net.paddyl.constraints.set;

import net.paddyl.util.Checks;
import net.paddyl.util.NumberType;

import java.util.Objects;

/**
 * Inclusive range of numbers.
 */
public abstract class NumberRange<R extends NumberRange<R, N>, N extends Number> implements ValueSet<R, N> {

    public final NumberType<N> type;
    public final N min;
    public final N max;
    public final N step;
    public final N stepOr1;

    private final N minMaxModStep;

    public NumberRange(NumberType<N> type, N min, N max, N step) {
        if (step != null) {
            Checks.assertGreaterThan(step, "step", 0, "zero");
        }

        // Canonicalize the empty range
        if (type.lt(max, min)) {
            min = type.coerce(0);
            max = type.coerce(-1);
            step = null;
        }

        // Canonicalize step to null when it is 1 or irrelevant
        if (step != null && (type.eq(step, type.one) || type.eq(min, max))) {
            step = null;
        }

        // Make sure max - min is a multiple of step
        if (step != null) {
            N maxMod = type.signedRemainder(max, step);
            N minMod = type.signedRemainder(min, step);
            N modDiff = type.subtract(maxMod, minMod);
            N mod = type.positiveRemainder(modDiff, step);
            max = type.subtract(max, mod);

            // Check that it makes sense
            maxMod = type.signedRemainder(max, step);
            Checks.assertThat(type.eq(minMod, maxMod), "minMod (" + minMod + ") != maxMod (" + maxMod + ")");
        }

        this.type = type;
        this.min = min;
        this.max = max;
        this.step = step;
        this.stepOr1 = (step == null ? type.one : step);

        this.minMaxModStep = (step != null ? type.positiveRemainder(max, step) : type.coerce(0));
    }

    @Override
    public boolean isEmpty() {
        return type.lt(max, min);
    }

    @Override
    public boolean isSingleValue() {
        return type.eq(min, max);
    }

    @Override
    public boolean contains(N value) {
        if (type.lt(value, min) || type.gt(value, max))
            return false;
        if (step == null)
            return true;

        N valueModStep = type.positiveRemainder(value, step);
        return type.eq(minMaxModStep, valueModStep);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !getClass().equals(obj.getClass()))
            return false;

        NumberRange other = (NumberRange) obj;
        return Objects.equals(min, other.min)
                && Objects.equals(max, other.max)
                && Objects.equals(step, other.step);
    }

    @Override
    public int hashCode() {
        return Objects.hash(min, max, step);
    }

    @Override
    public String toString() {
        String className = getClass().getSimpleName();

        if (isEmpty())
            return className + "{EMPTY}";
        if (type.eq(min, max))
            return className + "{" + min + "}";

        boolean minLowest = type.eq(min, type.minValue);
        boolean maxHighest = type.eq(max, type.maxValue);
        String stepStr = (step == null ? "" : " (step " + step + ")");
        if (minLowest && maxHighest)
            return className + "{ALL" + stepStr + "}";
        if (minLowest)
            return className + "{ -> " + max + stepStr + "}";
        if (maxHighest)
            return className + "{" + min + " -> " + stepStr + "}";

        return className + "{" + min + " -> " + max + stepStr + "}";
    }
}
