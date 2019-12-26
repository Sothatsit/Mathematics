package net.paddyl.indices;

import net.paddyl.util.Checks;

/**
 * Takes an arbitrary dimension index and flattens it into one dimension.
 *
 * Each index is multiplied by the product of the size of all
 * dimensions after it when they are summed into the one index.
 *
 * i.e. if {@code dim(i)} is the size of i'th index's dimension, and {@code d} is the number of dimensions:
 *    The last index will have stride {@code 1},
 *    the second last index will have the stride {@code dim(d - 1)},
 *    the third last index will have the stride {@code dim(d - 1) * dim(d - 2)},
 *    etc...
 *
 * @author Paddy Lamont
 */
public class FlatteningTransform implements IndexTransform {

    private final int[] inputShape;
    private final int[] outputShape;

    public FlatteningTransform(int[] inputShape) {
        Checks.assertNonNull(inputShape, "inputShape");
        Checks.assertPositive(inputShape.length, "inputShape.length");

        int totalElements = 1;
        for(int dimension : inputShape) {
            Checks.assertThat(dimension > 0, "all dimensions must be of size 1 or greater");

            totalElements *= dimension;
        }

        this.inputShape = inputShape;
        this.outputShape = new int[] {totalElements};
    }

    @Override
    public int[] getInputShape() {
        return inputShape;
    }

    @Override
    public int[] getOutputShape() {
        return outputShape;
    }

    @Override
    public int[] transform(int[] input) {
        Checks.assertNonNull(input, "input");

        int outputIndex = 0;
        int scalingFactor = 1;

        for(int index = inputShape.length - 1; index >= 0; --index) {
            int inputIndex = input[index];
            int dimension = inputShape[index];

            Checks.assertInRange(inputIndex, "input[" + index + "]", 0, dimension);

            outputIndex += inputIndex * scalingFactor;
            scalingFactor *= dimension;
        }

        return new int[] {outputIndex};
    }
}
