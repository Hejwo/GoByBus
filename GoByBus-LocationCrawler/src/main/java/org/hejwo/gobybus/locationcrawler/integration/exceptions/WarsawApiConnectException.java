package org.hejwo.gobybus.locationcrawler.integration.exceptions;

import java.net.ConnectException;

public class WarsawApiConnectException extends WarsawApiRetryableException {

    private static final String MESSAGE = "Server is not responding";

    public WarsawApiConnectException() {
        super(MESSAGE);
    }

    public WarsawApiConnectException(ConnectException ex) {
        super(MESSAGE, ex);
    }
}
