package net.paddyl.util;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Tests for the integer instances of NumberType found in NumberTypes.
 */
@RunWith(Parameterized.class)
@SuppressWarnings("unchecked")
public class IntegerNumberTypeTests {

    private final NumberType type;

    /**
     * Must be in ascending order, and the numbers must be symmetric
     * around zeroIndex in magnitude while opposite in sign.
     * (With the exception for the minimum and maximum values).
     */
    private final Number[] representativeNumbers;
    private final int zeroIndex;

    @SuppressWarnings("unused")
    public IntegerNumberTypeTests(String name, NumberType<?> type) {
        this.type = type;

        List<Number> numbers = new ArrayList<>();

        numbers.add(type.coerce(-15));
        numbers.add(type.coerce(-8));
        numbers.add(type.coerce(-2));
        numbers.add(type.coerce(-1));
        numbers.add(type.zero);
        numbers.add(type.one);
        numbers.add(type.coerce(2));
        numbers.add(type.coerce(8));
        numbers.add(type.coerce(15));
        if (type.isBounded) {
            numbers.add(0, type.minValue);
            numbers.add(type.maxValue);
        }

        zeroIndex = numbers.indexOf(type.zero);
        representativeNumbers = numbers.toArray(new Number[numbers.size()]);
    }

    @Test
    public void testCompare() {
        for (int index1 = 0; index1 < representativeNumbers.length; ++index1) {
            for (int index2 = 0; index2 < representativeNumbers.length; ++index2) {
                Number number1 = representativeNumbers[index1];
                Number number2 = representativeNumbers[index2];

                assertEquals(
                    signum(Integer.compare(index1, index2)),
                    signum(type.compare(number1, number2))
                );
            }
        }
    }

    private static int signum(int value) {
        return value < 0 ? -1 : (value > 0 ? 1 : 0);
    }

    @Test
    public void testEquals() {
        for (int index1 = 0; index1 < representativeNumbers.length; ++index1) {
            for (int index2 = 0; index2 < representativeNumbers.length; ++index2) {
                Number number1 = representativeNumbers[index1];
                Number number2 = representativeNumbers[index2];

                assertEquals(
                    index1 == index2,
                    type.eq(number1, number2)
                );
            }
        }
    }

    @Test
    public void testLessThan() {
        for (int index1 = 0; index1 < representativeNumbers.length; ++index1) {
            for (int index2 = 0; index2 < representativeNumbers.length; ++index2) {
                Number number1 = representativeNumbers[index1];
                Number number2 = representativeNumbers[index2];

                assertEquals(
                    index1 < index2,
                    type.lt(number1, number2)
                );
            }
        }
    }

    @Test
    public void testLessThanEquals() {
        for (int index1 = 0; index1 < representativeNumbers.length; ++index1) {
            for (int index2 = 0; index2 < representativeNumbers.length; ++index2) {
                Number number1 = representativeNumbers[index1];
                Number number2 = representativeNumbers[index2];

                assertEquals(
                    index1 <= index2,
                    type.lte(number1, number2)
                );
            }
        }
    }

    @Test
    public void testGreaterThan() {
        for (int index1 = 0; index1 < representativeNumbers.length; ++index1) {
            for (int index2 = 0; index2 < representativeNumbers.length; ++index2) {
                Number number1 = representativeNumbers[index1];
                Number number2 = representativeNumbers[index2];

                assertEquals(
                    index1 > index2,
                    type.gt(number1, number2)
                );
            }
        }
    }

    @Test
    public void testGreaterThanEquals() {
        for (int index1 = 0; index1 < representativeNumbers.length; ++index1) {
            for (int index2 = 0; index2 < representativeNumbers.length; ++index2) {
                Number number1 = representativeNumbers[index1];
                Number number2 = representativeNumbers[index2];

                assertEquals(
                    index1 >= index2,
                    type.gte(number1, number2)
                );
            }
        }
    }

    @Test
    public void testShiftLeft() {

    }

    @Test
    public void testShiftRight() {

    }

    @Test
    public void testAdd() {

    }

    @Test
    public void testSubtract() {

    }

    @Test
    public void testNegate() {
        if (type.isBounded) {
            assertEquals(type.minValue, type.negate(type.minValue));
        }

        int minIndex = type.isBounded ? 1 : 0;
        int maxIndex = representativeNumbers.length - minIndex - 1;
        for (int index = minIndex; index <= maxIndex; ++index) {
            Number number = representativeNumbers[index];
            Number negation = representativeNumbers[representativeNumbers.length - index - 1];
            assertEquals(negation, type.negate(number));
        }
    }

    @Test
    public void testMultiply() {

    }

    @Test
    public void testDivide() {

    }

    @Test
    public void testDivideRoundUp() {

    }

    @Test
    public void testSignedRemainder() {

    }

    @Test
    public void testPositiveRemainder() {

    }

    @Test
    public void testIsMultiple() {

    }

    @Test
    public void testAbsolute() {
        for (int index = 0; index < representativeNumbers.length; ++index) {
            Number number = representativeNumbers[index];

            // The minimum value of the type
            if (type.isBounded && index == 0) {
                assertEquals(number, type.abs(number));
                continue;
            }

            // Greater than or equal to zero, so shouldn't change
            if (index >= zeroIndex) {
                assertEquals(number, type.abs(number));
                continue;
            }

            // Less than zero, and not the minimum value, so should become positive
            Number expected = representativeNumbers[representativeNumbers.length - index - 1];
            Number absoluted = type.abs(number);
            assertEquals(expected, absoluted);
        }
    }

    @Test
    public void testMin() {
        for (int index1 = 0; index1 < representativeNumbers.length; ++index1) {
            for (int index2 = 0; index2 < representativeNumbers.length; ++index2) {
                Number number1 = representativeNumbers[index1];
                Number number2 = representativeNumbers[index2];

                assertEquals(
                    (index1 < index2 ? number1 : number2),
                    type.min(number1, number2)
                );
            }
        }
    }

    @Test
    public void testMax() {
        for (int index1 = 0; index1 < representativeNumbers.length; ++index1) {
            for (int index2 = 0; index2 < representativeNumbers.length; ++index2) {
                Number number1 = representativeNumbers[index1];
                Number number2 = representativeNumbers[index2];

                assertEquals(
                    (index1 > index2 ? number1 : number2),
                    type.max(number1, number2)
                );
            }
        }
    }

    @Test
    public void testClamp() {
        for (int index1 = 0; index1 < representativeNumbers.length; ++index1) {
            for (int index2 = 0; index2 < representativeNumbers.length; ++index2) {
                for (int index3 = 0; index3 < representativeNumbers.length; ++index3) {
                    Number number1 = representativeNumbers[index1];
                    Number number2 = representativeNumbers[index2];
                    Number number3 = representativeNumbers[index3];

                    assertEquals(
                        (index1 < index2 ? number2 : (index1 > index3 ? number3 : number1)),
                        type.clamp(number1, number2, number3)
                    );
                }
            }
        }
    }

    @Test
    public void testGreatestCommonDivisor() {

    }

    @Test
    public void testLeastCommonMultiple() {

    }

    @Test
    public void testOffsetLeastCommonMultiple() {

    }

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> getTestParameters() {
        List<Object[]> parameters = new ArrayList<>();
        for (NumberType<?> type : NumberTypes.INTEGER_TYPES) {
            parameters.add(new Object[] {
                    type.toString(), type
            });
        }
        return parameters;
    }
}
