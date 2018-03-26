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
public class BusStopLinesDownloadProperties {

    private final ActorRef ref;
    private final Duration initialDelay;
    private final Duration delayForNext;

    @Autowired
    public BusStopLinesDownloadProperties(ActorRef busStopLinesDownloadActorRef,
                                          @Value("${openData.warsawApi.busStop.crawler.actors.busStopLinesDownload.initialDelay}")
                                              String busStopsLinesDownloadInitialDelay,
                                          @Value("${openData.warsawApi.busStop.crawler.actors.busStopLinesDownload.delayForNext}")
                                              String busStopsLinesDownloadDelayForNext) {
        this.ref = busStopLinesDownloadActorRef;
        this.initialDelay = toDuration(busStopsLinesDownloadInitialDelay);
        this.delayForNext = toDuration(busStopsLinesDownloadDelayForNext);
    }

}
