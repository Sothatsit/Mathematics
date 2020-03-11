package net.paddyl.util;

import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.*;

import static net.paddyl.util.Checks.*;

public class NumbersTest {

    @Test
    public void testNumberTypeGet() {
        assertEquals(NumberTypes.BYTE,        NumberTypes.get((byte) 0));
        assertEquals(NumberTypes.SHORT,       NumberTypes.get((short) 0));
        assertEquals(NumberTypes.INT,         NumberTypes.get(0));
        assertEquals(NumberTypes.LONG,        NumberTypes.get(0L));
        assertEquals(NumberTypes.BIG_INT,     NumberTypes.get(BigInteger.ZERO));
        assertEquals(NumberTypes.FLOAT,       NumberTypes.get(0.0f));
        assertEquals(NumberTypes.DOUBLE,      NumberTypes.get(0.0d));
        assertEquals(NumberTypes.BIG_DECIMAL, NumberTypes.get(BigDecimal.ZERO));
    }

    private static void testType(NumberType<?> expected, Number... numbers) {
        assertEquals(expected, Num.type(numbers));
        assertEquals(expected, NumberTypes.getDominantType(numbers));
    }

    @Test
    public void testGetNextHigherPrecisionType() {
        assertEquals(NumberTypes.SHORT, NumberTypes.getNextHigherPrecisionType(NumberTypes.BYTE));
        assertEquals(NumberTypes.INT, NumberTypes.getNextHigherPrecisionType(NumberTypes.SHORT));
        assertEquals(NumberTypes.LONG, NumberTypes.getNextHigherPrecisionType(NumberTypes.INT));
        assertEquals(NumberTypes.BIG_INT, NumberTypes.getNextHigherPrecisionType(NumberTypes.LONG));
        assertEquals(null, NumberTypes.getNextHigherPrecisionType(NumberTypes.BIG_INT));

        assertEquals(NumberTypes.DOUBLE, NumberTypes.getNextHigherPrecisionType(NumberTypes.FLOAT));
        assertEquals(NumberTypes.BIG_DECIMAL, NumberTypes.getNextHigherPrecisionType(NumberTypes.DOUBLE));
        assertEquals(null, NumberTypes.getNextHigherPrecisionType(NumberTypes.BIG_DECIMAL));
    }

    @Test
    public void testGetNextLowerPrecisionType() {
        assertEquals(null, NumberTypes.getNextLowerPrecisionType(NumberTypes.BYTE));
        assertEquals(NumberTypes.BYTE, NumberTypes.getNextLowerPrecisionType(NumberTypes.SHORT));
        assertEquals(NumberTypes.SHORT, NumberTypes.getNextLowerPrecisionType(NumberTypes.INT));
        assertEquals(NumberTypes.INT, NumberTypes.getNextLowerPrecisionType(NumberTypes.LONG));
        assertEquals(NumberTypes.LONG, NumberTypes.getNextLowerPrecisionType(NumberTypes.BIG_INT));

        assertEquals(null, NumberTypes.getNextLowerPrecisionType(NumberTypes.FLOAT));
        assertEquals(NumberTypes.FLOAT, NumberTypes.getNextLowerPrecisionType(NumberTypes.DOUBLE));
        assertEquals(NumberTypes.DOUBLE, NumberTypes.getNextLowerPrecisionType(NumberTypes.BIG_DECIMAL));
    }

    @Test
    public void testNumberTypeGetDominantType() {
        assertFails(() -> Num.type());
        assertFails(() -> NumberTypes.getDominantType(new Number[0]));
        assertFails(() -> Num.type((Number[]) null));
        assertFails(() -> NumberTypes.getDominantType((Number[]) null));
        assertFails(() -> NumberTypes.getDominantType(new NumberType[0]));
        assertFails(() -> NumberTypes.getDominantType((NumberType[]) null));

        // One number
        testType(NumberTypes.BYTE,        (byte) 0);
        testType(NumberTypes.SHORT,       (short) 0);
        testType(NumberTypes.INT,         0);
        testType(NumberTypes.LONG,        0L);
        testType(NumberTypes.BIG_INT,     BigInteger.ZERO);
        testType(NumberTypes.FLOAT,       0.0f);
        testType(NumberTypes.DOUBLE,      0.0d);
        testType(NumberTypes.BIG_DECIMAL, BigDecimal.ZERO);

        // Two integer numbers
        testType(NumberTypes.BYTE,    (byte) 0, (byte) 1);
        testType(NumberTypes.SHORT,   (short) 0, (short) 1);
        testType(NumberTypes.SHORT,   (short) 0, (byte) 1);
        testType(NumberTypes.INT,     0, (byte) 1);
        testType(NumberTypes.INT,     0, (short) 1);
        testType(NumberTypes.INT,     0, 1);
        testType(NumberTypes.LONG,    0L, (short) 1);
        testType(NumberTypes.LONG,    0L, (byte) 1);
        testType(NumberTypes.LONG,    0L, 1);
        testType(NumberTypes.LONG,    0L, 1L);
        testType(NumberTypes.BIG_INT, BigInteger.ZERO, (short) 1);
        testType(NumberTypes.BIG_INT, BigInteger.ZERO, (byte) 1);
        testType(NumberTypes.BIG_INT, BigInteger.ZERO, 1);
        testType(NumberTypes.BIG_INT, BigInteger.ZERO, 1L);
        testType(NumberTypes.BIG_INT, BigInteger.ZERO, BigInteger.ONE);

        // Two floating point numbers
        testType(NumberTypes.FLOAT,       0.0f, 1.0f);
        testType(NumberTypes.DOUBLE,      0.0d, 1.0f);
        testType(NumberTypes.DOUBLE,      0.0d, 1.0d);
        testType(NumberTypes.BIG_DECIMAL, BigDecimal.ZERO, 1.0f);
        testType(NumberTypes.BIG_DECIMAL, BigDecimal.ZERO, 1.0d);
        testType(NumberTypes.BIG_DECIMAL, BigDecimal.ZERO, BigDecimal.ONE);

        // One floating point number, one integer number
        testType(NumberTypes.FLOAT,       0.0f, (byte) 1);
        testType(NumberTypes.FLOAT,       0.0f, (short) 1);
        testType(NumberTypes.DOUBLE,      0.0f, 1);
        testType(NumberTypes.DOUBLE,      0.0d, (byte) 1);
        testType(NumberTypes.DOUBLE,      0.0d, (short) 1);
        testType(NumberTypes.DOUBLE,      0.0d, 1);
        testType(NumberTypes.BIG_DECIMAL, 0.0f, 1L);
        testType(NumberTypes.BIG_DECIMAL, 0.0d, 1L);
        testType(NumberTypes.BIG_DECIMAL, 0.0f, BigInteger.ONE);
        testType(NumberTypes.BIG_DECIMAL, 0.0d, BigInteger.ONE);
        testType(NumberTypes.BIG_DECIMAL, BigDecimal.ZERO, (byte) 1);
        testType(NumberTypes.BIG_DECIMAL, BigDecimal.ZERO, (short) 1);
        testType(NumberTypes.BIG_DECIMAL, BigDecimal.ZERO, 1);
        testType(NumberTypes.BIG_DECIMAL, BigDecimal.ZERO, 1L);
        testType(NumberTypes.BIG_DECIMAL, BigDecimal.ZERO, BigInteger.ONE);

        // Two floating point numbers, one integer number
        testType(NumberTypes.FLOAT,       0.0f, (byte) 1, 2.0f);
        testType(NumberTypes.FLOAT,       0.0f, (short) 1, 2.0f);
        testType(NumberTypes.DOUBLE,      0.0f, 1, 2.0f);
        testType(NumberTypes.DOUBLE,      0.0d, (byte) 1, 2.0f);
        testType(NumberTypes.DOUBLE,      0.0d, (short) 1, 2.0f);
        testType(NumberTypes.DOUBLE,      0.0d, 1, 2.0f);
        testType(NumberTypes.BIG_DECIMAL, 0.0f, 1L, 2.0f);
        testType(NumberTypes.BIG_DECIMAL, 0.0d, 1L, 2.0f);
        testType(NumberTypes.BIG_DECIMAL, 0.0f, BigInteger.ONE, 2.0f);
        testType(NumberTypes.BIG_DECIMAL, 0.0d, BigInteger.ONE, 2.0f);
        testType(NumberTypes.BIG_DECIMAL, BigDecimal.ZERO, (byte) 1, 2.0f);
        testType(NumberTypes.BIG_DECIMAL, BigDecimal.ZERO, (short) 1, 2.0f);
        testType(NumberTypes.BIG_DECIMAL, BigDecimal.ZERO, 1, 2.0f);
        testType(NumberTypes.BIG_DECIMAL, BigDecimal.ZERO, 1L, 2.0f);
        testType(NumberTypes.BIG_DECIMAL, BigDecimal.ZERO, BigInteger.ONE, 2.0f);

        // Two floating point numbers, two integer numbers
        testType(NumberTypes.FLOAT,       0.0f, (byte) 1, 2.0f, (byte) 3);
        testType(NumberTypes.FLOAT,       0.0f, (short) 1, 2.0f, (byte) 3);
        testType(NumberTypes.DOUBLE,      0.0f, 1, 2.0f, (byte) 3);
        testType(NumberTypes.DOUBLE,      0.0d, (byte) 1, 2.0f, (byte) 3);
        testType(NumberTypes.DOUBLE,      0.0d, (short) 1, 2.0f, (byte) 3);
        testType(NumberTypes.DOUBLE,      0.0d, 1, 2.0f, (byte) 3);
        testType(NumberTypes.BIG_DECIMAL, 0.0f, 1L, 2.0f, (byte) 3);
        testType(NumberTypes.BIG_DECIMAL, 0.0d, 1L, 2.0f, (byte) 3);
        testType(NumberTypes.BIG_DECIMAL, 0.0f, BigInteger.ONE, 2.0f, (byte) 3);
        testType(NumberTypes.BIG_DECIMAL, 0.0d, BigInteger.ONE, 2.0f, (byte) 3);
        testType(NumberTypes.BIG_DECIMAL, BigDecimal.ZERO, (byte) 1, 2.0f, (byte) 3);
        testType(NumberTypes.BIG_DECIMAL, BigDecimal.ZERO, (short) 1, 2.0f, (byte) 3);
        testType(NumberTypes.BIG_DECIMAL, BigDecimal.ZERO, 1, 2.0f, (byte) 3);
        testType(NumberTypes.BIG_DECIMAL, BigDecimal.ZERO, 1L, 2.0f, (byte) 3);
        testType(NumberTypes.BIG_DECIMAL, BigDecimal.ZERO, BigInteger.ONE, 2.0f, (byte) 3);
    }

    /**
     * Makes sure that no NumberType loses precision for all
     * integers within its {@link NumberType#integerBitCount}.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testGetIntegerBits() {
        BigInteger two = BigInteger.valueOf(2);
        NumberType<BigInteger> bigIntType = NumberTypes.BIG_INT;

        for (NumberType type : NumberTypes.ALL) {
            if (type.integerBitCount == Integer.MAX_VALUE)
                continue;

            BigInteger twoPow = two.pow(type.integerBitCount - 1);
            BigInteger lowest = twoPow.multiply(BigInteger.valueOf(-1));
            BigInteger highest = twoPow.subtract(BigInteger.ONE);

            Number a = lowest;
            Number b = highest;
            for (int i=0; i < 100; ++i) {
                a = type.add(type.coerce(a), type.one);
                b = type.subtract(type.coerce(b), type.one);

                BigInteger aDiff = bigIntType.subtract(bigIntType.coerce(a), bigIntType.coerce(lowest));
                BigInteger bDiff = bigIntType.subtract(bigIntType.coerce(highest), bigIntType.coerce(b));

                assertEquals(aDiff, i + 1);
                assertEquals(bDiff, i + 1);
            }
        }
    }

    @Test
    public void testUnsupportedNumberTypes() {
        assertFails(() -> {
            NumberTypes.get(new DoubleAccumulator((a, b) -> a + b, 0));
        });

        assertFails(() -> {
            NumberTypes.get(new LongAccumulator((a, b) -> a + b, 0));
        });

        assertFails(() -> {
            NumberTypes.get(new DoubleAdder());
        });

        assertFails(() -> {
            NumberTypes.get(new LongAdder());
        });

        assertFails(() -> {
            NumberTypes.get(new AtomicInteger());
        });

        assertFails(() -> {
            NumberTypes.get(new AtomicLong());
        });
    }

    private static class NumberPair {

        public final Number one;
        public final Number two;

        public NumberPair(Number one, Number two) {
            this.one = one;
            this.two = two;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null || !obj.getClass().equals(getClass()))
                return false;
            NumberPair other = (NumberPair) obj;
            return Objects.equals(one, other.one) && Objects.equals(two, other.two);
        }
    }

    /**
     * Makes sure the test specific {@link NumberPair#equals(Object)} correctly reports
     * two cases as distinct if the stored numbers are equal, but are stored as different number types.
     */
    @Test
    public void testNumberCompareCaseEquals() {
        Number[] zeroes = {(byte) 0, (short) 0, 0, 0L, BigInteger.ZERO, 0.0f, 0.0d, BigDecimal.ZERO};

        for (int ai = 0; ai < zeroes.length; ++ai) {
            for (int bi = 0; bi < zeroes.length; ++bi) {

                NumberPair case1 = new NumberPair(zeroes[ai], zeroes[bi]);

                for (int ci = 0; ci < zeroes.length; ++ci) {
                    for (int di = 0; di < zeroes.length; ++di) {

                        NumberPair case2 = new NumberPair(zeroes[ci], zeroes[di]);

                        if (ai == ci && bi == di) {
                            assertTrue(case1.equals(case2));
                        } else {
                            assertFalse(case1.equals(case2));
                        }
                    }
                }
            }
        }
    }

    private static List<Number> coerceToAllCompatible(Number value) {
        List<Number> numbers = new ArrayList<>();
        for (NumberType<?> type : NumberTypes.getAllCompatible(value)) {
            numbers.add(type.coerce(value));
        }
        return numbers;
    }

    /**
     * Promotes {@param one} and {@param two} to all possible pairs of alternative Number
     * representations (including the pair passed in), and adds them all to {@param list}.
     */
    private static void addAllTypePairs(List<NumberPair> list, Number one, Number two) {
        List<Number> onesCoerced = coerceToAllCompatible(one);
        List<Number> twosCoerced = coerceToAllCompatible(two);

        for (Number oneCoerced : onesCoerced) {
            for (Number twoCoerced : twosCoerced) {
                list.add(new NumberPair(oneCoerced, twoCoerced));
            }
        }
    }

    private static void addEqualCase(Number number) {
        addEqualCase(number, number);
    }

    private static void addEqualCase(Number one, Number two) {
        addAllTypePairs(EQUAL, one, two);
    }

    private static void addLessThanCase(Number one, Number two) {
        addAllTypePairs(LESS_THAN, one, two);
    }

    private static final List<NumberPair> EQUAL = new ArrayList<>();
    private static final List<NumberPair> LESS_THAN = new ArrayList<>();
    static {
        addEqualCase((byte) 0);
        addEqualCase((byte) 27);
        addEqualCase((byte) -56);

        addEqualCase((short) 23463);
        addEqualCase((short) -2452);

        addEqualCase(23463245);
        addEqualCase(-574673);

        addEqualCase(2346324545345L);
        addEqualCase(-63546353L);

        addEqualCase(0.23423f);
        addEqualCase(23463245f);
        addEqualCase(-574673f);

        addEqualCase(2.2346324545345d);
        addEqualCase(2346324545345d);
        addEqualCase(-63546353d);

        addLessThanCase((byte) 5, (byte) 27);
        addLessThanCase((byte) 27, (byte) 45);
        addLessThanCase(Byte.MIN_VALUE, Byte.MAX_VALUE);

        addLessThanCase((short) 3562, (short) 23524);
        addLessThanCase((short) 2352, (short) 31323);
        addLessThanCase(Short.MIN_VALUE, Short.MAX_VALUE);

        addLessThanCase(345352, 5673574);
        addLessThanCase(234234, 467457245);
        addLessThanCase(Integer.MIN_VALUE, Integer.MAX_VALUE);

        addLessThanCase(243523452L, 2354625623L);
        addLessThanCase(23423423423L, 467457245435345L);
        addLessThanCase(Long.MIN_VALUE, Long.MAX_VALUE);

        addLessThanCase(5.345352f, 6.5673574f);
        addLessThanCase(345352f, 5673574f);
        addLessThanCase(453.234234f, 467.45724534f);
        addLessThanCase(234234f, 467457245f);
        addLessThanCase(Float.MIN_VALUE, Float.MAX_VALUE);

        addLessThanCase(243.523452d, 285.4625623d);
        addLessThanCase(243523452d, 2354625623d);
        addLessThanCase(234.23423423d, 46745.7245435345d);
        addLessThanCase(23423423423d, 467457245435345d);
        addLessThanCase(Double.MIN_VALUE, Double.MAX_VALUE);
    }

    @Test
    public void testCompare() {
        for (NumberPair equal : EQUAL) {
            assertTrue(Num.cmp(equal.one, equal.two) == 0);
            assertTrue(Numbers.compare(equal.one, equal.two) == 0);
        }

        for (NumberPair lessThan : LESS_THAN) {
            assertTrue(Num.cmp(lessThan.one, lessThan.two) < 0);
            assertTrue(Num.cmp(lessThan.two, lessThan.one) > 0);
            assertTrue(Numbers.compare(lessThan.one, lessThan.two) < 0);
            assertTrue(Numbers.compare(lessThan.two, lessThan.one) > 0);
        }
    }

    @Test
    public void testEqual() {
        for (NumberPair equal : EQUAL) {
            assertTrue(Num.eq(equal.one, equal.two));
            assertTrue(Numbers.equal(equal.one, equal.two));
        }
    }

    @Test
    public void testLessThan() {
        for (NumberPair lessThan : LESS_THAN) {
            assertTrue(Num.lt(lessThan.one, lessThan.two));
            assertTrue(Numbers.lessThan(lessThan.one, lessThan.two));
        }
    }

    @Test
    public void testLessThanOrEqual() {
        for (NumberPair equal : EQUAL) {
            assertTrue(Num.lte(equal.one, equal.two));
            assertTrue(Numbers.lessThanOrEqual(equal.one, equal.two));
        }

        for (NumberPair lessThan : LESS_THAN) {
            assertTrue(Num.lte(lessThan.one, lessThan.two));
            assertTrue(Numbers.lessThanOrEqual(lessThan.one, lessThan.two));
        }
    }

    @Test
    public void testGreaterThan() {
        for (NumberPair lessThan : LESS_THAN) {
            assertTrue(Num.gt(lessThan.two, lessThan.one));
            assertTrue(Numbers.greaterThan(lessThan.two, lessThan.one));
        }
    }

    @Test
    public void testGreaterThanOrEqual() {
        for (NumberPair equal : EQUAL) {
            assertTrue(Num.gte(equal.two, equal.one));
            assertTrue(Numbers.greaterThanOrEqual(equal.two, equal.one));
        }

        for (NumberPair lessThan : LESS_THAN) {
            assertTrue(Num.gte(lessThan.two, lessThan.one));
            assertTrue(Numbers.greaterThanOrEqual(lessThan.two, lessThan.one));
        }
    }

    private static void addAbsoluteCase(Number value, Number absolute) {
        addAllTypePairs(ABSOLUTE, value, absolute);
    }

    private static final List<NumberPair> ABSOLUTE = new ArrayList<>();
    static {
        addAbsoluteCase((byte) -1, (byte) 1);
        addAbsoluteCase((byte) -7, (byte) 7);
        addAbsoluteCase((byte) -121, (byte) 121);
        addAbsoluteCase((short) -128, (short) 128);
        addAbsoluteCase((short) -32111, (short) 32111);
        addAbsoluteCase(-32768,  32768);
        addAbsoluteCase(-2147483111, 2147483111);
        addAbsoluteCase(-2147483648L, 2147483648L);
        addAbsoluteCase(-9223372036854775111L, 9223372036854775111L);
        addAbsoluteCase(-9223372036854775808L, new BigInteger("9223372036854775808"));
        addAbsoluteCase(new BigInteger("-9223372036854775808123"), new BigInteger("9223372036854775808123"));
    }

    @Test
    public void testAbsolute() {
        for (NumberPair pair : ABSOLUTE) {
            Number abs1 = Num.abs(pair.one);
            Number abs2 = Numbers.absolute(pair.one);

            assertEquals(abs1, pair.two);
            assertEquals(abs2, pair.two);

            assertGreaterThan(0, pair.one);
            assertLessThan(0, abs1);
            assertLessThan(0, abs2);

            assertEquals(0, Numbers.add(pair.one, abs1));
            assertEquals(0, Numbers.add(pair.one, abs2));

            assertEquals(abs1, Num.abs(abs1));
            assertEquals(abs1, Numbers.absolute(abs1));
            assertEquals(abs2, Num.abs(abs2));
            assertEquals(abs2, Numbers.absolute(abs2));
        }

        for (NumberType<?> type : NumberTypes.ALL) {
            if (type.minValue == null || type.maxValue == null)
                continue;

            assertEquals(type.minValue, Num.sub(0, Num.abs(type.minValue)));
            assertEquals(type.minValue, Numbers.subtract(0, Numbers.absolute(type.minValue)));

            assertEquals(type.maxValue, Num.abs(type.maxValue));
            assertEquals(type.maxValue, Numbers.absolute(type.maxValue));
        }
    }

    // TODO : Tests for GCD and LCM as well as other methods added
}
