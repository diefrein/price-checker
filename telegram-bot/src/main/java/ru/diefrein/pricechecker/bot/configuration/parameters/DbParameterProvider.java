package ru.diefrein.pricechecker.bot.configuration.parameters;

import ru.diefrein.pricechecker.common.util.ConfigurationUtils;

public class DbParameterProvider {
    public static final String DB_URL = ConfigurationUtils.getEnv("DB_URL");
    public static final String DB_USERNAME = ConfigurationUtils.getEnv("DB_USERNAME");
    public static final String DB_PASSWORD = ConfigurationUtils.getEnv("DB_PASSWORD");
    public static final int MAXIMUM_POOL_SIZE =
            Integer.parseInt(ConfigurationUtils.getEnv("MAXIMUM_POOL_SIZE", "10"));
}
