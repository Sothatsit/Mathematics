package net.paddyl.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Allows for generic handling for Java's primitive number types.
 */
public abstract class NumberType<T extends Number> {

    public static final NumberType<Byte> BYTE = new ByteType();
    public static final NumberType<Short> SHORT = new ShortType();
    public static final NumberType<Integer> INT = new IntType();
    public static final NumberType<Long> LONG = new LongType();
    public static final NumberType<BigInteger> BIG_INT = new BigIntType();
    public static final NumberType<Float> FLOAT = new FloatType();
    public static final NumberType<Double> DOUBLE = new DoubleType();
    public static final NumberType<BigDecimal> BIG_DECIMAL = new BigDecimalType();

    public static final NumberType<?>[] ALL = {
            BYTE, SHORT, INT, BIG_INT, LONG, FLOAT, DOUBLE, BIG_DECIMAL
    };

    public static final NumberType<?>[] INTEGER_TYPES = Arrays.stream(ALL).filter(NumberType::isInteger)
                                                              .collect(Collectors.toList())
                                                              .toArray(new NumberType<?>[0]);

    public static final NumberType<?>[] FLOAT_TYPES = Arrays.stream(ALL).filter(NumberType::isFloatingPoint)
                                                            .collect(Collectors.toList())
                                                            .toArray(new NumberType<?>[0]);

    private static final Map<Class<? extends Number>, NumberType> BOX_TO_TYPE = new HashMap<>();
    static {
        for(NumberType<?> type : ALL) {
            NumberType previous = BOX_TO_TYPE.put(type.getBoxClass(), type);
            Checks.assertThat(previous == null, "duplicate NumberType entry found for data type " + type);
        }
    }

    private final Class<?> primitiveClass;
    private final Class<T> boxClass;
    private final int byteCount;
    private final boolean floatingPoint;
    private final int integerBitCount;
    private final T minValue;
    private final T maxValue;

    private NumberType(Class<?> primitiveClass,
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
     * @return The number type just below the precision of this one.
     *         If floating point, the next lower floating point type,
     *         else the next lower integer type.
     */
    public NumberType<?> getNextLowerPrecisionType() {
        NumberType<?> highestBelow = null;
        for (NumberType<?> type : isFloatingPoint() ? FLOAT_TYPES : INTEGER_TYPES) {
            if (type.getByteCount() >= getByteCount())
                continue;
            if (highestBelow != null && type.getByteCount() < highestBelow.getByteCount())
                continue;
            highestBelow = type;
        }
        return highestBelow;
    }

    /**
     * @return The number type just above the precision of this one.
     *         If floating point, the next higher floating point type,
     *         else the next higher integer type.
     */
    public NumberType<?> getNextHigherPrecisionType() {
        NumberType<?> lowestAbove = null;
        for (NumberType<?> type : isFloatingPoint() ? FLOAT_TYPES : INTEGER_TYPES) {
            if (type.getByteCount() <= getByteCount())
                continue;
            if (lowestAbove != null && type.getByteCount() > lowestAbove.getByteCount())
                continue;
            lowestAbove = type;
        }
        return lowestAbove;
    }

    /**
     * @return the value {@param number} converted to this number type.
     */
    public T coerce(Number number) {
        if (number == null)
            return null;
        if (number.getClass().equals(boxClass))
            return Cast.cast(number);
        return coerceImpl(number);
    }

    protected abstract T coerceImpl(Number number);

    /**
     * After coercing both values to this type, compare them.
     * @see #compareImpl(Number, Number)
     */
    public int compare(Number one, Number two) {
        return compareImpl(coerce(one), coerce(two));
    }

    /**
     * @return the value {@code 0} if {@code one == two};
     *         a value less than {@code 0} if {@code one < two}; and
     *         a value greater than {@code 0} if {@code one > two}
     */
    public abstract int compareImpl(T one, T two);

    /**
     * @return whether {@param one} is greater than or equal to {@param two} when coerced to this type.
     */
    public boolean gte(Number one, Number two) {
        return gteImpl(coerce(one), coerce(two));
    }

    /**
     * @return whether {@param one} is greater than or equal to {@param two}.
     */
    public boolean gteImpl(T one, T two) {
        return compareImpl(one, two) >= 0;
    }

    /**
     * @return whether {@param one} is greater than {@param two} when coerced to this type.
     */
    public boolean gt(Number one, Number two) {
        return gtImpl(coerce(one), coerce(two));
    }

    /**
     * @return whether {@param one} is greater than {@param two}.
     */
    public boolean gtImpl(T one, T two) {
        return compareImpl(one, two) > 0;
    }

    /**
     * @return whether {@param one} is less than or equal to {@param two} when coerced to this type.
     */
    public boolean lte(Number one, Number two) {
        return lteImpl(coerce(one), coerce(two));
    }

    /**
     * @return whether {@param one} is less than or equal to {@param two}.
     */
    public boolean lteImpl(T one, T two) {
        return compareImpl(one, two) <= 0;
    }

    /**
     * @return whether {@param one} is less than {@param two} when coerced to this type.
     */
    public boolean lt(Number one, Number two) {
        return ltImpl(coerce(one), coerce(two));
    }

    /**
     * @return whether {@param one} is less than {@param two}.
     */
    public boolean ltImpl(T one, T two) {
        return compareImpl(one, two) < 0;
    }

    /**
     * @return whether {@param one} is equal to {@param two} when coerced to this type.
     */
    public boolean eq(Number one, Number two) {
        return eqImpl(coerce(one), coerce(two));
    }

    /**
     * @return whether {@param one} is equal to {@param two}.
     */
    public boolean eqImpl(T one, T two) {
        if (one == null || two == null)
            return one == null && two == null;

        return compareImpl(one, two) == 0;
    }

    /**
     * @return the sum of {@param one} and {@param two} when coerced to this type.
     */
    public T add(Number one, Number two) {
        return addImpl(coerce(one), coerce(two));
    }

    /**
     * @return the sum of {@param one} and {@param two}.
     */
    public abstract T addImpl(T one, T two);

    /**
     * @return the value of {@param one} minus {@param two} when coerced to this type.
     */
    public T subtract(Number one, Number two) {
        return subtractImpl(coerce(one), coerce(two));
    }

    /**
     * @return the value of {@param one} minus {@param two}.
     */
    public abstract T subtractImpl(T one, T two);

    /**
     * @return the multiplication of {@param one} and {@param two} when coerced to this type.
     */
    public T mul(Number one, Number two) {
        return mulImpl(coerce(one), coerce(two));
    }

    /**
     * @return the multiplication of {@param one} and {@param two}.
     */
    public abstract T mulImpl(T one, T two);

    /**
     * @return the division of {@param one} and {@param two} when coerced to this type.
     */
    public T div(Number one, Number two) {
        return divImpl(coerce(one), coerce(two));
    }

    /**
     * @return the division of {@param one} and {@param two}.
     */
    public abstract T divImpl(T one, T two);

    /**
     * {@code one % two}
     *
     * @return the remainder of {@param one} divided by {@param two} when coerced to this type.
     */
    public T remainder(Number one, Number two) {
        return remainderImpl(coerce(one), coerce(two));
    }

    /**
     * {@code one % two}
     *
     * @return the remainder of {@param one} divided by {@param two}.
     */
    public abstract T remainderImpl(T one, T two);

    /**
     * @return the modulus of {@param one} and {@param two} when coerced to this type.
     */
    public T modulo(Number one, Number two) {
        return moduloImpl(coerce(one), coerce(two));
    }

    /**
     * @return the modulus of {@param one} and {@param two}.
     */
    public T moduloImpl(T one, T two) {
        T remainder = remainderImpl(one, two);
        return gteImpl(remainder, coerce(0)) ? remainder : addImpl(remainder, two);
    }

    /**
     * @return the absolute value of {@param number} when coerced to this type.
     */
    public T absolute(Number number) {
        return absoluteImpl(coerce(number));
    }

    /**
     * @return the absolute value of {@param number}.
     */
    public abstract T absoluteImpl(T number);

    /**
     * @return the minimum of {@param one} and {@param two} when coerced to this type.
     */
    public T min(Number one, Number two) {
        return minImpl(coerce(one), coerce(two));
    }

    /**
     * @return the minimum of {@param one} and {@param two}.
     */
    public T minImpl(T one, T two) {
        return ltImpl(one, two) ? one : two;
    }

    /**
     * @return the maximum of {@param one} and {@param two} when coerced to this type.
     */
    public T max(Number one, Number two) {
        return maxImpl(coerce(one), coerce(two));
    }

    /**
     * @return the maximum of {@param one} and {@param two}.
     */
    public T maxImpl(T one, T two) {
        return gtImpl(one, two) ? one : two;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    /**
     * @return the number type of {@param number}.
     */
    public static NumberType<?> get(Number number) {
        Checks.assertNonNull(number, "number");

        NumberType type = BOX_TO_TYPE.get(number.getClass());
        Checks.assertThat(type != null, "Unsupported number type " + number.getClass());

        return type;
    }

    /**
     * Find the number type with the lowest byte count that is able
     * to represent all of {@param numbers} without loss of precision.
     */
    public static NumberType<?> getDominantType(Number... numbers) {
        Checks.assertArrayNonNull(numbers, "numbers");
        Checks.assertTrue(numbers.length > 0, "numbers must be of at least length 1");

        NumberType<?>[] types = new NumberType<?>[numbers.length];
        for (int index = 0; index < numbers.length; ++index) {
            types[index] = get(numbers[index]);
        }

        return getDominantType(types);
    }

    /**
     * Find the number type with the lowest byte count that is able
     * to represent all of {@param numbers} without loss of precision.
     */
    public static NumberType<?> getDominantType(NumberType<?>... types) {
        Checks.assertArrayNonNull(types, "types");
        Checks.assertTrue(types.length > 0, "types must be of at least length 1");

        NumberType<?> dominantType = types[0];

        for(int index = 1; index < types.length; ++index) {
            NumberType<?> type = types[index];

            // If both integer or both floating point
            if(dominantType.isInteger() == type.isInteger()) {
                if(type.getByteCount() > dominantType.getByteCount()) {
                    dominantType = type;
                }
                continue;
            }

            // Canonicalise dominantType to the floating point, type to the integer
            if(type.isFloatingPoint()) {
                NumberType swap = type;
                type = dominantType;
                dominantType = swap;
            }

            // Promote dominantType to a type that can represent, if possible
            NumberType<?> next = dominantType.getNextHigherPrecisionType();
            while (type.getIntegerBits() > dominantType.getIntegerBits() && next != null) {
                dominantType = next;
                next = dominantType.getNextHigherPrecisionType();
            }
        }

        return dominantType;
    }

    /**
     * @return All NumberTypes that can losslessly represent all values of the same type as {@param value}.
     *         Includes the type of {@param value} in the returned list.
     */
    public static Set<NumberType<?>> getAllCompatible(Number value) {
        return getAllCompatible(get(value));
    }

    /**
     * @return All NumberTypes that can losslessly represent all values of type {@param type}.
     *         Includes {@param type} in the returned list.
     */
    public static Set<NumberType<?>> getAllCompatible(NumberType<?> type) {
        Set<NumberType<?>> compatible = new HashSet<>();
        compatible.add(type);

        if (type.isInteger()) {
            for (NumberType<?> other : ALL) {
                if (other.getIntegerBits() < type.getIntegerBits())
                    continue;

                compatible.add(other);
            }
        } else {
            for (NumberType<?> other : FLOAT_TYPES) {
                if (other.getByteCount() < type.getByteCount())
                    continue;

                compatible.add(other);
            }
        }

        return compatible;
    }

    private static final class ByteType extends NumberType<Byte> {

        private ByteType() {
            super(
                byte.class, Byte.class, 1, false, 8,
                Byte.MIN_VALUE, Byte.MAX_VALUE
            );
        }

        @Override
        public Byte coerceImpl(Number number) {
            return number.byteValue();
        }

        @Override
        public int compareImpl(Byte one, Byte two) {
            return Byte.compare(one, two);
        }

        @Override
        public boolean gteImpl(Byte one, Byte two) {
            return one >= two;
        }

        @Override
        public boolean gtImpl(Byte one, Byte two) {
            return one > two;
        }

        @Override
        public boolean lteImpl(Byte one, Byte two) {
            return one <= two;
        }

        @Override
        public boolean ltImpl(Byte one, Byte two) {
            return one < two;
        }

        @Override
        public Byte addImpl(Byte one, Byte two) {
            return (byte) (one + two);
        }

        @Override
        public Byte subtractImpl(Byte one, Byte two) {
            return (byte) (one - two);
        }

        @Override
        public Byte mulImpl(Byte one, Byte two) {
            return (byte) (one * two);
        }

        @Override
        public Byte divImpl(Byte one, Byte two) {
            return (byte) (one / two);
        }

        @Override
        public Byte remainderImpl(Byte one, Byte two) {
            return (byte) (one % two);
        }

        @Override
        public Byte absoluteImpl(Byte number) {
            return (byte) Math.abs(number);
        }
    }

    private static final class ShortType extends NumberType<Short> {

        private ShortType() {
            super(
                short.class, Short.class, 2, false, 16,
                Short.MIN_VALUE, Short.MAX_VALUE
            );
        }

        @Override
        public Short coerceImpl(Number number) {
            return number.shortValue();
        }

        @Override
        public int compareImpl(Short one, Short two) {
            return Short.compare(one, two);
        }

        @Override
        public boolean gteImpl(Short one, Short two) {
            return one >= two;
        }

        @Override
        public boolean gtImpl(Short one, Short two) {
            return one > two;
        }

        @Override
        public boolean lteImpl(Short one, Short two) {
            return one <= two;
        }

        @Override
        public boolean ltImpl(Short one, Short two) {
            return one < two;
        }

        @Override
        public Short addImpl(Short one, Short two) {
            return (short) (one + two);
        }

        @Override
        public Short subtractImpl(Short one, Short two) {
            return (short) (one - two);
        }

        @Override
        public Short mulImpl(Short one, Short two) {
            return (short) (one * two);
        }

        @Override
        public Short divImpl(Short one, Short two) {
            return (short) (one / two);
        }

        @Override
        public Short remainderImpl(Short one, Short two) {
            return (short) (one % two);
        }

        @Override
        public Short absoluteImpl(Short number) {
            return (short) Math.abs(number);
        }
    }

    private static final class IntType extends NumberType<Integer> {

        private IntType() {
            super(
                int.class, Integer.class, 4, false, 32,
                Integer.MIN_VALUE, Integer.MAX_VALUE
            );
        }

        @Override
        public Integer coerceImpl(Number number) {
            return number.intValue();
        }

        @Override
        public int compareImpl(Integer one, Integer two) {
            return Integer.compare(one, two);
        }

        @Override
        public boolean gteImpl(Integer one, Integer two) {
            return one >= two;
        }

        @Override
        public boolean gtImpl(Integer one, Integer two) {
            return one > two;
        }

        @Override
        public boolean lteImpl(Integer one, Integer two) {
            return one <= two;
        }

        @Override
        public boolean ltImpl(Integer one, Integer two) {
            return one < two;
        }

        @Override
        public Integer addImpl(Integer one, Integer two) {
            return one + two;
        }

        @Override
        public Integer subtractImpl(Integer one, Integer two) {
            return one - two;
        }

        @Override
        public Integer mulImpl(Integer one, Integer two) {
            return one * two;
        }

        @Override
        public Integer divImpl(Integer one, Integer two) {
            return one / two;
        }

        @Override
        public Integer remainderImpl(Integer one, Integer two) {
            return one % two;
        }

        @Override
        public Integer absoluteImpl(Integer number) {
            return Math.abs(number);
        }
    }

    private static final class LongType extends NumberType<Long> {

        private LongType() {
            super(
                long.class, Long.class, 8, false, 64,
                Long.MIN_VALUE, Long.MAX_VALUE
            );
        }

        @Override
        public Long coerceImpl(Number number) {
            return number.longValue();
        }

        @Override
        public int compareImpl(Long one, Long two) {
            return Long.compare(one, two);
        }

        @Override
        public boolean gteImpl(Long one, Long two) {
            return one >= two;
        }

        @Override
        public boolean gtImpl(Long one, Long two) {
            return one > two;
        }

        @Override
        public boolean lteImpl(Long one, Long two) {
            return one <= two;
        }

        @Override
        public boolean ltImpl(Long one, Long two) {
            return one < two;
        }

        @Override
        public Long addImpl(Long one, Long two) {
            return one + two;
        }

        @Override
        public Long subtractImpl(Long one, Long two) {
            return one - two;
        }

        @Override
        public Long mulImpl(Long one, Long two) {
            return one * two;
        }

        @Override
        public Long divImpl(Long one, Long two) {
            return one / two;
        }

        @Override
        public Long remainderImpl(Long one, Long two) {
            return one % two;
        }

        @Override
        public Long absoluteImpl(Long number) {
            return Math.abs(number);
        }
    }

    private static final class BigIntType extends NumberType<BigInteger> {

        private BigIntType() {
            super(
                null, BigInteger.class, Integer.MAX_VALUE, false, Integer.MAX_VALUE,
                null, null
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
        public int compareImpl(BigInteger one, BigInteger two) {
            return one.compareTo(two);
        }

        @Override
        public BigInteger addImpl(BigInteger one, BigInteger two) {
            return one.add(two);
        }

        @Override
        public BigInteger subtractImpl(BigInteger one, BigInteger two) {
            return one.subtract(two);
        }

        @Override
        public BigInteger mulImpl(BigInteger one, BigInteger two) {
            return one.multiply(two);
        }

        @Override
        public BigInteger divImpl(BigInteger one, BigInteger two) {
            return one.divide(two);
        }

        @Override
        public BigInteger remainderImpl(BigInteger one, BigInteger two) {
            return one.remainder(two);
        }

        @Override
        public BigInteger absoluteImpl(BigInteger number) {
            return number.abs();
        }
    }

    private static final class FloatType extends NumberType<Float> {

        private FloatType() {
            super(
                float.class, Float.class, 4, true, 25,
                -Float.MAX_VALUE, Float.MAX_VALUE
            );
        }

        @Override
        public Float coerceImpl(Number number) {
            return number.floatValue();
        }

        @Override
        public int compareImpl(Float one, Float two) {
            return Float.compare(one, two);
        }

        @Override
        public boolean gteImpl(Float one, Float two) {
            return one >= two;
        }

        @Override
        public boolean gtImpl(Float one, Float two) {
            return one > two;
        }

        @Override
        public boolean lteImpl(Float one, Float two) {
            return one <= two;
        }

        @Override
        public boolean ltImpl(Float one, Float two) {
            return one < two;
        }

        @Override
        public Float addImpl(Float one, Float two) {
            return one + two;
        }

        @Override
        public Float subtractImpl(Float one, Float two) {
            return one - two;
        }

        @Override
        public Float mulImpl(Float one, Float two) {
            return one * two;
        }

        @Override
        public Float divImpl(Float one, Float two) {
            return one / two;
        }

        @Override
        public Float remainderImpl(Float one, Float two) {
            return one % two;
        }

        @Override
        public Float absoluteImpl(Float number) {
            return Math.abs(number);
        }
    }

    private static final class DoubleType extends NumberType<Double> {

        private DoubleType() {
            super(
                double.class, Double.class, 8, true, 54,
                -Double.MAX_VALUE, Double.MAX_VALUE
            );
        }

        @Override
        public Double coerceImpl(Number number) {
            return number.doubleValue();
        }

        @Override
        public int compareImpl(Double one, Double two) {
            return Double.compare(one, two);
        }

        @Override
        public boolean gteImpl(Double one, Double two) {
            return one >= two;
        }

        @Override
        public boolean gtImpl(Double one, Double two) {
            return one > two;
        }

        @Override
        public boolean lteImpl(Double one, Double two) {
            return one <= two;
        }

        @Override
        public boolean ltImpl(Double one, Double two) {
            return one < two;
        }

        @Override
        public Double addImpl(Double one, Double two) {
            return one + two;
        }

        @Override
        public Double subtractImpl(Double one, Double two) {
            return one - two;
        }

        @Override
        public Double mulImpl(Double one, Double two) {
            return one * two;
        }

        @Override
        public Double divImpl(Double one, Double two) {
            return one / two;
        }

        @Override
        public Double remainderImpl(Double one, Double two) {
            return one % two;
        }

        @Override
        public Double absoluteImpl(Double number) {
            return Math.abs(number);
        }
    }

    private static final class BigDecimalType extends NumberType<BigDecimal> {

        private BigDecimalType() {
            super(
                null, BigDecimal.class, Integer.MAX_VALUE, true, Integer.MAX_VALUE,
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
        public int compareImpl(BigDecimal one, BigDecimal two) {
            return one.compareTo(two);
        }

        @Override
        public BigDecimal addImpl(BigDecimal one, BigDecimal two) {
            return one.add(two);
        }

        @Override
        public BigDecimal subtractImpl(BigDecimal one, BigDecimal two) {
            return one.subtract(two);
        }

        @Override
        public BigDecimal mulImpl(BigDecimal one, BigDecimal two) {
            return one.multiply(two);
        }

        @Override
        public BigDecimal divImpl(BigDecimal one, BigDecimal two) {
            throw new UnsupportedOperationException("BigDecimal division is unsupported");
        }

        @Override
        public BigDecimal remainderImpl(BigDecimal one, BigDecimal two) {
            throw new UnsupportedOperationException("BigDecimal remainder is unsupported");
        }

        @Override
        public BigDecimal absoluteImpl(BigDecimal number) {
            return number.abs();
        }
    }
}
