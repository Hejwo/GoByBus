package org.hejwo.gobybus.locationcrawler.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class StringTrimmingObjectMapperTest {

    private ObjectMapper mapper = new StringTrimmingObjectMapper();

    @Test
    public void shouldTrim_simpleString() throws Exception {
        String json = "\"  string to trim    \"";

        String value = mapper.readValue(json, String.class);

        assertThat(value).isEqualTo("string to trim");
    }

    @Test
    public void shouldTrim_complexObject() throws Exception {
        String json = "{ \"field\": \"  string to trim    \" }";

        Example example = mapper.readValue(json, Example.class);

        assertThat(example.getField()).isEqualTo("string to trim");
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    private static class Example {
        private String field;
    }
}