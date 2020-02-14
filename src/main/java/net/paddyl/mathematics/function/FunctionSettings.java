package net.paddyl.mathematics.function;

import net.paddyl.data.value.Value;
import net.paddyl.util.Checks;

import java.util.Arrays;

/**
 * Metadata about a Function to help with its validation.
 */
public class FunctionSettings {

    public final String name;
    public final int minInputs;
    public final int maxInputs;
    public final ParameterSettings[] parameterSettings;
    public final ReturnSettings returnSettings;

    public FunctionSettings(String name, int minInputs, int maxInputs,
                            ParameterSettings[] parameterSettings,
                            ReturnSettings returnSettings) {

        Checks.assertNonNull(name, "name");
        Checks.assertPositive(minInputs, "minInputs");
        Checks.assertGreaterThanOrEqual(maxInputs, "maxInputs", minInputs, "minInputs");
        Checks.assertArrayNonNull(parameterSettings, "parameterSettings");
        Checks.assertNonNull(returnSettings, "returnSettings");

        this.name = name;
        this.minInputs = minInputs;
        this.maxInputs = maxInputs;
        this.parameterSettings = parameterSettings;
        this.returnSettings = returnSettings;
    }

    /**
     * Metadata about the types of values accepted by a function.
     */
    public static class ParameterSettings {
        public final Class<?> acceptedType;

        public ParameterSettings(Class<?> acceptedType) {
            this.acceptedType = acceptedType;
        }
    }

    /**
     * Metadata about the type returned by a function.
     */
    public static class ReturnSettings {
        public final Class<?> returnedType;

        public ReturnSettings(Class<?> returnedType) {
            this.returnedType = returnedType;
        }
    }

    public static FunctionSettingsBuilder builder(String name) {
        return new FunctionSettingsBuilder(name);
    }

    public static class FunctionSettingsBuilder {
        private final String name;
        private int minInputs = -1;
        private int maxInputs = -1;
        private ParameterSettings[] parameterSettings = null;
        private ReturnSettings returnSettings = null;

        public FunctionSettingsBuilder(String name) {
            this.name = name;
        }

        private FunctionSettingsBuilder withMinInputs(int minInputs) {
            Checks.assertNotNegative(minInputs, "minInputs");
            Checks.assertThat(this.minInputs == -1, "minInputs has already been set");
            this.minInputs = minInputs;
            return this;
        }

        private FunctionSettingsBuilder withMaxInputs(int maxInputs) {
            Checks.assertNotNegative(maxInputs, "maxInputs");
            Checks.assertThat(this.maxInputs == -1, "maxInputs has already been set");
            this.maxInputs = maxInputs;
            return this;
        }

        public FunctionSettingsBuilder withUniformVariableParameters(ParameterSettings parameterSettings,
                                                                     int minInputs, int maxInputs) {

            Checks.assertArrayNonNull(parameterSettings, "parameterSettings");
            Checks.assertThat(this.parameterSettings == null, "parameterSettings has already been set");
            this.parameterSettings = new ParameterSettings[maxInputs];
            Arrays.fill(this.parameterSettings, parameterSettings);
            return withMinInputs(minInputs).withMaxInputs(maxInputs);
        }

        private FunctionSettingsBuilder withSetInputCount(int inputCount) {
            return withMinInputs(inputCount).withMaxInputs(inputCount);
        }

        public FunctionSettingsBuilder withParameterSettings(ParameterSettings[] parameterSettings) {
            Checks.assertArrayNonNull(parameterSettings, "parameterSettings");
            Checks.assertThat(this.parameterSettings == null, "parameterSettings has already been set");
            this.parameterSettings = parameterSettings;
            return withSetInputCount(parameterSettings.length);
        }

        public FunctionSettingsBuilder withParameterTypes(Class<?>[] types) {
            Checks.assertArrayNonNull(types, "types");
            ParameterSettings[] settings = new ParameterSettings[types.length];
            for (int index = 0; index < types.length; ++index) {
                settings[index] = new ParameterSettings(types[index]);
            }
            return withParameterSettings(settings);
        }

        public FunctionSettingsBuilder withReturnSettings(ReturnSettings returnSettings) {
            Checks.assertNonNull(returnSettings, "returnSettings");
            Checks.assertThat(this.returnSettings == null, "returnSettings has already been set");
            this.returnSettings = returnSettings;
            return this;
        }

        public FunctionSettingsBuilder withReturnType(Class<?> type) {
            Checks.assertNonNull(type, "type");
            return withReturnSettings(new ReturnSettings(type));
        }

        public FunctionSettings build() {
            return new FunctionSettings(name, minInputs, maxInputs, parameterSettings, returnSettings);
        }
    }
}
