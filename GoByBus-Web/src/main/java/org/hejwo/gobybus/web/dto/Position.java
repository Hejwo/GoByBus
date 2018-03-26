package org.hejwo.gobybus.web.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import org.hejwo.gobybus.commons.domain.LocationData;

import java.time.format.DateTimeFormatter;

@Builder
@Getter
public class Position {

    @JsonProperty("line")
    private String line;
    @JsonProperty("brigade")
    private String brigade;
    @JsonProperty("latitude")
    private Double latitude;
    @JsonProperty("longitude")
    private Double longitude;
    @JsonProperty("time")
    private String time;

    public static Position from(LocationData locationData) {
        DateTimeFormatter pattern = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return Position.builder()
                .line(locationData.getFirstLine())
                .brigade(locationData.getBrigade())
                .latitude(locationData.getLatitude())
                .longitude(locationData.getLongitude())
                .time(locationData.getTime().format(pattern))
                .build();
    }
}
