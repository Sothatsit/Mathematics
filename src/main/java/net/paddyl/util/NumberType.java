package net.paddyl.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

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

    private NumberType(Class<?> primitiveClass,
                       Class<T> boxClass,
                       int byteCount,
                       boolean floatingPoint,
                       int integerBitCount) {

        Checks.assertNonNull(boxClass, "boxClass");

        // Can't use assertPositive, as it uses NumberType
        Checks.assertThat(byteCount > 0, "byteCount must be greater than 0");
        Checks.assertThat(integerBitCount > 0, "integerBitCount must be greater than 0");

        this.primitiveClass = primitiveClass;
        this.boxClass = boxClass;
        this.byteCount = byteCount;
        this.floatingPoint = floatingPoint;
        this.integerBitCount = integerBitCount;
    }

    public Class<?> getPrimitiveClass() {
        return primitiveClass;
    }

    public Class<T> getBoxClass() {
        return boxClass;
    }

    public int getByteCount() {
        return byteCount;
    }

    public boolean isInteger() {
        return !floatingPoint;
    }

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

    // TODO : getNextLowerPrecisionType and getNextHigherPrecisionType could just use ALL,
    //        and not have to be overridden by every NumberType sub-class

    /**
     * @return The number type just below the precision of this one.
     *         If floating point, the next lower floating point type,
     *         else the next lower integer type.
     */
    public abstract NumberType<?> getNextLowerPrecisionType();

    /**
     * @return The number type just above the precision of this one.
     *         If floating point, the next higher floating point type,
     *         else the next higher integer type.
     */
    public abstract NumberType<?> getNextHigherPrecisionType();

    /**
     * @return {@param number} converted to this number type.
     */
    public T coerce(Number number) {
        if (number.getClass().equals(boxClass))
            return Cast.cast(number);
        return coerceImpl(number);
    }

    protected abstract T coerceImpl(Number number);

    public int compare(Number one, Number two) {
        return compareImpl(coerce(one), coerce(two));
    }

    protected abstract int compareImpl(T one, T two);

    public T add(Number one, Number two) {
        return addImpl(coerce(one), coerce(two));
    }

    protected abstract T addImpl(T one, T two);

    public T subtract(Number one, Number two) {
        return subtractImpl(coerce(one), coerce(two));
    }

    protected abstract T subtractImpl(T one, T two);

    public T absolute(Number number) {
        return absoluteImpl(coerce(number));
    }

    protected abstract T absoluteImpl(T number);

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

        NumberType<?> dominantType = NumberType.get(numbers[0]);

        for(int index = 1; index < numbers.length; ++index) {
            NumberType<?> type = NumberType.get(numbers[index]);

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

    private static final class ByteType extends NumberType<Byte> {

        private ByteType() {
            super(byte.class, Byte.class, 1, false, 8);
        }

        @Override
        public NumberType<?> getNextLowerPrecisionType() {
            return null;
        }

        @Override
        public NumberType<?> getNextHigherPrecisionType() {
            return SHORT;
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
            super(short.class, Short.class, 2, false, 16);
        }

        @Override
        public NumberType<?> getNextLowerPrecisionType() {
            return BYTE;
        }

        @Override
        public NumberType<?> getNextHigherPrecisionType() {
            return INT;
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
            super(int.class, Integer.class, 4, false, 32);
        }

        @Override
        public NumberType<?> getNextLowerPrecisionType() {
            return SHORT;
        }

        @Override
        public NumberType<?> getNextHigherPrecisionType() {
            return LONG;
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
            super(long.class, Long.class, 8, false, 64);
        }

        @Override
        public NumberType<?> getNextLowerPrecisionType() {
            return INT;
        }

        @Override
        public NumberType<?> getNextHigherPrecisionType() {
            return BIG_INT;
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
            super(null, BigInteger.class, Integer.MAX_VALUE, false, Integer.MAX_VALUE);
        }

        @Override
        public NumberType<?> getNextLowerPrecisionType() {
            return LONG;
        }

        @Override
        public NumberType<?> getNextHigherPrecisionType() {
            return null;
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
            super(float.class, Float.class, 4, true, 25);
        }

        @Override
        public NumberType<?> getNextLowerPrecisionType() {
            return null;
        }

        @Override
        public NumberType<?> getNextHigherPrecisionType() {
            return DOUBLE;
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
            super(double.class, Double.class, 8, true, 54);
        }

        @Override
        public NumberType<?> getNextLowerPrecisionType() {
            return FLOAT;
        }

        @Override
        public NumberType<?> getNextHigherPrecisionType() {
            return BIG_DECIMAL;
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
            super(null, BigDecimal.class, Integer.MAX_VALUE, true, Integer.MAX_VALUE);
        }

        @Override
        public NumberType<?> getNextLowerPrecisionType() {
            return DOUBLE;
        }

        @Override
        public NumberType<?> getNextHigherPrecisionType() {
            return null;
        }

        @Override
        public BigDecimal coerceImpl(Number number) {
            if (number instanceof BigDecimal)
                return (BigDecimal) number;
            if (number instanceof BigInteger)
                return new BigDecimal((BigInteger) number);
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
