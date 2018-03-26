package org.hejwo.gobybus.locationcrawler.jobs;

import com.google.common.collect.Lists;
import org.hejwo.gobybus.commons.domain.LocationData;
import org.hejwo.gobybus.locationcrawler.integration.WarsawLocationApi;
import org.hejwo.gobybus.locationcrawler.integration.exceptions.WarsawApiBadMethodOrParamsException;
import org.hejwo.gobybus.locationcrawler.integration.exceptions.WarsawApiCorruptedResponseException;
import org.hejwo.gobybus.locationcrawler.integration.exceptions.WarsawApiGeneralException;
import org.hejwo.gobybus.locationcrawler.integration.exceptions.WarsawApiInnerCauseException;
import org.hejwo.gobybus.locationcrawler.integration.exceptions.WarsawApiServerUnavailableException;
import org.hejwo.gobybus.locationcrawler.integration.exceptions.WarsawApiTimeoutException;
import org.hejwo.gobybus.locationcrawler.integration.exceptions.WarsawApiUnauthorizedException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationEventPublisher;

import java.net.SocketTimeoutException;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LocationDataImportJobTest {

    private static final String TEST_API_KEY = "test-api-key";

    @Mock
    private WarsawLocationApi warsawLocationApi;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    private LocationDataImportJob locationDataImportJob;

    @Before
    public void setUp() {
        locationDataImportJob = new LocationDataImportJob(TEST_API_KEY, warsawLocationApi, eventPublisher);
    }

    @Test
    public void importLines_shouldSend_whenEmptyLines() {
        when(warsawLocationApi.getAllLines(TEST_API_KEY)).thenReturn(Lists.newArrayList());

        locationDataImportJob.run();

        verify(eventPublisher, times(1)).publishEvent(any());
    }

    @Test
    public void importLines_shouldSend_whenLocations() {
        when(warsawLocationApi.getAllLines(TEST_API_KEY)).thenReturn(createMockedLocations());

        locationDataImportJob.run();

        verify(eventPublisher, times(1)).publishEvent(any());
    }

    @Test
    public void shouldHandleSilent_WarsawApiServerUnavailableException_importLines() {
        doThrow(new WarsawApiServerUnavailableException())
            .when(warsawLocationApi).getAllLines(TEST_API_KEY);

        locationDataImportJob.run();
    }

    @Test
    public void shouldHandleSilent_WarsawApiUnauthorizedException_importLines() {
        doThrow(new WarsawApiUnauthorizedException())
            .when(warsawLocationApi).getAllLines(TEST_API_KEY);

        locationDataImportJob.run();
    }

    @Test
    public void shouldHandleSilent_WarsawApiBadMethodOrParamsException_importLines() {
        doThrow(new WarsawApiBadMethodOrParamsException())
            .when(warsawLocationApi).getAllLines(TEST_API_KEY);

        locationDataImportJob.run();
    }

    @Test
    public void shouldHandleSilent_WarsawApiCorruptedResponse_importLines() {
        doThrow(new WarsawApiCorruptedResponseException())
            .when(warsawLocationApi).getAllLines(TEST_API_KEY);

        locationDataImportJob.run();
    }

    @Test
    public void shouldHandleSilent_WarsawApiTimeoutException_importLines() {
        doThrow(new WarsawApiTimeoutException())
            .when(warsawLocationApi).getAllLines(TEST_API_KEY);

        locationDataImportJob.run();
    }

    @Test
    public void shouldHandleSilent_WarsawApiGeneralException_importLines() {
        doThrow(new WarsawApiGeneralException("invokedMethod", 500, "message"))
            .when(warsawLocationApi).getAllLines(TEST_API_KEY);

        locationDataImportJob.run();
    }

    @Test
    public void shouldHandleSilent_WarsawApiInnerCauseException_importLines() {
        doThrow(new WarsawApiInnerCauseException(new SocketTimeoutException("timeout !")))
            .when(warsawLocationApi).getAllLines(TEST_API_KEY);

        locationDataImportJob.run();
    }

    private List<LocationData> createMockedLocations() {
        LocationData locationData1 = LocationData.builder()
            .status("RUNNING")
            .firstLine("19")
            .longitude(21.0019817)
            .lines(Lists.newArrayList("19", "18"))
            .time(LocalDateTime.of(2017, 12, 28, 12, 28, 51))
            .latitude(52.1898613)
            .lowFloor(true)
            .brigade("4").build();

        return Lists.newArrayList(locationData1);
    }

}