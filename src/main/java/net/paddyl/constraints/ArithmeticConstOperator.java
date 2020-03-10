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

        @SuppressWarnings("unchecked")
        public AddConstOperator(ValueSetFactory<S, V> factory, V value) {
            super(factory, value);

            if (!(value instanceof Number))
                throw new IllegalArgumentException("AddConstOperator only supports Number values");

            this.type = Num.type((Number) value);
            this.negatedValue = Cast.cast(type.negate((Number) value));
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
        @SuppressWarnings("unchecked")
        public String toString() {
            return type.lt((Number) value, type.getZero()) ? "- " + negatedValue : "+ " + value;
        }
    }

    public static class MulConstOperator<S extends ValueSet<S, V>, V> extends ArithmeticConstOperator<S, V> {

        private final NumberType type;

        public MulConstOperator(ValueSetFactory<S, V> factory, V value) {
            super(factory, value);

            if (!(value instanceof Number))
                throw new IllegalArgumentException("AddConstOperator only supports Number values");

            this.type = Num.type((Number) value);
        }

        @Override
        public S forward(S range) {
            return factory.scale(range, value);
        }

        @Override
        public S backward(S range) {
            return factory.unscale(range, value);
        }

        @Override
        public String toString() {
            return "* " + value;
        }
    }
}
