package org.hejwo.gobybus.commons.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StringToListDeserializer extends JsonDeserializer<List<String>> {

    @Override
    public List<String> deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException {
        JsonNode node = jsonParser.readValueAsTree();

        List<String> lineItems = Stream
            .of(node.asText().split(","))
            .map(String::trim)
            .filter(StringUtils::isNotEmpty)
            .collect(Collectors.toList());

        return lineItems;
    }
}
