package org.hejwo.gobybus.web.repositories;

import com.google.common.collect.Lists;
import io.vavr.collection.List;
import io.vavr.collection.Seq;
import org.hejwo.gobybus.commons.domain.LocationData;
import org.hejwo.gobybus.web.WebApp;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {
    WebApp.class,
    EmbeddedMongoAutoConfiguration.class
})
public class LocationDataRepositoryIntegrationTest {

    @Autowired
    private LocationDataRepository locationDataRepository;

    @Before
    public void setUp() {
        locationDataRepository.deleteAll();
    }

    @Test
    public void shouldSaveLocationData() {
        LocationData locationData1 = createLocationData("7", "14", LocalDateTime.now(), 1d, 1d);
        LocationData locationData2 = createLocationData("7", "14", LocalDateTime.now().plusSeconds(10), 1d, 2d);

        locationDataRepository.save(Lists.newArrayList(locationData1, locationData2));

        java.util.List<LocationData> allLocations = locationDataRepository.findAll();

        assertThat(allLocations).hasSize(2);
    }

    @Test
    public void shouldGetJavaSlangSequence() {
        createTwoItems("9", "14");

        Seq<LocationData> positions = locationDataRepository.findByFirstLine("9");

        assertThat(positions).hasSize(2);
    }

    @Test
    public void shouldFindByFirstLineAndTimeBetween() {
        createTwoItems("9", "14");

        List<LocationData> byFirstLineAndTimeBetween = locationDataRepository.findByFirstLineAndTimeBetween("9", LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1));

        assertThat(byFirstLineAndTimeBetween).hasSize(2);
    }

    private void createTwoItems(String line, String brigade) {
        LocationData locationData1 = createLocationData(line, brigade, LocalDateTime.now(), 1d, 1d);
        LocationData locationData2 = createLocationData(line, brigade, LocalDateTime.now().plusSeconds(10), 1d, 2d);

        locationDataRepository.save(Lists.newArrayList(locationData1, locationData2));
    }

    private LocationData createLocationData(String line, String brigade, LocalDateTime dateTime,
                                            Double longitude, Double latitude) {
        return LocationData.builder()
            .status("RUNNING")
            .brigade(brigade)
            .firstLine(line)
            .lines(Collections.singletonList(line))
            .lowFloor(true)
            .time(dateTime)
            .longitude(longitude)
            .latitude(latitude)
            .build();
    }

}