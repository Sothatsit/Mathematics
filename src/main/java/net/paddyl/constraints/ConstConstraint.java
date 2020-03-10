package net.paddyl.constraints;

import net.paddyl.constraints.set.ValueSet;

/**
 * A constraint that takes a single input.
 */
public interface ConstConstraint<S extends ValueSet<S, V>, V> {

    /**
     * Attempts to reduce the range of possible values {@param set} by
     * removing any entries from the set that do not match this constraint.
     *
     * Should _never_ return a set with added entries. The returned set
     * should always be a subset of the input {@param set}.
     */
    public S tryReduce(S set);

    /**
     * Repeatedly try to reduce {@param range} until it no longer changes.
     */
    public default S bruteReduce(S set) {
        S last;
        do {
            last = set;
            set = tryReduce(last);
        } while (!last.equals(set));

        return set;
    }
}
