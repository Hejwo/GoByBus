package org.hejwo.gobybus.busstopcrawler.integration.exceptions;

import org.apache.commons.lang3.exception.ExceptionUtils;

public class WarsawApiInnerCauseException extends WarsawApiNotRetryableException {

    public WarsawApiInnerCauseException(Exception cause) {
        super("Inner exception has been thrown : "+ ExceptionUtils.getMessage(cause));
        super.initCause(cause);
    }
}
