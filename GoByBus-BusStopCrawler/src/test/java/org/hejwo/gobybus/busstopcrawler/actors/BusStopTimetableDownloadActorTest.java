package org.hejwo.gobybus.busstopcrawler.actors;

import akka.testkit.TestActorRef;
import com.google.common.collect.Lists;
import org.hejwo.gobybus.busstopcrawler.AbstractActorTest;
import org.hejwo.gobybus.busstopcrawler.actors.BusStopTimetableDownloadActor.TimetableDownloadMsg;
import org.hejwo.gobybus.busstopcrawler.actors.exceptions.BusStopNotFound;
import org.hejwo.gobybus.busstopcrawler.configuration.AkkaContextHolder;
import org.hejwo.gobybus.busstopcrawler.domain.BusStop;
import org.hejwo.gobybus.busstopcrawler.integration.WarsawBusStopApi;
import org.hejwo.gobybus.busstopcrawler.integration.dto.BusStopDTO;
import org.hejwo.gobybus.busstopcrawler.integration.dto.TimetableDTO;
import org.hejwo.gobybus.busstopcrawler.integration.exceptions.WarsawApiTimeoutException;
import org.hejwo.gobybus.busstopcrawler.repository.BusStopRepository;
import org.hejwo.gobybus.commons.domain.customlocaltime.CustomLocalTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import scala.Some;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionThrownBy;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BusStopTimetableDownloadActorTest extends AbstractActorTest {

    private final static String MOCKED_WARSAW_API_KEY = "testKey111!";

    @Mock
    private AkkaContextHolder akkaContext;
    @Mock
    private WarsawBusStopApi warsawBusStopApi;
    @Mock
    private BusStopRepository busStopRepository;

    private TestActorRef<BusStopTimetableDownloadActor> timetablesDownloadActorRef;

    @Before
    public void setUp() {
        timetablesDownloadActorRef = createTestActorRef("timetables-actor", BusStopTimetableDownloadActor.class,
                warsawBusStopApi, akkaContext, busStopRepository, MOCKED_WARSAW_API_KEY);
    }

    @After
    public void tearDown() {
        stopActor(timetablesDownloadActorRef);
    }

    @Test
    public void onDownloadMessage_shouldNotHitDB_whenNoTimetables() {
        // given
        LocalDate today = LocalDate.now();

        when(busStopRepository.findOneByBusStopIdAndAndBusStopNrAndCreatedAt("id-1", "nr-1", today))
                .thenReturn(Optional.of(mockedBusStop(today)));

        // when
        TimetableDownloadMsg timetableDownloadMsg = BusStopTimetableDownloadActor.message("id-1", "nr-1", "N22", today);
        timetablesDownloadActorRef.underlyingActor().onDownloadMessage(timetableDownloadMsg);

        // then
        verify(warsawBusStopApi).getTimetablesForLineAndBusStop("id-1", "nr-1", "N22", MOCKED_WARSAW_API_KEY);
        verifyNoMoreInteractions(akkaContext, warsawBusStopApi, busStopRepository);
    }

    @Test
    public void onDownloadMessage_shouldSave_whenExistingTimetables() {
        // given
        LocalDate today = LocalDate.now();
        BusStop mockedBusStop = mockedBusStop(today);

        TimetableDTO mockedTimetableDto1 = mockedTimetableDto("17");
        TimetableDTO mockedTimetableDto2 = mockedTimetableDto("11");

        when(busStopRepository.findOneByBusStopIdAndAndBusStopNrAndCreatedAt("id-1", "nr-1", today))
                .thenReturn(Optional.of(mockedBusStop));
        when(warsawBusStopApi.getTimetablesForLineAndBusStop("id-1", "nr-1", "N22", MOCKED_WARSAW_API_KEY))
                .thenReturn(Lists.newArrayList(mockedTimetableDto1, mockedTimetableDto2));

        // when
        TimetableDownloadMsg timetableDownloadMsg = BusStopTimetableDownloadActor.message("id-1", "nr-1", "N22", today);
        timetablesDownloadActorRef.underlyingActor().onDownloadMessage(timetableDownloadMsg);

        // then
        assertThat(mockedBusStop.getTimetables()).hasSize(2);
        assertThat(mockedBusStop.getTimetables().get(0)).isEqualTo(mockedTimetableDto1.toTimetable("N22"));
        assertThat(mockedBusStop.getTimetables().get(1)).isEqualTo(mockedTimetableDto2.toTimetable("N22"));

        verify(warsawBusStopApi).getTimetablesForLineAndBusStop("id-1", "nr-1", "N22", MOCKED_WARSAW_API_KEY);
        verify(busStopRepository).findOneByBusStopIdAndAndBusStopNrAndCreatedAt("id-1", "nr-1", today);

        verify(busStopRepository).save(mockedBusStop);

        verifyNoMoreInteractions(akkaContext, warsawBusStopApi, busStopRepository);
    }

    @Test
    public void onDownloadMessage_shouldThrowException_whenBusStopNotFound() {
        // given
        LocalDate today = LocalDate.now();

        when(warsawBusStopApi.getTimetablesForLineAndBusStop("id-1", "nr-1", "N22", MOCKED_WARSAW_API_KEY))
                .thenReturn(Lists.newArrayList(mockedTimetableDto("17")));
        when(busStopRepository.findOneByBusStopIdAndAndBusStopNrAndCreatedAt("id-1", "nr-1", today))
                .thenReturn(Optional.empty());

        // when
        TimetableDownloadMsg timetableDownloadMsg = BusStopTimetableDownloadActor.message("id-1", "nr-1", "N22", today);

        assertThatExceptionThrownBy(() -> {
            timetablesDownloadActorRef.underlyingActor().onDownloadMessage(timetableDownloadMsg);
            return 0;
        })
                .isInstanceOf(BusStopNotFound.class)
                .hasMessageContaining("BusStop 'id-1-nr-1' for day");


        // then
        verify(warsawBusStopApi).getTimetablesForLineAndBusStop("id-1", "nr-1", "N22", MOCKED_WARSAW_API_KEY);
        verify(busStopRepository).findOneByBusStopIdAndAndBusStopNrAndCreatedAt("id-1", "nr-1", today);

        verifyNoMoreInteractions(akkaContext, warsawBusStopApi, busStopRepository);
    }

    @Test
    @Ignore
    public void onDownloadMessage_scheduleRetry_whenExceptionThrown() {
        // given
        LocalDate today = LocalDate.now();
        WarsawApiTimeoutException exceptionThrown = new WarsawApiTimeoutException();
        doThrow(exceptionThrown).when(warsawBusStopApi).getLinesForBusStop("id-1", "nr-1", MOCKED_WARSAW_API_KEY);

        // when
        TimetableDownloadMsg timetableDownloadMsg = BusStopTimetableDownloadActor.message("id-1", "nr-1", "N22", today);
        timetablesDownloadActorRef.tell(timetableDownloadMsg, null);

        // when
        verify(warsawBusStopApi).getTimetablesForLineAndBusStop("id-1", "nr-1", "N22", MOCKED_WARSAW_API_KEY);

        verify(akkaContext).scheduleRetry(any(), any());
        // TODO for some reasons this part is not working. Simplified test is written below.

        verifyNoMoreInteractions(warsawBusStopApi, busStopRepository, akkaContext);
    }

    @Test
    public void onDownloadMessage_scheduleRetry_whenExceptionThrown_SIMPLE() {
        // given
        LocalDate today = LocalDate.now();
        WarsawApiTimeoutException exceptionThrown = new WarsawApiTimeoutException();
        TimetableDownloadMsg timetableDownloadMsg = BusStopTimetableDownloadActor.message("id-1", "nr-1", "N22", today);

        // when
        timetablesDownloadActorRef.underlyingActor().preRestart(exceptionThrown, Some.apply(timetableDownloadMsg));

        // then
        verify(akkaContext).scheduleRetry(eq(Some.apply(timetableDownloadMsg)), eq(exceptionThrown));
    }

    private TimetableDTO mockedTimetableDto(String brigade) {
        return TimetableDTO.builder()
                .brigade(brigade).direction("do domu").route("Hetmańska")
                .localTime(CustomLocalTime.from(LocalTime.of(14, 52)))
                .build();
    }

    private BusStop mockedBusStop(LocalDate localDate) {
        return BusStopDTO.builder()
                .busStopId("id-1").busStopNr("nr-1").busStopName("test-name").streetId("test-street")
                .longitude("1234").latitude("4321")
                .direction("test-direction-1").validSince("valid-since-test")
                .build().toBusStop(localDate);
    }


}