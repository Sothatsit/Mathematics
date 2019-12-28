package net.paddyl.util;

/**
 * Methods for dealing with generic Numbers instead of
 * requiring duplicating methods for each number type.
 *
 * Note this is not efficient. It requires lookups
 * for type classes and boxing/unboxing primitives.
 *
 * @author Paddy Lamont
 */
public class Numbers {

    /**
     * @return the value {@code 0} if {@code one == two};
     *          a value less than {@code 0} if {@code one < two}; and
     *          a value greater than {@code 0} if {@code one > two}
     */
    public static int compare(Number one, Number two) {
        return NumberType.getDominantType(one, two).compare(one, two);
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
        return NumberType.getDominantType(one, two).add(one, two);
    }

    /**
     * @return the value of {@param one} minus {@param two}.
     *         Does not avoid integer overflow.
     */
    public static Number subtract(Number one, Number two) {
        return NumberType.getDominantType(one, two).subtract(one, two);
    }

    private static NumberType<?> getExactAddSubtractType(Number... numbers) {
        NumberType<?> type = NumberType.getDominantType(numbers);

        if (type.isFloatingPoint())
            return NumberType.BIG_DECIMAL;

        NumberType<?> higherPrecision = type.getNextHigherPrecisionType();
        return higherPrecision != null ? higherPrecision : type;
    }

    /**
     * Returns the sum of {@param one} and {@param two}, avoiding integer overflow and floating point errors.
     */
    public static Number addExact(Number one, Number two) {
        return getExactAddSubtractType(one, two).add(one, two);
    }

    /**
     * Returns the value of {@param one} minus {@param two}, avoiding integer overflow and floating point errors.
     */
    public static Number subtractExact(Number one, Number two) {
        return getExactAddSubtractType(one, two).subtract(one, two);
    }

    /**
     * @return the absolute value of {@param value}, and avoids integer overflow.
     */
    public static Number absolute(Number value) {
        NumberType<?> type = NumberType.get(value);

        if (type.isInteger()) {
            NumberType<?> higherPrecision = type.getNextHigherPrecisionType();
            type = (higherPrecision != null ? higherPrecision : type);
        }

        return type.absolute(value);
    }

    /**
     * Returns the absolute difference between {@param one} and {@param two}.
     * Avoids integer overflow and floating point error by promoting types.
     */
    public static Number absoluteDifference(Number one, Number two) {
        return absolute(subtractExact(one, two));
    }
}
