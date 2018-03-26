package org.hejwo.gobybus.commons.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LocalDateDeserializerTest {

    @Mock
    private JsonParser jsonParser;
    @Mock
    private JsonNode jsonNode;

    @Before
    public void setUp() throws Exception {
        jsonParser = mock(JsonParser.class);
        jsonNode = mock(JsonNode.class);

        when(jsonParser.readValueAsTree()).thenReturn(jsonNode);
    }

    @Test(expected = DateTimeParseException.class)
    public void deserializeTestWhenInvalidInput() throws Exception {
        when(jsonNode.asText()).thenReturn("invalid date");

        LocalDateDeserializer deserializer = new LocalDateDeserializer();
        deserializer.deserialize(jsonParser, null);
    }

    @Test
    public void deserializeTestWhenValid() throws Exception {
        LocalDateTime validDate = LocalDateTime.of(2011, 12, 3, 10, 15, 30);

        when(jsonNode.asText()).thenReturn("2011-12-03T10:15:30");

        LocalDateDeserializer deserializer = new LocalDateDeserializer();
        LocalDateTime deserializedDate = deserializer.deserialize(jsonParser, null);

        assertThat(deserializedDate).isEqualTo(validDate);
    }

}