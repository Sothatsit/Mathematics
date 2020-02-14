package net.paddyl.util.function;

/**
 * Six inputs, single output.
 */
@FunctionalInterface
public interface F6<A, B, C, D, E, F, R> {

    public R apply(A a, B b, C c, D d, E e, F f);
}
