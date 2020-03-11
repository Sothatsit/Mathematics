package net.paddyl.constraints;

import net.paddyl.constraints.set.NumberRangeFactory;
import net.paddyl.constraints.set.ValueSet;
import net.paddyl.constraints.set.ValueSetFactory;
import net.paddyl.util.NumberType;

/**
 * Base class for tests that require a NumberRangeFactory.
 */
@SuppressWarnings("unchecked")
public class NumberRangeTestBase {

    protected final NumberType numberType;
    protected final Number MAX;
    protected final Number MAX_M1;
    protected final Number MIN;
    protected final Number MIN_P1;

    protected final String numberTypeName;
    protected final ValueSetFactory factory;
    protected final ValueSet ALL;
    protected final ValueSet EMPTY;

    public NumberRangeTestBase(String numberTypeName, NumberRangeFactory factory) {
        this.numberTypeName = numberTypeName;
        this.numberType = factory.type;
        this.MAX = numberType.maxValue;
        this.MIN = numberType.minValue;
        this.MIN_P1 = numberType.add(MIN, numberType.one);
        this.MAX_M1 = numberType.add(MAX, numberType.coerce(-1));

        this.factory = factory;
        this.ALL = factory.all();
        this.EMPTY = factory.empty();
    }

    public Number coerce(Number value) {
        return numberType.coerce(value);
    }

    public ValueSet single(Number value) {
        return factory.single(coerce(value));
    }

    public ValueSet of(Number from, Number to) {
        return factory.range(coerce(from), coerce(to));
    }

    public ValueSet above(Number value) {
        return factory.aboveIncl(coerce(value));
    }

    public ValueSet aboveNotIncl(Number value) {
        return factory.above(coerce(value));
    }

    public ValueSet below(Number value) {
        return factory.belowIncl(coerce(value));
    }

    public ValueSet belowNotIncl(Number value) {
        return factory.below(coerce(value));
    }

    public ConstConstraint gte(Number value) {
        return factory.gte(coerce(value));
    }

    public ConstConstraint gt(Number value) {
        return factory.gt(coerce(value));
    }

    public ConstConstraint lte(Number value) {
        return factory.lte(coerce(value));
    }

    public ConstConstraint lt(Number value) {
        return factory.lt(coerce(value));
    }

    public ConstConstraint eq(Number value) {
        return factory.eq(coerce(value));
    }

    public final ConstConstraint and(ConstConstraint... constraints) {
        return factory.and(constraints);
    }

    public final ConstConstraint or(ConstConstraint... constraints) {
        return factory.or(constraints);
    }

    public ConstConstraint<?, ?> domain(
            ConstOperator<?, ?> operator,
            ConstConstraint<?, ?> constraint) {

        return factory.domain(operator, constraint);
    }

    public ConstOperator<?, ?> add(Number value) {
        return factory.add(coerce(value));
    }

    public ConstOperator<?, ?> mul(Number value) {
        return factory.mul(coerce(value));
    }

    public ConstOperator<?, ?> div(Number value) {
        return factory.div(coerce(value));
    }
}
