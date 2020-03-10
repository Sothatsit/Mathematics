package net.paddyl.util;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Allows for generic handling for Java's primitive number types.
 */
public abstract class NumberType<T extends Number> {

    protected final Class<?> primitiveClass;
    protected final Class<T> boxClass;
    protected final int byteCount;
    protected final boolean floatingPoint;
    protected final int integerBitCount;
    protected final T minValue;
    protected final T maxValue;
    protected final T zero;
    protected final T one;

    private NumberType(
            Class<?> primitiveClass,
            Class<T> boxClass,
            int byteCount,
            boolean floatingPoint,
            int integerBitCount,
            T minValue,
            T maxValue) {

        Checks.assertNonNull(boxClass, "boxClass");

        // Can't use assertPositive, as it uses NumberType
        Checks.assertThat(byteCount > 0, "byteCount must be greater than 0");
        Checks.assertThat(integerBitCount > 0, "integerBitCount must be greater than 0");

        this.primitiveClass = primitiveClass;
        this.boxClass = boxClass;
        this.byteCount = byteCount;
        this.floatingPoint = floatingPoint;
        this.integerBitCount = integerBitCount;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.zero = coerce(0);
        this.one = coerce(1);
    }

    /**
     * @return the primitive class for this number type (e.g. int for Integer),
     *         or null if this number type does not have a primitive equivalent.
     */
    public Class<?> getPrimitiveClass() {
        return primitiveClass;
    }

    /**
     * @return the Number sub-class that stores values of this number type.
     */
    public Class<T> getBoxClass() {
        return boxClass;
    }

    /**
     * @return the maximum byte count stored by a value in this number type.
     */
    public int getByteCount() {
        return byteCount;
    }

    /**
     * @return whether this number type is an integer type.
     */
    public boolean isInteger() {
        return !floatingPoint;
    }

    /**
     * @return whether this number type is a floating point type.
     */
    public boolean isFloatingPoint() {
        return floatingPoint;
    }

    /**
     * @return The maximum number of integer bits that can be represented without
     *         loss by this number type, including the sign bit.
     */
    public int getIntegerBits() {
        return integerBitCount;
    }

    /**
     * @return The largest magnitude negative value that can be represented by this number type.
     *         Will return null for types that do not have a minimum value.
     */
    public T getMinValue() {
        return minValue;
    }

    /**
     * @return The largest magnitude positive value that can be represented by this number type.
     *         Will return null for types that do not have a maximum value.
     */
    public T getMaxValue() {
        return maxValue;
    }

    /**
     * @return the value {@code 0} in this number type.
     */
    public T getZero() {
        return zero;
    }

    /**
     * @return the value {@code 1} in this number type.
     */
    public T getOne() {
        return one;
    }

    /**
     * @return the value {@param number} converted to this number type.
     */
    public T coerce(Number number) {
        if (number.getClass().equals(boxClass))
            return Cast.cast(number);
        return coerceImpl(number);
    }

    protected abstract T coerceImpl(Number number);

    /**
     * @return the value {@code 0} if {@code a == b};
     *         a value less than {@code 0} if {@code a < b}; and
     *         a value greater than {@code 0} if {@code a > b}
     */
    public abstract int compare(T a, T b);

    /**
     * {@code a >= b}
     *
     * @return whether {@param a} is greater than or equal to {@param b}.
     */
    public boolean gte(T a, T b) {
        return compare(a, b) >= 0;
    }

    /**
     * {@code a > b}
     *
     * @return whether {@param a} is greater than {@param b}.
     */
    public boolean gt(T a, T b) {
        return compare(a, b) > 0;
    }

    /**
     * {@code a <= b}
     *
     * @return whether {@param a} is less than or equal to {@param b}.
     */
    public boolean lte(T a, T b) {
        return compare(a, b) <= 0;
    }

    /**
     * {@code a < b}
     *
     * @return whether {@param a} is less than {@param b}.
     */
    public boolean lt(T a, T b) {
        return compare(a, b) < 0;
    }

    /**
     * {@code a == b}
     *
     * @return whether {@param a} is equal to {@param b}.
     */
    public boolean eq(T a, T b) {
        return compare(a, b) == 0;
    }

    /**
     * {@code min <= value && value <= max}
     *
     * @return whether {@param value} is in the range [{@param min}, {@param max}] inclusive.
     */
    public boolean inRange(T value, T min, T max) {
        return lte(min, value) && lte(value, max);
    }

    /**
     * {@code a + b}
     *
     * @return the sum of {@param a} and {@param b}.
     */
    public abstract T add(T a, T b);

    /**
     * {@code a - b}
     *
     * @return the value of {@param a} minus {@param b}.
     */
    public abstract T subtract(T a, T b);

    /**
     * {@code -value}
     *
     * @return the negation of {@param value}.
     */
    public T negate(T value) {
        return subtract(zero, value);
    }

    /**
     * {@code a * b}
     *
     * @return the product of {@param a} and {@param b}.
     */
    public abstract T mul(T a, T b);

    /**
     * {@code a / b}
     *
     * @return the division of {@param a} by {@param b}.
     */
    public abstract T div(T a, T b);

    /**
     * @return the division of {@param a} by {@param b}, rounded up.
     */
    public T divRoundUp(T a, T b) {
        T div = div(a, b);
        T correction = (isMultiple(a, b) ? zero : one);
        return add(div, correction);

    }

    /**
     * {@code a % b}
     *
     * @return the signed remainder of {@param a} divided by {@param b}.
     */
    public abstract T signedRemainder(T a, T b);

    /**
     * @return the positive remainder of {@param a} divided by {@param b}.
     */
    public T positiveRemainder(T one, T two) {
        T remainder = signedRemainder(one, two);
        return gte(remainder, zero) ? remainder : add(remainder, two);
    }

    /**
     * @return whether {@param a} is a multiple of {@param b}.
     */
    public boolean isMultiple(T a, T b) {
        return eq(zero, signedRemainder(a, b));
    }

    /**
     * @return the absolute value of {@param number}.
     */
    public abstract T absolute(T number);

    /**
     * @return the minimum of {@param a} and {@param b}.
     */
    public T min(T a, T b) {
        return lt(a, b) ? a : b;
    }

    /**
     * @return the maximum of {@param a} and {@param b}.
     */
    public T max(T a, T b) {
        return gt(a, b) ? a : b;
    }

    /**
     * @return if {@param value} is less than {@param min}, then {@param min}.
     *         else if {@param value} is greater than {@param max}, then {@param max}.
     *         or if {@param value} falls between these two values, then {@param value}.
     */
    public T clamp(T value, T min, T max) {
        return lt(value, min) ? min : (gt(value, max) ? max : value);
    }

    /**
     * @return the greatest common divisor of {@code abs(a)} and {@code abs(b)}.
     */
    public abstract T gcd(T a, T b);

    /**
     * @return the least common multiple of {@code abs(a)} and {@code abs(b)},
     *         or {@code -1} if it is out of the bounds of this NumberType.
     */
    public T lcm(T a, T b) {
        // Special case zero
        if (eq(a, zero) || eq(b, zero))
            return zero;

        // Take the absolute of one and two
        a = absolute(a);
        b = absolute(b);

        // Check for overflow
        if (lte(a, zero) || lte(b, zero)) {
            if (eq(a, one))
                return a;
            if (eq(b, one))
                return a;
            return coerce(-1);
        }

        // Reduce one such that one * two is the LCM
        T gcd = gcd(a, b);
        a = div(a, gcd);

        // Check for overflow
        if (maxValue != null && !eq(a, zero) && gt(b, div(maxValue, a)))
            return coerce(-1);

        return mul(a, b);
    }

    /**
     * @param step1 The first step
     * @param step2 The second step
     * @param advantage The advantage of {@param step1} over {@param step2}
     *
     * @return the smallest positive value of {@code step1 * x1 - advantage} and {@code step2 * x2} that satisfies
     *         the equation {@code step1 * x1 - advantage = step2 * x2}.
     *         Returns {@code -1} if the equation cannot be satisfied, or the equation overflows
     */
    public T offsetLCM(T step1, T step2, T advantage) {
        // If there is no advantage, then just do a regular LCM
        if (eq(advantage, zero))
            return lcm(step1, step2);

        // The steps should be positive
        step1 = absolute(step1);
        step2 = absolute(step2);
        if (lte(step1, zero) || lte(step2, zero)) {
            // Absolute overflowed
            return coerce(-1);
        }

        // Check that there is a solution
        T gcd = gcd(step1, step2);
        if (!isMultiple(advantage, gcd))
            return coerce(-1);

        // Make sure left and right start >= 0
        T left = advantage;
        T right = zero;
        if (lt(left, zero)) {
            T negatedAdvantage = negate(advantage);
            if (lte(negatedAdvantage, zero)) {
                // Negation overflowed
                return coerce(-1);
            }

            T multiplesToAdd = divRoundUp(negatedAdvantage, step1);
            left = add(left, mul(multiplesToAdd, step1));
        }

        // Brute force a solution
        while (!eq(left, right)) {
            // TODO : Brute force :(
            // Supposed to be able to use a version of the Extended Euclidean algorithm
            // to calculate this more efficiently.

            if (lt(left, right)) {
                left = add(left, step1);

                // Add overflowed
                if (lte(left, zero))
                    return coerce(-1);
            } else {
                right = add(right, step2);

                // Add overflowed
                if (lte(right, zero))
                    return coerce(-1);
            }
        }

        return left;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    public static abstract class IntegerNumberType<T extends Number> extends NumberType<T> {

        protected IntegerNumberType(
                Class<?> primitiveClass,
                Class<T> boxClass,
                int byteCount,
                T minValue,
                T maxValue) {

            super(
                primitiveClass, boxClass,
                byteCount, false,
                (byteCount == Integer.MAX_VALUE ? Integer.MAX_VALUE : byteCount * 8),
                minValue, maxValue
            );
        }

        /**
         * Uses Euclid's algorithm.
         */
        @Override
        public T gcd(T a, T b) {
            // TODO : Does this work for negative numbers?!?
            while (!eq(zero, b)) {
                T temp = b;
                b = signedRemainder(a, b);
                a = temp;
            }
            return a;
        }
    }

    public static abstract class FloatingPointNumberType<T extends Number> extends NumberType<T> {

        protected FloatingPointNumberType(
                Class<?> primitiveClass,
                Class<T> boxClass,
                int byteCount,
                int integerBitCount,
                T minValue,
                T maxValue) {

            super(primitiveClass, boxClass, byteCount, true, integerBitCount, minValue, maxValue);
        }

        @Override
        public T gcd(T a, T b) {
            throw new UnsupportedOperationException("floating point greatest common divisor is unsupported");
        }
    }

    public static final class ByteType extends IntegerNumberType<Byte> {

        protected ByteType() {
            super(
                byte.class, Byte.class, 1,
                Byte.MIN_VALUE, Byte.MAX_VALUE
            );
        }

        @Override
        public Byte coerceImpl(Number number) {
            return number.byteValue();
        }

        @Override
        public int compare(Byte a, Byte b) {
            return Byte.compare(a, b);
        }

        @Override
        public boolean gte(Byte a, Byte b) {
            return a >= b;
        }

        @Override
        public boolean gt(Byte a, Byte b) {
            return a > b;
        }

        @Override
        public boolean lte(Byte a, Byte b) {
            return a <= b;
        }

        @Override
        public boolean lt(Byte a, Byte b) {
            return a < b;
        }

        @Override
        public Byte add(Byte a, Byte b) {
            return (byte) (a + b);
        }

        @Override
        public Byte subtract(Byte a, Byte b) {
            return (byte) (a - b);
        }

        @Override
        public Byte mul(Byte a, Byte b) {
            return (byte) (a * b);
        }

        @Override
        public Byte div(Byte a, Byte b) {
            return (byte) (a / b);
        }

        @Override
        public Byte signedRemainder(Byte a, Byte b) {
            return (byte) (a % b);
        }

        @Override
        public Byte absolute(Byte number) {
            return (byte) Math.abs(number);
        }
    }

    public static final class ShortType extends IntegerNumberType<Short> {

        protected ShortType() {
            super(
                short.class, Short.class, 2,
                Short.MIN_VALUE, Short.MAX_VALUE
            );
        }

        @Override
        public Short coerceImpl(Number number) {
            return number.shortValue();
        }

        @Override
        public int compare(Short a, Short b) {
            return Short.compare(a, b);
        }

        @Override
        public boolean gte(Short a, Short b) {
            return a >= b;
        }

        @Override
        public boolean gt(Short a, Short b) {
            return a > b;
        }

        @Override
        public boolean lte(Short a, Short b) {
            return a <= b;
        }

        @Override
        public boolean lt(Short a, Short b) {
            return a < b;
        }

        @Override
        public Short add(Short a, Short b) {
            return (short) (a + b);
        }

        @Override
        public Short subtract(Short a, Short b) {
            return (short) (a - b);
        }

        @Override
        public Short mul(Short a, Short b) {
            return (short) (a * b);
        }

        @Override
        public Short div(Short a, Short b) {
            return (short) (a / b);
        }

        @Override
        public Short signedRemainder(Short a, Short b) {
            return (short) (a % b);
        }

        @Override
        public Short absolute(Short number) {
            return (short) Math.abs(number);
        }
    }

    public static final class IntType extends IntegerNumberType<Integer> {

        protected IntType() {
            super(
                int.class, Integer.class, 4,
                Integer.MIN_VALUE, Integer.MAX_VALUE
            );
        }

        @Override
        public Integer coerceImpl(Number number) {
            return number.intValue();
        }

        @Override
        public int compare(Integer a, Integer b) {
            return Integer.compare(a, b);
        }

        @Override
        public boolean gte(Integer a, Integer b) {
            return a >= b;
        }

        @Override
        public boolean gt(Integer a, Integer b) {
            return a > b;
        }

        @Override
        public boolean lte(Integer a, Integer b) {
            return a <= b;
        }

        @Override
        public boolean lt(Integer a, Integer b) {
            return a < b;
        }

        @Override
        public Integer add(Integer a, Integer b) {
            return a + b;
        }

        @Override
        public Integer subtract(Integer a, Integer b) {
            return a - b;
        }

        @Override
        public Integer mul(Integer a, Integer b) {
            return a * b;
        }

        @Override
        public Integer div(Integer a, Integer b) {
            return a / b;
        }

        @Override
        public Integer signedRemainder(Integer a, Integer b) {
            return a % b;
        }

        @Override
        public Integer absolute(Integer number) {
            return Math.abs(number);
        }
    }

    public static final class LongType extends IntegerNumberType<Long> {

        protected LongType() {
            super(
                long.class, Long.class, 8,
                Long.MIN_VALUE, Long.MAX_VALUE
            );
        }

        @Override
        public Long coerceImpl(Number number) {
            return number.longValue();
        }

        @Override
        public int compare(Long a, Long b) {
            return Long.compare(a, b);
        }

        @Override
        public boolean gte(Long a, Long b) {
            return a >= b;
        }

        @Override
        public boolean gt(Long a, Long b) {
            return a > b;
        }

        @Override
        public boolean lte(Long a, Long b) {
            return a <= b;
        }

        @Override
        public boolean lt(Long a, Long b) {
            return a < b;
        }

        @Override
        public Long add(Long a, Long b) {
            return a + b;
        }

        @Override
        public Long subtract(Long a, Long b) {
            return a - b;
        }

        @Override
        public Long mul(Long a, Long b) {
            return a * b;
        }

        @Override
        public Long div(Long a, Long b) {
            return a / b;
        }

        @Override
        public Long signedRemainder(Long a, Long b) {
            return a % b;
        }

        @Override
        public Long absolute(Long number) {
            return Math.abs(number);
        }
    }

    public static final class BigIntType extends IntegerNumberType<BigInteger> {

        protected BigIntType() {
            super(
                null, BigInteger.class, Integer.MAX_VALUE, null, null
            );
        }

        @Override
        public BigInteger coerceImpl(Number number) {
            if (number instanceof BigInteger)
                return (BigInteger) number;
            if (number instanceof BigDecimal)
                return ((BigDecimal) number).toBigInteger();
            return BigInteger.valueOf(number.longValue());
        }

        @Override
        public int compare(BigInteger a, BigInteger b) {
            return a.compareTo(b);
        }

        @Override
        public BigInteger add(BigInteger a, BigInteger b) {
            return a.add(b);
        }

        @Override
        public BigInteger subtract(BigInteger a, BigInteger b) {
            return a.subtract(b);
        }

        @Override
        public BigInteger mul(BigInteger a, BigInteger b) {
            return a.multiply(b);
        }

        @Override
        public BigInteger div(BigInteger a, BigInteger b) {
            return a.divide(b);
        }

        @Override
        public BigInteger signedRemainder(BigInteger a, BigInteger b) {
            return a.remainder(b);
        }

        @Override
        public BigInteger absolute(BigInteger number) {
            return number.abs();
        }

        @Override
        public BigInteger gcd(BigInteger a, BigInteger b) {
            return a.gcd(b);
        }
    }

    public static final class FloatType extends FloatingPointNumberType<Float> {

        protected FloatType() {
            super(
                float.class, Float.class, 4, 25,
                -Float.MAX_VALUE, Float.MAX_VALUE
            );
        }

        @Override
        public Float coerceImpl(Number number) {
            return number.floatValue();
        }

        @Override
        public int compare(Float a, Float b) {
            return Float.compare(a, b);
        }

        @Override
        public boolean gte(Float a, Float b) {
            return a >= b;
        }

        @Override
        public boolean gt(Float a, Float b) {
            return a > b;
        }

        @Override
        public boolean lte(Float a, Float b) {
            return a <= b;
        }

        @Override
        public boolean lt(Float a, Float b) {
            return a < b;
        }

        @Override
        public Float add(Float a, Float b) {
            return a + b;
        }

        @Override
        public Float subtract(Float a, Float b) {
            return a - b;
        }

        @Override
        public Float mul(Float a, Float b) {
            return a * b;
        }

        @Override
        public Float div(Float a, Float b) {
            return a / b;
        }

        @Override
        public Float signedRemainder(Float a, Float b) {
            return a % b;
        }

        @Override
        public Float absolute(Float number) {
            return Math.abs(number);
        }
    }

    public static final class DoubleType extends FloatingPointNumberType<Double> {

        protected DoubleType() {
            super(
                double.class, Double.class, 8, 54,
                -Double.MAX_VALUE, Double.MAX_VALUE
            );
        }

        @Override
        public Double coerceImpl(Number number) {
            return number.doubleValue();
        }

        @Override
        public int compare(Double a, Double b) {
            return Double.compare(a, b);
        }

        @Override
        public boolean gte(Double a, Double b) {
            return a >= b;
        }

        @Override
        public boolean gt(Double a, Double b) {
            return a > b;
        }

        @Override
        public boolean lte(Double a, Double b) {
            return a <= b;
        }

        @Override
        public boolean lt(Double a, Double b) {
            return a < b;
        }

        @Override
        public Double add(Double a, Double b) {
            return a + b;
        }

        @Override
        public Double subtract(Double a, Double b) {
            return a - b;
        }

        @Override
        public Double mul(Double a, Double b) {
            return a * b;
        }

        @Override
        public Double div(Double a, Double b) {
            return a / b;
        }

        @Override
        public Double signedRemainder(Double a, Double b) {
            return a % b;
        }

        @Override
        public Double absolute(Double number) {
            return Math.abs(number);
        }
    }

    public static final class BigDecimalType extends FloatingPointNumberType<BigDecimal> {

        protected BigDecimalType() {
            super(
                null, BigDecimal.class, Integer.MAX_VALUE, Integer.MAX_VALUE,
                null, null
            );
        }

        @Override
        public BigDecimal coerceImpl(Number number) {
            if (number instanceof BigDecimal)
                return (BigDecimal) number;
            if (number instanceof BigInteger)
                return new BigDecimal((BigInteger) number);
            if (number instanceof Long)
                return BigDecimal.valueOf(number.longValue());
            return BigDecimal.valueOf(number.doubleValue());
        }

        @Override
        public int compare(BigDecimal a, BigDecimal b) {
            return a.compareTo(b);
        }

        @Override
        public BigDecimal add(BigDecimal a, BigDecimal b) {
            return a.add(b);
        }

        @Override
        public BigDecimal subtract(BigDecimal a, BigDecimal b) {
            return a.subtract(b);
        }

        @Override
        public BigDecimal mul(BigDecimal a, BigDecimal b) {
            return a.multiply(b);
        }

        @Override
        public BigDecimal div(BigDecimal a, BigDecimal b) {
            throw new UnsupportedOperationException("BigDecimal division is unsupported");
        }

        @Override
        public BigDecimal signedRemainder(BigDecimal a, BigDecimal b) {
            throw new UnsupportedOperationException("BigDecimal signedRemainder is unsupported");
        }

        @Override
        public BigDecimal absolute(BigDecimal number) {
            return number.abs();
        }
    }
}
