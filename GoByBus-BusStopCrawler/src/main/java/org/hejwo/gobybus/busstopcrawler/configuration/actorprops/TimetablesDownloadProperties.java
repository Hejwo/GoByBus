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
public class TimetablesDownloadProperties {

    private final ActorRef ref;
    private final Duration initialDelay;
    private final Duration delayForNext;

    @Autowired
    public TimetablesDownloadProperties(ActorRef busStopTimetableDownloadActorRef,
                                        @Value("${openData.warsawApi.busStop.crawler.actors.timetableDownload.initialDelay}")
                                            String timetableDownloadInitialDelay,
                                        @Value("${openData.warsawApi.busStop.crawler.actors.timetableDownload.delayForNext}")
                                            String timetableDownloadDelayForNext) {
        this.ref = busStopTimetableDownloadActorRef;
        this.initialDelay = toDuration(timetableDownloadInitialDelay);
        this.delayForNext = toDuration(timetableDownloadDelayForNext);
    }

}
