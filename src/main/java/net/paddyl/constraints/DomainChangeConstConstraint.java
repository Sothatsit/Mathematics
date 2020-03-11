package net.paddyl.constraints;

import net.paddyl.constraints.set.ValueSet;
import net.paddyl.constraints.set.ValueSetFactory;

/**
 * A constraint that requires an input in a different domain.
 * e.g. x + 1 == 2
 */
public class DomainChangeConstConstraint<S extends ValueSet<S, V>, V> implements ConstConstraint<S, V> {

    private final ValueSetFactory<S, V> factory;
    private final ConstOperator<S, V> operator;
    private final ConstConstraint<S, V> constraint;

    public DomainChangeConstConstraint(
            ValueSetFactory<S, V> factory,
            ConstOperator<S, V> operator,
            ConstConstraint<S, V> constraint) {

        this.factory = factory;
        this.operator = operator;
        this.constraint = constraint;
    }

    @Override
    public S tryReduce(S range) {
        // Apply the constraint in the operator domain
        S result = operator.backward(constraint.tryReduce(operator.forward(range)));

        // The forwards and backwards of an operation can lose information,
        // but we know it should never increase the bounds of the range.
        // Therefore, we take the intersection of the original range and
        // the result to ensure this constraint.
        return factory.intersection(range, result);
    }
}
