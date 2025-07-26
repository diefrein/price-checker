package ru.diefrein.pricechecker.configuration.parameters;

import ru.diefrein.pricechecker.common.util.ConfigurationUtils;

public class SchedulerParameterProvider {
    public static int PRODUCT_PRICE_JOB_INITIAL_DELAY_SECONDS =
            Integer.parseInt(ConfigurationUtils.getEnv("PRODUCT_PRICE_JOB_INITIAL_DELAY_SECONDS", "60"));
    public static int PRODUCT_PRICE_JOB_RATE_SECONDS =
            Integer.parseInt(ConfigurationUtils.getEnv("PRODUCT_PRICE_JOB_RATE_SECONDS", "3600"));
}
