package ru.diefrein.pricechecker.transport.kafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.diefrein.pricechecker.transport.kafka.dto.PriceChangeEvent;

import java.util.Properties;

public class PriceChangeProducer {

    private static final Logger log = LoggerFactory.getLogger(PriceChangeProducer.class);

    private final KafkaProducer<String, String> producer;
    private final ObjectMapper objectMapper;
    private final String defaultTopic;

    public PriceChangeProducer(ObjectMapper objectMapper, String defaultTopic, Properties props) {
        this.objectMapper = objectMapper;
        this.defaultTopic = defaultTopic;
        this.producer = new KafkaProducer<>(props);
    }

    public void send(PriceChangeEvent event) {
        send(event.userId().toString(), event);
    }

    public void send(String key, PriceChangeEvent event) {
        ProducerRecord<String, String> record;
        try {
            record = new ProducerRecord<>(defaultTopic, key, objectMapper.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        producer.send(record, (metadata, e) -> {
            if (e != null) {
                log.error("Exception occurred while sending message to Kafka", e);
            } else {
                log.info("Sent to topic {}, partition {}, offset {}",
                        metadata.topic(), metadata.partition(), metadata.offset());
            }
        });
        producer.close();
    }
}
