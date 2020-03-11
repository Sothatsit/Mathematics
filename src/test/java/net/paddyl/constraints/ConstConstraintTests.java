package net.paddyl.constraints;

import static org.junit.Assert.*;

import net.paddyl.constraints.set.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
@SuppressWarnings("unchecked")
public class ConstConstraintTests extends NumberRangeTestBase {

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { "LongRange", new LongRange.LongRangeFactory() },
                { "IntRange", new IntRange.IntRangeFactory() },
                { "ShortRange", new ShortRange.ShortRangeFactory() },
                { "ByteRange", new ByteRange.ByteRangeFactory() }
        });
    }

    private CompTestCase[] compTestCases = null;
    private MultiTestCase[] multiTestCases = null;

    public ConstConstraintTests(String name, NumberRangeFactory factory) {
        super(name, factory);
    }

    private CompTestCase[] getCompTestCases() {
        if (compTestCases != null)
            return compTestCases;

        compTestCases = new CompTestCase[] {
                new CompTestCase(ALL, 5).gte(above(5)).gt(above(6)).lte(below(5)).lt(below(4)).eq(single(5)),
                new CompTestCase(ALL, 0).gte(above(0)).gt(above(1)).lte(below(0)).lt(below(-1)).eq(single(0)),
                new CompTestCase(ALL, -5).gte(above(-5)).gt(above(-4)).lte(below(-5)).lt(below(-6)).eq(single(-5)),
                new CompTestCase(ALL, MAX).gte(single(MAX)).gt(EMPTY).lte(ALL).lt(below(MAX_M1)).eq(single(MAX)),
                new CompTestCase(ALL, MIN).gte(ALL).gt(above(MIN_P1)).lte(single(MIN)).lt(EMPTY).eq(single(MIN)),

                new CompTestCase(below(5), 5).gte(single(5)).gt(EMPTY).lte(below(5)).lt(below(4)).eq(single(5)),
                new CompTestCase(below(5), 0).gte(of(0, 5)).gt(of(1, 5)).lte(below(0)).lt(below(-1)).eq(single(0)),
                new CompTestCase(below(5), -5).gte(of(-5, 5)).gt(of(-4, 5)).lte(below(-5)).lt(below(-6)).eq(single(-5)),
                new CompTestCase(below(5), MAX).gte(EMPTY).gt(EMPTY).lte(below(5)).lt(below(5)).eq(EMPTY),
                new CompTestCase(below(5), MIN).gte(below(5)).gt(of(MIN_P1, 5)).lte(single(MIN)).lt(EMPTY).eq(single(MIN)),

                new CompTestCase(above(5), 5).gte(above(5)).gt(above(6)).lte(single(5)).lt(EMPTY).eq(single(5)),
                new CompTestCase(above(5), 0).gte(above(5)).gt(above(5)).lte(EMPTY).lt(EMPTY).eq(EMPTY),
                new CompTestCase(above(5), -5).gte(above(5)).gt(above(5)).lte(EMPTY).lt(EMPTY).eq(EMPTY),
                new CompTestCase(above(5), MAX).gte(single(MAX)).gt(EMPTY).lte(above(5)).lt(of(5, MAX_M1)).eq(single(MAX)),
                new CompTestCase(above(5), MIN).gte(above(5)).gt(above(5)).lte(EMPTY).lt(EMPTY).eq(EMPTY),

                new CompTestCase(of(0, 5), 5).gte(single(5)).gt(EMPTY).lte(of(0, 5)).lt(of(0, 4)).eq(single(5)),
                new CompTestCase(of(0, 5), 0).gte(of(0, 5)).gt(of(1, 5)).lte(single(0)).lt(EMPTY).eq(single(0)),
                new CompTestCase(of(0, 5), -5).gte(of(0, 5)).gt(of(0, 5)).lte(EMPTY).lt(EMPTY).eq(EMPTY),
                new CompTestCase(of(0, 5), MAX).gte(EMPTY).gt(EMPTY).lte(of(0, 5)).lt(of(0, 5)).eq(EMPTY),
                new CompTestCase(of(0, 5), MIN).gte(of(0, 5)).gt(of(0, 5)).lte(EMPTY).lt(EMPTY).eq(EMPTY),
        };
        return compTestCases;
    }

    private MultiTestCase[] getMultiTestCases() {
        if (multiTestCases != null)
            return multiTestCases;

        multiTestCases = new MultiTestCase[] {
                new MultiTestCase(ALL, eq(5)).and(single(5)).or(single(5)),
                new MultiTestCase(ALL, gte(5)).and(above(5)).or(above(5)),
                new MultiTestCase(ALL, gt(5)).and(aboveNotIncl(5)).or(aboveNotIncl(5)),
                new MultiTestCase(ALL, lte(5)).and(below(5)).or(below(5)),
                new MultiTestCase(ALL, lt(5)).and(belowNotIncl(5)).or(belowNotIncl(5)),

                new MultiTestCase(ALL, gt(5), lt(5)).and(EMPTY).or(ALL),
                new MultiTestCase(ALL, gte(5), lte(5)).and(single(5)).or(ALL),
                new MultiTestCase(ALL, gte(0), lt(5)).and(of(0, 4)).or(ALL),
                new MultiTestCase(ALL, gt(0), lte(5)).and(of(1, 5)).or(ALL),
                new MultiTestCase(ALL, gte(5), lte(5), eq(5)).and(single(5)).or(ALL),

                new MultiTestCase(ALL, gt(5), lt(5), gte(10)).and(EMPTY).or(ALL),
                new MultiTestCase(ALL, gte(5), lte(5), gt(10)).and(EMPTY).or(ALL),
                new MultiTestCase(ALL, gte(0), lt(5), gt(10)).and(EMPTY).or(ALL),
                new MultiTestCase(ALL, gt(0), lte(5), gte(10)).and(EMPTY).or(ALL),
                new MultiTestCase(ALL, gte(5), lte(5), eq(5), gt(10)).and(EMPTY).or(ALL),

                new MultiTestCase(ALL, gt(5), gt(10)).and(aboveNotIncl(10)).or(aboveNotIncl(5)),
                new MultiTestCase(ALL, gte(5), gt(10)).and(aboveNotIncl(10)).or(above(5)),
                new MultiTestCase(ALL, gt(5), gte(10)).and(above(10)).or(aboveNotIncl(5)),
                new MultiTestCase(ALL, gte(5), gte(10)).and(above(10)).or(above(5)),

                new MultiTestCase(ALL, gt(5), gt(10), gte(15)).and(above(15)).or(aboveNotIncl(5)),
                new MultiTestCase(ALL, gte(5), gt(10), gt(15)).and(aboveNotIncl(15)).or(above(5)),
                new MultiTestCase(ALL, gt(5), gte(10), gte(15)).and(above(15)).or(aboveNotIncl(5)),
                new MultiTestCase(ALL, gte(5), gte(10), gt(15)).and(aboveNotIncl(15)).or(above(5)),

                new MultiTestCase(EMPTY, eq(5)).and(EMPTY).or(EMPTY),
                new MultiTestCase(EMPTY, gte(5)).and(EMPTY).or(EMPTY),
                new MultiTestCase(EMPTY, gt(5)).and(EMPTY).or(EMPTY),
                new MultiTestCase(EMPTY, lte(5)).and(EMPTY).or(EMPTY),
                new MultiTestCase(EMPTY, lt(5)).and(EMPTY).or(EMPTY),
                new MultiTestCase(EMPTY, gt(5), lt(5)).and(EMPTY).or(EMPTY),
                new MultiTestCase(EMPTY, gte(5), lte(5)).and(EMPTY).or(EMPTY),
                new MultiTestCase(EMPTY, gte(0), lt(5)).and(EMPTY).or(EMPTY),
                new MultiTestCase(EMPTY, gt(0), lte(5)).and(EMPTY).or(EMPTY),
                new MultiTestCase(EMPTY, gte(5), lte(5), eq(5)).and(EMPTY).or(EMPTY),
                new MultiTestCase(EMPTY, gt(5), lt(5), gte(10)).and(EMPTY).or(EMPTY),
                new MultiTestCase(EMPTY, gte(5), lte(5), gt(10)).and(EMPTY).or(EMPTY),
                new MultiTestCase(EMPTY, gte(0), lt(5), gt(10)).and(EMPTY).or(EMPTY),
                new MultiTestCase(EMPTY, gt(0), lte(5), gte(10)).and(EMPTY).or(EMPTY),
                new MultiTestCase(EMPTY, gte(5), lte(5), eq(5), gt(10)).and(EMPTY).or(EMPTY),
                new MultiTestCase(EMPTY, gt(5), gt(10)).and(EMPTY).or(EMPTY),
                new MultiTestCase(EMPTY, gte(5), gt(10)).and(EMPTY).or(EMPTY),
                new MultiTestCase(EMPTY, gt(5), gte(10)).and(EMPTY).or(EMPTY),
                new MultiTestCase(EMPTY, gte(5), gte(10)).and(EMPTY).or(EMPTY),
                new MultiTestCase(EMPTY, gt(5), gt(10), gte(15)).and(EMPTY).or(EMPTY),
                new MultiTestCase(EMPTY, gte(5), gt(10), gt(15)).and(EMPTY).or(EMPTY),
                new MultiTestCase(EMPTY, gt(5), gte(10), gte(15)).and(EMPTY).or(EMPTY),
                new MultiTestCase(EMPTY, gte(5), gte(10), gt(15)).and(EMPTY).or(EMPTY),

                // TODO : Test cases with different input ranges
        };
        return multiTestCases;
    }

    @Test
    public void testGTE() {
        for (CompTestCase testCase : getCompTestCases()) {
            ConstConstraint constraint = gte(testCase.value);
            ValueSet constraintReduced = constraint.bruteReduce(testCase.original);
            assertEquals(testCase.original + " gte " + testCase.value, testCase.gte, constraintReduced);
        }
    }

    @Test
    public void testGT() {
        for (CompTestCase testCase : getCompTestCases()) {
            ConstConstraint constraint = gt(testCase.value);
            ValueSet constraintReduced = constraint.bruteReduce(testCase.original);
            assertEquals(testCase.original + " gt " + testCase.value, testCase.gt, constraintReduced);
        }
    }

    @Test
    public void testLTE() {
        for (CompTestCase testCase : getCompTestCases()) {
            ConstConstraint constraint = lte(testCase.value);
            ValueSet constraintReduced = constraint.bruteReduce(testCase.original);
            assertEquals(testCase.original + " lte " + testCase.value, testCase.lte, constraintReduced);
        }
    }

    @Test
    public void testLT() {
        for (CompTestCase testCase : getCompTestCases()) {
            ConstConstraint constraint = lt(testCase.value);
            ValueSet constraintReduced = constraint.bruteReduce(testCase.original);
            assertEquals(testCase.original + " lt " + testCase.value, testCase.lt, constraintReduced);
        }
    }

    @Test
    public void testEq() {
        for (CompTestCase testCase : getCompTestCases()) {
            ConstConstraint constraint = eq(testCase.value);
            ValueSet constraintReduced = constraint.bruteReduce(testCase.original);
            assertEquals(testCase.original + " eq " + testCase.value, testCase.eq, constraintReduced);
        }
    }

    @Test
    public void testAnd() {
        for (MultiTestCase testCase : getMultiTestCases()) {
            ConstConstraint constraint = and(testCase.constraints);
            ValueSet constraintReduced = constraint.bruteReduce(testCase.original);
            String desc = "and " + Arrays.asList(testCase.constraints) + " of " + testCase.original;
            assertEquals(desc, testCase.and, constraintReduced);
        }
    }

    @Test
    public void testOr() {
        for (MultiTestCase testCase : getMultiTestCases()) {
            ConstConstraint constraint = or(testCase.constraints);
            ValueSet constraintReduced = constraint.bruteReduce(testCase.original);
            String desc = "or " + Arrays.asList(testCase.constraints) + " of " + testCase.original;
            assertEquals(desc, testCase.or, constraintReduced);
        }
    }

    @Test
    public void testDomainChange() {
        ConstOperator add1 = add(1);
        ConstOperator sub1 = add(-1);

        { // x + 1 == 2
            ConstConstraint constraint = domain(add1, eq(2));
            assertEquals(single(1), constraint.bruteReduce(ALL));
        }

        { // x - 1 == 2
            ConstConstraint constraint = domain(sub1, eq(2));
            assertEquals(single(3), constraint.bruteReduce(ALL));
        }

        { // x + 1 >= 3
            ConstConstraint constraint = domain(add1, gte(3));
            assertEquals(of(2, MAX_M1), constraint.bruteReduce(ALL));
        }

        { // x * 2 >= 3
            ConstConstraint constraint = domain(mul(2), gte(3));
            assertEquals(of(2, 10), constraint.bruteReduce(of(-10, 10)));
        }

        { // x / 2 >= 3
            ConstConstraint constraint = domain(div(2), gte(3));
            assertEquals(of(6, 10), constraint.bruteReduce(of(-10, 10)));
        }

        { // x - 1 <= 3
            ConstConstraint constraint = domain(sub1, lte(3));
            assertEquals(of(MIN_P1, 4), constraint.bruteReduce(ALL));
        }

        { // x + 1 >= 0 && x - 1 <= 0
            ConstConstraint constraint = and(domain(add1, gte(0)), domain(sub1, lte(0)));
            assertEquals(of(-1, 1), constraint.bruteReduce(ALL));
        }

        { // (x + 1 >= -5 && x - 1 <= 5) || x - 1 < 0
            ConstConstraint constraint = or(and(domain(add1, gte(-5)), domain(sub1, lte(5))), domain(sub1, lt(0)));
            assertEquals(of(MIN_P1, 6), constraint.bruteReduce(ALL));
        }
    }

    private static class CompTestCase {
        private final ValueSet original;
        private final Number value;

        private ValueSet gte;
        private ValueSet gt;
        private ValueSet lte;
        private ValueSet lt;
        private ValueSet eq;

        private CompTestCase(
                ValueSet original,
                Number value) {

            this.original = original;
            this.value = value;
        }

        public CompTestCase gte(ValueSet expected) {
            this.gte = expected;
            return this;
        }

        public CompTestCase gt(ValueSet expected) {
            this.gt = expected;
            return this;
        }

        public CompTestCase lte(ValueSet expected) {
            this.lte = expected;
            return this;
        }

        public CompTestCase lt(ValueSet expected) {
            this.lt = expected;
            return this;
        }

        public CompTestCase eq(ValueSet expected) {
            this.eq = expected;
            return this;
        }
    }

    private static class MultiTestCase {
        private final ValueSet original;
        private final ConstConstraint[] constraints;

        private ValueSet and;
        private ValueSet or;

        private MultiTestCase(ValueSet original, ConstConstraint... constraints) {
            this.original = original;
            this.constraints = constraints;
        }

        public MultiTestCase and(ValueSet expected) {
            this.and = expected;
            return this;
        }

        public MultiTestCase or(ValueSet expected) {
            this.or = expected;
            return this;
        }
    }
}
