package org.hejwo.gobybus.web.utils;

import org.hejwo.gobybus.commons.Constraints;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

public class StringToLocalDateConverter {

    public static LocalDate timestampToLocalDate(String date) {
        Objects.requireNonNull(date, "date can't be null");

        return Instant.ofEpochMilli(Long.parseLong(date))
            .atZone(Constraints.TIME_ZONE)
            .toLocalDate();
    }

    public static String toTimeStampString(LocalDate localDate) {

        long timestamp = localDate.atStartOfDay(Constraints.TIME_ZONE).toInstant().toEpochMilli();
        return String.valueOf(timestamp);
    }

}
