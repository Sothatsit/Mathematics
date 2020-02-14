package net.paddyl.constraints;

/**
 * Combined multiple ConstConstraints
 */
public class CompoundConstConstraint implements ConstConstraint {

    private final ConstConstraint[] constraints;

    public CompoundConstConstraint(ConstConstraint... constraints) {
        this.constraints = constraints;
    }

    @Override
    public LongRange tryReduce(LongRange range) {
        for (ConstConstraint constraint : constraints) {
            range = constraint.tryReduce(range);
        }
        return range;
    }
}
