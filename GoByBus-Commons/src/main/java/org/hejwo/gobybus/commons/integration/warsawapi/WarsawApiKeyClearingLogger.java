package org.hejwo.gobybus.commons.integration.warsawapi;

import feign.Logger;
import feign.Request;

public class WarsawApiKeyClearingLogger extends Logger.ErrorLogger {

    @Override
    protected void logRequest(String configKey, Level logLevel, Request request) {
        Request requestWithRemovedApiKey = createWithRemovedApiKey(request);
        super.logRequest(configKey, logLevel, requestWithRemovedApiKey);
    }

    protected Request createWithRemovedApiKey(Request request) {
        String url = request.url().replaceFirst("apikey=[a-zA-Z0-9]*-", "apikey=????????-");
        return Request.create(request.method(), url, request.headers(), request.body(), request.charset());
    }
}
