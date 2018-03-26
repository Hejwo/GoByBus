package org.hejwo.gobybus.commons.parsers;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.hejwo.gobybus.commons.domain.LocationData;
import org.json.JSONException;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class LocationDataParserTest {

    private final static String SAMPLE_JSON = "[{\"status\":\"RUNNING\",\"firstLine\":\"17\",\"longitude\":21.0019817," +
            "\"lines\":[\"17\"," +
            "\"18\"],\"time\":{\"date\":{\"year\":2016,\"month\":12,\"day\":28},\"time\":{\"hour\":12,\"minute\":28,\"second\":51,\"nano\":0}},\"latitude\":52.1898613,\"lowFloor\":true,\"brigade\":\"4\"}]";

    @Test
    public void fromRecordsWithJson() throws Exception {
        ConsumerRecord<String, String> record1 = mockRecord(SAMPLE_JSON);
        ConsumerRecord<String, String> record2 = mockRecord(SAMPLE_JSON);

        Map<TopicPartition, List<ConsumerRecord<String, String>>> recordsMap = createRecordsMap(record1, record2);
        ConsumerRecords<String, String> records = new ConsumerRecords<>(recordsMap);

        List<LocationData> locations = LocationDataParser.parseFromRecordsWithJson(records);

        assertThat(locations).hasSize(2);
        assertThat(locations.get(0).getStatus()).isEqualTo("RUNNING");
        assertThat(locations.get(0).getFirstLine()).isEqualTo("17");
        assertThat(locations.get(0).getLatitude()).isEqualTo(52.1898613);
        assertThat(locations.get(0).getLongitude()).isEqualTo(21.0019817);
        assertThat(locations.get(0).getLines()).isEqualTo(Lists.newArrayList("17", "18"));

        assertThat(locations.get(1).getStatus()).isEqualTo("RUNNING");
        assertThat(locations.get(1).getFirstLine()).isEqualTo("17");
        assertThat(locations.get(1).getLatitude()).isEqualTo(52.1898613);
        assertThat(locations.get(1).getLongitude()).isEqualTo(21.0019817);
        assertThat(locations.get(1).getLines()).isEqualTo(Lists.newArrayList("17", "18"));
    }

    @Test
    public void fromRecordWithJson() throws Exception {
        ConsumerRecord<String, String> record = mockRecord(SAMPLE_JSON);

        Stream<LocationData> locationDataStream = LocationDataParser.fromRecordsWithJson(record);
        LocationData locationData = locationDataStream.findFirst().get();

        assertThat(locationData).isNotNull();
        assertThat(locationData.getStatus()).isEqualTo("RUNNING");
        assertThat(locationData.getFirstLine()).isEqualTo("17");
        assertThat(locationData.getLatitude()).isEqualTo(52.1898613);
        assertThat(locationData.getLongitude()).isEqualTo(21.0019817);
        assertThat(locationData.getLines()).isEqualTo(Lists.newArrayList("17", "18"));
    }

    @Test
    public void toProducerRecord() throws JSONException {
        LocationData locationData = LocationData.builder()
                .status("RUNNING")
                .firstLine("17")
                .longitude(21.0019817)
                .lines(Lists.newArrayList("17", "18"))
                .time(LocalDateTime.of(2016, 12, 28, 12, 28, 51))
                .latitude(52.1898613)
                .lowFloor(true)
                .brigade("4").build();

        ProducerRecord<String, String> record = LocationDataParser.toProducerRecord(Lists.newArrayList(locationData), "topic1");
        JSONAssert.assertEquals(SAMPLE_JSON, record.value(), true);
    }

    @Test
    public void fromJsonStringTest() throws Exception {
        List<LocationData> locationDatas = LocationDataParser.fromJson(SAMPLE_JSON);
        LocationData locationData = locationDatas.get(0);

        assertThat(locationData).isNotNull();
        assertThat(locationData.getStatus()).isEqualTo("RUNNING");
        assertThat(locationData.getFirstLine()).isEqualTo("17");
        assertThat(locationData.getLatitude()).isEqualTo(52.1898613);
        assertThat(locationData.getLongitude()).isEqualTo(21.0019817);
        assertThat(locationData.getLines()).isEqualTo(Lists.newArrayList("17", "18"));
    }

    private ConsumerRecord<String, String> mockRecord(String json) {
        ConsumerRecord<String, String> record = new ConsumerRecord<>("", -1, -1, null, json);
        return record;
    }


    private Map<TopicPartition, List<ConsumerRecord<String, String>>> createRecordsMap(ConsumerRecord<String, String>... records) {
        Map<TopicPartition, List<ConsumerRecord<String, String>>> recordsMap = Maps.newHashMap();
        recordsMap.put(
                new TopicPartition("locations-test", 1), Arrays.asList(records)
        );
        return recordsMap;
    }
}