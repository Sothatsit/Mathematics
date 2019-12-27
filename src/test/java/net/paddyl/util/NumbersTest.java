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
        assertEquals(NumberType.BYTE,        NumberType.get((byte) 0));
        assertEquals(NumberType.SHORT,       NumberType.get((short) 0));
        assertEquals(NumberType.INT,         NumberType.get(0));
        assertEquals(NumberType.LONG,        NumberType.get(0L));
        assertEquals(NumberType.BIG_INT,     NumberType.get(BigInteger.ZERO));
        assertEquals(NumberType.FLOAT,       NumberType.get(0.0f));
        assertEquals(NumberType.DOUBLE,      NumberType.get(0.0d));
        assertEquals(NumberType.BIG_DECIMAL, NumberType.get(BigDecimal.ZERO));
    }

    private static void testType(NumberType<?> expected, Number... numbers) {
        assertEquals(expected, Num.type(numbers));
        assertEquals(expected, NumberType.getDominantType(numbers));
    }

    @Test
    public void testNumberTypeGetDominantType() {
        assertFails(() -> Num.type());
        assertFails(() -> NumberType.getDominantType());
        assertFails(() -> Num.type((Number[]) null));
        assertFails(() -> NumberType.getDominantType((Number[]) null));

        // One number
        testType(NumberType.BYTE,        (byte) 0);
        testType(NumberType.SHORT,       (short) 0);
        testType(NumberType.INT,         0);
        testType(NumberType.LONG,        0L);
        testType(NumberType.BIG_INT,     BigInteger.ZERO);
        testType(NumberType.FLOAT,       0.0f);
        testType(NumberType.DOUBLE,      0.0d);
        testType(NumberType.BIG_DECIMAL, BigDecimal.ZERO);

        // Two integer numbers
        testType(NumberType.BYTE,    (byte) 0, (byte) 1);
        testType(NumberType.SHORT,   (short) 0, (short) 1);
        testType(NumberType.SHORT,   (short) 0, (byte) 1);
        testType(NumberType.INT,     0, (byte) 1);
        testType(NumberType.INT,     0, (short) 1);
        testType(NumberType.INT,     0, 1);
        testType(NumberType.LONG,    0L, (short) 1);
        testType(NumberType.LONG,    0L, (byte) 1);
        testType(NumberType.LONG,    0L, 1);
        testType(NumberType.LONG,    0L, 1L);
        testType(NumberType.BIG_INT, BigInteger.ZERO, (short) 1);
        testType(NumberType.BIG_INT, BigInteger.ZERO, (byte) 1);
        testType(NumberType.BIG_INT, BigInteger.ZERO, 1);
        testType(NumberType.BIG_INT, BigInteger.ZERO, 1L);
        testType(NumberType.BIG_INT, BigInteger.ZERO, BigInteger.ONE);

        // Two floating point numbers
        testType(NumberType.FLOAT,       0.0f, 1.0f);
        testType(NumberType.DOUBLE,      0.0d, 1.0f);
        testType(NumberType.DOUBLE,      0.0d, 1.0d);
        testType(NumberType.BIG_DECIMAL, BigDecimal.ZERO, 1.0f);
        testType(NumberType.BIG_DECIMAL, BigDecimal.ZERO, 1.0d);
        testType(NumberType.BIG_DECIMAL, BigDecimal.ZERO, BigDecimal.ONE);

        // One floating point number, one integer number
        testType(NumberType.FLOAT,       0.0f, (byte) 1);
        testType(NumberType.FLOAT,       0.0f, (short) 1);
        testType(NumberType.DOUBLE,      0.0f, 1);
        testType(NumberType.DOUBLE,      0.0d, (byte) 1);
        testType(NumberType.DOUBLE,      0.0d, (short) 1);
        testType(NumberType.DOUBLE,      0.0d, 1);
        testType(NumberType.BIG_DECIMAL, 0.0f, 1L);
        testType(NumberType.BIG_DECIMAL, 0.0d, 1L);
        testType(NumberType.BIG_DECIMAL, 0.0f, BigInteger.ONE);
        testType(NumberType.BIG_DECIMAL, 0.0d, BigInteger.ONE);
        testType(NumberType.BIG_DECIMAL, BigDecimal.ZERO, (byte) 1);
        testType(NumberType.BIG_DECIMAL, BigDecimal.ZERO, (short) 1);
        testType(NumberType.BIG_DECIMAL, BigDecimal.ZERO, 1);
        testType(NumberType.BIG_DECIMAL, BigDecimal.ZERO, 1L);
        testType(NumberType.BIG_DECIMAL, BigDecimal.ZERO, BigInteger.ONE);

        // Two floating point numbers, one integer number
        testType(NumberType.FLOAT,       0.0f, (byte) 1, 2.0f);
        testType(NumberType.FLOAT,       0.0f, (short) 1, 2.0f);
        testType(NumberType.DOUBLE,      0.0f, 1, 2.0f);
        testType(NumberType.DOUBLE,      0.0d, (byte) 1, 2.0f);
        testType(NumberType.DOUBLE,      0.0d, (short) 1, 2.0f);
        testType(NumberType.DOUBLE,      0.0d, 1, 2.0f);
        testType(NumberType.BIG_DECIMAL, 0.0f, 1L, 2.0f);
        testType(NumberType.BIG_DECIMAL, 0.0d, 1L, 2.0f);
        testType(NumberType.BIG_DECIMAL, 0.0f, BigInteger.ONE, 2.0f);
        testType(NumberType.BIG_DECIMAL, 0.0d, BigInteger.ONE, 2.0f);
        testType(NumberType.BIG_DECIMAL, BigDecimal.ZERO, (byte) 1, 2.0f);
        testType(NumberType.BIG_DECIMAL, BigDecimal.ZERO, (short) 1, 2.0f);
        testType(NumberType.BIG_DECIMAL, BigDecimal.ZERO, 1, 2.0f);
        testType(NumberType.BIG_DECIMAL, BigDecimal.ZERO, 1L, 2.0f);
        testType(NumberType.BIG_DECIMAL, BigDecimal.ZERO, BigInteger.ONE, 2.0f);

        // Two floating point numbers, two integer numbers
        testType(NumberType.FLOAT,       0.0f, (byte) 1, 2.0f, (byte) 3);
        testType(NumberType.FLOAT,       0.0f, (short) 1, 2.0f, (byte) 3);
        testType(NumberType.DOUBLE,      0.0f, 1, 2.0f, (byte) 3);
        testType(NumberType.DOUBLE,      0.0d, (byte) 1, 2.0f, (byte) 3);
        testType(NumberType.DOUBLE,      0.0d, (short) 1, 2.0f, (byte) 3);
        testType(NumberType.DOUBLE,      0.0d, 1, 2.0f, (byte) 3);
        testType(NumberType.BIG_DECIMAL, 0.0f, 1L, 2.0f, (byte) 3);
        testType(NumberType.BIG_DECIMAL, 0.0d, 1L, 2.0f, (byte) 3);
        testType(NumberType.BIG_DECIMAL, 0.0f, BigInteger.ONE, 2.0f, (byte) 3);
        testType(NumberType.BIG_DECIMAL, 0.0d, BigInteger.ONE, 2.0f, (byte) 3);
        testType(NumberType.BIG_DECIMAL, BigDecimal.ZERO, (byte) 1, 2.0f, (byte) 3);
        testType(NumberType.BIG_DECIMAL, BigDecimal.ZERO, (short) 1, 2.0f, (byte) 3);
        testType(NumberType.BIG_DECIMAL, BigDecimal.ZERO, 1, 2.0f, (byte) 3);
        testType(NumberType.BIG_DECIMAL, BigDecimal.ZERO, 1L, 2.0f, (byte) 3);
        testType(NumberType.BIG_DECIMAL, BigDecimal.ZERO, BigInteger.ONE, 2.0f, (byte) 3);
    }

    /**
     * Makes sure that no NumberType loses precision for all
     * integers within its {@link NumberType#getIntegerBits()}.
     */
    @Test
    public void testGetIntegerBits() {
        BigInteger two = BigInteger.valueOf(2);

        for (NumberType<?> type : NumberType.ALL) {
            if (type.getIntegerBits() == Integer.MAX_VALUE)
                continue;

            BigInteger twoPow = two.pow(type.getIntegerBits() - 1);
            BigInteger lowest = twoPow.multiply(BigInteger.valueOf(-1));
            BigInteger highest = twoPow.subtract(BigInteger.ONE);

            Number a = lowest;
            Number b = highest;
            for (int i=0; i < 100; ++i) {
                a = type.add(a, 1);
                b = type.subtract(b, 1);

                BigInteger aDiff = NumberType.BIG_INT.subtract(a, lowest);
                BigInteger bDiff = NumberType.BIG_INT.subtract(highest, b);

                assertEquals(aDiff, i + 1);
                assertEquals(bDiff, i + 1);
            }
        }
    }

    @Test
    public void testUnsupportedNumberTypes() {
        assertFails(() -> {
            NumberType.get(new DoubleAccumulator((a, b) -> a + b, 0));
        });

        assertFails(() -> {
            NumberType.get(new LongAccumulator((a, b) -> a + b, 0));
        });

        assertFails(() -> {
            NumberType.get(new DoubleAdder());
        });

        assertFails(() -> {
            NumberType.get(new LongAdder());
        });

        assertFails(() -> {
            NumberType.get(new AtomicInteger());
        });

        assertFails(() -> {
            NumberType.get(new AtomicLong());
        });
    }

    private static class NumberCompareCase {

        public final Number one;
        public final Number two;

        public NumberCompareCase(Number one, Number two) {
            this.one = one;
            this.two = two;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null || !obj.getClass().equals(getClass()))
                return false;
            NumberCompareCase other = (NumberCompareCase) obj;
            return Objects.equals(one, other.one) && Objects.equals(two, other.two);
        }
    }

    /**
     * Makes sure the test specific {@link NumberCompareCase#equals(Object)} correctly reports
     * two cases as distinct if the stored numbers are equal, but are stored as different number types.
     */
    @Test
    public void testNumberCompareCaseEquals() {
        Number[] zeroes = {(byte) 0, (short) 0, 0, 0L, BigInteger.ZERO, 0.0f, 0.0d, BigDecimal.ZERO};

        for (int ai = 0; ai < zeroes.length; ++ai) {
            for (int bi = 0; bi < zeroes.length; ++bi) {

                NumberCompareCase case1 = new NumberCompareCase(zeroes[ai], zeroes[bi]);

                for (int ci = 0; ci < zeroes.length; ++ci) {
                    for (int di = 0; di < zeroes.length; ++di) {

                        NumberCompareCase case2 = new NumberCompareCase(zeroes[ci], zeroes[di]);

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

    public static final List<NumberCompareCase> EQUAL = new ArrayList<>();
    public static final List<NumberCompareCase> LESS_THAN = new ArrayList<>();

    /**
     * Promotes {@param one} and {@param two} to all possible pairs of alternative Number
     * representations (including the pair passed in), and adds them all to {@param list}.
     */
    private static void addAllTypePairs(List<NumberCompareCase> list, Number one, Number two) {
        NumberCompareCase compareCase = new NumberCompareCase(one, two);
        if (list.contains(compareCase))
            return;

        list.add(compareCase);

        if (one instanceof Byte) {
            addAllTypePairs(list, one.shortValue(), two);
            addAllTypePairs(list, one.floatValue(), two);
        }

        if (two instanceof Byte) {
            addAllTypePairs(list, one, two.shortValue());
            addAllTypePairs(list, one, two.floatValue());
        }

        if (one instanceof Short) {
            addAllTypePairs(list, one.intValue(), two);
            addAllTypePairs(list, one.floatValue(), two);
        }

        if (two instanceof Short) {
            addAllTypePairs(list, one, two.intValue());
            addAllTypePairs(list, one, two.floatValue());
        }

        if (one instanceof Integer) {
            addAllTypePairs(list, one.longValue(), two);
            addAllTypePairs(list, one.doubleValue(), two);
        }

        if (two instanceof Integer) {
            addAllTypePairs(list, one, two.longValue());
            addAllTypePairs(list, one, two.doubleValue());
        }

        if (one instanceof Long) {
            addAllTypePairs(list, BigInteger.valueOf(one.longValue()), two);
            addAllTypePairs(list, BigDecimal.valueOf(one.longValue()), two);
        }

        if (two instanceof Long) {
            addAllTypePairs(list, one, BigInteger.valueOf(two.longValue()));
            addAllTypePairs(list, one, BigDecimal.valueOf(two.longValue()));
        }

        if (one instanceof Float) {
            addAllTypePairs(list, one.doubleValue(), two);
        }

        if (two instanceof Float) {
            addAllTypePairs(list, one, two.doubleValue());
        }

        if (one instanceof Double) {
            addAllTypePairs(list, BigDecimal.valueOf(one.doubleValue()), two);
        }

        if (two instanceof Double) {
            addAllTypePairs(list, one, BigDecimal.valueOf(two.doubleValue()));
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
        for (NumberCompareCase equal : EQUAL) {
            assertTrue(Num.cmp(equal.one, equal.two) == 0);
            assertTrue(Numbers.compare(equal.one, equal.two) == 0);
        }

        for (NumberCompareCase lessThan : LESS_THAN) {
            assertTrue(Num.cmp(lessThan.one, lessThan.two) < 0);
            assertTrue(Num.cmp(lessThan.two, lessThan.one) > 0);
            assertTrue(Numbers.compare(lessThan.one, lessThan.two) < 0);
            assertTrue(Numbers.compare(lessThan.two, lessThan.one) > 0);
        }
    }

    @Test
    public void testEqual() {
        for (NumberCompareCase equal : EQUAL) {
            assertTrue(Num.eq(equal.one, equal.two));
            assertTrue(Numbers.equal(equal.one, equal.two));
        }
    }

    @Test
    public void testLessThan() {
        for (NumberCompareCase lessThan : LESS_THAN) {
            assertTrue(Num.lt(lessThan.one, lessThan.two));
            assertTrue(Numbers.lessThan(lessThan.one, lessThan.two));
        }
    }

    @Test
    public void testLessThanOrEqual() {
        for (NumberCompareCase equal : EQUAL) {
            assertTrue(Num.lte(equal.one, equal.two));
            assertTrue(Numbers.lessThanOrEqual(equal.one, equal.two));
        }

        for (NumberCompareCase lessThan : LESS_THAN) {
            assertTrue(Num.lte(lessThan.one, lessThan.two));
            assertTrue(Numbers.lessThanOrEqual(lessThan.one, lessThan.two));
        }
    }

    @Test
    public void testGreaterThan() {
        for (NumberCompareCase lessThan : LESS_THAN) {
            assertTrue(Num.gt(lessThan.two, lessThan.one));
            assertTrue(Numbers.greaterThan(lessThan.two, lessThan.one));
        }
    }

    @Test
    public void testGreaterThanOrEqual() {
        for (NumberCompareCase equal : EQUAL) {
            assertTrue(Num.gte(equal.two, equal.one));
            assertTrue(Numbers.greaterThanOrEqual(equal.two, equal.one));
        }

        for (NumberCompareCase lessThan : LESS_THAN) {
            assertTrue(Num.gte(lessThan.two, lessThan.one));
            assertTrue(Numbers.greaterThanOrEqual(lessThan.two, lessThan.one));
        }
    }
}
