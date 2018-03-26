package org.hejwo.gobybus.locationviewer.services;

import com.google.common.collect.Lists;
import org.hejwo.gobybus.commons.domain.LocationData;
import org.hejwo.gobybus.locationviewer.repositories.LocationDataRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDateTime;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(MockitoJUnitRunner.class)
public class LocationServiceTest {

    @Mock
    private LocationDataRepository locationDataRepository;

    private LocationService locationService;

    @Before
    public void setUp() {
        locationService = new LocationServiceImpl(locationDataRepository);
    }

    @Test
    public void saveLocations_shouldPassForEmptyList() {
        locationService.saveLocations(Lists.newArrayList());

        verifyNoMoreInteractions(locationDataRepository);
    }

    @Test
    public void saveLocations_shouldPassForFilledList() {
        LocationData location1 = createSingeLocation("12");
        LocationData location2 = createSingeLocation("14");

        locationService.saveLocations(Lists.newArrayList(location1, location2));

        verify(locationDataRepository, times(2)).insert(any(LocationData.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void saveLocations_shouldHandleNull() {
        locationService.saveLocations(null);
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