package org.hejwo.gobybus.locationcrawler;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.hejwo.gobybus.commons.domain.LocationData;
import org.hejwo.gobybus.commons.parsers.LocationDataParser;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDateTime;
import java.util.List;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static java.util.Collections.singleton;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = LocationCrawlerApp.class)
public class LocationCrawlerE2EIntegrationTest {

    private static final int SERVER_PORT = 1111;
    @ClassRule
    public static WireMockRule wireMockRule = new WireMockRule(options()
        .port(SERVER_PORT)
        .usingFilesUnderClasspath("src/integrationTests/resources"), false);

    @Autowired
    private LocationCrawlerApp crawlerApp;

    @Autowired
    private ArgumentCaptor<ProducerRecord> kafkaProducerArgumentCaptor;

    @Test
    public void successfulFlow() throws Exception {
        // High sleep is required for BitBucket pipeline passing.
        Thread.sleep(7000);
        // TODO : Kinna nasty, platform dependent solution - investigate better options.

        ProducerRecord producerRecord = kafkaProducerArgumentCaptor.getValue();
        String producedJson = producerRecord.value().toString();

        List<LocationData> producedLocationData = LocationDataParser.fromJson(producedJson);

        assertThat(producedLocationData).hasSize(2);
        assertThat(producedLocationData.get(0).getStatus()).isEqualTo("RUNNING");

        assertThat(producedLocationData.get(0).getFirstLine()).isEqualTo("9");
        assertThat(producedLocationData.get(0).getLines()).hasSameElementsAs(singleton("9"));
        assertThat(producedLocationData.get(0).getBrigade()).isEqualTo("1");

        assertThat(producedLocationData.get(0).getLongitude()).isEqualTo(20.9438667);
        assertThat(producedLocationData.get(0).getLatitude()).isEqualTo(52.1761093);
        assertThat(producedLocationData.get(0).getTime()).isEqualTo(LocalDateTime.of(2017, 9, 4, 12, 40, 59));

        assertThat(producedLocationData.get(0).getLowFloor()).isEqualTo(false);

        assertThat(producedLocationData.get(1).getStatus()).isEqualTo("RUNNING");
        assertThat(producedLocationData.get(1).getFirstLine()).isEqualTo("26");
        assertThat(producedLocationData.get(1).getLines()).hasSameElementsAs(singleton("26"));
    }

}
