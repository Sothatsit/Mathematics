package net.paddyl.mathematics.function;

import net.paddyl.util.Checks;
import net.paddyl.util.function.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * A function that works on various value types.
 *
 * @author Paddy Lamont
 */
public abstract class Function {

    public final FunctionSettings settings;

    public Function(FunctionSettings settings) {
        this.settings = settings;
    }

    public Object apply(Object... values) {
        Checks.assertArrayNonNull(values, "values");
        Checks.assertGreaterThanOrEqual(values.length, "values.length", settings.minInputs, "minInputs");
        Checks.assertLessThanOrEqual(values.length, "values.length", settings.maxInputs, "maxInputs");
        return applyImpl(values);
    }

    protected abstract Object applyImpl(Object... values);

    public static <R> Function create(String name, F0<R> function) {
        return new FunctionWithImpl(name, function, 0);
    }

    public static <A, R> Function create(String name, F1<A, R> function) {
        return new FunctionWithImpl(name, function, 1);
    }

    public static <A, B, R> Function create(String name, F2<A, B, R> function) {
        return new FunctionWithImpl(name, function, 2);
    }

    public static <A, B, C, R> Function create(String name, F3<A, B, C, R> function) {
        return new FunctionWithImpl(name, function, 3);
    }

    public static <A, B, C, D, R> Function create(String name, F4<A, B, C, D, R> function) {
        return new FunctionWithImpl(name, function, 4);
    }

    public static <A, B, C, D, E, R> Function create(String name, F5<A, B, C, D, E, R> function) {
        return new FunctionWithImpl(name, function, 5);
    }

    public static <A, B, C, D, E, F, R> Function create(String name, F6<A, B, C, D, E, F, R> function) {
        return new FunctionWithImpl(name, function, 6);
    }

    public static <A, B, C, D, E, F, G, R> Function create(String name, F7<A, B, C, D, E, F, G, R> function) {
        return new FunctionWithImpl(name, function, 7);
    }

    public static <A, B, C, D, E, F, G, H, R> Function create(String name, F8<A, B, C, D, E, F, G, H, R> function) {
        return new FunctionWithImpl(name, function, 8);
    }

    public static <A, B, C, D, E, F, G, H, I, R> Function create(String name, F9<A, B, C, D, E, F, G, H, I, R> function) {
        return new FunctionWithImpl(name, function, 9);
    }

    public static class FunctionWithImpl extends Function {

        public final Object function;
        public final Method applyMethod;

        public FunctionWithImpl(String name, Object function, int paramCount) {
            super(generateFunctionSettings(name, function, paramCount));

            this.function = function;
            this.applyMethod = findApplyMethod(function.getClass(), paramCount);
        }

        @Override
        protected Object applyImpl(Object... values) {
            try {
                return applyMethod.invoke(function, values);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new IllegalStateException("Unable to apply function " + settings.name, e);
            }
        }
    }

    private static FunctionSettings generateFunctionSettings(String name, Object function, int paramCount) {
        Method applyMethod = findApplyMethod(function.getClass(), paramCount);
        Class<?>[] paramTypes = applyMethod.getParameterTypes();
        Class<?> returnType = applyMethod.getReturnType();

        return FunctionSettings.builder(name)
                .withParameterTypes(paramTypes)
                .withReturnType(returnType)
                .build();
    }

    private static Method findApplyMethod(Class<?> functionClass, int paramCount) {
        Method[] methods = functionClass.getMethods();
        Method found = null;
        for (Method method : methods) {
            if (!"apply".equals(method.getName()))
                continue;
            if (paramCount != method.getParameterCount())
                continue;

            if (found != null) {
                throw new IllegalStateException(
                        "Found more than one method named \"apply\" with " +
                        paramCount + " parameters in class " + functionClass
                );
            }

            found = method;
        }
        if (found == null) {
            throw new IllegalStateException(
                    "Could not find method named \"apply\" with " +
                    paramCount + " parameters in class " + functionClass
            );
        }
        return found;
    }
}
