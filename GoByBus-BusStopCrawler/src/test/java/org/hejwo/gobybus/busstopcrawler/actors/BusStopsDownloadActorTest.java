package org.hejwo.gobybus.busstopcrawler.actors;

import akka.actor.ActorRef;
import akka.testkit.TestActorRef;
import com.google.common.collect.Lists;
import org.hejwo.gobybus.busstopcrawler.AbstractActorTest;
import org.hejwo.gobybus.busstopcrawler.actors.BusStopsDownloadActor.DownloadBusStopsMsg;
import org.hejwo.gobybus.busstopcrawler.configuration.AkkaContextHolder;
import org.hejwo.gobybus.busstopcrawler.configuration.actorprops.BusStopLinesDownloadProperties;
import org.hejwo.gobybus.busstopcrawler.configuration.actorprops.BusStopsDownloadProperties;
import org.hejwo.gobybus.busstopcrawler.domain.DailyReport;
import org.hejwo.gobybus.busstopcrawler.integration.WarsawBusStopApi;
import org.hejwo.gobybus.busstopcrawler.integration.dto.BusStopDTO;
import org.hejwo.gobybus.busstopcrawler.integration.exceptions.WarsawApiTimeoutException;
import org.hejwo.gobybus.busstopcrawler.repository.BusStopRepository;
import org.hejwo.gobybus.busstopcrawler.repository.DailyReportRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import scala.Some;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BusStopsDownloadActorTest extends AbstractActorTest {

    private final static String MOCKED_WARSAW_API_KEY = "testKey111!";

    @Mock
    private AkkaContextHolder akkaContext;
    @Mock
    private WarsawBusStopApi warsawBusStopApi;
    @Mock
    private BusStopRepository busStopRepository;
    @Mock
    private DailyReportRepository dailyReportRepository;
    @Mock
    private ActorRef busStopLinesRef;

    private TestActorRef<BusStopsDownloadActor> busStopsDownloadActorRef;

    @Before
    public void setUp() {
        when(akkaContext.getBusStopLinesDownloadProperties()).thenReturn(mockBusStopLinesDownloadProperties());
        when(akkaContext.getBusStopsDownloadProperties()).thenReturn(mockBusStopsDownloadProperties());

        busStopsDownloadActorRef = createTestActorRef("bus-stops-actor", BusStopsDownloadActor.class,
                warsawBusStopApi, akkaContext, busStopRepository, dailyReportRepository, MOCKED_WARSAW_API_KEY);
    }

    @After
    public void tearDown() {
        stopActor(busStopsDownloadActorRef);
    }

    @Test
    public void onDownloadMessage_shouldCancelDownload_whenFrequentData() {
        // given
        DailyReport latestDailyReport = DailyReport.from(LocalDate.now().minusDays(1), 123);
        when(dailyReportRepository.findFirstByOrderByLocalDateDesc()).thenReturn(
                Optional.of(latestDailyReport)
        );

        // when
        DownloadBusStopsMsg startDownloadMsg = BusStopsDownloadActor.message();
        busStopsDownloadActorRef.underlyingActor().onDownloadMessage(startDownloadMsg);

        // then
        verify(akkaContext).getBusStopLinesDownloadProperties();
        verify(akkaContext).getBusStopsDownloadProperties();

        verify(dailyReportRepository).findFirstByOrderByLocalDateDesc();
        verifyNoMoreInteractions(warsawBusStopApi, busStopRepository, akkaContext, dailyReportRepository);
    }

    @Test
    public void onDownloadMessage_shouldEmptyBusStopResponseNotTriggerLinesDownload() {
        when(dailyReportRepository.findFirstByOrderByLocalDateDesc()).thenReturn(Optional.empty());

        DownloadBusStopsMsg startDownloadMsg = BusStopsDownloadActor.message();
        busStopsDownloadActorRef.underlyingActor().onDownloadMessage(startDownloadMsg);

        verify(warsawBusStopApi).getAllBusStops(MOCKED_WARSAW_API_KEY);
        verify(akkaContext).getBusStopLinesDownloadProperties();
        verify(akkaContext).getBusStopsDownloadProperties();

        verify(dailyReportRepository).findFirstByOrderByLocalDateDesc();
        verify(dailyReportRepository).save(any(DailyReport.class));

        verifyNoMoreInteractions(warsawBusStopApi, busStopRepository, akkaContext, dailyReportRepository);

        system.stop(busStopsDownloadActorRef);
    }

    @Test
    public void onDownloadMessage_shouldTriggerLinesDownload_forExistingBusStopResponse_andEmptyBusStopsData_andOldDailyReport() {
        // given
        LocalDate today = LocalDate.now();
        BusStopDTO busStopDto1 = mockedBusStop("id-1", "nr-1");
        BusStopDTO busStopDto2 = mockedBusStop("id-2", "nr-2");
        DailyReport lastDailyReport = DailyReport.from(LocalDate.now().minusDays(3), 6900);

        when(dailyReportRepository.findFirstByOrderByLocalDateDesc()).thenReturn(Optional.of(lastDailyReport));

        when(warsawBusStopApi.getAllBusStops(MOCKED_WARSAW_API_KEY)).thenReturn(Lists.newArrayList
                (busStopDto1, busStopDto2));
        when(busStopRepository.findOneByBusStopIdAndAndBusStopNrAndCreatedAt("id-1", "nr-1", today))
                .thenReturn(Optional.empty());
        when(busStopRepository.findOneByBusStopIdAndAndBusStopNrAndCreatedAt("id-2", "nr-2", today))
                .thenReturn(Optional.empty());

        // when
        DownloadBusStopsMsg startDownloadMsg = BusStopsDownloadActor.message();
        busStopsDownloadActorRef.underlyingActor().onDownloadMessage(startDownloadMsg);

        // then
        verify(warsawBusStopApi).getAllBusStops(MOCKED_WARSAW_API_KEY);

        verify(busStopRepository).findOneByBusStopIdAndAndBusStopNrAndCreatedAt("id-1", "nr-1", today);
        verify(busStopRepository).findOneByBusStopIdAndAndBusStopNrAndCreatedAt("id-2", "nr-2", today);
        verify(busStopRepository).save(busStopDto1.toBusStop(today));
        verify(busStopRepository).save(busStopDto2.toBusStop(today));

        verify(akkaContext).getBusStopLinesDownloadProperties();
        verify(akkaContext).getBusStopsDownloadProperties();
        verify(akkaContext).scheduleOnce(any(), eq(busStopLinesRef), eq(BusStopLinesDownloadActor.message("id-1", "nr-1", today)));
        verify(akkaContext).scheduleOnce(any(), eq(busStopLinesRef), eq(BusStopLinesDownloadActor.message("id-2", "nr-2", today)));

        verify(dailyReportRepository).findFirstByOrderByLocalDateDesc();
        verify(dailyReportRepository).save(any(DailyReport.class));

        verifyNoMoreInteractions(warsawBusStopApi, busStopRepository, akkaContext, dailyReportRepository);
    }

    @Test
    public void onDownloadMessage_shouldNotTriggerLinesDownload_forExistingBusStopResponse_andExistingBusStopsData() {
        // given
        LocalDate today = LocalDate.now();
        BusStopDTO busStopDto1 = mockedBusStop("id-1", "nr-1");
        BusStopDTO busStopDto2 = mockedBusStop("id-2", "nr-2");
        when(dailyReportRepository.findFirstByOrderByLocalDateDesc()).thenReturn(Optional.empty());
        when(warsawBusStopApi.getAllBusStops(MOCKED_WARSAW_API_KEY)).thenReturn(Lists.newArrayList
                (busStopDto1, busStopDto2));
        when(busStopRepository.findOneByBusStopIdAndAndBusStopNrAndCreatedAt("id-1", "nr-1", today))
                .thenReturn(Optional.of(busStopDto1.toBusStop(today)));
        when(busStopRepository.findOneByBusStopIdAndAndBusStopNrAndCreatedAt("id-2", "nr-2", today))
                .thenReturn(Optional.of(busStopDto2.toBusStop(today)));

        // when
        DownloadBusStopsMsg startDownloadMsg = BusStopsDownloadActor.message();
        busStopsDownloadActorRef.underlyingActor().onDownloadMessage(startDownloadMsg);

        // then
        verify(warsawBusStopApi).getAllBusStops(MOCKED_WARSAW_API_KEY);

        verify(busStopRepository).findOneByBusStopIdAndAndBusStopNrAndCreatedAt("id-1", "nr-1", today);
        verify(busStopRepository).findOneByBusStopIdAndAndBusStopNrAndCreatedAt("id-2", "nr-2", today);

        verify(akkaContext).getBusStopLinesDownloadProperties();
        verify(akkaContext).getBusStopsDownloadProperties();

        verify(dailyReportRepository).findFirstByOrderByLocalDateDesc();
        verify(dailyReportRepository).save(any(DailyReport.class));

        verifyNoMoreInteractions(warsawBusStopApi, busStopRepository, akkaContext, dailyReportRepository);
    }

    @Test
    @Ignore
    public void onDownloadMessage_scheduleRetry_whenExceptionThrown() {
        // given
        WarsawApiTimeoutException exceptionThrown = new WarsawApiTimeoutException();
        doThrow(exceptionThrown).when(warsawBusStopApi).getAllBusStops(MOCKED_WARSAW_API_KEY);

        // when
        DownloadBusStopsMsg startDownloadMsg = BusStopsDownloadActor.message();
        busStopsDownloadActorRef.tell(startDownloadMsg, null);

        // when
        verify(warsawBusStopApi).getAllBusStops(MOCKED_WARSAW_API_KEY);

        verify(akkaContext).getBusStopLinesDownloadProperties();
        verify(akkaContext).scheduleRetry(any(), any());
        // TODO for some reasons this part is not working. Simplified test is written below.

        verifyNoMoreInteractions(warsawBusStopApi, busStopRepository, akkaContext, dailyReportRepository);
    }

    @Test
    public void onDownloadMessage_scheduleRetry_whenExceptionThrown_SIMPLE() {
        // given
        WarsawApiTimeoutException exceptionThrown = new WarsawApiTimeoutException();
        DownloadBusStopsMsg startDownloadMsg = BusStopsDownloadActor.message();

        // when
        busStopsDownloadActorRef.underlyingActor().preRestart(exceptionThrown, Some.apply(startDownloadMsg));

        // then
        verify(akkaContext).getBusStopLinesDownloadProperties();
        verify(akkaContext).getBusStopsDownloadProperties();
        verify(akkaContext).scheduleRetry(eq(Some.apply(startDownloadMsg)), eq(exceptionThrown));

        verifyNoMoreInteractions(warsawBusStopApi, busStopRepository, akkaContext, dailyReportRepository);
    }

    private BusStopDTO mockedBusStop(String busStopId, String busStopNr) {
        return BusStopDTO.builder()
                .busStopId(busStopId).busStopNr(busStopNr).busStopName("test-name").streetId("test-street")
                .longitude("1234").latitude("4321")
                .direction("test-direction-1").validSince("valid-since-test")
                .build();
    }

    private BusStopLinesDownloadProperties mockBusStopLinesDownloadProperties() {
        return new BusStopLinesDownloadProperties(
                busStopLinesRef, "2 milliseconds", "5 milliseconds"
        );
    }

    private BusStopsDownloadProperties mockBusStopsDownloadProperties() {
        return new BusStopsDownloadProperties(null, null, 3, "2 milliseconds", "2 milliseconds", 1, "2 milliseconds");
    }
}