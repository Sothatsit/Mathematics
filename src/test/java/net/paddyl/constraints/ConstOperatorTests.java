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
                new ArithmeticTestCase(ALL, 1).add(ALL).mul(ALL),
                new ArithmeticTestCase(single(1), 1).add(single(2)).mul(single(1)),
                new ArithmeticTestCase(single(1), 2).add(single(3)).mul(single(2))
        };
        return arithmeticTestCases;
    }

    @Test
    public void testAddOperator() {
        for (ArithmeticTestCase testCase : getArithmeticTestCases()) {
            ConstOperator addOperator = add(testCase.value);

            ValueSet original = testCase.originalRange;
            ValueSet forward = addOperator.forward(original);
            ValueSet backward = addOperator.backward(forward);

            assertEquals(testCase.added, forward);
            assertTrue(factory.isSubset(original, backward));
        }
    }

    @Test
    public void testMulOperator() {
        for (ArithmeticTestCase testCase : getArithmeticTestCases()) {
            ConstOperator mulOperator = mul(testCase.value);

            ValueSet original = testCase.originalRange;
            ValueSet forward = mulOperator.forward(original);
            ValueSet backward = mulOperator.backward(forward);

            assertEquals(testCase.multiplied, forward);
            assertTrue(factory.isSubset(original, backward));
        }
    }

    private static class ArithmeticTestCase {

        private final ValueSet originalRange;
        private final Number value;

        private ValueSet added;
        private ValueSet multiplied;

        private ArithmeticTestCase(ValueSet originalRange, Number value) {
            this.originalRange = originalRange;
            this.value = value;
        }

        public ArithmeticTestCase add(ValueSet added) {
            this.added = added;
            return this;
        }

        public ArithmeticTestCase mul(ValueSet multiplied) {
            this.multiplied = multiplied;
            return this;
        }
    }
}
