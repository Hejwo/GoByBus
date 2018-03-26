package org.hejwo.gobybus.commons.domain.customlocaltime;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalTime;

@AllArgsConstructor(staticName = "from")
@Getter
public class CustomLocalTime {

    private final LocalTime localTime;
    private final boolean lateNight;

    public static CustomLocalTime from(LocalTime localTime) {
        return new CustomLocalTime(localTime, false);
    }

    public String timeToString() {
        return localTime.toString();
    }

}
