package org.hejwo.gobybus.busstopcrawler.integration.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.hejwo.gobybus.busstopcrawler.domain.BusStop;
import org.hejwo.gobybus.busstopcrawler.integration.deserializers.BusStopDTODeserializer;

import java.time.LocalDate;
import java.util.Map;

import static java.util.Collections.emptyList;

@Getter
@AllArgsConstructor
@Builder
@JsonDeserialize(using = BusStopDTODeserializer.class)
@ToString
public class BusStopDTO {

    private String busStopId;
    private String busStopNr;
    private String busStopName;
    private String streetId;
    private String longitude;
    private String latitude;
    private String direction;
    private String validSince;

    public BusStop toBusStop(LocalDate currentDate) {
        return BusStop.builder()
            .busStopId(busStopId)
            .busStopNr(busStopNr)
            .busStopName(busStopName)
            .streetId(streetId)
            .longitude(longitude)
            .latitude(latitude)
            .direction(direction)
            .validSince(validSince)
            .lines(emptyList())
            .timetables(emptyList())
            .createdAt(currentDate).build();
    }

    public static BusStopDTO from(Map<String, String> map) {
        String busStopId = map.get("zespol");
        String busStopNr = map.get("slupek");
        String busStopName = map.get("nazwa_zespolu");
        String streetId = map.get("id_ulicy");
        String longitude = map.get("dlug_geo");
        String latitude = map.get("szer_geo");
        String direction = map.get("kierunek");
        String validSince = map.get("obowiazuje_od");

        return builder()
            .busStopId(busStopId)
            .busStopNr(busStopNr)
            .busStopName(busStopName)
            .streetId(streetId)
            .longitude(longitude)
            .latitude(latitude)
            .direction(direction)
            .validSince(validSince)
            .build();
    }
}
