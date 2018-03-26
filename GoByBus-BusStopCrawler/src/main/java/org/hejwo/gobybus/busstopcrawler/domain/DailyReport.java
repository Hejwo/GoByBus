package org.hejwo.gobybus.busstopcrawler.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;

@AllArgsConstructor(staticName = "from")
@Getter
public class DailyReport {

    @Id
    private String id;

    private LocalDate localDate;
    private Integer downloadedBusStops;

    public static DailyReport from(LocalDate today, int savedBusStops) {
        return new DailyReport(null, today, savedBusStops);
    }
}
