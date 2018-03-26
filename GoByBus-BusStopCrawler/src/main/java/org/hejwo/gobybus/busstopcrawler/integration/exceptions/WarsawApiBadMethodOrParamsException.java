package org.hejwo.gobybus.busstopcrawler.integration.exceptions;

public class WarsawApiBadMethodOrParamsException extends WarsawApiNotRetryableException {

    private static final String EXCEPTION_MESSAGE = "Bad method or params where invoked";

    public WarsawApiBadMethodOrParamsException() {
        super(EXCEPTION_MESSAGE);
    }
}
