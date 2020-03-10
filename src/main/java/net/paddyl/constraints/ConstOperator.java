package net.paddyl.constraints;

import net.paddyl.constraints.set.ValueSet;

/**
 * An operation on a single range.
 */
public interface ConstOperator<S extends ValueSet<S, V>, V> {

    /**
     * Returns the set of all possible values after applying
     * the operation to the values contained within {@param set}.
     */
    public S forward(S range);

    /**
     * Returns the set of all possible input values
     * that could produce a value from {@param set}.
     *
     * The result of {@code backward(forward(set))} should _always_
     * contain all of the values of the original set.
     *
     * New values can be added (due to loss of information in domain change),
     * but values should never be lost.
     */
    public S backward(S set);
}
