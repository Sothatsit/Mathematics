package net.paddyl.constraints;

import static org.junit.Assert.*;

import net.paddyl.constraints.set.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

/**
 * Tests for ConstOperator sub-classes.
 */
@RunWith(Parameterized.class)
@SuppressWarnings("unchecked")
public class ConstOperatorTests extends NumberRangeTestBase{

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { "LongRange", new LongRange.LongRangeFactory() },
                { "IntRange", new IntRange.IntRangeFactory() },
                { "ShortRange", new ShortRange.ShortRangeFactory() },
                { "ByteRange", new ByteRange.ByteRangeFactory() }
        });
    }

    private ArithmeticTestCase[] arithmeticTestCases = null;

    public ConstOperatorTests(String name, NumberRangeFactory factory) {
        super(name, factory);
    }

    private ArithmeticTestCase[] getArithmeticTestCases() {
        if (arithmeticTestCases != null)
            return arithmeticTestCases;

        arithmeticTestCases = new ArithmeticTestCase[] {
                new ArithmeticTestCase(ALL, 1)
                        .add(ALL, ALL).mul(ALL, ALL).div(ALL, ALL),

                new ArithmeticTestCase(single(1), 1)
                        .add(single(2), single(1)).mul(single(1), single(1)).div(single(1), single(1)),

                new ArithmeticTestCase(single(1), -1)
                        .add(single(0), single(1)).mul(single(-1), single(1)).div(single(-1), single(1)),

                new ArithmeticTestCase(single(1), 2)
                        .add(single(3), single(1)).mul(single(2), single(1)).div(single(0), of(-1, 1)),

                new ArithmeticTestCase(single(1), -2)
                        .add(single(-1), single(1)).mul(single(-2), single(1)).div(single(0), of(-1, 1)),

                new ArithmeticTestCase(of(-10, 10), 3)
                        .add(of(-7, 13), of(-10, 10)).mul(of(-30, 30, 3), of(-10, 10)).div(of(-3, 3), of(-11, 11)),

                new ArithmeticTestCase(of(-10, 10), -3)
                        .add(of(-13, 7), of(-10, 10)).mul(of(-30, 30, 3), of(-10, 10)).div(of(-3, 3), of(-11, 11)),

                new ArithmeticTestCase(of(5, 10), 3)
                        .add(of(8, 13), of(5, 10)).mul(of(15, 30, 3), of(5, 10)).div(of(1, 3), of(3, 11)),

                new ArithmeticTestCase(of(5, 10), -3)
                        .add(of(2, 7), of(5, 10)).mul(of(-30, -15, 3), of(5, 10)).div(of(-3, -1), of(3, 11))
        };
        return arithmeticTestCases;
    }

    @Test
    public void testAddOperator() {
        for (ArithmeticTestCase testCase : getArithmeticTestCases()) {
            try {
                ConstOperator addOperator = add(testCase.value);

                ValueSet original = testCase.original;
                ValueSet forward = addOperator.forward(original);
                ValueSet backward = addOperator.backward(forward);

                assertEquals(testCase.added, forward);
                assertEquals(testCase.reverseAdded, backward);
            } catch (AssertionError e) {
                e.addSuppressed(new Exception(testCase.addDesc()));
                throw e;
            }
        }
    }

    @Test
    public void testMulOperator() {
        for (ArithmeticTestCase testCase : getArithmeticTestCases()) {
            try {
                ConstOperator mulOperator = mul(testCase.value);

                ValueSet original = testCase.original;
                ValueSet forward = mulOperator.forward(original);
                ValueSet backward = mulOperator.backward(forward);

                assertEquals(testCase.multiplied, forward);
                assertEquals(testCase.reverseMultiplied, backward);
            } catch (AssertionError e) {
                e.addSuppressed(new Exception(testCase.mulDesc()));
                throw e;
            }
        }
    }

    @Test
    public void testDivOperator() {
        for (ArithmeticTestCase testCase : getArithmeticTestCases()) {
            try {
                ConstOperator divOperator = div(testCase.value);

                ValueSet original = testCase.original;
                ValueSet forward = divOperator.forward(original);
                ValueSet backward = divOperator.backward(forward);

                assertEquals(testCase.divided, forward);
                assertEquals(testCase.reverseDivided, backward);
            } catch (AssertionError e) {
                e.addSuppressed(new Exception(testCase.divDesc()));
                throw e;
            }
        }
    }

    private static class ArithmeticTestCase {

        private final ValueSet original;
        private final Number value;

        private ValueSet added;
        private ValueSet reverseAdded;
        private ValueSet multiplied;
        private ValueSet reverseMultiplied;
        private ValueSet divided;
        private ValueSet reverseDivided;

        private ArithmeticTestCase(ValueSet original, Number value) {
            this.original = original;
            this.value = value;
        }

        public ArithmeticTestCase add(ValueSet added, ValueSet reversed) {
            this.added = added;
            this.reverseAdded = reversed;
            return this;
        }

        public String addDesc() {
            return "add " + value + " to " + original + " = " + added + ", reversed to " + reverseAdded;
        }

        public ArithmeticTestCase mul(ValueSet multiplied, ValueSet reversed) {
            this.multiplied = multiplied;
            this.reverseMultiplied = reversed;
            return this;
        }

        public String mulDesc() {
            return original + " multiplied by " + value + " = " + multiplied + ", reversed to " + reverseMultiplied;
        }

        public ArithmeticTestCase div(ValueSet divided, ValueSet reversed) {
            this.divided = divided;
            this.reverseDivided = reversed;
            return this;
        }

        public String divDesc() {
            return original + " divided by " + value + " = " + divided + ", reversed to " + reverseDivided;
        }
    }
}
