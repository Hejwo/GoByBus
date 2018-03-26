package org.hejwo.gobybus.locationviewer.configuration;

import lombok.Getter;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Getter
@Component
@Configuration
public class KafkaConsumerConfig {

    private final String servers;
    private final Class keySerializer;
    private final Class valueSerializer;

    private final String topicName;
    private final String groupId;
    private final Boolean offsetAutoCommit;
    private final Integer pollTime;
    private final Boolean resetOffset;

    @Autowired
    public KafkaConsumerConfig(@Value("${kafka.newLocation.bootstrap.servers}") String servers,
                               @Value("${kafka.newLocation.topic}") String topicName,
                               @Value("${kafka.newLocation.groupId}") String groupId,
                               @Value("${kafka.newLocation.poll.time.ms}") Integer pollTime,
                               @Value("${kafka.newLocation.offset.auto.commit}") Boolean offsetAutoCommit,
                               @Value("${kafka.newLocation.reset.offset}") Boolean resetOffset) {
        this.servers = servers;
        this.keySerializer = StringDeserializer.class;
        this.valueSerializer = StringDeserializer.class;
        this.topicName = topicName;
        this.groupId = groupId;
        this.pollTime = pollTime;
        this.offsetAutoCommit = offsetAutoCommit;
        this.resetOffset = resetOffset;
    }

    public Properties asProperties() {
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", servers);
        properties.setProperty("key.deserializer", keySerializer.getName());
        properties.setProperty("value.deserializer", valueSerializer.getName());
        properties.setProperty("topic.name", topicName);
        properties.setProperty("group.id", groupId);
        properties.setProperty("enable.auto.commit", offsetAutoCommit.toString());
        properties.setProperty("poll.time", pollTime.toString());

        return properties;
    }

    @Bean
    @Profile(value = "native")
    public KafkaConsumer<String, String> consumer(KafkaConsumerConfig consumerConfig) {
        return new KafkaConsumer<>(consumerConfig.asProperties());
    }
}
