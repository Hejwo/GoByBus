package org.hejwo.gobybus.busstopcrawler.integration.deserializers;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.hejwo.gobybus.busstopcrawler.integration.dto.BusStopDTO;

import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class BusStopDTODeserializer extends StdDeserializer<BusStopDTO> {

    private static final String ROOT_NODE_NAME = "values";
    private static final String KEY = "key";
    private static final String VALUE = "value";

    public BusStopDTODeserializer() {
        this(null);
    }

    public BusStopDTODeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public BusStopDTO deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode rootNode = p.getCodec().readTree(p);
        JsonNode values = rootNode.get(ROOT_NODE_NAME);

        Map<String, String> map = toMap(values);
        return BusStopDTO.from(map);
    }

    private Map<String, String> toMap(JsonNode values) {
        return StreamSupport.stream(values.spliterator(), true)
                .map(this::toEntry)
                .collect(Collectors.toMap(SimpleEntry::getKey, SimpleEntry::getValue));
    }

    private SimpleEntry<String,String> toEntry(JsonNode node) {
        String key = node.get(KEY).asText();
        String value = node.get(VALUE).asText();

        return new SimpleEntry<>(key, value);
    }

}
