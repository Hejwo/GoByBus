package org.hejwo.gobybus.commons.parsers;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.hejwo.gobybus.commons.domain.LocationData;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public abstract class LocationDataParser {

    public static List<LocationData> parseFromRecordsWithJson(ConsumerRecords<String, String> records) {
        return StreamSupport.stream(records.spliterator(), false)
                .flatMap(LocationDataParser::fromRecordsWithJson)
                .collect(Collectors.toList());
    }

    public static Stream<LocationData> fromRecordsWithJson(ConsumerRecord<String, String> record) {
        return fromJson(record.value()).stream();
    }

    public static List<LocationData> fromJson(String json) {
        return new Gson().fromJson(json, new TypeToken<List<LocationData>>() {}.getType());
    }

    public static ProducerRecord<String, String> toProducerRecord(List<LocationData> locationData, String topicName) {
        String jsonLocations = toJson(locationData);
        return new ProducerRecord<>(topicName, jsonLocations);
    }

    private static String toJson(List<LocationData> locationData) {
        return new Gson().toJson(locationData);
    }

}
