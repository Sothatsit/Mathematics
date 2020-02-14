package net.paddyl.util.function;

/**
 * Six inputs, single output.
 */
@FunctionalInterface
public interface F7<A, B, C, D, E, F, G, R> {

    public R apply(A a, B b, C c, D d, E e, F f, G g);
}
