package ru.diefrein.pricechecker.common.util;

import ch.qos.logback.core.util.StringUtil;

public class ConfigurationUtils {

    /**
     * Gets variable from environment
     *
     * @param name variable name
     * @return variable value
     * @throws IllegalStateException if variable with specified name not found
     */
    public static String getEnv(String name) {
        String env = System.getenv(name);
        if (StringUtil.isNullOrEmpty(env)) {
            throw new IllegalStateException(String.format("Environment variable with name=%s is not set", name));
        }
        return env;
    }

    /**
     * Gets variable from environment. Provides default value if not found
     *
     * @param name variable name
     * @param defaultValue value by default
     * @return variable value
     * @throws IllegalStateException if variable with specified name not found and no default value provided
     */
    public static String getEnv(String name, String defaultValue) {
        String env = System.getenv(name);
        if (StringUtil.isNullOrEmpty(env)) {
            if (StringUtil.isNullOrEmpty(defaultValue)) {
                throw new IllegalStateException(
                        String.format(
                                "Environment variable with name=%s is not set, and default is null or empty",
                                name
                        )
                );
            }
            return defaultValue;
        }
        return env;
    }
}
