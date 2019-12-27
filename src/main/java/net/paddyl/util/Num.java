package net.paddyl.util;

/**
 * Contains shorthand aliases for methods in {@link Numbers}.
 *
 * @author Paddy Lamont
 */
public class Num {

    /**
     * @see NumberType#getDominantType(Number...)
     */
    public static NumberType<?> type(Number... numbers) {
        return NumberType.getDominantType(numbers);
    }

    /**
     * @see Numbers#compare(Number, Number)
     */
    public static int cmp(Number one, Number two) {
        return Numbers.compare(one, two);
    }

    /**
     * @see Numbers#equal(Number, Number)
     */
    public static boolean eq(Number one, Number two) {
        return Numbers.equal(one, two);
    }

    /**
     * @see Numbers#lessThan(Number, Number)
     */
    public static boolean lt(Number one, Number two) {
        return Numbers.lessThan(one, two);
    }

    /**
     * @see Numbers#lessThanOrEqual(Number, Number)
     */
    public static boolean lte(Number one, Number two) {
        return Numbers.lessThanOrEqual(one, two);
    }

    /**
     * @see Numbers#greaterThan(Number, Number)
     */
    public static boolean gt(Number one, Number two) {
        return Numbers.greaterThan(one, two);
    }

    /**
     * @see Numbers#greaterThanOrEqual(Number, Number)
     */
    public static boolean gte(Number one, Number two) {
        return Numbers.greaterThanOrEqual(one, two);
    }

    /**
     * @see Numbers#absoluteDifference(Number, Number)
     */
    public static Number absDiff(Number one, Number two) {
        return Numbers.absoluteDifference(one, two);
    }

    /**
     * @see Numbers#equal(Number, Number, Number)
     */
    public static boolean eq(Number one, Number two, Number epsilon) {
        return Numbers.equal(one, two, epsilon);
    }

    /**
     * @see Numbers#subtract(Number, Number)
     */
    public static Number sub(Number one, Number two) {
        return Numbers.subtract(one, two);
    }

    /**
     * @see Numbers#add(Number, Number)
     */
    public static Number add(Number one, Number two) {
        return Numbers.add(one, two);
    }
}
