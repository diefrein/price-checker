package ru.diefrein.pricechecker.util;

import ch.qos.logback.core.util.StringUtil;

public class ConfigurationUtils {

    public static String getEnv(String name) {
        String env = System.getenv(name);
        if (StringUtil.isNullOrEmpty(env)) {
            throw new IllegalStateException(String.format("Environment variable with name=%s is not set", name));
        }
        return env;
    }

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
