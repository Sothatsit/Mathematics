package net.paddyl.util;

import org.junit.Test;

/**
 * Tests for {@link Checks}.
 */
public class ChecksTest {

    @Test
    public void testThrows() {
        Checks.assertThrows(() -> {
            throw new Checks.AssertionFailure("testThrows");
        }, Checks.AssertionFailure.class);

        try {
            Checks.assertThrows(() -> {}, Checks.AssertionFailure.class);
            throw new Checks.AssertionFailure("Expected assertThrows to fail as no exception was thrown");
        } catch (Checks.AssertionFailure failure) {
            // Expected
        }

        try {
            Checks.assertThrows(() -> {
                throw new RuntimeException("testThrows");
            }, Checks.AssertionFailure.class);
            throw new Checks.AssertionFailure("Expected assertThrows to fail as the wrong exception type was thrown");
        } catch (Checks.AssertionFailure failure) {
            // Expected
        }
    }
}
