package org.hejwo.gobybus.busstopcrawler.integration.exceptions;

import lombok.Getter;

import static java.lang.String.format;

@Getter
public class WarsawApiGeneralException extends WarsawApiNotRetryableException {

    private final String methodKey;
    private final Integer status;
    private final String body;

    public WarsawApiGeneralException(String methodKey, int status, String body) {
        super(status, format("Status: %s, method: %s, body: '%s'", status, methodKey, body));
        this.methodKey = methodKey;
        this.status = status;
        this.body = body;
    }
}
