package org.hejwo.gobybus.busstopcrawler.actors.exceptions;

import java.time.LocalDate;

public class BusStopNotFound extends RuntimeException {

    public BusStopNotFound(String busStopId, String busStopNr, LocalDate date) {
        super(String.format("BusStop '%s-%s' for day '%s' was not found !", busStopId, busStopNr, date));
    }
}
