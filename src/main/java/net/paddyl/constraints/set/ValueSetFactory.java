package net.paddyl.constraints.set;

import net.paddyl.constraints.*;
import net.paddyl.util.Cast;

import java.lang.reflect.Array;

/**
 * A factory for constructing and manipulating ValueSets.
 */
public abstract class ValueSetFactory<S extends ValueSet<S, V>, V> {

    public final Class<S> setClass;
    public final Class<V> valueClass;

    public ValueSetFactory(Class<S> setClass, Class<V> valueClass) {
        this.setClass = setClass;
        this.valueClass = valueClass;
    }

    public V[] newValueArray(int length) {
        @SuppressWarnings("unchecked")
        V[] array = (V[]) Array.newInstance(valueClass, length);
        return array;
    }

    public S[] newSetArray(int length) {
        @SuppressWarnings("unchecked")
        S[] array = (S[]) Array.newInstance(setClass, length);
        return array;
    }

    public V[] newValueArray(Object... values) {
        V[] array = newValueArray(values.length);
        for (int index = 0; index < array.length; ++index) {
            array[index] = valueClass.cast(values[index]);
        }
        return array;
    }

    public S[] newSetArray(Object... values) {
        S[] array = newSetArray(values.length);
        for (int index = 0; index < array.length; ++index) {
            array[index] = setClass.cast(values[index]);
        }
        return array;
    }

    /**
     * @return the empty set.
     */
    public abstract S empty();

    /**
     * @return a set containing all possible values.
     */
    public abstract S all();

    /**
     * @return a set including only {@param value}.
     */
    public S single(V value) {
        return range(value, value);
    }

    /**
     * @return a set including all values from {@param from} to {@param to} inclusive.
     */
    public S range(V from, V to) {
        return steppedRange(from, to, null);
    }

    /**
     * If {@param step} is null, it indicates no step is to be used.
     *
     * @return a set including every {@param step}'th value from {@param from} to {@param to} inclusive.
     */
    public abstract S steppedRange(V from, V to, V step);

    /**
     * @return a set including all values less than {@param maxExclusive}.
     */
    public abstract S below(V maxExclusive);

    /**
     * @return a set including all values less than or equal to {@param max}.
     */
    public abstract S belowIncl(V max);

    /**
     * @return a set including all values greater than {@param minExclusive}.
     */
    public abstract S above(V minExclusive);

    /**
     * @return a set including all values greater than or equal to {@param min}.
     */
    public abstract S aboveIncl(V min);

    /**
     * @return a set containing the values from {@param one} and {@param two}.
     */
    public abstract S union(S one, S two);

    /**
     * @return a set containing the values that are contained in both {@param one} and {@param two}.
     */
    public abstract S intersection(S one, S two);

    /**
     * @return whether {@param one} is a subset of {@param two}.
     */
    public abstract boolean isSubset(S one, S two);

    /**
     * @return {@param set} shifted by {@param shift}.
     */
    public abstract S add(S set, V shift);

    /**
     * @return the negation of all values in {@param set}
     */
    public abstract S negate(S set);

    /**
     * @return {@param set} multiplied by {@param multiply}.
     */
    public abstract S multiply(S set, V multiplicand);

    /**
     * Performs the inverse operation to {@link #multiply(ValueSet, Object)}.
     *
     * @return the possible set of values that could have resulted in {@param set} when multiplied by {@param multiplicand}.
     */
    public abstract S reverseMultiply(S set, V multiplicand);

    /**
     * @return {@param set} divided by {@param divisor}.
     */
    public abstract S divide(S set, V divisor);

    /**
     * Performs the inverse operation to {@link #divide(ValueSet, Object)}.
     *
     * @return the possible set of values that could have resulted in {@param set} when divided by {@param divisor}.
     */
    public abstract S reverseDivide(S set, V divisor);

    /**
     * Greater than equals. range >= value.
     */
    public ConstConstraint<S, V> gte(V value) {
        return new ComparisonConstConstraint.GTEConstConstraint<>(this, value);
    }

    /**
     * Greater than. range > value.
     */
    public ConstConstraint<S, V> gt(V value) {
        return new ComparisonConstConstraint.GTConstConstraint<>(this, value);
    }

    /**
     * Less than equals. range <= value.
     */
    public ConstConstraint<S, V> lte(V value) {
        return new ComparisonConstConstraint.LTEConstConstraint<>(this, value);
    }

    /**
     * Less than. range < value.
     */
    public ConstConstraint<S, V> lt(V value) {
        return new ComparisonConstConstraint.LTConstConstraint<>(this, value);
    }

    /**
     * Equals. range == value.
     */
    public ConstConstraint<S, V> eq(V value) {
        return new ComparisonConstConstraint.EqualsConstConstraint<>(this, value);
    }

    /**
     * And. constraint1 && constraint2 && ... && constraintN.
     */
    public ConstConstraint<S, V> and(ConstConstraint<S, V>[] constraints) {
        return new CompoundConstConstraint.AndConstConstraint<>(this, constraints);
    }

    /**
     * Or. constraint1 || constraint2 || ... || constraintN.
     */
    public ConstConstraint<S, V> or(ConstConstraint<S, V>[] constraints) {
        return new CompoundConstConstraint.OrConstConstraint<>(this, constraints);
    }

    /**
     * Change the domain of the input to the constraint.
     */
    public ConstConstraint<S, V> domain(ConstOperator<S, V> operator, ConstConstraint<S, V> constraint) {
        return new DomainChangeConstConstraint<>(this, operator, constraint);
    }

    /**
     * @return an operator that adds {@param value}.
     */
    public ConstOperator<S, V> add(V value) {
        return new ArithmeticConstOperator.AddConstOperator<>(this, value);
    }

    /**
     * @return an operator that multiples by {@param value}.
     */
    public ConstOperator<S, V> mul(V value) {
        return new ArithmeticConstOperator.MulConstOperator<>(this, value);
    }

    /**
     * @return an operator that divides by {@param value}.
     */
    public ConstOperator<S, V> div(V value) {
        return new ArithmeticConstOperator.DivConstOperator<>(this, value);
    }

    /**
     * All of {@param operators} must be parameterized in the same way as this ValueSetFactory.
     *
     * @return an operator that applies each of {@param operators} in order.
     */
    public ConstOperator<S, V> chain(ConstOperator... operators) {
        ConstOperator<S, V>[] castOperators = Cast.cast(operators);
        return new ChainedConstOperator<>(castOperators);
    }
}
