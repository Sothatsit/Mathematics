package net.paddyl.mathematics.function;

import net.paddyl.data.value.Value;

/**
 * A pure function that works on various value types.
 *
 * @author Paddy Lamont
 */
public interface Function {

    public Value apply(Value... values);
}
