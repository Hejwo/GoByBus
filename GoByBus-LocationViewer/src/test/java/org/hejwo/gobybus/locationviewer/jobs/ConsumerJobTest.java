package org.hejwo.gobybus.locationviewer.jobs;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.hejwo.gobybus.locationviewer.configuration.KafkaConsumerConfig;
import org.hejwo.gobybus.locationviewer.services.LocationService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class ConsumerJobTest {

    @Mock
    private KafkaConsumerConfig consumerConfig;
    @Mock
    private LocationService locationService;
    @Mock
    private KafkaConsumer<String, String> consumer;

    private ConsumerJob consumerJob;

    @Before
    public void setUp() {
        consumerJob = new ConsumerJob(consumerConfig, locationService, consumer);
    }

    @Test
    @Ignore
    public void run_should_saveEmptyList() {
        // TODO: 07.10.17, phejwowski,
//        when(consumer.poll(any())).thenReturn(mockedLocations());
//
//        verify(locationService, times(1)).saveLocations(eq(Lists.newArrayList()));
    }

    private ConsumerRecords<String, String> mockedLocations() {
        Map<TopicPartition, List<ConsumerRecord<String, String>>> records = Maps.newHashMap();
        records.put(new TopicPartition("testTopic1", 1), Lists.newArrayList());
        return new ConsumerRecords<>(records);
    }
}