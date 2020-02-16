package net.paddyl.constraints;

import net.paddyl.constraints.set.ValueSet;

/**
 * An operation on a single range.
 */
public interface ConstOperator<S extends ValueSet<S, V>, V> {

    /**
     * Apply the operation.
     */
    public S forward(S range);

    /**
     * Reverse the operation.
     */
    public S backward(S range);

    /**
     * @return the inverse operation.
     */
    public default ConstOperator<S, V> inverse() {
        ConstOperator<S, V> original = this;
        return new ConstOperator<S, V>() {
            @Override
            public S forward(S range) {
                return original.backward(range);
            }

            @Override
            public S backward(S range) {
                return original.forward(range);
            }

            @Override
            public String toString() {
                return "inverse{" + original.toString() + "}";
            }
        };
    }
}