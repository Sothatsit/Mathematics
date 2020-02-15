package net.paddyl.constraints;

/**
 * Combined multiple ConstConstraints
 */
public abstract class CompoundConstConstraint implements ConstConstraint {

    protected final ConstConstraint[] constraints;

    public CompoundConstConstraint(ConstConstraint... constraints) {
        if (constraints.length == 0)
            throw new IllegalArgumentException("Must supply at least one constraint");

        this.constraints = constraints;
    }

    /**
     * The and, &&, operator.
     */
    public static class AndConstConstraint extends CompoundConstConstraint {

        public AndConstConstraint(ConstConstraint... constraints) {
            super(constraints);
        }

        @Override
        public LongRange tryReduce(LongRange range) {
            for (ConstConstraint constraint : constraints) {
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
    public static class OrConstConstraint extends CompoundConstConstraint {

        public OrConstConstraint(ConstConstraint... constraints) {
            super(constraints);
        }

        @Override
        public LongRange tryReduce(LongRange range) {
            LongRange result = LongRange.EMPTY;
            for (ConstConstraint constraint : constraints) {
                result = result.union(constraint.tryReduce(range));
            }
            return result;
        }

        @Override
        public String toString() {
            return "(" + join(" || ", constraints) + ")";
        }
    }

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
