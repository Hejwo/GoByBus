package org.hejwo.gobybus.busstopcrawler.configuration.actorprops;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import scala.concurrent.duration.Duration;

import static org.hejwo.gobybus.busstopcrawler.utils.DurationUtils.toDuration;

@Component
@Getter
public class RetryProperties {

    private final Duration initialDelay;

    public RetryProperties(@Value("${openData.warsawApi.busStop.crawler.actors.retry.initialDelay}")
                                   String retryDelay) {
        this.initialDelay = toDuration(retryDelay);
    }

}
