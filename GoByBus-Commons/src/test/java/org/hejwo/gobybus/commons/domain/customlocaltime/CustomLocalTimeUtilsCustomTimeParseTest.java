package org.hejwo.gobybus.commons.domain.customlocaltime;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hejwo.gobybus.commons.domain.customlocaltime.CustomLocalTimeUtils.toCustomLocalTime;

public class CustomLocalTimeUtilsCustomTimeParseTest {

    @Test
    public void shouldParse_AM_Time() {
        String time = "07:01";
        CustomLocalTime parsedTime = toCustomLocalTime(time);

        assertThat(parsedTime.timeToString()).isEqualTo("07:01");
        assertThat(parsedTime.isLateNight()).isFalse();
    }

    @Test
    public void shouldParse_PM_Time() {
        String time = "17:02";
        CustomLocalTime parsedTime = toCustomLocalTime(time);

        assertThat(parsedTime.timeToString()).isEqualTo("17:02");
        assertThat(parsedTime.isLateNight()).isFalse();
    }

    @Test
    public void shouldParse_late_PM_Time() {
        String time = "23:21";
        CustomLocalTime parsedTime = toCustomLocalTime(time);

        assertThat(parsedTime.timeToString()).isEqualTo("23:21");
        assertThat(parsedTime.isLateNight()).isFalse();
    }

    @Test
    public void shouldParse_customTime() {
        String time = "24:03";
        CustomLocalTime parsedTime = toCustomLocalTime(time);

        assertThat(parsedTime.timeToString()).isEqualTo("00:03");
        assertThat(parsedTime.isLateNight()).isTrue();
    }

    @Test
    public void shouldParse_customTime2() {
        String time = "25:03";
        CustomLocalTime parsedTime = toCustomLocalTime(time);

        assertThat(parsedTime.timeToString()).isEqualTo("01:03");
        assertThat(parsedTime.isLateNight()).isTrue();
    }

}