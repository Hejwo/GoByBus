package org.hejwo.gobybus.web.utils;

import org.junit.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class StringToLocalDateConverterTest {

    @Test
    public void getLocalDate() throws Exception {
        String date = "1492293600000";

        LocalDate localDate = StringToLocalDateConverter.timestampToLocalDate(date);

        assertThat(localDate).isEqualTo(LocalDate.of(2017, 4, 16));
    }

    @Test
    public void getLocalDateWhenBoth() {
        LocalDate localDate = LocalDate.now();

        String timestamp = StringToLocalDateConverter.toTimeStampString(localDate);
        LocalDate localDateBack = StringToLocalDateConverter.timestampToLocalDate(timestamp);

        assertThat(localDateBack).isEqualTo(localDate);
    }

}