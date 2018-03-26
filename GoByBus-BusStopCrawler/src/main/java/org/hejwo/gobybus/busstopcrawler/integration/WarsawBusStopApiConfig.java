package org.hejwo.gobybus.busstopcrawler.integration;

import feign.Feign;
import feign.Logger;
import feign.Request;
import feign.Retryer;
import feign.slf4j.Slf4jLogger;
import org.hejwo.gobybus.commons.integration.warsawapi.WarsawApiKeyClearingLogger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static java.util.concurrent.TimeUnit.SECONDS;

@Configuration
public class WarsawBusStopApiConfig {

    private final static int HANDSHAKE_TIMEOUT_MILLIS = 2500;
    private final static int READ_TIMEOUT_MILLIS = 4000;

    @Bean
    public WarsawBusStopApi warsawApi(@Value("${openData.warsawApi.url}") String warsawApiUrl) {
        return Feign.builder()
                .decoder(new WarsawApiResponseDecoder())
                .client(new WarsawApiProperStatusAssigningClient())
                .errorDecoder(new WarsawApiErrorDecoder())
                .retryer(new Retryer.Default(100, SECONDS.toMillis(1), 3))
                .logger(new Slf4jLogger())
                .logLevel(Logger.Level.BASIC)
                .logger(new WarsawApiKeyClearingLogger())
                .options(new Request.Options(HANDSHAKE_TIMEOUT_MILLIS, READ_TIMEOUT_MILLIS))
                .target(WarsawBusStopApi.class, warsawApiUrl);
    }

}
