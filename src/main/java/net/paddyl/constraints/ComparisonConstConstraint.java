package net.paddyl.constraints;

import net.paddyl.constraints.set.ValueSet;
import net.paddyl.constraints.set.ValueSetFactory;

/**
 * Contains the basic comparison with constant constraints.
 */
public class ComparisonConstConstraint<S extends ValueSet<S, V>, V> implements ConstConstraint<S, V> {

    protected final ValueSetFactory<S, V> factory;
    protected final V value;
    protected final S validRange;

    private ComparisonConstConstraint(ValueSetFactory<S, V> factory, V value, S validRange) {
        this.factory = factory;
        this.value = value;
        this.validRange = validRange;
    }

    @Override
    public S tryReduce(S range) {
        return factory.intersection(range, validRange);
    }

    /**
     * range >= minValue.
     */
    public static class GTEConstConstraint<S extends ValueSet<S, V>, V> extends ComparisonConstConstraint<S, V> {

        public GTEConstConstraint(ValueSetFactory<S, V> factory, V minValue) {
            super(factory, minValue, factory.aboveIncl(minValue));
        }

        @Override
        public String toString() {
            return ">= " + value;
        }
    }

    /**
     * range > minValue.
     */
    public static class GTConstConstraint<S extends ValueSet<S, V>, V> extends ComparisonConstConstraint<S, V> {

        public GTConstConstraint(ValueSetFactory<S, V> factory, V minValue) {
            super(factory, minValue, factory.above(minValue));
        }

        @Override
        public String toString() {
            return "> " + value;
        }
    }

    /**
     * range <= maxValue.
     */
    public static class LTEConstConstraint<S extends ValueSet<S, V>, V> extends ComparisonConstConstraint<S, V> {

        public LTEConstConstraint(ValueSetFactory<S, V> factory, V maxValue) {
            super(factory, maxValue, factory.belowIncl(maxValue));
        }

        @Override
        public String toString() {
            return "<= " + value;
        }
    }

    /**
     * range < maxValue.
     */
    public static class LTConstConstraint<S extends ValueSet<S, V>, V> extends ComparisonConstConstraint<S, V> {

        public LTConstConstraint(ValueSetFactory<S, V> factory, V maxValue) {
            super(factory, maxValue, factory.below(maxValue));
        }

        @Override
        public String toString() {
            return "< " + value;
        }
    }

    /**
     * range == value.
     */
    public static class EqualsConstConstraint<S extends ValueSet<S, V>, V> extends ComparisonConstConstraint<S, V> {

        public EqualsConstConstraint(ValueSetFactory<S, V> factory, V value) {
            super(factory, value, factory.single(value));
        }

        @Override
        public String toString() {
            return "== " + value;
        }
    }
}
