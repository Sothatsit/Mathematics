package net.paddyl.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/**
 * Methods for dealing with generic Numbers instead of
 * requiring duplicating methods for each number type.
 *
 * Note this is not efficient. It requires lookups
 * for type classes and boxing/unboxing primitives.
 * It should be used sparingly.
 *
 * @author Paddy Lamont
 */
public class Numbers {

    /**
     * Generic handling for Java's primitive number types.
     */
    public static abstract class NumberType<T extends Number> {

        public static final NumberType<Byte> BYTE = new NumberType<Byte>(
                byte.class, Byte.class, 1, false
        ) {
            @Override
            public NumberType<?> getNextHigherPrecisionType() {
                return SHORT;
            }

            @Override
            public Byte coerce(Number number) {
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
        };

        public static final NumberType<Short> SHORT = new NumberType<Short>(
                short.class, Short.class, 2, false
        ) {
            @Override
            public NumberType<?> getNextHigherPrecisionType() {
                return INT;
            }

            @Override
            public Short coerce(Number number) {
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
        };

        public static final NumberType<Integer> INT = new NumberType<Integer>(
                int.class, Integer.class, 4, false
        ) {
            @Override
            public NumberType<?> getNextHigherPrecisionType() {
                return LONG;
            }

            @Override
            public Integer coerce(Number number) {
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
        };

        public static final NumberType<Long> LONG = new NumberType<Long>(
                long.class, Long.class, 8, false
        ) {
            @Override
            public NumberType<?> getNextHigherPrecisionType() {
                return BIG_INTEGER;
            }

            @Override
            public Long coerce(Number number) {
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
        };

        public static final NumberType<BigInteger> BIG_INTEGER = new NumberType<BigInteger>(
                null, BigInteger.class, Integer.MAX_VALUE, false
        ) {
            @Override
            public NumberType<?> getNextHigherPrecisionType() {
                return this;
            }

            @Override
            public BigInteger coerce(Number number) {
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
        };

        public static final NumberType<Float> FLOAT = new NumberType<Float>(
                float.class, Float.class, 4, true, 24
        ) {
            @Override
            public NumberType<?> getNextHigherPrecisionType() {
                return DOUBLE;
            }

            @Override
            public Float coerce(Number number) {
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
        };

        public static final NumberType<Double> DOUBLE = new NumberType<Double>(
                double.class, Double.class, 8, true, 53
        ) {
            @Override
            public NumberType<?> getNextHigherPrecisionType() {
                return BIG_DECIMAL;
            }

            @Override
            public Double coerce(Number number) {
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
        };

        public static final NumberType<BigDecimal> BIG_DECIMAL = new NumberType<BigDecimal>(
                null, BigDecimal.class, Integer.MAX_VALUE, true, Integer.MAX_VALUE
        ) {
            @Override
            public NumberType<?> getNextHigherPrecisionType() {
                return this;
            }

            @Override
            public BigDecimal coerce(Number number) {
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
        };

        public static final NumberType<?>[] ALL = {BYTE, SHORT, INT, BIG_INTEGER, LONG, FLOAT, DOUBLE, BIG_DECIMAL};

        private static final Map<Class<? extends Number>, NumberType> classToTypes = new HashMap<>();
        static {
            for(NumberType<?> type : ALL) {
                NumberType previous = classToTypes.put(type.getBoxClass(), type);
                Checks.assertThat(previous == null, "duplicate NumberType entry found for data type " + type);
            }
        }

        private final Class<?> primitiveClass;
        private final Class<T> boxClass;
        private final int byteCount;
        private final boolean floatingPoint;
        private final int maxRepresentableIntegerBits;

        private NumberType(Class<?> primitiveClass,
                           Class<T> boxClass,
                           int byteCount,
                           boolean floatingPoint) {

            this(primitiveClass, boxClass, byteCount, false, byteCount);

            Checks.assertThat(!floatingPoint, "use the other constructor for floating point");
        }

        private NumberType(Class<?> primitiveClass,
                           Class<T> boxClass,
                           int byteCount,
                           boolean floatingPoint,
                           int maxRepresentableIntegerBits) {

            Checks.assertNonNull(boxClass, "boxClass");
            Checks.assertThat(byteCount > 0, "byteCount must be positive");

            this.primitiveClass = primitiveClass;
            this.boxClass = boxClass;
            this.byteCount = byteCount;
            this.floatingPoint = floatingPoint;
            this.maxRepresentableIntegerBits = maxRepresentableIntegerBits;
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

        public int getMaxRepresentableIntegerBits() {
            return maxRepresentableIntegerBits;
        }

        public abstract NumberType<?> getNextHigherPrecisionType();

        public abstract T coerce(Number number);

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

        public static NumberType get(Number number) {
            Checks.assertNonNull(number, "number");

            NumberType type = classToTypes.get(number.getClass());
            Checks.assertThat(type != null, "Unknown number type " + number + " (" + number.getClass() + ")");

            return type;
        }
    }

    /**
     * Attempts to find the best number type to represent all
     * of {@param numbers} with the least loss of precision.
     *
     * Will promote to largest bit representation when all integers or all floating point.
     *
     * If there are integers and floating point numbers, will return either:
     * 1) FLOAT if all numbers are byte, short or floats
     * 2) DOUBLE if any numbers are int, long or double
     */
    public static NumberType getDominantType(Number... numbers) {
        Checks.assertArrayNonNull(numbers, "numbers");
        Checks.assertTrue(numbers.length > 0, "numbers must be of at least length 1");

        NumberType dominantType = NumberType.get(numbers[0]);

        for(int index = 1; index < numbers.length; ++index) {
            NumberType type = NumberType.get(numbers[index]);

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

            // If we lose precision by converting the integer type to the floating point type
            if(type.getByteCount() > dominantType.getMaxRepresentableIntegerBits()) {
                dominantType = NumberType.DOUBLE;
            }
        }

        return dominantType;
    }

    /**
     * @return the value {@code 0} if {@code one == two};
     *          a value less than {@code 0} if {@code one < two}; and
     *          a value greater than {@code 0} if {@code one > two}
     */
    public static int compare(Number one, Number two) {
        return getDominantType(one, two).compare(one, two);
    }

    /**
     * @return {@code true} iff {@code one == two}, else {@code false}
     */
    public static boolean equal(Number one, Number two) {
        return compare(one, two) == 0;
    }

    /**
     * @return {@code true} iff {@code one > two}, else {@code false}
     */
    public static boolean greaterThan(Number one, Number two) {
        return compare(one, two) > 0;
    }

    /**
     * @return {@code true} if {@code one < two}, else {@code false}
     */
    public static boolean lessThan(Number one, Number two) {
        return compare(one, two) < 0;
    }

    /**
     * @return {@code true} if {@code one >= two}, else {@code false}
     */
    public static boolean greaterThanOrEqual(Number one, Number two) {
        return compare(one, two) >= 0;
    }

    /**
     * @return {@code true} if {@code one <= two}, else {@code false}
     */
    public static boolean lessThanOrEqual(Number one, Number two) {
        return compare(one, two) <= 0;
    }

    /**
     * Returns the absolute difference between {@param one} and {@param two}.
     * Will promote the type of {@param one} and {@param two} to ensure
     * there is no integer overflow.
     * e.g. absDiff(int, int) may return long
     */
    public static Number absoluteDifference(Number one, Number two) {
        NumberType<?> type = getDominantType(one, two);

        // Avoid integer overflow
        if (type.isInteger()) {
            type = type.getNextHigherPrecisionType();
        }

        return type.absolute(type.subtract(one, two));
    }

    /**
     * @return {@code true} if the difference between {@param one} and {@param two}
     *         is less than or equal to {@param epsilon}, else {@code false}.
     */
    public static boolean equal(Number one, Number two, Number epsilon) {
        Number difference = absoluteDifference(one, two);
        return lessThanOrEqual(difference, epsilon);
    }

    /**
     * @return the value of {@param one} added to {@param two}.
     *         Does not avoid integer overflow.
     */
    public static Number add(Number one, Number two) {
        return getDominantType(one, two).add(one, two);
    }

    /**
     * @return the value of {@param one} minus {@param two}.
     *         Does not avoid integer overflow.
     */
    public static Number subtract(Number one, Number two) {
        return getDominantType(one, two).subtract(one, two);
    }
}
