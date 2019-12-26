package net.paddyl.indices;

import net.paddyl.util.Checks;

import java.util.Arrays;

/**
 * Allows chaining of many index transforms together.
 *
 * @author Paddy Lamont
 */
public class CompoundIndexTransform implements IndexTransform {

    private final IndexTransform[] transforms;

    public CompoundIndexTransform(IndexTransform[] transforms) {
        Checks.assertArrayNonNull(transforms, "transforms");
        Checks.assertPositive(transforms.length, "transforms.length");

        for(int index = 0; index < transforms.length - 2; ++index) {
            IndexTransform curr = transforms[index];
            IndexTransform next = transforms[index + 1];
            Checks.assertThat(Arrays.equals(curr.getOutputShape(), next.getInputShape()),
                    "Output shape of indices " + index + " does not match input of indices " + (index + 1));
        }

        this.transforms = transforms;
    }

    @Override
    public int[] getInputShape() {
        return transforms[0].getInputShape();
    }

    @Override
    public int[] getOutputShape() {
        return transforms[transforms.length - 1].getOutputShape();
    }

    @Override
    public int[] transform(int[] input) {
        int[] output = input;

        for(IndexTransform transform : transforms) {
            output = transform.transform(output);
        }

        return output;
    }
}
