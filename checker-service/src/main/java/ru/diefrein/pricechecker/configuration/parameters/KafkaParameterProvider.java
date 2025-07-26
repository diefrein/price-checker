package ru.diefrein.pricechecker.configuration.parameters;

import ru.diefrein.pricechecker.common.util.ConfigurationUtils;

public class KafkaParameterProvider {
    public static final String PRICE_CHANGE_TOPIC = ConfigurationUtils.getEnv(
            "PRICE_CHANGE_TOPIC",
            "product.price.update"
    );
    public static final String BOOTSTRAP_SERVERS_CONFIG = ConfigurationUtils.getEnv(
            "BOOTSTRAP_SERVERS_CONFIG",
            "kafka:9092"
    );
    public static final String KEY_SERIALIZER_CLASS_CONFIG = ConfigurationUtils.getEnv(
            "KEY_SERIALIZER_CLASS_CONFIG",
            "org.apache.kafka.common.serialization.StringSerializer"
    );
    public static final String VALUE_SERIALIZER_CLASS_CONFIG = ConfigurationUtils.getEnv(
            "VALUE_SERIALIZER_CLASS_CONFIG",
            "org.apache.kafka.common.serialization.StringSerializer"
    );
}
