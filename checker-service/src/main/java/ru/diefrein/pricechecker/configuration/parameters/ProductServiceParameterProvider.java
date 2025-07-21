package ru.diefrein.pricechecker.configuration.parameters;

import ru.diefrein.pricechecker.util.ConfigurationUtils;

import java.util.List;

public class ProductServiceParameterProvider {
    public static final int USERS_PAGE_SIZE = Integer.parseInt(ConfigurationUtils.getEnv("USERS_PAGE_SIZE", "5"));
    public static final int PRODUCTS_PAGE_SIZE = Integer.parseInt(ConfigurationUtils.getEnv("PRODUCTS_PAGE_SIZE", "5"));
}
