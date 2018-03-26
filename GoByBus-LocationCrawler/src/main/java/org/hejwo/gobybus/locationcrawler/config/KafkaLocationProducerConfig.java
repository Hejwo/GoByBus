package org.hejwo.gobybus.locationcrawler.config;

import lombok.Getter;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Getter
@Component
public class KafkaLocationProducerConfig {

    private final String servers;
    private final Class keySerializer;
    private final Class valueSerializer;

    private final String topicName;
    private final String acks;
    private final String retries;
    private final String timeout;

    @Autowired
    public KafkaLocationProducerConfig(@Value("${kafka.newLocation.bootstrap.servers}") String servers,
                                       @Value("${kafka.newLocation.topic}") String topicName,
                                       @Value("${kafka.newLocation.acks}") String acks,
                                       @Value("${kafka.newLocation.retires}") String retries,
                                       @Value("${kafka.newLocation.timeout}") String timeout) {
        this.servers = servers;
        this.acks = acks;
        this.retries = retries;
        this.keySerializer = StringSerializer.class;
        this.valueSerializer = StringSerializer.class;
        this.topicName = topicName;
        this.timeout = timeout;
    }

    public Properties asProperties() {
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", servers);
        properties.setProperty("key.serializer", keySerializer.getName());
        properties.setProperty("value.serializer", valueSerializer.getName());
        properties.setProperty("topic.name", topicName);
        properties.setProperty("acks", acks); // Matching default
        properties.setProperty("retries", retries);
        properties.setProperty("timeout.ms", timeout);

        return properties;
    }
}
