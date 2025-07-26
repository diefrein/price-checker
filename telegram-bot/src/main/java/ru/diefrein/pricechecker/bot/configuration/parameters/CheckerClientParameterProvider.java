package ru.diefrein.pricechecker.bot.configuration.parameters;

import ru.diefrein.pricechecker.common.util.ConfigurationUtils;

public class CheckerClientParameterProvider {
    public static final String CHECKER_SERVICE_HOST =
            ConfigurationUtils.getEnv("CHECKER_SERVICE_HOST", "http://checker-service");
    public static final int CHECKER_SERVICE_PORT =
            Integer.parseInt(ConfigurationUtils.getEnv("CHECKER_SERVICE_PORT", "8080"));
    public static final int REQUEST_TIMEOUT_MS =
            Integer.parseInt(ConfigurationUtils.getEnv("REQUEST_TIMEOUT_MS", "1000"));
    public static final String CHECKER_SERVICE_URL =
            String.format("%s:%s", CHECKER_SERVICE_HOST, CHECKER_SERVICE_PORT);
}
