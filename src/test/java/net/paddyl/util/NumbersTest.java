package net.paddyl.util;

import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static net.paddyl.util.Checks.assertTrue;

public class NumbersTest {

    private static class NumberCompareCase {
        public final Number one;
        public final Number two;

        public NumberCompareCase(Number one, Number two) {
            this.one = one;
            this.two = two;
        }
    }

    public static final List<NumberCompareCase> EQUAL = new ArrayList<>();
    public static final List<NumberCompareCase> LESS_THAN = new ArrayList<>();

    private static void addAllTypePairs(List<NumberCompareCase> list, Number one, Number two) {
        list.add(new NumberCompareCase(one, two));

        if (one instanceof Byte) {
            addAllTypePairs(list, one.shortValue(), two);
        }

        if (two instanceof Byte) {
            addAllTypePairs(list, one, two.shortValue());
        }

        if (one instanceof Short) {
            addAllTypePairs(list, one.intValue(), two);
        }

        if (two instanceof Short) {
            addAllTypePairs(list, one, two.intValue());
        }

        if (one instanceof Integer) {
            addAllTypePairs(list, one.longValue(), two);
        }

        if (two instanceof Integer) {
            addAllTypePairs(list, one, two.longValue());
        }

        if (one instanceof Long) {
            addAllTypePairs(list, BigInteger.valueOf(one.longValue()), two);
        }

        if (two instanceof Long) {
            addAllTypePairs(list, one, BigInteger.valueOf(two.longValue()));
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
