package org.hejwo.gobybus.busstopcrawler.configuration;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Cancellable;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.hejwo.gobybus.busstopcrawler.configuration.actorprops.BusStopLinesDownloadProperties;
import org.hejwo.gobybus.busstopcrawler.configuration.actorprops.BusStopsDownloadProperties;
import org.hejwo.gobybus.busstopcrawler.configuration.actorprops.RetryProperties;
import org.hejwo.gobybus.busstopcrawler.configuration.actorprops.TimetablesDownloadProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import scala.Option;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

@Component
@Getter
@Slf4j
public class AkkaContextHolder {

    private final ActorSystem actorSystem;

    private final BusStopsDownloadProperties busStopsDownloadProperties;
    private final BusStopLinesDownloadProperties busStopLinesDownloadProperties;
    private final TimetablesDownloadProperties timetablesDownloadProperties;
    private final RetryProperties retryProperties;

    @Autowired
    public AkkaContextHolder(ActorSystem actorSystem,
                             BusStopsDownloadProperties busStopsDownloadProperties,
                             BusStopLinesDownloadProperties busStopLinesDownloadProperties,
                             TimetablesDownloadProperties timetablesDownloadProperties,
                             RetryProperties retryProperties) {
        this.actorSystem = actorSystem;
        this.busStopsDownloadProperties = busStopsDownloadProperties;
        this.busStopLinesDownloadProperties = busStopLinesDownloadProperties;
        this.timetablesDownloadProperties = timetablesDownloadProperties;
        this.retryProperties = retryProperties;
    }

    public Cancellable scheduleCyclic(Duration initialDelay, Duration interval,
                                      ActorRef actorRef, Object message) {
        return actorSystem.scheduler().schedule(
                (FiniteDuration) initialDelay, (FiniteDuration) interval, actorRef, message,
            actorSystem.dispatcher(), null);
    }

    public Cancellable scheduleOnce(Duration initialDelay,
                                    ActorRef actorRef, Object message) {
        return actorSystem.scheduler().scheduleOnce(
                (FiniteDuration) initialDelay, actorRef, message,
            actorSystem.dispatcher(), null);
    }

    public void scheduleRetry(Option<Object> message, Throwable reason) {
        log.info("Added new retry. Message : '{}', Reason: '{}', ReasonMsg: '{}'", message, reason.getClass().getSimpleName(), reason.getMessage());

        Duration retryDelay = retryProperties.getInitialDelay();
        ActorRef actorRef = busStopsDownloadProperties.getSupervisorRef();
        scheduleOnce(retryDelay, actorRef, message.get());
    }
}
