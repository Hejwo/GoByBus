package org.hejwo.gobybus.locationcrawler.integration;

import com.google.common.collect.Maps;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.hejwo.gobybus.locationcrawler.integration.exceptions.WarsawApiBadMethodOrParamsException;
import org.hejwo.gobybus.locationcrawler.integration.exceptions.WarsawApiGeneralException;
import org.hejwo.gobybus.locationcrawler.integration.exceptions.WarsawApiNotRetryableException;
import org.hejwo.gobybus.locationcrawler.integration.exceptions.WarsawApiRetryableException;
import org.hejwo.gobybus.locationcrawler.integration.exceptions.WarsawApiServerUnavailableException;
import org.hejwo.gobybus.locationcrawler.integration.exceptions.WarsawApiUnauthorizedException;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

public class WarsawLocationApiErrorDecoderTest {

    private ErrorDecoder errorDecoder = new WarsawApiErrorDecoder();

    @Test
    public void decode_shouldReturnWarsawApiServerUnavailableException_when503() {
        Response badResponse = Response.builder()
                .status(HttpStatus.SERVICE_UNAVAILABLE.value())
                .headers(Maps.newHashMap())
                .build();

        Exception resolvedException = errorDecoder.decode("method1", badResponse);

        assertThat(resolvedException).isInstanceOf(WarsawApiServerUnavailableException.class);
        assertThat(resolvedException).isInstanceOf(WarsawApiRetryableException.class);
    }

    @Test
    public void decode_shouldReturnWarsawApiUnauthorizedException_when401() {
        Response badResponse = Response.builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .headers(Maps.newHashMap())
                .build();

        Exception resolvedException = errorDecoder.decode("method1", badResponse);

        assertThat(resolvedException).isInstanceOf(WarsawApiUnauthorizedException.class);
        assertThat(resolvedException).isInstanceOf(WarsawApiNotRetryableException.class);
    }

    @Test
    public void decode_shouldReturnWarsawApiBadMethodOrParamsException_when400() {
        Response badResponse = Response.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .headers(Maps.newHashMap())
                .build();

        Exception resolvedException = errorDecoder.decode("method1", badResponse);

        assertThat(resolvedException).isInstanceOf(WarsawApiBadMethodOrParamsException.class);
        assertThat(resolvedException).isInstanceOf(WarsawApiNotRetryableException.class);
    }

    @Test
    public void decode_shouldReturnWarsawApiGeneralException_whenOtherErrors() {
        Response badResponse = Response.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .headers(Maps.newHashMap())
                .build();

        Exception resolvedException = errorDecoder.decode("method1", badResponse);

        assertThat(resolvedException).isInstanceOf(WarsawApiGeneralException.class);
        assertThat(resolvedException).isInstanceOf(WarsawApiNotRetryableException.class);
    }

    @Test
    public void decode_shouldReturnWarsawApiGeneralException_whenOtherErrorsAndExistingResponse() {
        Response badResponse = Response.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .headers(Maps.newHashMap())
                .body("Message error".getBytes())
                .build();

        Exception resolvedException = errorDecoder.decode("method1", badResponse);

        assertThat(resolvedException).isInstanceOf(WarsawApiGeneralException.class);
        assertThat(resolvedException).isInstanceOf(WarsawApiNotRetryableException.class);
    }

}