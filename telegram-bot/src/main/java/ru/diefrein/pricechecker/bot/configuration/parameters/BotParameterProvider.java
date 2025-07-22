package ru.diefrein.pricechecker.bot.configuration.parameters;

import ru.diefrein.pricechecker.bot.util.ConfigurationUtils;

public class BotParameterProvider {
    public static final String TOKEN = ConfigurationUtils.getEnv("BOT_TOKEN");
    public static final String NAME = ConfigurationUtils.getEnv("BOT_NAME", "price-checker-bot");
}
