package org.hejwo.gobybus.busstopcrawler.actors;

import akka.actor.ActorRef;
import akka.testkit.TestActorRef;
import org.hejwo.gobybus.busstopcrawler.AbstractActorTest;
import org.hejwo.gobybus.busstopcrawler.actors.BusStopsDownloadActor.DownloadBusStopsMsg;
import org.hejwo.gobybus.busstopcrawler.configuration.AkkaContextHolder;
import org.hejwo.gobybus.busstopcrawler.configuration.actorprops.BusStopsDownloadProperties;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BusStopsDownloadSupervisorActorTest extends AbstractActorTest {

    @Mock
    private AkkaContextHolder akkaContext;
    @Mock
    private ActorRef busStopDownloadRef;
    @Mock
    private ActorRef supervisorRef;

    private TestActorRef<BusStopsDownloadSupervisorActor> busStopsDownloadSupervisorActorRef;

    @Before
    public void setUp() {
        when(akkaContext.getBusStopsDownloadProperties()).thenReturn(mockBusStopsDownloadProperties());

        busStopsDownloadSupervisorActorRef = createTestActorRef("bus-stops-download-supervisor", BusStopsDownloadSupervisorActor.class,
                akkaContext);
    }

    @Test
    public void onForward_shouldForwardToBusStopsDownload_whenProperMessage() {
        DownloadBusStopsMsg msg = BusStopsDownloadActor.message();
        busStopsDownloadSupervisorActorRef.underlyingActor().onForward(msg);

        verify(akkaContext).getBusStopsDownloadProperties();
        verifyNoMoreInteractions(akkaContext);
    }

    private BusStopsDownloadProperties mockBusStopsDownloadProperties() {
        int downloadEveryNDays = 3;
        return new BusStopsDownloadProperties(busStopDownloadRef, supervisorRef,
                downloadEveryNDays, "1 milliseconds", "1 milliseconds", 1, "2 milliseconds");
    }

    @After
    public void tearDown() {
        stopActor(busStopsDownloadSupervisorActorRef);
    }

}