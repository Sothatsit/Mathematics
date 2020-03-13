package net.paddyl.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * Provides access to NumberType instances for the Java primitives.
 */
public class NumberTypes {

    public static final NumberType<Byte> BYTE = new NumberType.ByteType();
    public static final NumberType<Short> SHORT = new NumberType.ShortType();
    public static final NumberType<Integer> INT = new NumberType.IntType();
    public static final NumberType<Long> LONG = new NumberType.LongType();
    public static final NumberType<BigInteger> BIG_INT = new NumberType.BigIntType();
    public static final NumberType<Float> FLOAT = new NumberType.FloatType();
    public static final NumberType<Double> DOUBLE = new NumberType.DoubleType();
    public static final NumberType<BigDecimal> BIG_DECIMAL = new NumberType.BigDecimalType();

    public static final NumberType<?>[] ALL = {
            BYTE, SHORT, INT, LONG, BIG_INT,
            FLOAT, DOUBLE, BIG_DECIMAL
    };

    public static NumberType<?>[] INTEGER_TYPES;
    public static NumberType<?>[] FLOAT_TYPES;

    public static NumberType<?>[] BOUNDED_INTEGER_TYPES;
    public static NumberType<?>[] BOUNDED_FLOAT_TYPES;

    public static NumberType<?>[] UNBOUNDED_INTEGER_TYPES;
    public static NumberType<?>[] UNBOUNDED_FLOAT_TYPES;
    static {
        List<NumberType<?>> integerTypes = new ArrayList<>();
        List<NumberType<?>> floatTypes = new ArrayList<>();
        List<NumberType<?>> boundedIntegerTypes = new ArrayList<>();
        List<NumberType<?>> boundedFloatTypes = new ArrayList<>();
        List<NumberType<?>> unboundedIntegerTypes = new ArrayList<>();
        List<NumberType<?>> unboundedFloatTypes = new ArrayList<>();

        for (NumberType<?> type : ALL) {
            if (type.isInteger) integerTypes.add(type);
            if (type.isFloatingPoint) floatTypes.add(type);
            if (type.isBounded && type.isInteger) boundedIntegerTypes.add(type);
            if (type.isBounded && type.isFloatingPoint) boundedFloatTypes.add(type);
            if (!type.isBounded && type.isInteger) unboundedIntegerTypes.add(type);
            if (!type.isBounded && type.isFloatingPoint) unboundedFloatTypes.add(type);
        }

        INTEGER_TYPES = integerTypes.toArray(new NumberType<?>[integerTypes.size()]);
        FLOAT_TYPES = floatTypes.toArray(new NumberType<?>[floatTypes.size()]);
        BOUNDED_INTEGER_TYPES = boundedIntegerTypes.toArray(new NumberType<?>[boundedIntegerTypes.size()]);
        BOUNDED_FLOAT_TYPES = boundedFloatTypes.toArray(new NumberType<?>[boundedFloatTypes.size()]);
        UNBOUNDED_INTEGER_TYPES = unboundedIntegerTypes.toArray(new NumberType<?>[unboundedIntegerTypes.size()]);
        UNBOUNDED_FLOAT_TYPES = unboundedFloatTypes.toArray(new NumberType<?>[unboundedFloatTypes.size()]);
    }

    private static final Map<Class<? extends Number>, NumberType> BOX_TO_TYPE = new HashMap<>();
    static {
        for(NumberType<?> type : ALL) {
            NumberType previous = BOX_TO_TYPE.put(type.boxClass, type);
            Checks.assertThat(previous == null, "duplicate NumberType entry found for data type " + type);
        }
    }

    /**
     * This class shouldn't be instantiated.
     */
    private NumberTypes() {}

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
            if(dominantType.isInteger == type.isInteger) {
                if(type.byteCount > dominantType.byteCount) {
                    dominantType = type;
                }
                continue;
            }

            // Canonicalise dominantType to the floating point, type to the integer
            if(type.isFloatingPoint) {
                NumberType swap = type;
                type = dominantType;
                dominantType = swap;
            }

            // Promote dominantType to a type that can represent, if possible
            NumberType<?> next = getNextHigherPrecisionType(dominantType);
            while (type.integerBitCount > dominantType.integerBitCount && next != null) {
                dominantType = next;
                next = getNextHigherPrecisionType(dominantType);
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

        if (type.isInteger) {
            for (NumberType<?> other : ALL) {
                if (other.integerBitCount < type.integerBitCount)
                    continue;

                compatible.add(other);
            }
        } else {
            for (NumberType<?> other : FLOAT_TYPES) {
                if (other.byteCount < type.byteCount)
                    continue;

                compatible.add(other);
            }
        }

        return compatible;
    }

    /**
     * @return The number type just below the precision of {@param type}.
     *         If floating point, the next lower floating point type,
     *         else the next lower integer type.
     */
    public static NumberType<?> getNextLowerPrecisionType(NumberType<?> type) {
        NumberType<?> highestBelow = null;
        for (NumberType<?> otherType : type.isFloatingPoint ? FLOAT_TYPES : INTEGER_TYPES) {
            if (otherType.byteCount >= type.byteCount)
                continue;
            if (highestBelow != null && otherType.byteCount < highestBelow.byteCount)
                continue;
            highestBelow = otherType;
        }
        return highestBelow;
    }

    /**
     * @return The number type just above the precision of {@param type}.
     *         If floating point, the next higher floating point type,
     *         else the next higher integer type.
     */
    public static NumberType<?> getNextHigherPrecisionType(NumberType<?> type) {
        NumberType<?> lowestAbove = null;
        for (NumberType<?> otherType : type.isFloatingPoint ? FLOAT_TYPES : INTEGER_TYPES) {
            if (otherType.byteCount <= type.byteCount)
                continue;
            if (lowestAbove != null && otherType.byteCount > lowestAbove.byteCount)
                continue;
            lowestAbove = otherType;
        }
        return lowestAbove;
    }
}
