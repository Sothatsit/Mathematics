package net.paddyl.constraints;

import static net.paddyl.constraints.LongRange.*;

import org.junit.Assert;
import org.junit.Test;

public class ConstConstraintTests {

    private static final long MAX = Long.MAX_VALUE;
    private static final long MIN = Long.MIN_VALUE;

    private static final TestCase[] testCases = {
            new TestCase(ALL, 5).gte(above(5)).gt(above(6)).lte(below(5)).lt(below(4)).eq(single(5)),
            new TestCase(ALL, 0).gte(above(0)).gt(above(1)).lte(below(0)).lt(below(-1)).eq(single(0)),
            new TestCase(ALL, -5).gte(above(-5)).gt(above(-4)).lte(below(-5)).lt(below(-6)).eq(single(-5)),
            new TestCase(ALL, MAX).gte(single(MAX)).gt(EMPTY).lte(ALL).lt(below(MAX - 1)).eq(single(MAX)),
            new TestCase(ALL, MIN).gte(ALL).gt(above(MIN + 1)).lte(single(MIN)).lt(EMPTY).eq(single(MIN)),

            new TestCase(below(5), 5).gte(single(5)).gt(EMPTY).lte(below(5)).lt(below(4)).eq(single(5)),
            new TestCase(below(5), 0).gte(of(0, 5)).gt(of(1, 5)).lte(below(0)).lt(below(-1)).eq(single(0)),
            new TestCase(below(5), -5).gte(of(-5, 5)).gt(of(-4, 5)).lte(below(-5)).lt(below(-6)).eq(single(-5)),
            new TestCase(below(5), MAX).gte(EMPTY).gt(EMPTY).lte(below(5)).lt(below(5)).eq(EMPTY),
            new TestCase(below(5), MIN).gte(below(5)).gt(of(MIN + 1, 5)).lte(single(MIN)).lt(EMPTY).eq(single(MIN)),

            new TestCase(above(5), 5).gte(above(5)).gt(above(6)).lte(single(5)).lt(EMPTY).eq(single(5)),
            new TestCase(above(5), 0).gte(above(5)).gt(above(5)).lte(EMPTY).lt(EMPTY).eq(EMPTY),
            new TestCase(above(5), -5).gte(above(5)).gt(above(5)).lte(EMPTY).lt(EMPTY).eq(EMPTY),
            new TestCase(above(5), MAX).gte(single(MAX)).gt(EMPTY).lte(above(5)).lt(of(5, MAX - 1)).eq(single(MAX)),
            new TestCase(above(5), MIN).gte(above(5)).gt(above(5)).lte(EMPTY).lt(EMPTY).eq(EMPTY),

            new TestCase(of(0, 5), 5).gte(single(5)).gt(EMPTY).lte(of(0, 5)).lt(of(0, 4)).eq(single(5)),
            new TestCase(of(0, 5), 0).gte(of(0, 5)).gt(of(1, 5)).lte(single(0)).lt(EMPTY).eq(single(0)),
            new TestCase(of(0, 5), -5).gte(of(0, 5)).gt(of(0, 5)).lte(EMPTY).lt(EMPTY).eq(EMPTY),
            new TestCase(of(0, 5), MAX).gte(EMPTY).gt(EMPTY).lte(of(0, 5)).lt(of(0, 5)).eq(EMPTY),
            new TestCase(of(0, 5), MIN).gte(of(0, 5)).gt(of(0, 5)).lte(EMPTY).lt(EMPTY).eq(EMPTY),
    };

    @Test
    public void testGTE() {
        for (TestCase testCase : testCases) {
            ConstConstraint constraint = ConstConstraint.gte(testCase.value);
            LongRange constraintReduced = constraint.bruteReduce(testCase.original);
            Assert.assertEquals(testCase.original + " gte " + testCase.value, testCase.gte, constraintReduced);
        }
    }

    @Test
    public void testGT() {
        for (TestCase testCase : testCases) {
            ConstConstraint constraint = ConstConstraint.gt(testCase.value);
            LongRange constraintReduced = constraint.bruteReduce(testCase.original);
            Assert.assertEquals(testCase.original + " gt " + testCase.value, testCase.gt, constraintReduced);
        }
    }

    @Test
    public void testLTE() {
        for (TestCase testCase : testCases) {
            ConstConstraint constraint = ConstConstraint.lte(testCase.value);
            LongRange constraintReduced = constraint.bruteReduce(testCase.original);
            Assert.assertEquals(testCase.original + " lte " + testCase.value, testCase.lte, constraintReduced);
        }
    }

    @Test
    public void testLT() {
        for (TestCase testCase : testCases) {
            ConstConstraint constraint = ConstConstraint.lt(testCase.value);
            LongRange constraintReduced = constraint.bruteReduce(testCase.original);
            Assert.assertEquals(testCase.original + " lt " + testCase.value, testCase.lt, constraintReduced);
        }
    }

    @Test
    public void testEq() {
        for (TestCase testCase : testCases) {
            ConstConstraint constraint = ConstConstraint.eq(testCase.value);
            LongRange constraintReduced = constraint.bruteReduce(testCase.original);
            Assert.assertEquals(testCase.original + " eq " + testCase.value, testCase.eq, constraintReduced);
        }
    }

    private static class TestCase {
        private final LongRange original;
        private final long value;

        private LongRange gte;
        private LongRange gt;
        private LongRange lte;
        private LongRange lt;
        private LongRange eq;

        private TestCase(
                LongRange original,
                long value) {

            this.original = original;
            this.value = value;
        }

        public TestCase gte(LongRange expected) {
            this.gte = expected;
            return this;
        }

        public TestCase gt(LongRange expected) {
            this.gt = expected;
            return this;
        }

        public TestCase lte(LongRange expected) {
            this.lte = expected;
            return this;
        }

        public TestCase lt(LongRange expected) {
            this.lt = expected;
            return this;
        }

        public TestCase eq(LongRange expected) {
            this.eq = expected;
            return this;
        }
    }
}
