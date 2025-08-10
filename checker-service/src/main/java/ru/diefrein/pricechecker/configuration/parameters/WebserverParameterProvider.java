package ru.diefrein.pricechecker.configuration.parameters;

import ru.diefrein.pricechecker.common.util.ConfigurationUtils;

public class WebserverParameterProvider {
    public static final String HOST =
            ConfigurationUtils.getEnv("SERVER_HOST", "0.0.0.0");
    public static final int PORT =
            Integer.parseInt(ConfigurationUtils.getEnv("SERVER_PORT", "8080"));
    public static final String FRONTEND_URL =
            ConfigurationUtils.getEnv("FRONTEND_URL", "http://localhost:3000");
}
