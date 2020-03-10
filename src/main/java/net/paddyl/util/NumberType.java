package net.paddyl.util;

import java.math.BigDecimal;
import java.math.BigInteger;

// TODO : Rename parameters to "a", "b", etc... instead of "one", "two", as those are now fields for the actual numbers

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
     * @return the value {@code 0} if {@code one == two};
     *         a value less than {@code 0} if {@code one < two}; and
     *         a value greater than {@code 0} if {@code one > two}
     */
    public abstract int compare(T one, T two);

    /**
     * {@code one >= two}
     *
     * @return whether {@param one} is greater than or equal to {@param two}.
     */
    public boolean gte(T one, T two) {
        return compare(one, two) >= 0;
    }

    /**
     * {@code one > two}
     *
     * @return whether {@param one} is greater than {@param two}.
     */
    public boolean gt(T one, T two) {
        return compare(one, two) > 0;
    }

    /**
     * {@code one <= two}
     *
     * @return whether {@param one} is less than or equal to {@param two}.
     */
    public boolean lte(T one, T two) {
        return compare(one, two) <= 0;
    }

    /**
     * {@code one < two}
     *
     * @return whether {@param one} is less than {@param two}.
     */
    public boolean lt(T one, T two) {
        return compare(one, two) < 0;
    }

    /**
     * {@code one == two}
     *
     * @return whether {@param one} is equal to {@param two}.
     */
    public boolean eq(T one, T two) {
        if (one == null || two == null)
            return one == null && two == null;

        return compare(one, two) == 0;
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
     * {@code one + two}
     *
     * @return the sum of {@param one} and {@param two}.
     */
    public abstract T add(T one, T two);

    /**
     * {@code one - two}
     *
     * @return the value of {@param one} minus {@param two}.
     */
    public abstract T subtract(T one, T two);

    /**
     * {@code -value}
     *
     * @return the negation of {@param value}.
     */
    public T negate(T value) {
        return subtract(zero, value);
    }

    /**
     * {@code one * two}
     *
     * @return the multiplication of {@param one} and {@param two}.
     */
    public abstract T mul(T one, T two);

    /**
     * {@code one / two}
     *
     * @return the division of {@param one} and {@param two}.
     */
    public abstract T div(T one, T two);

    /**
     * @return the division of {@param a} by {@param b}, rounded up.
     */
    public T divRoundUp(T a, T b) {
        T div = div(a, b);
        T correction = (isMultiple(a, b) ? zero : one);
        return add(div, correction);

    }

    /**
     * {@code one % two}
     *
     * @return the signedRemainder of {@param one} divided by {@param two}.
     */
    public abstract T signedRemainder(T one, T two);

    /**
     * @return the modulus of {@param one} and {@param two}.
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
     * @return the minimum of {@param one} and {@param two}.
     */
    public T min(T one, T two) {
        return lt(one, two) ? one : two;
    }

    /**
     * @return the maximum of {@param one} and {@param two}.
     */
    public T max(T one, T two) {
        return gt(one, two) ? one : two;
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
     * @return the greatest common divisor of {@code abs(one)} and {@code abs(two)}.
     */
    public abstract T gcd(T one, T two);

    /**
     * @return the least common multiple of {@code abs(one)} and {@code abs(two)},
     *         or {@code -1} if it is out of the bounds of this NumberType.
     */
    public T lcm(T one, T two) {
        // Special case zero
        if (eq(one, zero) || eq(two, zero))
            return zero;

        // Take the absolute of one and two
        one = absolute(one);
        two = absolute(two);

        // Check for overflow
        if (lte(one, zero) || lte(two, zero)) {
            if (eq(one, this.one))
                return two;
            if (eq(two, this.one))
                return one;
            return coerce(-1);
        }

        // Reduce one such that one * two is the LCM
        T gcd = gcd(one, two);
        one = div(one, gcd);

        // Check for overflow
        if (maxValue != null && !eq(one, zero) && gt(two, div(maxValue, one)))
            return coerce(-1);

        return mul(one, two);
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
        while (!eq(left, right)) { // TODO : Brute force :(
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
        public T gcd(T one, T two) {
            // TODO : Does this work for negative numbers?!?
            while (!eq(zero, two)) {
                T temp = two;
                two = signedRemainder(one, two);
                one = temp;
            }
            return one;
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
        public T gcd(T one, T two) {
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
        public int compare(Byte one, Byte two) {
            return Byte.compare(one, two);
        }

        @Override
        public boolean gte(Byte one, Byte two) {
            return one >= two;
        }

        @Override
        public boolean gt(Byte one, Byte two) {
            return one > two;
        }

        @Override
        public boolean lte(Byte one, Byte two) {
            return one <= two;
        }

        @Override
        public boolean lt(Byte one, Byte two) {
            return one < two;
        }

        @Override
        public Byte add(Byte one, Byte two) {
            return (byte) (one + two);
        }

        @Override
        public Byte subtract(Byte one, Byte two) {
            return (byte) (one - two);
        }

        @Override
        public Byte mul(Byte one, Byte two) {
            return (byte) (one * two);
        }

        @Override
        public Byte div(Byte one, Byte two) {
            return (byte) (one / two);
        }

        @Override
        public Byte signedRemainder(Byte one, Byte two) {
            return (byte) (one % two);
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
        public int compare(Short one, Short two) {
            return Short.compare(one, two);
        }

        @Override
        public boolean gte(Short one, Short two) {
            return one >= two;
        }

        @Override
        public boolean gt(Short one, Short two) {
            return one > two;
        }

        @Override
        public boolean lte(Short one, Short two) {
            return one <= two;
        }

        @Override
        public boolean lt(Short one, Short two) {
            return one < two;
        }

        @Override
        public Short add(Short one, Short two) {
            return (short) (one + two);
        }

        @Override
        public Short subtract(Short one, Short two) {
            return (short) (one - two);
        }

        @Override
        public Short mul(Short one, Short two) {
            return (short) (one * two);
        }

        @Override
        public Short div(Short one, Short two) {
            return (short) (one / two);
        }

        @Override
        public Short signedRemainder(Short one, Short two) {
            return (short) (one % two);
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
        public int compare(Integer one, Integer two) {
            return Integer.compare(one, two);
        }

        @Override
        public boolean gte(Integer one, Integer two) {
            return one >= two;
        }

        @Override
        public boolean gt(Integer one, Integer two) {
            return one > two;
        }

        @Override
        public boolean lte(Integer one, Integer two) {
            return one <= two;
        }

        @Override
        public boolean lt(Integer one, Integer two) {
            return one < two;
        }

        @Override
        public Integer add(Integer one, Integer two) {
            return one + two;
        }

        @Override
        public Integer subtract(Integer one, Integer two) {
            return one - two;
        }

        @Override
        public Integer mul(Integer one, Integer two) {
            return one * two;
        }

        @Override
        public Integer div(Integer one, Integer two) {
            return one / two;
        }

        @Override
        public Integer signedRemainder(Integer one, Integer two) {
            return one % two;
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
        public int compare(Long one, Long two) {
            return Long.compare(one, two);
        }

        @Override
        public boolean gte(Long one, Long two) {
            return one >= two;
        }

        @Override
        public boolean gt(Long one, Long two) {
            return one > two;
        }

        @Override
        public boolean lte(Long one, Long two) {
            return one <= two;
        }

        @Override
        public boolean lt(Long one, Long two) {
            return one < two;
        }

        @Override
        public Long add(Long one, Long two) {
            return one + two;
        }

        @Override
        public Long subtract(Long one, Long two) {
            return one - two;
        }

        @Override
        public Long mul(Long one, Long two) {
            return one * two;
        }

        @Override
        public Long div(Long one, Long two) {
            return one / two;
        }

        @Override
        public Long signedRemainder(Long one, Long two) {
            return one % two;
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
        public int compare(BigInteger one, BigInteger two) {
            return one.compareTo(two);
        }

        @Override
        public BigInteger add(BigInteger one, BigInteger two) {
            return one.add(two);
        }

        @Override
        public BigInteger subtract(BigInteger one, BigInteger two) {
            return one.subtract(two);
        }

        @Override
        public BigInteger mul(BigInteger one, BigInteger two) {
            return one.multiply(two);
        }

        @Override
        public BigInteger div(BigInteger one, BigInteger two) {
            return one.divide(two);
        }

        @Override
        public BigInteger signedRemainder(BigInteger one, BigInteger two) {
            return one.remainder(two);
        }

        @Override
        public BigInteger absolute(BigInteger number) {
            return number.abs();
        }

        @Override
        public BigInteger gcd(BigInteger one, BigInteger two) {
            return one.gcd(two);
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
        public int compare(Float one, Float two) {
            return Float.compare(one, two);
        }

        @Override
        public boolean gte(Float one, Float two) {
            return one >= two;
        }

        @Override
        public boolean gt(Float one, Float two) {
            return one > two;
        }

        @Override
        public boolean lte(Float one, Float two) {
            return one <= two;
        }

        @Override
        public boolean lt(Float one, Float two) {
            return one < two;
        }

        @Override
        public Float add(Float one, Float two) {
            return one + two;
        }

        @Override
        public Float subtract(Float one, Float two) {
            return one - two;
        }

        @Override
        public Float mul(Float one, Float two) {
            return one * two;
        }

        @Override
        public Float div(Float one, Float two) {
            return one / two;
        }

        @Override
        public Float signedRemainder(Float one, Float two) {
            return one % two;
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
        public int compare(Double one, Double two) {
            return Double.compare(one, two);
        }

        @Override
        public boolean gte(Double one, Double two) {
            return one >= two;
        }

        @Override
        public boolean gt(Double one, Double two) {
            return one > two;
        }

        @Override
        public boolean lte(Double one, Double two) {
            return one <= two;
        }

        @Override
        public boolean lt(Double one, Double two) {
            return one < two;
        }

        @Override
        public Double add(Double one, Double two) {
            return one + two;
        }

        @Override
        public Double subtract(Double one, Double two) {
            return one - two;
        }

        @Override
        public Double mul(Double one, Double two) {
            return one * two;
        }

        @Override
        public Double div(Double one, Double two) {
            return one / two;
        }

        @Override
        public Double signedRemainder(Double one, Double two) {
            return one % two;
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
        public int compare(BigDecimal one, BigDecimal two) {
            return one.compareTo(two);
        }

        @Override
        public BigDecimal add(BigDecimal one, BigDecimal two) {
            return one.add(two);
        }

        @Override
        public BigDecimal subtract(BigDecimal one, BigDecimal two) {
            return one.subtract(two);
        }

        @Override
        public BigDecimal mul(BigDecimal one, BigDecimal two) {
            return one.multiply(two);
        }

        @Override
        public BigDecimal div(BigDecimal one, BigDecimal two) {
            throw new UnsupportedOperationException("BigDecimal division is unsupported");
        }

        @Override
        public BigDecimal signedRemainder(BigDecimal one, BigDecimal two) {
            throw new UnsupportedOperationException("BigDecimal signedRemainder is unsupported");
        }

        @Override
        public BigDecimal absolute(BigDecimal number) {
            return number.abs();
        }
    }
}
