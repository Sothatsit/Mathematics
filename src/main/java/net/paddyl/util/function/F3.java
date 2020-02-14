package net.paddyl.util.function;

/**
 * Three inputs, single output.
 */
@FunctionalInterface
public interface F3<A, B, C, R> {

    public R apply(A a, B b, C c);
}
