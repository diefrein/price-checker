package ru.diefrein.pricechecker.bot.configuration.parameters;

import ru.diefrein.pricechecker.common.util.ConfigurationUtils;

public class BotParameterProvider {
    public static final String TOKEN = ConfigurationUtils.getEnv("BOT_TOKEN");
    public static final String NAME = ConfigurationUtils.getEnv("BOT_NAME", "price-checker-bot");

    public static final String UNKNOWN_COMMAND_RESPONSE = ConfigurationUtils.getEnv(
            "UNKNOWN_COMMAND_RESPONSE",
            "Unknown command. Type /start to list all available commands"
    );
    public static final String USER_NOT_FOUND_RESPONSE = ConfigurationUtils.getEnv(
            "USER_NOT_FOUND_RESPONSE",
            "User not found. Please use /register first"
    );
    public static final String GENERAL_ERROR_RESPONSE = ConfigurationUtils.getEnv(
            "GENERAL_ERROR_RESPONSE",
            "Unknown error, please contact system administrator"
    );
    public static final String REGISTER_RESPONSE = ConfigurationUtils.getEnv(
            "REGISTER_RESPONSE",
            "User was successfully registered"
    );
    public static final String START_RESPONSE = ConfigurationUtils.getEnv(
            "START_RESPONSE",
            """
                    Welcome to Price Checker Bot!
                    Type /register to create a profile
                    Type /subscribe to subscribe for price updates
                    Type /subscriptions to list all your subscriptions
                    """
    );
    public static final String SUBSCRIBE_INITIAL_RESPONSE = ConfigurationUtils.getEnv(
            "SUBSCRIBE_INITIAL_RESPONSE",
            "Type link in message below"
    );
    public static final String SUBSCRIBE_WAIT_FOR_LINK_RESPONSE = ConfigurationUtils.getEnv(
            "SUBSCRIBE_WAIT_FOR_LINK_RESPONSE",
            "Product saved successfully!"
    );
    public static final String SUBSCRIBE_WAIT_FOR_LINK_ERROR_RESPONSE = ConfigurationUtils.getEnv(
            "SUBSCRIBE_INITIAL_RESPONSE",
            "Error while saving product, please retry request later"
    );
    public static final String USER_ALREADY_EXISTS_RESPONSE = ConfigurationUtils.getEnv(
            "USER_ALREADY_EXISTS_RESPONSE",
            "User already exists. Please use /start to find available commands"
    );
    public static final String SUBSCRIPTIONS_NO_SUBSCRIPTIONS_FOUND_RESPONSE = ConfigurationUtils.getEnv(
            "SUBSCRIPTIONS_NO_SUBSCRIPTIONS_FOUND_RESPONSE",
            "No subscriptions found. Type /subscribe to create new"
    );
    public static final String SUBSCRIPTIONS_SUBSCRIPTIONS_FOUND_RESPONSE = ConfigurationUtils.getEnv(
            "SUBSCRIPTIONS_SUBSCRIPTIONS_FOUND_RESPONSE",
            "Following subscriptions found:\n\n"
    );
    public static final String SUBSCRIPTIONS_SUBSCRIPTION_TEMPLATE = ConfigurationUtils.getEnv(
            "SUBSCRIPTIONS_SUBSCRIPTION_TEMPLATE",
            "%s\n%s\n%s"
    );
    public static final String SUBSCRIPTIONS_SUBSCRIPTION_BUTTON_TEMPLATE = ConfigurationUtils.getEnv(
            "SUBSCRIPTIONS_SUBSCRIPTION_BUTTON_TEMPLATE",
            "❌ Remove (%s) %s"
    );
    public static final String REMOVE_SUBSCRIPTION_RESPONSE = ConfigurationUtils.getEnv(
            "REMOVE_SUBSCRIPTION_RESPONSE",
            "Subscription has been successfully removed"
    );
}
