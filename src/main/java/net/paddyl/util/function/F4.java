package net.paddyl.util.function;

/**
 * Four inputs, single output.
 */
@FunctionalInterface
public interface F4<A, B, C, D, R> {

    public R apply(A a, B b, C c, D d);
}
