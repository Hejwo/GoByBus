package org.hejwo.gobybus.locationcrawler.integration;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.RuntimeJsonMappingException;
import feign.FeignException;
import feign.Response;
import feign.Util;
import feign.codec.Decoder;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;

class WarsawApiResponseDecoder implements Decoder {

    private final ObjectMapper mapper = new StringTrimmingObjectMapper();

    public WarsawApiResponseDecoder() {
        mapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES);
        mapper.enable(JsonGenerator.Feature.IGNORE_UNKNOWN);
    }

    @Override
    public Object decode(Response response, Type type) throws IOException, FeignException {
        if (response.status() == 404) return Util.emptyValueOf(type);
        if (response.body() == null) return null;
        Reader reader = response.body().asReader();
        if (!reader.markSupported()) {
            reader = new BufferedReader(reader, 1);
        }
        try {
            // Read the first byte to see if we have any data
            reader.mark(1);
            if (reader.read() == -1) {
                return null; // Eagerly returning null avoids "No content to map due to end-of-input"
            }
            reader.reset();
            return readNestedValue(type, reader);
        } catch (RuntimeJsonMappingException e) {
            if (e.getCause() != null && e.getCause() instanceof IOException) {
                throw IOException.class.cast(e.getCause());
            }
            throw e;
        }
    }

    private Object readNestedValue(Type type, Reader reader) throws IOException {
        String fullJsonContent = IOUtils.toString(reader);
        String filteredContent = mapper.readTree(fullJsonContent).path("result").toString();
        return mapper.readValue(filteredContent, mapper.constructType(type));
    }
}
