package net.paddyl.constraints;

import net.paddyl.constraints.set.ValueSet;

/**
 * A constraint that takes a single input.
 */
public interface ConstConstraint<S extends ValueSet<S, V>, V> {

    public S tryReduce(S range);

    /**
     * Repeatedly try to reduce {@param range} until it no longer changes.
     */
    public default S bruteReduce(S range) {
        S last;
        do {
            last = range;
            range = tryReduce(last);
        } while (!last.equals(range));

        return range;
    }
}
