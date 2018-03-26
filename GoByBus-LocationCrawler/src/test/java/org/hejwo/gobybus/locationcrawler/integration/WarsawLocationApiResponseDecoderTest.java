package org.hejwo.gobybus.locationcrawler.integration;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.google.common.collect.Maps;
import feign.Response;
import feign.codec.Decoder;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionThrownBy;

public class WarsawLocationApiResponseDecoderTest {

    private Decoder decoder = new WarsawApiResponseDecoder();

    @Test
    public void decode_shouldReturnNull_whenNullBody() throws IOException {
        Response response = createResponse(200, null);

        Object decoded = decoder.decode(response, String.class);

        assertThat(decoded).isNull();
    }

    @Test
    public void decode_shouldReturnEmpty_when404() throws IOException {
        Response response = createResponse(404, "some body");

        Object decoded = decoder.decode(response, String.class);

        assertThat(decoded).isNull();
    }

    @Test
    public void decode_shouldReturnInnerContent_when200AndProperFormat() throws IOException {
        Response response = createResponse(200, "{ \"result\": \"some body\"}");

        Object decoded = decoder.decode(response, String.class);

        assertThat(decoded).isEqualTo("some body");
    }

    @Test
    @Ignore
    public void decode_shouldReturnInnerContent_handleWrongFormat() throws IOException {
        Response response = createResponse(200, "{ \"wyniki\": \"some body\"}");

        Object decodd = decoder.decode(response, String.class);
        // TODO ????????
    }

    @Test
    public void decode_shouldReturnInnerContent_handleWrongMapping() throws IOException {
        Response response = createResponse(200, "{ \"result\": \"some body\"}");

        assertThatExceptionThrownBy(() -> decoder.decode(response, Integer.class))
                .isInstanceOf(InvalidFormatException.class)
                .hasMessageContaining("Can not deserialize value of type java.lang.Integer from String \"some body\": not a valid Integer value");
    }

    private Response createResponse(int status, String body) {
        return Response.builder()
                .status(status)
                .headers(Maps.newHashMap())
                .body(body != null ? body.getBytes() : null)
                .build();
    }
}