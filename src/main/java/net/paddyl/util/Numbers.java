package net.paddyl.util;

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
     * Returns the absolute difference between {@param one} and {@param two}.
     * Will promote the type of {@param one} and {@param two} to ensure
     * there is no integer overflow.
     * e.g. absDiff(int, int) may return long
     */
    public static Number absoluteDifference(Number one, Number two) {
        NumberType<?> type = NumberType.getDominantType(one, two);

        // Avoid integer overflow if possible
        if (type.isInteger()) {
            NumberType<?> higherPrecision = type.getNextHigherPrecisionType();
            type = (higherPrecision != null ? higherPrecision : type);
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
        return NumberType.getDominantType(one, two).add(one, two);
    }

    /**
     * @return the value of {@param one} minus {@param two}.
     *         Does not avoid integer overflow.
     */
    public static Number subtract(Number one, Number two) {
        return NumberType.getDominantType(one, two).subtract(one, two);
    }
}
