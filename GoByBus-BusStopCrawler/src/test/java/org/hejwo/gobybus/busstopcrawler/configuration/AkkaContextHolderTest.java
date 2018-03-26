package org.hejwo.gobybus.busstopcrawler.configuration;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Scheduler;
import org.hejwo.gobybus.busstopcrawler.configuration.actorprops.BusStopLinesDownloadProperties;
import org.hejwo.gobybus.busstopcrawler.configuration.actorprops.BusStopsDownloadProperties;
import org.hejwo.gobybus.busstopcrawler.configuration.actorprops.RetryProperties;
import org.hejwo.gobybus.busstopcrawler.configuration.actorprops.TimetablesDownloadProperties;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import scala.Some;
import scala.concurrent.ExecutionContextExecutor;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

import static org.hejwo.gobybus.busstopcrawler.utils.DurationUtils.toDuration;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AkkaContextHolderTest {

    @Mock
    private ActorSystem actorSystem;
    @Mock
    private BusStopsDownloadProperties busStopsDownloadProperties;
    @Mock
    private BusStopLinesDownloadProperties busStopLinesDownloadProperties;
    @Mock
    private TimetablesDownloadProperties timetablesDownloadProperties;
    @Mock
    private RetryProperties retryProperties;
    @Mock
    private Scheduler scheduler;
    @Mock
    private ExecutionContextExecutor dispatcher;

    private AkkaContextHolder contextHolder;

    @Before
    public void setUp() {
        when(actorSystem.scheduler()).thenReturn(scheduler);
        when(actorSystem.dispatcher()).thenReturn(dispatcher);

        contextHolder = new AkkaContextHolder(
                actorSystem, busStopsDownloadProperties, busStopLinesDownloadProperties, timetablesDownloadProperties, retryProperties
        );
    }

    @Test
    public void scheduleOnce_shouldSchedule_whenHappyPath() {
        // given
        Duration initialDuration = toDuration("1 milliseconds");

        // when
        String msg = "test msg";
        ActorRef actorRef = mock(ActorRef.class);
        contextHolder.scheduleOnce(initialDuration, actorRef, msg);

        // then
        verify(scheduler).scheduleOnce((FiniteDuration) initialDuration, actorRef, msg, dispatcher, null);

        verify(actorSystem).scheduler();
        verify(actorSystem).dispatcher();

        verifyNoMoreInteractions(actorSystem,
                busStopsDownloadProperties, busStopLinesDownloadProperties, timetablesDownloadProperties, retryProperties,
                scheduler, dispatcher
        );
    }

    @Test
    public void scheduleRetry_shouldSchedule_whenHappyPath() {
        // given
        ActorRef supervisorRef = mock(ActorRef.class);
        Duration retryDuration = toDuration("1 milliseconds");
        when(retryProperties.getInitialDelay()).thenReturn(retryDuration);
        when(busStopsDownloadProperties.getSupervisorRef()).thenReturn(supervisorRef);

        // when
        String msg = "Message causing error";
        Throwable theError = new RuntimeException("End of world");
        contextHolder.scheduleRetry(Some.apply(msg), theError);

        // then
        verify(scheduler).scheduleOnce((FiniteDuration) retryDuration, supervisorRef, msg, dispatcher, null);
        verify(actorSystem).scheduler();
        verify(actorSystem).dispatcher();
        verify(busStopsDownloadProperties).getSupervisorRef();
        verify(retryProperties).getInitialDelay();

        verifyNoMoreInteractions(actorSystem,
                busStopsDownloadProperties, busStopLinesDownloadProperties, timetablesDownloadProperties, retryProperties,
                scheduler, dispatcher
        );
    }

    @Test
    @Ignore
    public void scheduleCyclic_shouldSchedule_whenHappyPath() {
        // given
        Duration initialDuration = toDuration("1 milliseconds");
        Duration intervalDuration = toDuration("2 milliseconds");
        ActorRef actorRef = mock(ActorRef.class);

        // when
        String msg = "test msg";
        contextHolder.scheduleCyclic(initialDuration, intervalDuration, actorRef, msg);

        // then
        verify(scheduler).scheduleOnce((FiniteDuration) intervalDuration, actorRef, msg, dispatcher, null);

        verify(actorSystem).scheduler();
        verify(actorSystem).dispatcher();

        verifyNoMoreInteractions(actorSystem,
                busStopsDownloadProperties, busStopLinesDownloadProperties, timetablesDownloadProperties, retryProperties,
                scheduler, dispatcher
        );
    }
}