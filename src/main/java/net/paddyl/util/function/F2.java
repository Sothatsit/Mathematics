package net.paddyl.util.function;

/**
 * Two inputs, single output.
 */
@FunctionalInterface
public interface F2<A, B, R> {

    public R apply(A a, B b);
}
