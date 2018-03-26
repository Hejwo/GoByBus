package org.hejwo.gobybus.busstopcrawler.integration.exceptions;

import feign.FeignException;

import java.net.ConnectException;

public abstract class WarsawApiNotRetryableException extends FeignException {

    public WarsawApiNotRetryableException(String message) {
        super(message);
    }

    public WarsawApiNotRetryableException(int status, String message) {
        super(status, message);
    }

    public WarsawApiNotRetryableException(String message, ConnectException connectionEx) {
        super(message, connectionEx);
    }
}
