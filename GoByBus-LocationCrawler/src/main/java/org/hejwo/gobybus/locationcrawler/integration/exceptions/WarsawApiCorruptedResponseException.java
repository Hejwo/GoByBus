package org.hejwo.gobybus.locationcrawler.integration.exceptions;

import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.IOException;

public class WarsawApiCorruptedResponseException extends WarsawApiRetryableException {

    private static final String MESSAGE = "Server response is corrupted ";

    public WarsawApiCorruptedResponseException() {
        super(MESSAGE);
    }

    public WarsawApiCorruptedResponseException(IOException ioEx) {
        super(MESSAGE + ExceptionUtils.getMessage(ioEx), ioEx);
    }
}
