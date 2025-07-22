package ru.diefrein.pricechecker.bot.transport.kafka.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.diefrein.pricechecker.bot.configuration.parameters.KafkaParameterProvider;
import ru.diefrein.pricechecker.bot.transport.kafka.dto.PriceChangeEvent;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class PriceChangeConsumer implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(PriceChangeConsumer.class);

    private final KafkaConsumer<String, String> consumer;
    private final PriceChangeProcessor priceChangeProcessor;
    private final ObjectMapper objectMapper;
    private volatile boolean isActive = true;

    public PriceChangeConsumer(String topic,
                               Properties props,
                               PriceChangeProcessor priceChangeProcessor,
                               ObjectMapper objectMapper) {
        this.consumer = new KafkaConsumer<>(props);
        this.consumer.subscribe(Collections.singletonList(topic));
        this.priceChangeProcessor = priceChangeProcessor;
        this.objectMapper = objectMapper;
    }

    @Override
    public void run() {
        try {
            consume();
        } catch (Exception e) {
            log.error("Exception occurred while receiving message from Kafka", e);
        } finally {
            consumer.close();
        }
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    private void consume() throws JsonProcessingException {
        while (isActive) {
            ConsumerRecords<String, String> records =
                    consumer.poll(Duration.ofMillis(KafkaParameterProvider.CONSUMER_POLL_TIMEOUT_MS));
            for (ConsumerRecord<String, String> record : records) {
                log.info("Received message key={}, value={}, offset={}", record.key(), record.value(), record.offset());

                PriceChangeEvent event = objectMapper.readValue(record.value(), PriceChangeEvent.class);
                priceChangeProcessor.process(event);
            }
        }
    }
}
