package net.paddyl.util.function;

/**
 * Single input, single output.
 */
@FunctionalInterface
public interface F1<A, R> {

    public R apply(A a);
}
