package org.hejwo.gobybus.busstopcrawler.integration.deserializers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.hejwo.gobybus.busstopcrawler.integration.StringTrimmingObjectMapper;
import org.hejwo.gobybus.busstopcrawler.integration.dto.LineDTO;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hejwo.gobybus.commons.utils.SerializationUtils.getCollectionType;

public class LineDTODeserializerTest {

    private StringTrimmingObjectMapper mapper = new StringTrimmingObjectMapper();

    @Test
    public void shouldParseLinesData() throws IOException {
        String filename = "busStop-lines.json";
        String filteredContent = loadJsonContentWithoutMainTag(filename);

        List<LineDTO> lines = mapper.readValue(filteredContent, getCollectionType(List.class, LineDTO.class));

        assertThat(lines).hasSize(6);
        assertThat(lines).extracting(LineDTO::getLine)
            .containsExactly("135", "138", "166", "509", "517", "N21");
    }

    private String loadJsonContentWithoutMainTag(String filename) throws IOException {
        InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream(filename);
        String fileContent = IOUtils.toString(resourceAsStream, Charset.defaultCharset());

        return new ObjectMapper().readTree(fileContent).path("result").toString();
    }

}