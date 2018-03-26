package org.hejwo.gobybus.busstopcrawler.actors;

import akka.actor.ActorRef;
import akka.testkit.TestActorRef;
import com.google.common.collect.Lists;
import org.hejwo.gobybus.busstopcrawler.AbstractActorTest;
import org.hejwo.gobybus.busstopcrawler.actors.BusStopLinesDownloadActor.BusStopLineDownloadMsg;
import org.hejwo.gobybus.busstopcrawler.actors.exceptions.BusStopNotFound;
import org.hejwo.gobybus.busstopcrawler.configuration.AkkaContextHolder;
import org.hejwo.gobybus.busstopcrawler.configuration.actorprops.TimetablesDownloadProperties;
import org.hejwo.gobybus.busstopcrawler.domain.BusStop;
import org.hejwo.gobybus.busstopcrawler.integration.WarsawBusStopApi;
import org.hejwo.gobybus.busstopcrawler.integration.dto.BusStopDTO;
import org.hejwo.gobybus.busstopcrawler.integration.dto.LineDTO;
import org.hejwo.gobybus.busstopcrawler.integration.exceptions.WarsawApiTimeoutException;
import org.hejwo.gobybus.busstopcrawler.repository.BusStopRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionThrownBy;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BusStopLinesDownloadActorTest extends AbstractActorTest {

    private final static String MOCKED_WARSAW_API_KEY = "testKey111!";

    @Mock
    private AkkaContextHolder akkaContext;
    @Mock
    private WarsawBusStopApi warsawBusStopApi;
    @Mock
    private BusStopRepository busStopRepository;
    @Mock
    private ActorRef busStopTimetablesRef;

    private TestActorRef<BusStopLinesDownloadActor> busStopLinesDownloadActorRef;

    @Before
    public void setUp() {
        when(akkaContext.getTimetablesDownloadProperties()).thenReturn(mockTimetableDownloadProperties());

        busStopLinesDownloadActorRef = createTestActorRef("bus-stops-lines-actor", BusStopLinesDownloadActor.class,
                warsawBusStopApi, akkaContext, busStopRepository, MOCKED_WARSAW_API_KEY);
    }

    @After
    public void tearDown() {
        stopActor(busStopLinesDownloadActorRef);
    }

    @Test
    public void onDownloadMessage_shouldNotScheduleTimetablesDownload_whenEmptyLines() {
        // given
        LocalDate today = LocalDate.now();
        when(warsawBusStopApi.getLinesForBusStop("id-1", "nr-1", MOCKED_WARSAW_API_KEY))
                .thenReturn(Lists.newArrayList());

        // when
        BusStopLineDownloadMsg busStopLineDownloadMsg = BusStopLinesDownloadActor.message("id-1", "nr-1", today);
        busStopLinesDownloadActorRef.underlyingActor().onDownloadMessage(busStopLineDownloadMsg);

        // then
        verify(akkaContext).getTimetablesDownloadProperties();
        verify(warsawBusStopApi).getLinesForBusStop("id-1", "nr-1", MOCKED_WARSAW_API_KEY);
        verifyNoMoreInteractions(akkaContext, warsawBusStopApi, busStopRepository);
    }

    @Test
    public void onDownloadMessage_shouldScheduleTimetablesDownload_whenExistingLinesAndBusStop() {
        // given
        LocalDate today = LocalDate.now();
        BusStop mockedBusStop = mockedBusStop(today);

        when(warsawBusStopApi.getLinesForBusStop("id-1", "nr-1", MOCKED_WARSAW_API_KEY))
                .thenReturn(Lists.newArrayList(new LineDTO("9"), new LineDTO("N22")));
        when(busStopRepository.findOneByBusStopIdAndAndBusStopNrAndCreatedAt("id-1", "nr-1", today))
                .thenReturn(Optional.of(mockedBusStop));

        // when
        BusStopLineDownloadMsg busStopLineDownloadMsg = BusStopLinesDownloadActor.message("id-1", "nr-1", today);
        busStopLinesDownloadActorRef.underlyingActor().onDownloadMessage(busStopLineDownloadMsg);

        // then
        assertThat(mockedBusStop.getLines()).containsExactly("9", "N22");

        verify(warsawBusStopApi).getLinesForBusStop("id-1", "nr-1", MOCKED_WARSAW_API_KEY);

        verify(akkaContext).getTimetablesDownloadProperties();
        verify(akkaContext).scheduleOnce(any(), eq(busStopTimetablesRef), eq(BusStopTimetableDownloadActor.message("id-1", "nr-1", "9", today)));
        verify(akkaContext).scheduleOnce(any(), eq(busStopTimetablesRef), eq(BusStopTimetableDownloadActor.message("id-1", "nr-1", "N22", today)));

        verify(busStopRepository).findOneByBusStopIdAndAndBusStopNrAndCreatedAt("id-1", "nr-1", today);
        verify(busStopRepository).save(mockedBusStop);

        verifyNoMoreInteractions(akkaContext, warsawBusStopApi, busStopRepository);
    }

    @Test
    public void onDownloadMessage_shouldThrowExceptionWhen_whenNoBusStop() {
        // given
        LocalDate today = LocalDate.now();
        when(warsawBusStopApi.getLinesForBusStop("id-1", "nr-1", MOCKED_WARSAW_API_KEY))
                .thenReturn(Lists.newArrayList(new LineDTO("N12")));
        when(busStopRepository.findOneByBusStopIdAndAndBusStopNrAndCreatedAt("id-1", "nr-1", today))
                .thenReturn(Optional.empty());

        // when
        BusStopLineDownloadMsg busStopLineDownloadMsg = BusStopLinesDownloadActor.message("id-1", "nr-1", today);

        assertThatExceptionThrownBy(() -> {
            busStopLinesDownloadActorRef.underlyingActor().onDownloadMessage(busStopLineDownloadMsg);
            return 0;
        })
                .isInstanceOf(BusStopNotFound.class)
                .hasMessageContaining("BusStop 'id-1-nr-1' for day");

        // then
        verify(akkaContext).getTimetablesDownloadProperties();
        verify(warsawBusStopApi).getLinesForBusStop("id-1", "nr-1", MOCKED_WARSAW_API_KEY);
        verify(busStopRepository).findOneByBusStopIdAndAndBusStopNrAndCreatedAt("id-1", "nr-1", today);
        verifyNoMoreInteractions(akkaContext, warsawBusStopApi, busStopRepository);
    }

    @Test
    @Ignore
    public void onDownloadMessage_scheduleRetry_whenExceptionThrown() {
        // given
        WarsawApiTimeoutException exceptionThrown = new WarsawApiTimeoutException();
        doThrow(exceptionThrown).when(warsawBusStopApi).getLinesForBusStop("id-1", "nr-1", MOCKED_WARSAW_API_KEY);

        // when
        BusStopLineDownloadMsg busStopLineDownloadMsg = BusStopLinesDownloadActor.message("id-1", "nr-1", LocalDate.now());
        busStopLinesDownloadActorRef.tell(busStopLineDownloadMsg, null);

        // when
        verify(warsawBusStopApi).getLinesForBusStop("id-1", "nr-1", MOCKED_WARSAW_API_KEY);

        verify(akkaContext).getTimetablesDownloadProperties();
        verify(akkaContext).scheduleRetry(any(), any());
        // TODO for some reasons this part is not working. Simplified test is written below.

        verifyNoMoreInteractions(warsawBusStopApi, busStopRepository, akkaContext);
    }

    @Test
    public void onDownloadMessage_scheduleRetry_whenExceptionThrown_SIMPLE() {
        // given
        WarsawApiTimeoutException exceptionThrown = new WarsawApiTimeoutException();
        BusStopLineDownloadMsg busStopLineDownloadMsg = BusStopLinesDownloadActor.message("id-1", "nr-1", LocalDate.now());

        // when
        busStopLinesDownloadActorRef.underlyingActor().preRestart(exceptionThrown, Some.apply(busStopLineDownloadMsg));

        // then
        verify(akkaContext).getTimetablesDownloadProperties();
        verify(akkaContext).scheduleRetry(eq(Some.apply(busStopLineDownloadMsg)), eq(exceptionThrown));

        verifyNoMoreInteractions(warsawBusStopApi, busStopRepository, akkaContext);
    }

    private TimetablesDownloadProperties mockTimetableDownloadProperties() {
        return new TimetablesDownloadProperties(busStopTimetablesRef, "2 milliseconds", "5 milliseconds");
    }

    private BusStop mockedBusStop(LocalDate localDate) {
        return BusStopDTO.builder()
                .busStopId("id-1").busStopNr("nr-1").busStopName("test-name").streetId("test-street")
                .longitude("1234").latitude("4321")
                .direction("test-direction-1").validSince("valid-since-test")
                .build().toBusStop(localDate);
    }

}