package org.hejwo.gobybus.busstopcrawler.integration.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hejwo.gobybus.busstopcrawler.domain.Timetable;
import org.hejwo.gobybus.busstopcrawler.integration.deserializers.TimetableDTODeserializer;
import org.hejwo.gobybus.commons.domain.customlocaltime.CustomLocalTime;

import java.util.Map;

import static org.hejwo.gobybus.commons.domain.customlocaltime.CustomLocalTimeUtils.toCustomLocalTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonDeserialize(using = TimetableDTODeserializer.class)
@Builder
public class TimetableDTO {

    private String brigade;
    private String direction;
    private String route;
    private CustomLocalTime localTime;

    public static TimetableDTO from(Map<String, String> map) {
        String brigade = map.get("brygada");
        String direction = map.get("kierunek");
        String route = map.get("trasa");
        String localTimeStr = map.get("czas");

        return builder()
                .brigade(brigade)
                .direction(direction)
                .route(route)
                .localTime(toCustomLocalTime(localTimeStr)).build();
    }

    public Timetable toTimetable(String line) {
        return Timetable.builder()
                .line(line)
                .brigade(brigade)
                .direction(direction)
                .route(route)
                .localTime(localTime).build();
    }
}
