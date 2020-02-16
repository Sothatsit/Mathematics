package net.paddyl.constraints.set;

import net.paddyl.constraints.*;

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
     * @return a set including only {@param value}.
     */
    public S single(V value) {
        return range(value, value);
    }

    /**
     * @return a set including all values from {@param from} to {@param to} inclusive.
     */
    public abstract S range(V from, V to);

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
     * @return {@param set} shifted by {@param shift}.
     */
    public abstract S shift(S set, V shift);

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
}
