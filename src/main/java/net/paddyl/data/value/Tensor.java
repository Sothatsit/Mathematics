package net.paddyl.data.value;

import net.paddyl.data.ArrayView;
import net.paddyl.util.Checks;

import java.util.Arrays;

/**
 * An arbritrary dimension collection of ordered elements accessible through arrays of indices.
 *
 * Can contain null values, if this is not wanted use {@link #assertElementsNonNull(String)}.
 *
 * @param <T> the type of element contained in this tensor
 */
public class Tensor<T extends Value> extends Value<Tensor<T>> {

    private final int[] shape;
    private final int elementCount;
    private final ArrayView<T> elements;

    public Tensor(Class<T> elementType, int[] shape) {
        Checks.assertNonNull(shape, "shape");
        Checks.assertThat(shape.length > 0, "shape cannot be empty");
        Checks.assertNonNull(elementType, "elementType");

        int elementCount = 1;
        for(int dimension : shape) {
            Checks.assertThat(dimension > 0, "shape must contain all positive values");
            elementCount *= dimension;
        }

        this.shape = shape;
        this.elementCount = elementCount;
        this.elements = ArrayView.create(elementType, elementCount);
    }

    public int[] getShape() {
        return shape;
    }

    public int getElementCount() {
        return elementCount;
    }

    public ArrayView<T> getRawElements() {
        return elements;
    }

    public Class<T> getElementType() {
        return elements.getElementType();
    }

    public void assertElementsNonNull(String name) {
        for(T element : elements) {
            Checks.assertThat(element != null, "expected all elements of " + name + " to be non-null");
        }
    }

    public int getRawIndex(int[] indices) {
        Checks.assertNonNull(indices, "indices");
        Checks.assertThat(
            shape.length == indices.length,
            "indices must have the same length as the shape of this tensor"
        );

        int rawIndex = 0;
        int scalingFactor = 1;
        for(int i = shape.length - 1; i >= 0; --i) {
            int index = indices[i];
            int dimension = shape[i];
            Checks.assertInRange(index, "index", 0, dimension);

            rawIndex += index * scalingFactor;
            scalingFactor *= dimension;
        }

        return rawIndex;
    }

    public T get(int[] indices) {
        return elements.get(getRawIndex(indices));
    }

    public void set(int[] indices, T value) {
        elements.set(getRawIndex(indices), value);
    }

    @Override
    public boolean isEqualTo(Tensor<T> value) {
        return elements.equals(value.elements);
    }

    @Override
    public String toString() {
        int currentDim = 0;
        int[] indices = new int[shape.length];
        Arrays.fill(indices, -1);

        StringBuilder builder = new StringBuilder("[");
        while(currentDim >= 0) {
            indices[currentDim] += 1;

            if(indices[currentDim] >= shape[currentDim]) {
                indices[currentDim] = -1;
                currentDim -= 1;
                builder.append("]");
                continue;
            }

            if(indices[currentDim] > 0) {
                builder.append(currentDim == 0 ? ",\n" : ", ");
            }

            if(currentDim < shape.length - 1) {
                currentDim += 1;
                builder.append("[");
                continue;
            }

            builder.append(get(indices));
        }
        return builder.toString();
    }

    public static void main(String[] arg) {
        Tensor<IntValue> tensor = new Tensor<>(IntValue.class, new int[] {4, 3, 2});

        for(int i=0; i < 2; ++i) {
            for(int j=0; j < 3; ++j) {
                for(int k=0; k < 4; ++k) {
                    tensor.set(new int[] {k, j, i}, new IntValue(6 * k + 2 * j + i));
                }
            }
        }

        System.out.println(tensor.toString());
    }
}
