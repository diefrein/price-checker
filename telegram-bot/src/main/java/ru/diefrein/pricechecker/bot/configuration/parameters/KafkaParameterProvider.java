package ru.diefrein.pricechecker.bot.configuration.parameters;

import ru.diefrein.pricechecker.bot.util.ConfigurationUtils;

public class KafkaParameterProvider {
    public static final String PRICE_CHANGE_TOPIC = ConfigurationUtils.getEnv(
            "PRICE_CHANGE_TOPIC",
            "product.price.update"
    );
    public static final String BOOTSTRAP_SERVERS_CONFIG = ConfigurationUtils.getEnv(
            "BOOTSTRAP_SERVERS_CONFIG",
            "kafka:9092"
    );
    public static final String KEY_DESERIALIZER_CLASS_CONFIG = ConfigurationUtils.getEnv(
            "KEY_DESERIALIZER_CLASS_CONFIG",
            "org.apache.kafka.common.serialization.StringDeserializer"
    );
    public static final String VALUE_DESERIALIZER_CLASS_CONFIG = ConfigurationUtils.getEnv(
            "VALUE_DESERIALIZER_CLASS_CONFIG",
            "org.apache.kafka.common.serialization.StringDeserializer"
    );
    public static final String GROUP_ID_CONFIG = ConfigurationUtils.getEnv(
            "GROUP_ID_CONFIG",
            "bot-consumer"
    );
    public static final String AUTO_OFFSET_RESET_CONFIG = ConfigurationUtils.getEnv(
            "AUTO_OFFSET_RESET_CONFIG",
            "earliest"
    );
    public static final int CONSUMER_POLL_TIMEOUT_MS = Integer.parseInt(ConfigurationUtils.getEnv(
            "CONSUMER_POLL_TIMEOUT_MS",
            "1000"
    ));
    public static final int CONSUMER_SHUTDOWN_TIMEOUT_MS = Integer.parseInt(ConfigurationUtils.getEnv(
            "CONSUMER_SHUTDOWN_TIMEOUT_MS",
            "30000"
    ));
    public static final int CONSUMER_THREAD_COUNT = Integer.parseInt(ConfigurationUtils.getEnv(
            "CONSUMER_THREAD_COUNT",
            "3"
    ));
}
