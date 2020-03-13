package net.paddyl.constraints;

import net.paddyl.constraints.set.*;
import net.paddyl.util.NumberType;
import net.paddyl.util.NumberTypes;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Base class for tests that require a NumberRangeFactory.
 */
@RunWith(Parameterized.class)
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

    public ValueSet of(Number from, Number to, Number step) {
        return factory.steppedRange(coerce(from), coerce(to), coerce(step));
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

    public ConstOperator<?, ?> chain(ConstOperator... operators) {
        return factory.chain(operators);
    }

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> getTestParameters() {
        return Arrays.asList(new Object[][] {
                { "LongRange", new LongRange.LongRangeFactory() },
                { "IntRange", new IntRange.IntRangeFactory() },
                { "ShortRange", new ShortRange.ShortRangeFactory() },
                { "ByteRange", new ByteRange.ByteRangeFactory() }
        });
    }
}
