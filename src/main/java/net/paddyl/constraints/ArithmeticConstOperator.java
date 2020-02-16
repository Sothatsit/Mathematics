package net.paddyl.constraints;

import net.paddyl.constraints.set.ValueSet;
import net.paddyl.constraints.set.ValueSetFactory;
import net.paddyl.util.Cast;
import net.paddyl.util.Num;
import net.paddyl.util.NumberType;

/**
 * Basic arithmetic const operator.
 */
public abstract class ArithmeticConstOperator<S extends ValueSet<S, V>, V> implements ConstOperator<S, V> {

    protected final ValueSetFactory<S, V> factory;
    protected final V value;

    public ArithmeticConstOperator(ValueSetFactory<S, V> factory, V value) {
        this.factory = factory;
        this.value = value;
    }

    /**
     * range + value.
     */
    public static class AddConstOperator<S extends ValueSet<S, V>, V> extends ArithmeticConstOperator<S, V> {

        private final NumberType type;
        private final V negatedValue;

        public AddConstOperator(ValueSetFactory<S, V> factory, V value) {
            super(factory, value);

            if (!(value instanceof Number))
                throw new UnsupportedOperationException();

            this.type = Cast.cast(Num.type((Number) value));
            this.negatedValue = Cast.cast(type.subtract(0, (Number) value));
        }

        @Override
        public S forward(S range) {
            return factory.shift(range, value);
        }

        @Override
        public S backward(S range) {
            return factory.shift(range, negatedValue);
        }

        @Override
        public String toString() {
            return type.compare((Number) value, 0) < 0 ? "- " + negatedValue : "+ " + value;
        }
    }
}
