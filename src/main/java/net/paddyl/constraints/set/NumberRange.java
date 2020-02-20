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

    private final N minMaxModStep;

    public NumberRange(NumberType<N> type, N min, N max, N step) {
        if (step != null) {
            Checks.assertGreaterThan(step, "step", 0, "zero");
        }

        // Canonicalize the empty range
        if (type.ltImpl(max, min)) {
            min = type.coerce(0);
            max = type.coerce(-1);
            step = null;
        }

        // Canonicalize step to null when it is 1 or irrelevant
        if (step != null && (type.eq(step, 1) || type.eqImpl(min, max))) {
            step = null;
        }

        // Make sure max - min is a multiple of step
        if (step != null) {
            N maxMod = type.remainderImpl(max, step);
            N minMod = type.remainderImpl(min, step);
            N modDiff = type.subtractImpl(maxMod, minMod);
            N mod = type.moduloImpl(modDiff, step);
            max = type.subtractImpl(max, mod);

            // Check that it makes sense
            maxMod = type.remainderImpl(max, step);
            Checks.assertThat(type.eqImpl(minMod, maxMod), "minMod (" + minMod + ") != maxMod (" + maxMod + ")");
        }

        this.type = type;
        this.min = min;
        this.max = max;
        this.step = step;

        this.minMaxModStep = (step != null ? type.moduloImpl(max, step) : type.coerce(0));
    }

    @Override
    public boolean isEmpty() {
        return type.ltImpl(max, min);
    }

    @Override
    public boolean contains(N value) {
        if (type.ltImpl(value, min) || type.gtImpl(value, max))
            return false;
        if (step == null)
            return true;

        N valueModStep = type.moduloImpl(value, step);
        return type.eqImpl(minMaxModStep, valueModStep);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !getClass().equals(obj.getClass()))
            return false;

        NumberRange other = (NumberRange) obj;
        return type.eq(min, other.min) && type.eq(max, other.max) && type.eq(step, other.step);
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
        if (type.eqImpl(min, max))
            return className + "{" + min + "}";

        boolean minLowest = type.eqImpl(min, type.getMinValue());
        boolean maxHighest = type.eqImpl(max, type.getMaxValue());
        String stepStr = (step == null ? "" : " (every " + step + ")");
        if (minLowest && maxHighest)
            return className + "{ALL" + stepStr + "}";
        if (minLowest)
            return className + "{ -> " + max + stepStr + "}";
        if (maxHighest)
            return className + "{" + min + " -> " + stepStr + "}";

        return className + "{" + min + " -> " + max + stepStr + "}";
    }
}
