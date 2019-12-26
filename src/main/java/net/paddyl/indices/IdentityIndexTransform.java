package net.paddyl.indices;

import net.paddyl.util.Checks;

/**
 * Performs no change to the indices.
 *
 * @author Paddy Lamont
 */
public class IdentityIndexTransform implements IndexTransform {

    private final int[] shape;

    public IdentityIndexTransform(int[] shape) {
        Checks.assertNonNull(shape, "shape");

        for(int dimension : shape) {
            Checks.assertThat(dimension > 0, "all dimensions must be of size 1 or greater");
        }

        this.shape = shape;
    }

    @Override
    public int[] getInputShape() {
        return shape;
    }

    @Override
    public int[] getOutputShape() {
        return shape;
    }

    @Override
    public int[] transform(int[] input) {
        IndexTransform.assertIndicesMatchShape(input, shape);
        return input;
    }
}
