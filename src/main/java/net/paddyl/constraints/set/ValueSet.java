package net.paddyl.constraints.set;

/**
 * Represents a set of values.
 */
public interface ValueSet<S extends ValueSet, V> {

    /**
     * @return whether this set contains no values.
     */
    public boolean isEmpty();

    /**
     * @return whether this set contains {@param value}.
     */
    public boolean contains(V value);
}
