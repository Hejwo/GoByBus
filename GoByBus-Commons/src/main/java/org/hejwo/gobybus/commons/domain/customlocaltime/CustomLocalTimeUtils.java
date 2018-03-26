package org.hejwo.gobybus.commons.domain.customlocaltime;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

import static java.time.temporal.ChronoField.HOUR_OF_DAY;
import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;
import static java.time.temporal.ChronoField.SECOND_OF_MINUTE;

public class CustomLocalTimeUtils {

    private final static DateTimeFormatter TIME_FORMATTER = new DateTimeFormatterBuilder()
        .appendValue(HOUR_OF_DAY, 2)
        .appendLiteral(':')
        .appendValue(MINUTE_OF_HOUR, 2)
        .optionalStart()
        .appendLiteral(':')
        .appendValue(SECOND_OF_MINUTE, 2).toFormatter();

    public static CustomLocalTime toCustomLocalTime(String localTimeStr) {
        if (isAfterMidnightHour(localTimeStr)) {
            String transformedTime = toStandardTime(localTimeStr);
            LocalTime localTime = LocalTime.parse(transformedTime, TIME_FORMATTER);
            return CustomLocalTime.from(localTime, true);
        } else {
            LocalTime localTime = LocalTime.parse(localTimeStr, TIME_FORMATTER);
            return CustomLocalTime.from(localTime);
        }
    }

    private static boolean isAfterMidnightHour(String localTimeStr) {
        return localTimeStr.startsWith("24:") ||
            localTimeStr.startsWith("25:") ||
            localTimeStr.startsWith("26:") ||
            localTimeStr.startsWith("27:") ||
            localTimeStr.startsWith("28:") ||
            localTimeStr.startsWith("29:");
    }

    private static String toStandardTime(String localTimeStr) {
        return localTimeStr
            .replace("24:", "00:")
            .replace("25:", "01:")
            .replace("26:", "02:")
            .replace("27:", "03:")
            .replace("28:", "04:")
            .replace("29:", "05:");
    }

}
