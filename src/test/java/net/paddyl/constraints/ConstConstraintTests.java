package net.paddyl.constraints;

import static net.paddyl.constraints.LongRange.*;
import static net.paddyl.constraints.ConstConstraint.*;
import static org.junit.Assert.*;

import org.junit.Test;

import java.util.Arrays;

public class ConstConstraintTests {

    private static final long MAX = Long.MAX_VALUE;
    private static final long MIN = Long.MIN_VALUE;

    private static final CompTestCase[] compTestCases = {
            new CompTestCase(ALL, 5).gte(above(5)).gt(above(6)).lte(below(5)).lt(below(4)).eq(single(5)),
            new CompTestCase(ALL, 0).gte(above(0)).gt(above(1)).lte(below(0)).lt(below(-1)).eq(single(0)),
            new CompTestCase(ALL, -5).gte(above(-5)).gt(above(-4)).lte(below(-5)).lt(below(-6)).eq(single(-5)),
            new CompTestCase(ALL, MAX).gte(single(MAX)).gt(EMPTY).lte(ALL).lt(below(MAX - 1)).eq(single(MAX)),
            new CompTestCase(ALL, MIN).gte(ALL).gt(above(MIN + 1)).lte(single(MIN)).lt(EMPTY).eq(single(MIN)),

            new CompTestCase(below(5), 5).gte(single(5)).gt(EMPTY).lte(below(5)).lt(below(4)).eq(single(5)),
            new CompTestCase(below(5), 0).gte(of(0, 5)).gt(of(1, 5)).lte(below(0)).lt(below(-1)).eq(single(0)),
            new CompTestCase(below(5), -5).gte(of(-5, 5)).gt(of(-4, 5)).lte(below(-5)).lt(below(-6)).eq(single(-5)),
            new CompTestCase(below(5), MAX).gte(EMPTY).gt(EMPTY).lte(below(5)).lt(below(5)).eq(EMPTY),
            new CompTestCase(below(5), MIN).gte(below(5)).gt(of(MIN + 1, 5)).lte(single(MIN)).lt(EMPTY).eq(single(MIN)),

            new CompTestCase(above(5), 5).gte(above(5)).gt(above(6)).lte(single(5)).lt(EMPTY).eq(single(5)),
            new CompTestCase(above(5), 0).gte(above(5)).gt(above(5)).lte(EMPTY).lt(EMPTY).eq(EMPTY),
            new CompTestCase(above(5), -5).gte(above(5)).gt(above(5)).lte(EMPTY).lt(EMPTY).eq(EMPTY),
            new CompTestCase(above(5), MAX).gte(single(MAX)).gt(EMPTY).lte(above(5)).lt(of(5, MAX - 1)).eq(single(MAX)),
            new CompTestCase(above(5), MIN).gte(above(5)).gt(above(5)).lte(EMPTY).lt(EMPTY).eq(EMPTY),

            new CompTestCase(of(0, 5), 5).gte(single(5)).gt(EMPTY).lte(of(0, 5)).lt(of(0, 4)).eq(single(5)),
            new CompTestCase(of(0, 5), 0).gte(of(0, 5)).gt(of(1, 5)).lte(single(0)).lt(EMPTY).eq(single(0)),
            new CompTestCase(of(0, 5), -5).gte(of(0, 5)).gt(of(0, 5)).lte(EMPTY).lt(EMPTY).eq(EMPTY),
            new CompTestCase(of(0, 5), MAX).gte(EMPTY).gt(EMPTY).lte(of(0, 5)).lt(of(0, 5)).eq(EMPTY),
            new CompTestCase(of(0, 5), MIN).gte(of(0, 5)).gt(of(0, 5)).lte(EMPTY).lt(EMPTY).eq(EMPTY),
    };

    private static final MultiTestCase[] multiTestCases = {
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

    @Test
    public void testGTE() {
        for (CompTestCase testCase : compTestCases) {
            ConstConstraint constraint = ConstConstraint.gte(testCase.value);
            LongRange constraintReduced = constraint.bruteReduce(testCase.original);
            assertEquals(testCase.original + " gte " + testCase.value, testCase.gte, constraintReduced);
        }
    }

    @Test
    public void testGT() {
        for (CompTestCase testCase : compTestCases) {
            ConstConstraint constraint = ConstConstraint.gt(testCase.value);
            LongRange constraintReduced = constraint.bruteReduce(testCase.original);
            assertEquals(testCase.original + " gt " + testCase.value, testCase.gt, constraintReduced);
        }
    }

    @Test
    public void testLTE() {
        for (CompTestCase testCase : compTestCases) {
            ConstConstraint constraint = ConstConstraint.lte(testCase.value);
            LongRange constraintReduced = constraint.bruteReduce(testCase.original);
            assertEquals(testCase.original + " lte " + testCase.value, testCase.lte, constraintReduced);
        }
    }

    @Test
    public void testLT() {
        for (CompTestCase testCase : compTestCases) {
            ConstConstraint constraint = ConstConstraint.lt(testCase.value);
            LongRange constraintReduced = constraint.bruteReduce(testCase.original);
            assertEquals(testCase.original + " lt " + testCase.value, testCase.lt, constraintReduced);
        }
    }

    @Test
    public void testEq() {
        for (CompTestCase testCase : compTestCases) {
            ConstConstraint constraint = ConstConstraint.eq(testCase.value);
            LongRange constraintReduced = constraint.bruteReduce(testCase.original);
            assertEquals(testCase.original + " eq " + testCase.value, testCase.eq, constraintReduced);
        }
    }

    @Test
    public void testAnd() {
        for (MultiTestCase testCase : multiTestCases) {
            ConstConstraint constraint = ConstConstraint.and(testCase.constraints);
            LongRange constraintReduced = constraint.bruteReduce(testCase.original);
            String desc = "and " + Arrays.asList(testCase.constraints) + " of " + testCase.original;
            assertEquals(desc, testCase.and, constraintReduced);
        }
    }

    @Test
    public void testOr() {
        for (MultiTestCase testCase : multiTestCases) {
            ConstConstraint constraint = ConstConstraint.or(testCase.constraints);
            LongRange constraintReduced = constraint.bruteReduce(testCase.original);
            String desc = "or " + Arrays.asList(testCase.constraints) + " of " + testCase.original;
            assertEquals(desc, testCase.or, constraintReduced);
        }
    }

    @Test
    public void testDomainChange() {
        ConstOperator add1 = ConstOperator.add(1);
        ConstOperator sub1 = ConstOperator.add(-1);

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
            assertEquals(of(2, MAX - 1), constraint.bruteReduce(ALL));
        }

        { // x - 1 <= 3
            ConstConstraint constraint = domain(sub1, lte(3));
            assertEquals(of(MIN + 1, 4), constraint.bruteReduce(ALL));
        }

        { // x + 1 >= 0 && x - 1 <= 0
            ConstConstraint constraint = and(domain(add1, gte(0)), domain(sub1, lte(0)));
            assertEquals(of(-1, 1), constraint.bruteReduce(ALL));
        }

        { // (x + 1 >= -5 && x - 1 <= 5) || x - 1 < 0
            ConstConstraint constraint = or(and(domain(add1, gte(-5)), domain(sub1, lte(5))), domain(sub1, lt(0)));
            assertEquals(of(MIN + 1, 6), constraint.bruteReduce(ALL));
        }
    }

    private static class CompTestCase {
        private final LongRange original;
        private final long value;

        private LongRange gte;
        private LongRange gt;
        private LongRange lte;
        private LongRange lt;
        private LongRange eq;

        private CompTestCase(
                LongRange original,
                long value) {

            this.original = original;
            this.value = value;
        }

        public CompTestCase gte(LongRange expected) {
            this.gte = expected;
            return this;
        }

        public CompTestCase gt(LongRange expected) {
            this.gt = expected;
            return this;
        }

        public CompTestCase lte(LongRange expected) {
            this.lte = expected;
            return this;
        }

        public CompTestCase lt(LongRange expected) {
            this.lt = expected;
            return this;
        }

        public CompTestCase eq(LongRange expected) {
            this.eq = expected;
            return this;
        }
    }

    private static class MultiTestCase {
        private final LongRange original;
        private final ConstConstraint[] constraints;

        private LongRange and;
        private LongRange or;

        private MultiTestCase(LongRange original, ConstConstraint... constraints) {
            this.original = original;
            this.constraints = constraints;
        }

        public MultiTestCase and(LongRange expected) {
            this.and = expected;
            return this;
        }

        public MultiTestCase or(LongRange expected) {
            this.or = expected;
            return this;
        }
    }
}
