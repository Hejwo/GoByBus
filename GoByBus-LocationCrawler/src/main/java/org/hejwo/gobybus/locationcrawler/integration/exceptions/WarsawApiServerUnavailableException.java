package org.hejwo.gobybus.locationcrawler.integration.exceptions;

public class WarsawApiServerUnavailableException extends WarsawApiRetryableException {

    private static final String EXCEPTION_MESSAGE = "Warsaw API is temporary unavailable";

    public WarsawApiServerUnavailableException() {
        super(EXCEPTION_MESSAGE);
    }
}
