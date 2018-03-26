package org.hejwo.gobybus.web.services;

import com.google.common.collect.Lists;
import io.vavr.collection.Set;
import org.hejwo.gobybus.commons.domain.LocationData;
import org.hejwo.gobybus.web.WebApp;
import org.hejwo.gobybus.web.repositories.LocationDataRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {
    WebApp.class,
    EmbeddedMongoAutoConfiguration.class
})
public class LocationDataServiceIntegrationTest {

    @Autowired
    private LocationDataRepository locationDataRepository;

    @Autowired
    private LocationDataService locationDataService;

    @Before
    public void setUp() {
        locationDataRepository.deleteAll();
    }

    @Test
    public void findLinesForDay_shouldReturnOnlyLinesInDateRange() {
        // given
        LocationData location1 = createLocationData("9", "27", LocalDateTime.of(2017, 10, 31, 12, 0), 12d, 12d);
        LocationData location2 = createLocationData("10", "27", LocalDateTime.of(2017, 10, 31, 12, 1), 12d, 12d);
        LocationData location3 = createLocationData("9", "27", LocalDateTime.of(2017, 10, 31, 12, 2), 12d, 12d);
        LocationData location4 = createLocationData("10", "27", LocalDateTime.of(2017, 10, 31, 12, 3), 12d, 12d);

        LocationData location5 = createLocationData("9", "27", LocalDateTime.of(2017, 10, 12, 12, 3), 12d, 12d);
        LocationData location6 = createLocationData("11", "27", LocalDateTime.of(2017, 10, 12, 12, 3), 12d, 12d);
        LocationData location7 = createLocationData("57", "27", LocalDateTime.of(2017, 10, 12, 12, 3), 12d, 12d);

        List<LocationData> locations = Lists.newArrayList(location1, location2, location3, location4, location5, location6, location7);

        locationDataRepository.save(locations);

        // when
        Set<String> linesListForDay = locationDataService.findLinesListForDay(LocalDate.of(2017, 10, 31));

        assertThat(linesListForDay).hasSize(2);
    }

    @Test
    public void findDays_shouldWork() {
        // given
        LocationData location1 = createLocationData("9", "27", LocalDateTime.of(2017, 10, 31, 12, 0), 12d, 12d);
        LocationData location2 = createLocationData("10", "27", LocalDateTime.of(2017, 10, 31, 12, 1), 12d, 12d);
        LocationData location3 = createLocationData("9", "27", LocalDateTime.of(2017, 10, 31, 12, 2), 12d, 12d);
        LocationData location4 = createLocationData("10", "27", LocalDateTime.of(2017, 10, 31, 12, 3), 12d, 12d);

        LocationData location5 = createLocationData("9", "27", LocalDateTime.of(2017, 10, 12, 12, 3), 12d, 12d);
        LocationData location6 = createLocationData("11", "27", LocalDateTime.of(2017, 10, 12, 12, 3), 12d, 12d);
        LocationData location7 = createLocationData("57", "27", LocalDateTime.of(2017, 10, 12, 12, 3), 12d, 12d);

        LocationData location8 = createLocationData("57", "27", LocalDateTime.of(2017, 10, 2, 12, 3), 12d, 12d);

        List<LocationData> locations = Lists.newArrayList(location1, location2, location3, location4,
            location5, location6, location7, location8);
        locationDataRepository.save(locations);

        Set<String> days = locationDataService.findDays();

        assertThat(days).containsExactly("2017-10-02", "2017-10-12", "2017-10-31");
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