package org.hejwo.gobybus.busstopcrawler.integration.deserializers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.assertj.core.api.Condition;
import org.hejwo.gobybus.busstopcrawler.integration.StringTrimmingObjectMapper;
import org.hejwo.gobybus.busstopcrawler.integration.dto.TimetableDTO;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hejwo.gobybus.commons.utils.SerializationUtils.getCollectionType;

public class TimetableDTODeserializerTest {

    private StringTrimmingObjectMapper mapper = new StringTrimmingObjectMapper();

    @Test
    public void shouldParseTimetableData() throws IOException {
        String filename = "busStop-single-timetable.json";
        String filteredContent = loadJsonContentWithoutMainTag(filename);

        List<TimetableDTO> timetables = mapper.readValue(filteredContent, getCollectionType(List.class, TimetableDTO.class));

        Condition<String> nonNullValues = new Condition<>(str -> !str.contains("null"), "Does not contain null");

        assertThat(timetables).hasSize(61);
        assertThat(timetables).extracting(TimetableDTO::toString).have(nonNullValues);
    }

    @Test
    public void shouldParse_afterMidnightDate() throws IOException {
        String json = "[{\"values\":[{\"key\":\"symbol_2\",\"value\":\"null\"},{\"key\":\"symbol_1\",\"value\":\"null\"},{\"key\":\"brygada\",\"value\":\"5\"},{\"key\":\"kierunek\",\"value\":\"Dw.Wschodni (Lubelska)\"},{\"key\":\"trasa\",\"value\":\"TP-DWL\"},{\"key\":\"czas\",\"value\":\"25:03:00\"}]}]";

        List<TimetableDTO> timetables = mapper.readValue(json, getCollectionType(List.class, TimetableDTO.class));

        assertThat(timetables).hasSize(1);
        assertThat(timetables.get(0).getDirection()).isEqualToIgnoringCase("Dw.Wschodni (Lubelska)");

    }

    private String loadJsonContentWithoutMainTag(String filename) throws IOException {
        InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream(filename);
        String fileContent = IOUtils.toString(resourceAsStream, Charset.defaultCharset());

        return new ObjectMapper().readTree(fileContent).path("result").toString();
    }

}