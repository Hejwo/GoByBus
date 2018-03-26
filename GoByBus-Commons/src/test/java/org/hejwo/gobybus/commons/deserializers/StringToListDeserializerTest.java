package org.hejwo.gobybus.commons.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StringToListDeserializerTest {

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

    @Test
    public void deserializeTestWhenOneInput() throws Exception {
        StringToListDeserializer deserializer = new StringToListDeserializer();

        when(jsonNode.asText()).thenReturn("8");

        List<String> stringList = deserializer.deserialize(jsonParser, null);

        assertThat(stringList).hasSize(1);
        assertThat(stringList).isEqualTo(Lists.newArrayList("8"));
    }

    @Test
    public void deserializeTestWhenManyInputs() throws Exception {
        StringToListDeserializer deserializer = new StringToListDeserializer();

        when(jsonNode.asText()).thenReturn("8,9,10,11,12");

        List<String> stringList = deserializer.deserialize(jsonParser, null);

        assertThat(stringList).hasSize(5);
        assertThat(stringList).isEqualTo(Lists.newArrayList("8","9","10","11","12"));
    }

    @Test
    public void deserializeTestWhenManyInputsUntrimmed() throws Exception {
        StringToListDeserializer deserializer = new StringToListDeserializer();

        when(jsonNode.asText()).thenReturn("   8  , 9,  10 , 11  ,  12     ");

        List<String> stringList = deserializer.deserialize(jsonParser, null);

        assertThat(stringList).hasSize(5);
        assertThat(stringList).isEqualTo(Lists.newArrayList("8","9","10","11","12"));
    }

    @Test
    public void deserializeTestWhenEmpty() throws Exception {
        StringToListDeserializer deserializer = new StringToListDeserializer();

        when(jsonNode.asText()).thenReturn("");

        List<String> stringList = deserializer.deserialize(jsonParser, null);

        assertThat(stringList).hasSize(0);
    }

    @Test
    public void deserializeTestWhenManyEmpty() throws Exception {
        StringToListDeserializer deserializer = new StringToListDeserializer();

        when(jsonNode.asText()).thenReturn(",,,,,");

        List<String> stringList = deserializer.deserialize(jsonParser, null);

        assertThat(stringList).hasSize(0);
    }

}