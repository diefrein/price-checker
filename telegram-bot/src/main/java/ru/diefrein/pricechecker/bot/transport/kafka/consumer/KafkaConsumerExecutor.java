package ru.diefrein.pricechecker.bot.transport.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.diefrein.pricechecker.bot.configuration.parameters.KafkaParameterProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class KafkaConsumerExecutor {

    private static final Logger log = LoggerFactory.getLogger(KafkaConsumerExecutor.class);

    private final List<PriceChangeConsumer> consumers = new CopyOnWriteArrayList<>();
    private final ExecutorService executor = Executors.newFixedThreadPool(KafkaParameterProvider.CONSUMER_THREAD_COUNT);

    public KafkaConsumerExecutor(String topic,
                                 Properties props,
                                 PriceChangeProcessor priceChangeProcessor,
                                 ObjectMapper objectMapper) {
        for (int i = 0; i < KafkaParameterProvider.CONSUMER_THREAD_COUNT; i++) {
            consumers.add(new PriceChangeConsumer(topic, props, priceChangeProcessor, objectMapper));
        }
    }

    public void start() {
        for (int i = 0; i < KafkaParameterProvider.CONSUMER_THREAD_COUNT; i++) {
            executor.submit(consumers.get(i));
        }
        log.info("Started Kafka polling in {} threads", KafkaParameterProvider.CONSUMER_THREAD_COUNT);
    }

    public void stop() {
        consumers.forEach(consumer -> consumer.setActive(false));
        executor.shutdown();

        try {
            if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }

        log.info("Kafka consumers stopped successfully");
    }
}
