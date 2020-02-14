package net.paddyl.util.function;

/**
 * Five inputs, single output.
 */
@FunctionalInterface
public interface F5<A, B, C, D, E, R> {

    public R apply(A a, B b, C c, D d, E e);
}
