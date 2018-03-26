package locationviewer.repositories;

import com.google.common.collect.Lists;
import locationviewer.configuration.IntegrationTestConfig;
import org.hejwo.gobybus.commons.domain.LocationData;
import org.hejwo.gobybus.locationviewer.LocationViewerApp;
import org.hejwo.gobybus.locationviewer.repositories.LocationDataRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {LocationViewerApp.class, EmbeddedMongoAutoConfiguration.class, IntegrationTestConfig.class})
public class LocationDataRepositoryIntegrationTest {

    @Autowired
    private LocationDataRepository locationDataRepository;

    @Before
    public void setUp() {
        locationDataRepository.deleteAll();
    }

    @Test
    public void save_shouldInsertSingleLocationData() {
        LocationData locationData = createSingeLocation("17");

        locationDataRepository.save(locationData);

        List<LocationData> locations = locationDataRepository.findAll();
        assertThat(locations).hasSize(1);
    }

    @Test
    public void save_shouldInsertMultipleLocationData() {
        LocationData locationData1 = createSingeLocation("17");
        LocationData locationData2 = createSingeLocation("19");

        locationDataRepository.save(Lists.newArrayList(locationData1, locationData2));

        List<LocationData> locations = locationDataRepository.findAll();
        assertThat(locations).hasSize(2);
    }

    private LocationData createSingeLocation(String firstLine) {
        return LocationData.builder()
            .status("RUNNING")
            .firstLine(firstLine)
            .longitude(21.0019817)
            .lines(Lists.newArrayList(firstLine))
            .time(LocalDateTime.of(2016, 12, 28, 12, 28, 51))
            .latitude(52.1898613)
            .lowFloor(true)
            .brigade("4").build();
    }
}