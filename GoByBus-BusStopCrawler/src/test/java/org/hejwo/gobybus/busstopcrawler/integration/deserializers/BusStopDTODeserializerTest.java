package org.hejwo.gobybus.busstopcrawler.integration.deserializers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.hejwo.gobybus.busstopcrawler.integration.StringTrimmingObjectMapper;
import org.hejwo.gobybus.busstopcrawler.integration.dto.BusStopDTO;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hejwo.gobybus.commons.utils.SerializationUtils.getCollectionType;

public class BusStopDTODeserializerTest {

    private StringTrimmingObjectMapper mapper = new StringTrimmingObjectMapper();

    @Test
    public void shouldParseBusStopData() throws IOException {
        String filename = "busStop-all.json";
        String filteredContent = loadJsonContentWithoutMainTag(filename);

        List<BusStopDTO> busStops = mapper.readValue(filteredContent, getCollectionType(List.class, BusStopDTO.class));

        assertThat(busStops).hasSize(3);
        assertThat(busStops).extracting(BusStopDTO::getBusStopId).containsExactly("1001", "1001", "1001");
        assertThat(busStops).extracting(BusStopDTO::getBusStopNr).containsExactly("01", "02", "03");
        assertThat(busStops).extracting(BusStopDTO::getBusStopName).containsExactly("Kijowska", "Kijowska", "Kijowska");
    }

    private String loadJsonContentWithoutMainTag(String filename) throws IOException {
        InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream(filename);
        String fileContent = IOUtils.toString(resourceAsStream, Charset.defaultCharset());

        String filteredContent = new ObjectMapper().readTree(fileContent).path("result").toString();
        return filteredContent;
    }

}