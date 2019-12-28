package net.paddyl.util;

import sun.reflect.CallerSensitive;

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
    public int compare(Number one, Number two) {
        return compareImpl(coerce(one), coerce(two));
    }

    protected abstract int compareImpl(T one, T two);

    /**
     * @return the sum of {@param one} and {@param two} in this number type.
     */
    public T add(Number one, Number two) {
        return addImpl(coerce(one), coerce(two));
    }

    protected abstract T addImpl(T one, T two);

    /**
     * @return the value of {@param one} minus {@param two} in this number type.
     */
    public T subtract(Number one, Number two) {
        return subtractImpl(coerce(one), coerce(two));
    }

    protected abstract T subtractImpl(T one, T two);

    /**
     * @return the absolute value of {@param number} in this number type.
     */
    public T absolute(Number number) {
        return absoluteImpl(coerce(number));
    }

    protected abstract T absoluteImpl(T number);

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
        protected Byte addImpl(Byte one, Byte two) {
            return (byte) (one + two);
        }

        @Override
        protected Byte subtractImpl(Byte one, Byte two) {
            return (byte) (one - two);
        }

        @Override
        protected Byte absoluteImpl(Byte number) {
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
        protected Short addImpl(Short one, Short two) {
            return (short) (one + two);
        }

        @Override
        protected Short subtractImpl(Short one, Short two) {
            return (short) (one - two);
        }

        @Override
        protected Short absoluteImpl(Short number) {
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
        protected Integer addImpl(Integer one, Integer two) {
            return one + two;
        }

        @Override
        protected Integer subtractImpl(Integer one, Integer two) {
            return one - two;
        }

        @Override
        protected Integer absoluteImpl(Integer number) {
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
        protected Long addImpl(Long one, Long two) {
            return one + two;
        }

        @Override
        protected Long subtractImpl(Long one, Long two) {
            return one - two;
        }

        @Override
        protected Long absoluteImpl(Long number) {
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
        protected BigInteger addImpl(BigInteger one, BigInteger two) {
            return one.add(two);
        }

        @Override
        protected BigInteger subtractImpl(BigInteger one, BigInteger two) {
            return one.subtract(two);
        }

        @Override
        protected BigInteger absoluteImpl(BigInteger number) {
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
        protected Float addImpl(Float one, Float two) {
            return one + two;
        }

        @Override
        protected Float subtractImpl(Float one, Float two) {
            return one - two;
        }

        @Override
        protected Float absoluteImpl(Float number) {
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
        protected Double addImpl(Double one, Double two) {
            return one + two;
        }

        @Override
        protected Double subtractImpl(Double one, Double two) {
            return one - two;
        }

        @Override
        protected Double absoluteImpl(Double number) {
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
        protected BigDecimal addImpl(BigDecimal one, BigDecimal two) {
            return one.add(two);
        }

        @Override
        protected BigDecimal subtractImpl(BigDecimal one, BigDecimal two) {
            return one.subtract(two);
        }

        @Override
        protected BigDecimal absoluteImpl(BigDecimal number) {
            return number.abs();
        }
    }
}
