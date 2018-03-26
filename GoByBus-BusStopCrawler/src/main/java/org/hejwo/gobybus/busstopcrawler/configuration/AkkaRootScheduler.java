package org.hejwo.gobybus.busstopcrawler.configuration;

import akka.actor.ActorRef;
import org.hejwo.gobybus.busstopcrawler.actors.BusStopsDownloadActor;
import org.hejwo.gobybus.busstopcrawler.configuration.actorprops.BusStopsDownloadProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import scala.concurrent.duration.Duration;

@Component
public class AkkaRootScheduler {

    private final AkkaContextHolder akkaContext;

    @Autowired
    public AkkaRootScheduler(AkkaContextHolder akkaContext) {
        this.akkaContext = akkaContext;
    }

    @EventListener(ContextRefreshedEvent.class)
    public void contextRefreshedEvent() {
        scheduleBusStopsDownload();
    }

    private void scheduleBusStopsDownload() {
        BusStopsDownloadProperties busStopsDownloadProperties = akkaContext.getBusStopsDownloadProperties();

        Duration invocationDelay = busStopsDownloadProperties.getInitialDelay();
        Duration interval = busStopsDownloadProperties.getInterval();
        ActorRef busStopsDownloadSupervisorActorRef = busStopsDownloadProperties.getSupervisorRef();

        akkaContext.scheduleCyclic(invocationDelay, interval,
            busStopsDownloadSupervisorActorRef, BusStopsDownloadActor.message());
    }
}
