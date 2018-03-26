package org.hejwo.gobybus.locationcrawler.integration.exceptions;

import org.apache.commons.lang3.exception.ExceptionUtils;

import java.net.SocketTimeoutException;


public class WarsawApiTimeoutException extends WarsawApiNotRetryableException {

    public static final String MESSAGE = "Connection has timed out. ";

    public WarsawApiTimeoutException() {
        super(MESSAGE);
    }

    public WarsawApiTimeoutException(SocketTimeoutException timeoutEx) {
        super(MESSAGE + ExceptionUtils.getMessage(timeoutEx));
    }
}
