package org.hejwo.gobybus.busstopcrawler.integration;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

public class StringTrimmingObjectMapper extends ObjectMapper {

    public StringTrimmingObjectMapper() {
        registerModule(new StringTrimmingModule());

        enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES);
        enable(JsonGenerator.Feature.IGNORE_UNKNOWN);
    }

    private class StringTrimmingModule extends SimpleModule {

        private StringTrimmingModule() {
            addDeserializer(String.class, new StdScalarDeserializer<String>(String.class) {
                @Override
                public String deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
                    return StringUtils.trim(jp.getValueAsString());
                }
            });
        }
    }
}

