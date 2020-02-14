package net.paddyl.mathematics.function;

import net.paddyl.data.value.*;
import net.paddyl.util.Checks;

/**
 * A function that performs on sets of basic values, and where operations
 * on vectors are performed for each element in the vectors.
 *
 * @author Paddy Lamont
 */
public class PerValueFunction extends Function {

    private final Function function;

    public PerValueFunction(Function function) {
        super(null);

        Checks.assertNonNull(function, "function");

        this.function = function;
    }

    @Override
    public Object applyImpl(Object... values) {
        return null;

        /*
        if(values.length == 0)
            return apply();

        Class<?> datatype = values[0].getDataType();
        int vectorLength = -1;

        for(int index = 1; index < values.length; ++index) {
            Value value = values[index];
            Class<?> type = value.getDataType();

            if(type.equals(Vector.class)) {
                int length = value.asVector().length();

                if(vectorLength == -1) {
                    vectorLength = length;
                }

                if(vectorLength == length)
                    continue;

                throw new UnsupportedOperationException(getClass().getSimpleName() + " does not support vectors of different lengths");
            }

            if(datatype.equals(type))
                continue;

            throw new UnsupportedOperationException(getClass().getSimpleName() + " does not support mixed datatype inputs");
        }

        if(vectorLength != -1) {
            Value[] inputs = new Value[values.length];
            Value[] outputs = new Value[values.length];

            for(int index = 0; index < vectorLength; ++index) {
                for(int valueIndex = 0; valueIndex < values.length; ++valueIndex) {
                    Value value = values[valueIndex];

                    if(value instanceof Vector) {
                        value = value.asVector().value(index);
                    }

                    inputs[valueIndex] = value;
                }

                outputs[index] = apply(inputs);
            }

            return function.apply(outputs);
        }

        return function.apply(values);*/
    }
}
