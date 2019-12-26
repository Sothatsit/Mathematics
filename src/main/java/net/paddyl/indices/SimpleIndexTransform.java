package net.paddyl.indices;

import net.paddyl.util.Checks;

/**
 * A simple one-dimensional index indices that can take a sub-sequence
 * of the output space and change the increment to reverse of skip some indices.
 *
 * i.e. {@code outputIndex = start + increment * inputIndex} where
 *      {@code inputIndex} is in the range {@code [0, length - 1)}
 *
 * @author Paddy Lamont
 */
public class SimpleIndexTransform implements IndexTransform {

    private final int[] originalShape;

    private final int start;
    private final int increment;
    private final int length;
    private final int[] inputShape;

    public SimpleIndexTransform(int originalLength, int start, int increment, int length) {
        Checks.assertPositive(originalLength, "originalLength");
        Checks.assertInRange(start, "start", 0, originalLength);
        Checks.assertInRange(start + increment * (length - 1), "last", 0, originalLength);
        Checks.assertNonZero(increment, "increment");
        Checks.assertPositive(length, "length");

        this.originalShape = new int[] {originalLength};

        this.start = start;
        this.increment = increment;
        this.length = length;
        this.inputShape = new int[] {length};
    }

    @Override
    public int[] getInputShape() {
        return inputShape;
    }

    @Override
    public int[] getOutputShape() {
        return originalShape;
    }

    public int transform(int input) {
        Checks.assertInRange(input, "input", 0, length);

        return start + increment * input;
    }

    @Override
    public int[] transform(int[] input) {
        Checks.assertNonNull(input, "input");
        Checks.assertThat(input.length == 1, "expected input of length 1");

        return new int[] {transform(input[0])};
    }
}
