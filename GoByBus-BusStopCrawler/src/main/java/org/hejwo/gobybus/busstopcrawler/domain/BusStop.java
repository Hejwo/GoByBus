package org.hejwo.gobybus.busstopcrawler.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor(staticName = "from")
@Builder
@NoArgsConstructor
@ToString
@Document
@EqualsAndHashCode
public class BusStop {

    @Id
    private String id;

    private String busStopId;
    private String busStopNr;
    private String busStopName;
    private String streetId;
    private String longitude;
    private String latitude;
    private String direction;
    private String validSince;
    private LocalDate createdAt;

    private List<String> lines;
    private List<Timetable> timetables;

    public static BusStop from(String busStopId,
                                   String busStopNr,
                                   String busStopName,
                                   String steetId,
                                   String longitude,
                                   String latitude,
                                   String direction,
                                   String validSince,
                                   LocalDate createdAt,
                                   List<String> lines,
                                   List<Timetable> timetables) {
        return BusStop.from(null,
            busStopId, busStopNr, busStopName, steetId,
            longitude, latitude,
            direction, validSince, createdAt, lines, timetables);
    }
}
