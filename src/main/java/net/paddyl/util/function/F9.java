package net.paddyl.util.function;

/**
 * Seven inputs, single output.
 */
@FunctionalInterface
public interface F9<A, B, C, D, E, F, G, H, I, R> {

    public R apply(A a, B b, C c, D d, E e, F f, G g, H h, I i);
}
