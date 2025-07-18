package ru.diefrein.pricechecker.configuration.parameters;

import ru.diefrein.pricechecker.util.ConfigurationUtils;

import java.util.List;

public class ParserParameterProvider {
    public static final List<String> USER_AGENTS =
            List.of(ConfigurationUtils.getEnv("USER_AGENTS", "Googlebot,Mozilla").split(","));
    public static final String DEFAULT_USER_AGENT =
            ConfigurationUtils.getEnv("DEFAULT_USER_AGENT", "Googlebot");
    public static final int REQUEST_TIMEOUT_MS =
            Integer.parseInt(ConfigurationUtils.getEnv("PARSE_REQUEST_TIMEOUT_MS", "15000"));
}
