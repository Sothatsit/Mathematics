package net.paddyl.data;

import net.paddyl.util.Cast;
import net.paddyl.util.Checks;

import java.util.Iterator;
import java.util.Objects;

public class ArrayView<T> implements Iterable<T> {

    private final T[] elements;
    private final Class<T> elementType;
    private final int first;
    private final int increment;
    private final int length;

    private ArrayView(T[] elements, int first, int increment, int length) {
        Checks.assertNonNull(elements, "elements");
        Checks.assertThat(length >= 0, "length must not be negative");

        if(length > 0) {
            Checks.assertThat(increment != 0, "increment must not be 0");
            Checks.assertIndexValid(first, "first", elements, "elements");
            Checks.assertIndexValid(first + increment * (length - 1), "last", elements, "elements");
        } else {
            first = 0;
            increment = 0;
        }

        this.elements = elements;
        this.elementType = Cast.cast(elements.getClass().getComponentType());
        this.first = first;
        this.increment = increment;
        this.length = length;
    }

    public Class<T> getElementType() {
        return elementType;
    }

    public int getLength() {
        return length;
    }

    public boolean isEmpty() {
        return length == 0;
    }

    public int getRawIndex(int index) {
        Checks.assertIndexValid(index, "index", elements, "elements");
        return first + index * increment;
    }

    public T get(int index) {
        return elements[getRawIndex(index)];
    }

    public void set(int index, T value) {
        if(value != null) {
            Checks.assertThat(elementType.isInstance(value), "value must be of type " + elementType);
        }

        elements[getRawIndex(index)] = value;
    }

    public ArrayView<T> subarray(int from, int increment, int length) {
        return new ArrayView<>(elements, getRawIndex(from), this.increment * increment, length);
    }

    public ArrayView<T> subarray(int from, int length) {
        return subarray(from, 1, length);
    }

    public ArrayView<T> every(int increment) {
        Checks.assertThat(increment != 0, "increment must not be 0");
        if(length == 0)
            return this;

        int from = (increment > 0 ? 0 : length - 1);
        int newLength = Math.abs((length - 1) / increment) + 1;

        return subarray(from, increment, newLength);
    }

    public ArrayView<T> reverse() {
        return every(-1);
    }

    public static <T> ArrayView<T> create(Class<T> elementType, int length) {
        return from(Cast.newArray(elementType, length));
    }

    public static <T> ArrayView<T> from(T[] elements) {
        return new ArrayView<>(elements, 0, 1, elements.length);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null || !getClass().equals(obj.getClass()))
            return false;

        ArrayView other = (ArrayView) obj;
        if(length != other.getLength())
            return false;

        for(int index = 0; index < length; ++index) {
            if(!Objects.equals(get(index), other.get(index)))
                return false;
        }

        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("[");

        for(int index = 0; index < length; ++index) {
            if(index > 0) {
                builder.append(", ");
            }

            builder.append(get(index));
        }

        return builder.append("]").toString();
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private int index = 0;

            @Override
            public boolean hasNext() {
                return index < length;
            }

            @Override
            public T next() {
                return get(index++);
            }
        };
    }
}
