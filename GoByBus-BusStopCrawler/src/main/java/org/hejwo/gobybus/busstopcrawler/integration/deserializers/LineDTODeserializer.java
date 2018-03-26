package org.hejwo.gobybus.busstopcrawler.integration.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.hejwo.gobybus.busstopcrawler.integration.dto.LineDTO;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class LineDTODeserializer extends StdDeserializer<LineDTO> {

    private static final String ROOT_NODE_NAME = "values";
    private static final String KEY = "key";
    private static final String VALUE = "value";

    public LineDTODeserializer() {
        this(null);
    }

    public LineDTODeserializer(Class<?> vc) {
        super(vc);
    }


    @Override
    public LineDTO deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
        JsonNode rootNode = parser.getCodec().readTree(parser);
        JsonNode values = rootNode.get(ROOT_NODE_NAME);

        Map<String, String> map = toMap(values);
        return LineDTO.from(map);
    }

    private Map<String, String> toMap(JsonNode values) {
        return StreamSupport.stream(values.spliterator(), true)
            .map(this::toEntry)
            .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
    }

    private AbstractMap.SimpleEntry<String,String> toEntry(JsonNode node) {
        String key = node.get(KEY).asText();
        String value = node.get(VALUE).asText();

        return new AbstractMap.SimpleEntry<>(key, value);
    }
}
