package org.hejwo.gobybus.busstopcrawler.configuration.actorprops;

import akka.actor.ActorRef;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import scala.concurrent.duration.Duration;

import static org.hejwo.gobybus.busstopcrawler.utils.DurationUtils.toDuration;

@Component
@Getter
public class BusStopsDownloadProperties {

    private final ActorRef ref;
    private final ActorRef supervisorRef;

    private final Integer downloadEveryNDays;
    private final Duration initialDelay;
    private final Duration interval;
    private final Integer maxRetries;
    private final Duration retriesWindow;

    @Autowired
    public BusStopsDownloadProperties(ActorRef busStopsDownloadActorRef,
                                      ActorRef busStopsDownloadSupervisorActorRef,
                                      @Value("${openData.warsawApi.busStop.crawler.actors.busStopsDownload.downloadEveryNDays}")
                                              Integer downloadEveryNDays,
                                      @Value("${openData.warsawApi.busStop.crawler.actors.busStopsDownload.initialDelay}")
                                              String busStopsDownloadInitialDelay,
                                      @Value("${openData.warsawApi.busStop.crawler.actors.busStopsDownload.interval}")
                                              String busStopsDownloadInterval,
                                      @Value("${openData.warsawApi.busStop.crawler.actors.busStopsDownload.maxRetries}")
                                              Integer busStopsDownloadMaxRetries,
                                      @Value("${openData.warsawApi.busStop.crawler.actors.busStopsDownload.retriesWindow}")
                                              String busStopsDownloadRetriesWindow) {
        this.ref = busStopsDownloadActorRef;
        this.supervisorRef = busStopsDownloadSupervisorActorRef;

        this.downloadEveryNDays = downloadEveryNDays;
        this.initialDelay = toDuration(busStopsDownloadInitialDelay);
        this.interval = toDuration(busStopsDownloadInterval);
        this.maxRetries = busStopsDownloadMaxRetries;
        this.retriesWindow = toDuration(busStopsDownloadRetriesWindow);
    }

}
