package org.hejwo.gobybus.busstopcrawler.integration.exceptions;

public class WarsawApiUnauthorizedException extends WarsawApiNotRetryableException {

    private static final String EXCEPTION_MESSAGE = "Missing API key or invalid one";

    public WarsawApiUnauthorizedException() {
        super(EXCEPTION_MESSAGE);
    }
}
