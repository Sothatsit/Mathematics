package net.paddyl.indices;

import net.paddyl.util.Checks;

/**
 * Can apply separate transformations to each index.
 *
 * @author Paddy Lamont
 */
public class MultiIndexTransform implements IndexTransform {

    private final IndexTransform[] transforms;
    private final int[] inputShape;
    private final int[] outputShape;

    public MultiIndexTransform(IndexTransform... transforms) {
        Checks.assertArrayNonNull(transforms, "transforms");

        int totalInputs = 0;
        int totalOutputs = 0;
        for(IndexTransform transform : transforms) {
            totalInputs += transform.getInputShape().length;
            totalOutputs += transform.getOutputShape().length;
        }

        int inputIndex = 0;
        int outputIndex = 0;
        this.inputShape = new int[totalInputs];
        this.outputShape = new int[totalOutputs];
        for(IndexTransform transform : transforms) {
            for(int dimension : transform.getInputShape()) {
                inputShape[inputIndex++] = dimension;
            }

            for(int dimension : transform.getOutputShape()) {
                outputShape[outputIndex++] = dimension;
            }
        }

        this.transforms = transforms;
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
        Checks.assertThat(inputShape.length == input.length, "input does not match inputShape");

        for(int index = 0; index < input.length; ++index) {
            Checks.assertInRange(input[index], "input[" + index + "]", 0, inputShape[index]);
        }

        int inputIndex = 0;
        int outputIndex = 0;

        int[] output = new int[outputShape.length];

        for(IndexTransform transform : transforms) {
            int[] transformInput = new int[transform.getInputShape().length];
            for(int index = 0; index < transformInput.length; ++index) {
                transformInput[index] = input[inputIndex++];
            }

            int[] transformed = transform.transform(transformInput);
            for (int transformedIndex : transformed) {
                output[outputIndex++] = transformedIndex;
            }
        }

        return output;
    }
}
