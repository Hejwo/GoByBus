package org.hejwo.gobybus.commons.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.hejwo.gobybus.commons.deserializers.LocalDateDeserializer;
import org.hejwo.gobybus.commons.deserializers.StringToListDeserializer;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class LocationData {

    private final String status;
    private final String firstLine;
    @JsonDeserialize(using = StringToListDeserializer.class)
    private final List<String> lines;
    private final String brigade;
    private final Double longitude;
    private final Double latitude;
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private final LocalDateTime time;
    private final Boolean lowFloor;

    @JsonCreator
    public LocationData(@JsonProperty("status") String status,
                        @JsonProperty("firstLine") String firstLine,
                        @JsonProperty("lines") List<String> lines,
                        @JsonProperty("brigade") String brigade,
                        @JsonProperty("lon") Double longitude,
                        @JsonProperty("lat") Double latitude,
                        @JsonProperty("time") LocalDateTime time,
                        @JsonProperty("lowFloor") Boolean lowFloor) {
        this.status = status;
        this.firstLine = firstLine;
        this.lines = lines;
        this.brigade = brigade;
        this.longitude = longitude;
        this.latitude = latitude;
        this.time = time;
        this.lowFloor = lowFloor;
    }
}