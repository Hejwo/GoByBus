package org.hejwo.gobybus.locationviewer.jobs;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetCommitCallback;
import org.apache.kafka.common.TopicPartition;
import org.hejwo.gobybus.commons.domain.LocationData;
import org.hejwo.gobybus.commons.parsers.LocationDataParser;
import org.hejwo.gobybus.locationviewer.configuration.KafkaConsumerConfig;
import org.hejwo.gobybus.locationviewer.services.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static java.lang.String.format;
import static java.util.Objects.isNull;

@Slf4j
@Component
public class ConsumerJob implements Runnable {

    private final KafkaConsumer<String, String> consumer;
    private final KafkaConsumerConfig consumerConfig;
    private final LocationService locationService;

    @Autowired
    public ConsumerJob(KafkaConsumerConfig consumerConfig,
                       LocationService locationService,
                       KafkaConsumer<String, String> kafkaConsumer) {
        this.consumerConfig = consumerConfig;
        this.consumer = kafkaConsumer;
        this.locationService = locationService;

        consumer.subscribe(Collections.singleton(consumerConfig.getTopicName()));
        resetOffset(consumerConfig, consumer);
    }

    private static void resetOffset(KafkaConsumerConfig consumerConfig, KafkaConsumer<String, String> consumer) {
        if (consumerConfig.getResetOffset()) {
            consumer.poll(consumerConfig.getPollTime());
            Set<TopicPartition> assignment = consumer.assignment();
            consumer.seekToBeginning(assignment);
        }
    }

    @Override
    public void run() {
        ConsumerRecords<String, String> records = consumer.poll(consumerConfig.getPollTime());

        List<LocationData> locations = LocationDataParser.parseFromRecordsWithJson(records);
        locationService.saveLocations(locations);

        log.debug(format("Consumed %s locations.", locations.size()));
        consumer.commitAsync(getCommitCallback(records));
    }

    private OffsetCommitCallback getCommitCallback(ConsumerRecords<String, String> records) {
        return (offsets, exception) -> {
            if (isNull(exception)) {
                log.debug(String.format("Successfully processed %s messages", records.count()));
            } else {
                log.error(String.format("Error while processing records: %s", exception));
            }
        };
    }

    @PreDestroy
    public void onClose() {
        consumer.close();
    }
}
