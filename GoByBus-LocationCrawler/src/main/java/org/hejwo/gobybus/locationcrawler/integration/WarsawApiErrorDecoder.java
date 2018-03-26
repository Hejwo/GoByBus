package org.hejwo.gobybus.locationcrawler.integration;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.apache.commons.io.IOUtils;
import org.hejwo.gobybus.locationcrawler.integration.exceptions.WarsawApiBadMethodOrParamsException;
import org.hejwo.gobybus.locationcrawler.integration.exceptions.WarsawApiGeneralException;
import org.hejwo.gobybus.locationcrawler.integration.exceptions.WarsawApiServerUnavailableException;
import org.hejwo.gobybus.locationcrawler.integration.exceptions.WarsawApiUnauthorizedException;
import org.springframework.http.HttpStatus;

import java.io.InputStream;
import java.nio.charset.Charset;

class WarsawApiErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        int status = response.status();

        if(HttpStatus.SERVICE_UNAVAILABLE.value() == status) {
            return new WarsawApiServerUnavailableException();
        }

        if(HttpStatus.UNAUTHORIZED.value() == status) {
            return new WarsawApiUnauthorizedException();
        }

        if(HttpStatus.BAD_REQUEST.value() == status) {
            return new WarsawApiBadMethodOrParamsException();
        }

        String body = getBodySafe(response);
        return new WarsawApiGeneralException(methodKey, response.status(), body);
    }

    private String getBodySafe(Response response) {
        try {
            InputStream inputStream = response.body().asInputStream();
            return IOUtils.toString(inputStream, Charset.defaultCharset());
        } catch (Exception ex) {
            return null;
        }
    }
}
