package net.paddyl.constraints;

import static net.paddyl.constraints.set.LongRange.*;
import static org.junit.Assert.*;

import net.paddyl.constraints.set.LongRange;
import net.paddyl.constraints.set.LongRangeFactory;
import org.junit.Test;

import java.util.Arrays;

public class ConstConstraintLongTests {

    private final LongRangeFactory factory = new LongRangeFactory();
    private final long MAX = Long.MAX_VALUE;
    private final long MIN = Long.MIN_VALUE;

    private final CompTestCase[] compTestCases = {
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

    private final MultiTestCase[] multiTestCases = {
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
            ConstConstraint<LongRange, Long> constraint = gte(testCase.value);
            LongRange constraintReduced = constraint.bruteReduce(testCase.original);
            assertEquals(testCase.original + " gte " + testCase.value, testCase.gte, constraintReduced);
        }
    }

    @Test
    public void testGT() {
        for (CompTestCase testCase : compTestCases) {
            ConstConstraint<LongRange, Long> constraint = gt(testCase.value);
            LongRange constraintReduced = constraint.bruteReduce(testCase.original);
            assertEquals(testCase.original + " gt " + testCase.value, testCase.gt, constraintReduced);
        }
    }

    @Test
    public void testLTE() {
        for (CompTestCase testCase : compTestCases) {
            ConstConstraint<LongRange, Long> constraint = lte(testCase.value);
            LongRange constraintReduced = constraint.bruteReduce(testCase.original);
            assertEquals(testCase.original + " lte " + testCase.value, testCase.lte, constraintReduced);
        }
    }

    @Test
    public void testLT() {
        for (CompTestCase testCase : compTestCases) {
            ConstConstraint<LongRange, Long> constraint = lt(testCase.value);
            LongRange constraintReduced = constraint.bruteReduce(testCase.original);
            assertEquals(testCase.original + " lt " + testCase.value, testCase.lt, constraintReduced);
        }
    }

    @Test
    public void testEq() {
        for (CompTestCase testCase : compTestCases) {
            ConstConstraint<LongRange, Long> constraint = eq(testCase.value);
            LongRange constraintReduced = constraint.bruteReduce(testCase.original);
            assertEquals(testCase.original + " eq " + testCase.value, testCase.eq, constraintReduced);
        }
    }

    @Test
    public void testAnd() {
        for (MultiTestCase testCase : multiTestCases) {
            ConstConstraint<LongRange, Long> constraint = and(testCase.constraints);
            LongRange constraintReduced = constraint.bruteReduce(testCase.original);
            String desc = "and " + Arrays.asList(testCase.constraints) + " of " + testCase.original;
            assertEquals(desc, testCase.and, constraintReduced);
        }
    }

    @Test
    public void testOr() {
        for (MultiTestCase testCase : multiTestCases) {
            ConstConstraint<LongRange, Long> constraint = or(testCase.constraints);
            LongRange constraintReduced = constraint.bruteReduce(testCase.original);
            String desc = "or " + Arrays.asList(testCase.constraints) + " of " + testCase.original;
            assertEquals(desc, testCase.or, constraintReduced);
        }
    }

    @Test
    public void testDomainChange() {
        ConstOperator<LongRange, Long> add1 = add(1);
        ConstOperator<LongRange, Long> sub1 = add(-1);

        { // x + 1 == 2
            ConstConstraint<LongRange, Long> constraint = domain(add1, eq(2));
            assertEquals(single(1), constraint.bruteReduce(ALL));
        }

        { // x - 1 == 2
            ConstConstraint<LongRange, Long> constraint = domain(sub1, eq(2));
            assertEquals(single(3), constraint.bruteReduce(ALL));
        }

        { // x + 1 >= 3
            ConstConstraint<LongRange, Long> constraint = domain(add1, gte(3));
            assertEquals(of(2, MAX - 1), constraint.bruteReduce(ALL));
        }

        { // x - 1 <= 3
            ConstConstraint<LongRange, Long> constraint = domain(sub1, lte(3));
            assertEquals(of(MIN + 1, 4), constraint.bruteReduce(ALL));
        }

        { // x + 1 >= 0 && x - 1 <= 0
            ConstConstraint<LongRange, Long> constraint = and(domain(add1, gte(0)), domain(sub1, lte(0)));
            assertEquals(of(-1, 1), constraint.bruteReduce(ALL));
        }

        { // (x + 1 >= -5 && x - 1 <= 5) || x - 1 < 0
            ConstConstraint<LongRange, Long> constraint = or(and(domain(add1, gte(-5)), domain(sub1, lte(5))), domain(sub1, lt(0)));
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

            new Exception(original + " - " + value).printStackTrace(System.out);
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
        private final ConstConstraint<LongRange, Long>[] constraints;

        private LongRange and;
        private LongRange or;

        @SafeVarargs
        private MultiTestCase(LongRange original, ConstConstraint<LongRange, Long>... constraints) {
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

    public LongRange single(long value) {
        return factory.single(value);
    }

    public LongRange of(long from, long to) {
        return factory.range(from, to);
    }

    public LongRange above(long value) {
        return factory.aboveIncl(value);
    }

    public LongRange aboveNotIncl(long value) {
        return factory.above(value);
    }

    public LongRange below(long value) {
        return factory.belowIncl(value);
    }

    public LongRange belowNotIncl(long value) {
        return factory.below(value);
    }

    public ConstConstraint<LongRange, Long> gte(long value) {
        return factory.gte(value);
    }

    public ConstConstraint<LongRange, Long> gt(long value) {
        return factory.gt(value);
    }

    public ConstConstraint<LongRange, Long> lte(long value) {
        return factory.lte(value);
    }

    public ConstConstraint<LongRange, Long> lt(long value) {
        return factory.lt(value);
    }

    public ConstConstraint<LongRange, Long> eq(long value) {
        return factory.eq(value);
    }

    @SafeVarargs
    public final ConstConstraint<LongRange, Long> and(ConstConstraint<LongRange, Long>... constraints) {
        return factory.and(constraints);
    }

    @SafeVarargs
    public final ConstConstraint<LongRange, Long> or(ConstConstraint<LongRange, Long>... constraints) {
        return factory.or(constraints);
    }

    public ConstConstraint<LongRange, Long> domain(
            ConstOperator<LongRange, Long> operator,
            ConstConstraint<LongRange, Long> constraint) {

        return factory.domain(operator, constraint);
    }

    public ConstOperator<LongRange, Long> add(long value) {
        return factory.add(value);
    }
}
