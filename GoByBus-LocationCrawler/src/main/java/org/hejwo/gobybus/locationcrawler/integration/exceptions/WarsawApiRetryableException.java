package org.hejwo.gobybus.locationcrawler.integration.exceptions;

import feign.RetryableException;

import java.io.IOException;

public abstract class WarsawApiRetryableException extends RetryableException {

    public WarsawApiRetryableException(String message) {
        super(message, null);
    }

    public WarsawApiRetryableException(String message, IOException ioEx) {
        super(message, ioEx, null);
    }
}
