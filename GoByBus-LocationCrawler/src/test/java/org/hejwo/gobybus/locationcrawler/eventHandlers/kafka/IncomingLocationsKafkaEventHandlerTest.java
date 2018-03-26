package org.hejwo.gobybus.locationcrawler.eventHandlers.kafka;

import com.google.common.collect.Lists;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.hejwo.gobybus.commons.domain.LocationData;
import org.hejwo.gobybus.locationcrawler.config.KafkaLocationProducerConfig;
import org.hejwo.gobybus.locationcrawler.events.IncomingLocationsEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.List;

import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(MockitoJUnitRunner.class)
public class IncomingLocationsKafkaEventHandlerTest {

    @Mock
    private KafkaProducer<String, String> kafkaProducer;

    private IncomingLocationsKafkaEventHandler locationsKafkaEventHandler;

    @Before
    public void setUp() {
        locationsKafkaEventHandler = new IncomingLocationsKafkaEventHandler(kafkaProducer,
            getMockedKafkaConfig());
    }

    @Test
    public void onApplicationEvent_shouldPass() throws Exception {
        ArgumentCaptor<ProducerRecord> producerRecordCaptor = ArgumentCaptor.forClass(ProducerRecord.class);
        List<LocationData> mockedLocations = createMockedLocations();
        String mockedLocationsJson = "[{\"status\":\"RUNNING\",\"firstLine\":\"17\",\"lines\":[\"17\",\"18\"],\"brigade\":\"4\",\"longitude\":21.0019817,\"latitude\":52.1898613,\"time\":{\"date\":{\"year\":2016,\"month\":12,\"day\":28},\"time\":{\"hour\":12,\"minute\":28,\"second\":51,\"nano\":0}},\"lowFloor\":true}]";

        locationsKafkaEventHandler.onApplicationEvent(IncomingLocationsEvent.create(mockedLocations));

        verify(kafkaProducer).send(producerRecordCaptor.capture(), Mockito.any(Callback.class));
        ProducerRecord<String, String> recordEmitted = producerRecordCaptor.getValue();

        assertThat(recordEmitted.topic()).isEqualTo("test-topic");
        assertThatJson(recordEmitted.value()).isEqualTo(mockedLocationsJson);
    }

    @Test
    public void onApplicationEvent_shouldNotEmmit_whenEmpty() throws Exception {
        List<LocationData> mockedLocations = Lists.newArrayList();

        locationsKafkaEventHandler.onApplicationEvent(IncomingLocationsEvent.create(mockedLocations));

        verifyNoMoreInteractions(kafkaProducer);
    }

    @Test
    public void onApplicationEvent_shouldCloseConnectionWhenBeanDestroyed() throws Exception {
        locationsKafkaEventHandler.close();

        verify(kafkaProducer).close();
    }

    @Test
    public void callback_shouldSilentQuitError() {
        // Well... I guess that checking if we silent quit exception is handy

        Callback callback = locationsKafkaEventHandler.getCallback(Lists.newArrayList());
        callback.onCompletion(null, new RuntimeException("Kafka hard error"));
    }

    @Test
    public void callback_shouldSilentHandle_normalAppend() {
        // Well... I guess that checking if we silently append new records is good
        RecordMetadata recordsMetadata = new RecordMetadata(new TopicPartition("locations", 1), 457, 400);

        Callback callback = locationsKafkaEventHandler.getCallback(Lists.newArrayList());
        callback.onCompletion(recordsMetadata, null);
    }

    private List<LocationData> createMockedLocations() {
        LocationData locationData1 = LocationData.builder()
            .status("RUNNING")
            .firstLine("17")
            .longitude(21.0019817)
            .lines(Lists.newArrayList("17","18"))
            .time(LocalDateTime.of(2016,12,28,12,28,51))
            .latitude(52.1898613)
            .lowFloor(true)
            .brigade("4").build();

        return Lists.newArrayList(locationData1);
    }

    private KafkaLocationProducerConfig getMockedKafkaConfig() {
        return new KafkaLocationProducerConfig(
            "localhost:9092",
            "test-topic",
            "1",
            "0",
            "5000"
        );
    }

}