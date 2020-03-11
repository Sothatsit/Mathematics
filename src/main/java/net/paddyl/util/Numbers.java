package net.paddyl.util;

/**
 * Methods for dealing with generic Numbers instead of
 * requiring duplicating methods for each number type.
 *
 * Note this is not efficient. It requires lookups
 * for type classes and boxing/unboxing primitives.
 *
 * If more efficiency is required, consider caching
 * the NumberType that the operations are to be
 * performed in and using it directly.
 *
 * @author Paddy Lamont
 */
@SuppressWarnings("unchecked")
public class Numbers {

    /**
     * @see NumberTypes#getDominantType(Number...)
     */
    public static NumberType<?> getDominantType(Number... numbers) {
        return NumberTypes.getDominantType(numbers);
    }

    /**
     * @return the value {@code 0} if {@code one == two};
     *          a value less than {@code 0} if {@code one < two}; and
     *          a value greater than {@code 0} if {@code one > two}
     */
    public static int compare(Number one, Number two) {
        NumberType type = getDominantType(one, two);
        return type.compare(type.coerce(one), type.coerce(two));
    }

    /**
     * @return {@code true} iff {@code one == two}, else {@code false}
     */
    public static boolean equal(Number one, Number two) {
        NumberType type = getDominantType(one, two);
        return type.eq(type.coerce(one), type.coerce(two));
    }

    /**
     * @return {@code true} iff {@code one > two}, else {@code false}
     */
    public static boolean greaterThan(Number one, Number two) {
        NumberType type = getDominantType(one, two);
        return type.gt(type.coerce(one), type.coerce(two));
    }

    /**
     * @return {@code true} if {@code one < two}, else {@code false}
     */
    public static boolean lessThan(Number one, Number two) {
        NumberType type = getDominantType(one, two);
        return type.lt(type.coerce(one), type.coerce(two));
    }

    /**
     * @return {@code true} if {@code one >= two}, else {@code false}
     */
    public static boolean greaterThanOrEqual(Number one, Number two) {
        NumberType type = getDominantType(one, two);
        return type.gte(type.coerce(one), type.coerce(two));
    }

    /**
     * @return {@code true} if {@code one <= two}, else {@code false}
     */
    public static boolean lessThanOrEqual(Number one, Number two) {
        NumberType type = getDominantType(one, two);
        return type.lte(type.coerce(one), type.coerce(two));
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
     * @return the sum of {@param one} and {@param two}.
     *         Does not avoid integer overflow.
     */
    public static Number add(Number one, Number two) {
        NumberType type = getDominantType(one, two);
        return type.add(type.coerce(one), type.coerce(two));
    }

    /**
     * @return the value of {@param one} minus {@param two}.
     *         Does not avoid integer overflow.
     */
    public static Number subtract(Number one, Number two) {
        NumberType type = getDominantType(one, two);
        return type.subtract(type.coerce(one), type.coerce(two));
    }

    private static NumberType<?> getExactAddSubtractType(Number... numbers) {
        NumberType<?> type = getDominantType(numbers);

        if (type.isFloatingPoint)
            return NumberTypes.BIG_DECIMAL;

        NumberType<?> higherPrecision = NumberTypes.getNextHigherPrecisionType(type);
        return higherPrecision != null ? higherPrecision : type;
    }

    /**
     * Returns the sum of {@param one} and {@param two}, avoiding integer overflow and floating point errors.
     */
    public static Number addExact(Number one, Number two) {
        NumberType type = getExactAddSubtractType(one, two);
        return type.add(type.coerce(one), type.coerce(two));
    }

    /**
     * Returns the value of {@param one} minus {@param two}, avoiding integer overflow and floating point errors.
     */
    public static Number subtractExact(Number one, Number two) {
        NumberType type = getExactAddSubtractType(one, two);
        return type.subtract(type.coerce(one), type.coerce(two));
    }

    /**
     * @return the absolute value of {@param value}, avoiding overflow.
     */
    public static Number absolute(Number value) {
        NumberType type = NumberTypes.get(value);

        if (type.isInteger) {
            NumberType higherPrecision = NumberTypes.getNextHigherPrecisionType(type);
            type = (higherPrecision != null ? higherPrecision : type);
        }

        return type.absolute(type.coerce(value));
    }

    /**
     * Returns the absolute difference between {@param one} and {@param two}.
     * Avoids integer overflow and floating point error by promoting types.
     */
    public static Number absoluteDifference(Number one, Number two) {
        return absolute(subtractExact(one, two));
    }
}
