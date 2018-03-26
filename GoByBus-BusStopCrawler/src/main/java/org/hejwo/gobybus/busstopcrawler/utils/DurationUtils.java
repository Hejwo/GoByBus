package org.hejwo.gobybus.busstopcrawler.utils;

import scala.concurrent.duration.Duration;

public abstract class DurationUtils {

    public static Duration toDuration(String valueToConvert) {
        return Duration.create(valueToConvert);
    }

}
