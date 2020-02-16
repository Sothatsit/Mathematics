package net.paddyl.constraints;

import net.paddyl.constraints.set.ValueSet;
import net.paddyl.constraints.set.ValueSetFactory;

/**
 * Combined multiple ConstConstraints
 */
public abstract class CompoundConstConstraint<S extends ValueSet<S, V>, V> implements ConstConstraint<S, V> {

    protected final ValueSetFactory<S, V> factory;
    protected final ConstConstraint<S, V>[] constraints;

    public CompoundConstConstraint(ValueSetFactory<S, V> factory, ConstConstraint<S, V>[] constraints) {
        if (constraints.length == 0)
            throw new IllegalArgumentException("Must supply at least one constraint");

        this.factory = factory;
        this.constraints = constraints;
    }

    /**
     * The and, &&, operator.
     */
    public static class AndConstConstraint<S extends ValueSet<S, V>, V> extends CompoundConstConstraint<S, V> {

        public AndConstConstraint(ValueSetFactory<S, V> factory, ConstConstraint<S, V>[] constraints) {
            super(factory, constraints);
        }

        @Override
        public S tryReduce(S range) {
            for (ConstConstraint<S, V> constraint : constraints) {
                range = constraint.tryReduce(range);
            }
            return range;
        }

        @Override
        public String toString() {
            return "(" + join(" && ", constraints) + ")";
        }
    }

    /**
     * The or, ||, operator.
     */
    public static class OrConstConstraint<S extends ValueSet<S, V>, V> extends CompoundConstConstraint<S, V> {

        public OrConstConstraint(ValueSetFactory<S, V> factory, ConstConstraint<S, V>[] constraints) {
            super(factory, constraints);
        }

        @Override
        public S tryReduce(S range) {
            S result = factory.empty();
            for (ConstConstraint<S, V> constraint : constraints) {
                result = factory.union(result, constraint.tryReduce(range));
            }
            return result;
        }

        @Override
        public String toString() {
            return "(" + join(" || ", constraints) + ")";
        }
    }

    @SafeVarargs
    private static <T> String join(String delimiter, T... values) {
        StringBuilder builder = new StringBuilder("(");
        for (int index = 0; index < values.length; ++index) {
            if (index > 0) {
                builder.append(delimiter);
            }
            builder.append(values[index]);
        }
        return builder.toString();
    }
}
