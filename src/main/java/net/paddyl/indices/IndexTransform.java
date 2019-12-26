package net.paddyl.indices;

import net.paddyl.util.Checks;

/**
 * A indices applied to indexes that takes indexes shaped as in {@link #getInputShape()}
 * and transforms them into indexes shaped as in {@link #getOutputShape()} through the
 * method {@link #transform(int[])}.
 *
 * @author Paddy Lamont
 */
public interface IndexTransform {

    /**
     * Get the shape of the input indices where the output is an
     * array containing the number of indices in each dimension.
     *
     * This must not change through life of the object.
     */
    public int[] getInputShape();

    /**
     * Get the sahpe of the output indices where the output is an
     * array containing the number of indices in each dimension.
     *
     * This must not change through life of the object.
     */
    public int[] getOutputShape();

    /**
     * Transform the indices in {@param input} from the {@link #getInputShape()} space
     * to the {@link #getOutputShape()} space through some index transformation.
     */
    public int[] transform(int[] input);

    /**
     * Throws a {@link Checks.AssertionFailure} if any of the indices
     * in {@param indices} fall outside of the shape {@param shape}.
     * i.e.
     * 1) The length of {@param shape} and {@param indices} differ
     * 2) Any of {@param indices} are negative
     * 3) Any index in {@param indices} are >= their equivalent {@param shape} length
     */
    public static void assertIndicesMatchShape(int[] indices, int[] shape) {
        Checks.assertNonNull(shape, "shape");
        Checks.assertNonNull(indices, "indices");
        Checks.assertEquals(shape.length, "shape.length", indices.length, "indices.length");

        for (int i = 0; i < shape.length; ++i) {
            Checks.assertIndexValid(indices[i], "indices[" + i + "]", shape[i], "shape[" + i + "]");
        }
    }
}
