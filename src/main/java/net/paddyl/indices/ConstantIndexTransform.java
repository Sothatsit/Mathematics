package net.paddyl.indices;

import net.paddyl.util.Checks;

/**
 * Takes in no indices, and returns its own constant indices.
 *
 * @author Paddy Lamont
 */
public class ConstantIndexTransform implements IndexTransform {

    private final int[] inputShape;
    private final int[] outputShape;
    private final int[] output;

    public ConstantIndexTransform(int[] outputShape, int[] output) {
        Checks.assertNonNull(outputShape, "outputShape");
        Checks.assertNonNull(output, "output");
        Checks.assertThat(outputShape.length == output.length, "expected outputShape to match output");

        this.inputShape = new int[0];
        this.outputShape = outputShape;
        this.output = output;
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
        return output;
    }
}
