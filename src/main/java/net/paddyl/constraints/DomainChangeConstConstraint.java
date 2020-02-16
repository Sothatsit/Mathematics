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
        S result = operator.backward(constraint.tryReduce(operator.forward(range)));
        return factory.intersection(range, result);
    }
}
