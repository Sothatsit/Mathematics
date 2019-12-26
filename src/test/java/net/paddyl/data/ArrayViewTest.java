package net.paddyl.data;

import net.paddyl.util.Checks;
import org.junit.Test;

public class ArrayViewTest {

    public ArrayView<Integer> create(int first, int length) {
        return create(first, 1, length);
    }

    public ArrayView<Integer> create(int first, int increment, int length) {
        ArrayView<Integer> array = ArrayView.create(Integer.class, length);

        for(int index = 0; index < length; ++index) {
            array.set(index, first + increment * index);
        }

        return array;
    }

    @Test
    public void testSubarray() {
        { // Array 1 -> 10, subarray 1 -> 10 (1)
            ArrayView actual = create(1, 10).subarray(0, 10);
            ArrayView expected = create(1, 10);

            Checks.assertEquals(actual, expected);
        }

        { // Array 1 -> 10, subarray 3 -> 7 (1)
            ArrayView actual = create(1, 10).subarray(2, 5);
            ArrayView expected = create(3, 5);

            Checks.assertEquals(actual, expected);
        }

        { // Array 1 -> 10, subarray 3 -> 7 (-1)
            ArrayView actual = create(1, 10).subarray(6, -1, 5);
            ArrayView expected = create(7, -1, 5);

            Checks.assertEquals(actual, expected);
        }

        { // Array 1 -> 10, subarray 3 -> 7 (-2)
            ArrayView actual = create(1, 10).subarray(6, -2, 3);
            ArrayView expected = create(7, -2, 3);

            Checks.assertEquals(actual, expected);
        }

        { // Array 1 -> 100, subarray 10 -> 90 (5), subarray 15 -> 85 (-10)
            ArrayView actual = create(1, 100).subarray(9, 5, 16).subarray(15, -2, 7);
            ArrayView expected = create(85, -10, 7);

            Checks.assertEquals(actual, expected);
        }

        { // Array 1 -> 99, subarray 10 -> 90 (5), subarray 15 -> 85 (-10)
            ArrayView actual = create(1, 99).subarray(9, 5, 16).subarray(15, -2, 7);
            ArrayView expected = create(85, -10, 7);

            Checks.assertEquals(actual, expected);
        }

        { // Array 2 -> 99, subarray 10 -> 90 (5), subarray 15 -> 85 (-10)
            ArrayView actual = create(2, 98).subarray(8, 5, 16).subarray(15, -2, 7);
            ArrayView expected = create(85, -10, 7);

            Checks.assertEquals(actual, expected);
        }

        { // Array 2 -> 98, subarray 10 -> 90 (5), subarray 15 -> 85 (-10)
            ArrayView actual = create(2, 97).subarray(8, 5, 16).subarray(15, -2, 7);
            ArrayView expected = create(85, -10, 7);

            Checks.assertEquals(actual, expected);
        }
    }

    @Test
    public void testEvery() {
        { // Array 1 -> 10, every 1
            ArrayView actual = create(1, 10).every(1);
            ArrayView expected = create(1, 10);

            Checks.assertEquals(actual, expected);
        }

        { // Array 1 -> 10, every 2
            ArrayView actual = create(1, 10).every(2);
            ArrayView expected = create(1, 2, 5);

            Checks.assertEquals(actual, expected);
        }

        { // Array 1 -> 9, every -2
            ArrayView actual = create(1, 9).every(-2);
            ArrayView expected = create(9, -2, 5);

            Checks.assertEquals(actual, expected);
        }

        { // Array 4 -> 100, every -2 every -2, and every 4
            ArrayView original = create(4, 97);
            ArrayView everyNeg2Neg2 = original.every(-2).every(-2);
            ArrayView every4 = original.every(4);

            Checks.assertEquals(everyNeg2Neg2, every4);
        }
    }

    @Test
    public void testReverse() {
        { // Simple 1 -> 10 numbers, no increment, no subarray
            ArrayView original = create(1, 10);
            ArrayView reversed = original.reverse();
            ArrayView expected = create(10, -1, 10);
            ArrayView twiceReversed = reversed.reverse();

            Checks.assertEquals(reversed, expected);
            Checks.assertEquals(twiceReversed, original);
        }

        { // 1 -> 100, subarray 10 -> 100 (10), positive increment
            ArrayView original = create(1, 100).subarray(9, 10, 10);
            ArrayView reversed = original.reverse();
            ArrayView expected = create(100, -10, 10);
            ArrayView twiceReversed = reversed.reverse();

            Checks.assertEquals(reversed, expected);
            Checks.assertEquals(twiceReversed, original);
        }

        { // 1 -> 100, subarray 10 -> 100 (-10), negative increment
            ArrayView original = create(1, 100).subarray(99, -10, 10);
            ArrayView reversed = original.reverse();
            ArrayView expected = create(10, 10, 10);
            ArrayView twiceReversed = reversed.reverse();

            Checks.assertEquals(reversed, expected);
            Checks.assertEquals(twiceReversed, original);
        }
    }
}
