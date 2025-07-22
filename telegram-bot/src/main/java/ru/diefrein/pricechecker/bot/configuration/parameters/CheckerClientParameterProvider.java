package ru.diefrein.pricechecker.bot.configuration.parameters;

import ru.diefrein.pricechecker.bot.util.ConfigurationUtils;

public class CheckerClientParameterProvider {
    public static final String CHECKER_SERVICE_HOST =
            ConfigurationUtils.getEnv("CHECKER_SERVICE_HOST", "http://checker-service");
    public static final int CHECKER_SERVICE_PORT =
            Integer.parseInt(ConfigurationUtils.getEnv("CHECKER_SERVICE_PORT", "8080"));
}
