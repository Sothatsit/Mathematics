package net.paddyl.util;

import java.lang.reflect.Array;

/**
 * A utility class to provide warning free casting of values.
 *
 * @author Paddy Lamont
 */
public class Cast {

    /**
     * Cast {@param obj} to the type {@param T}.
     */
    public static <T> T cast(Object obj) {
        return (T) obj;
    }

    /**
     * Create a new generic array of type {@param elementType} with length {@param length}.
     */
    public static <T> T[] newArray(Class<T> elementType, int length) {
        return cast(Array.newInstance(elementType, length));
    }
}
