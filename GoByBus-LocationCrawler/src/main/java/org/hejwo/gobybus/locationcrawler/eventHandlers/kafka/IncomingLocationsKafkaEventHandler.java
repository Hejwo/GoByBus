package org.hejwo.gobybus.locationcrawler.eventHandlers.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.hejwo.gobybus.commons.domain.LocationData;
import org.hejwo.gobybus.commons.parsers.LocationDataParser;
import org.hejwo.gobybus.locationcrawler.config.KafkaLocationProducerConfig;
import org.hejwo.gobybus.locationcrawler.events.IncomingLocationsEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.List;

import static java.util.Objects.nonNull;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

@Slf4j
@Component
public class IncomingLocationsKafkaEventHandler implements ApplicationListener<IncomingLocationsEvent> {

    private final KafkaProducer<String, String> producer;
    private final KafkaLocationProducerConfig config;

    @Autowired
    public IncomingLocationsKafkaEventHandler(KafkaProducer<String, String> kafkaProducer,
                                              KafkaLocationProducerConfig locationProducerConfig) {
        this.producer = kafkaProducer;
        this.config = locationProducerConfig;
    }

    @Override
    public void onApplicationEvent(IncomingLocationsEvent event) {
        List<LocationData> locations = event.getSource();
        if (isNotEmpty(locations)) {
            log.info(String.format("New locations to append, count: %s", locations.size()));
            log.debug(String.format("Locations list: '%s'", locations));
            ProducerRecord<String, String> record = LocationDataParser.toProducerRecord(locations, config.getTopicName());
            log.debug(String.format("Locations json: '%s'", record.value()));
            producer.send(record, getCallback(locations));
        } else {
            log.info("No new locations");
        }
    }

    protected Callback getCallback(List<LocationData> source) {
        return (metadata, exception) -> {
            if (nonNull(exception)) {
                log.error(String.format("Message not send due to %s, source: `%s`", exception, source));
            } else {
                log.info(String.format("Locations appended to Kafka log, count: %s", source.size()));
            }
        };
    }

    @PreDestroy
    public void close() {
        producer.close();
    }

}
