package org.hejwo.gobybus.busstopcrawler.integration.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hejwo.gobybus.busstopcrawler.integration.deserializers.LineDTODeserializer;

import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonDeserialize(using = LineDTODeserializer.class)
public class LineDTO {

    private String line;

    public static LineDTO from(Map<String, String> map) {
        String line = map.get("linia");
        return new LineDTO(line);
    }
}
