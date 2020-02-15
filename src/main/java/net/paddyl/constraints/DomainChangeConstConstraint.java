package net.paddyl.constraints;

/**
 * A constraint that requires an input in a different domain.
 * e.g. x + 1 == 2
 */
public class DomainChangeConstConstraint implements ConstConstraint {

    private final ConstOperator operator;
    private final ConstConstraint constraint;

    public DomainChangeConstConstraint(ConstOperator operator, ConstConstraint constraint) {
        this.operator = operator;
        this.constraint = constraint;
    }

    @Override
    public LongRange tryReduce(LongRange range) {
        range = operator.forward(range);
        LongRange result = constraint.tryReduce(range);
        return operator.backward(result);
    }
}
