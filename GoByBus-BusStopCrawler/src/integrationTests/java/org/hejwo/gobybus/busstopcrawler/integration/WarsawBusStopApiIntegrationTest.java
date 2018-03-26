package org.hejwo.gobybus.busstopcrawler.integration;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.assertj.core.api.Condition;
import org.hejwo.gobybus.busstopcrawler.integration.dto.BusStopDTO;
import org.hejwo.gobybus.busstopcrawler.integration.dto.LineDTO;
import org.hejwo.gobybus.busstopcrawler.integration.dto.TimetableDTO;
import org.hejwo.gobybus.busstopcrawler.integration.exceptions.WarsawApiBadMethodOrParamsException;
import org.hejwo.gobybus.busstopcrawler.integration.exceptions.WarsawApiCorruptedResponseException;
import org.hejwo.gobybus.busstopcrawler.integration.exceptions.WarsawApiGeneralException;
import org.hejwo.gobybus.busstopcrawler.integration.exceptions.WarsawApiServerUnavailableException;
import org.hejwo.gobybus.busstopcrawler.integration.exceptions.WarsawApiTimeoutException;
import org.hejwo.gobybus.busstopcrawler.integration.exceptions.WarsawApiUnauthorizedException;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.function.Predicate;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionThrownBy;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = WarsawBusStopApiConfig.class)
@Configuration
public class WarsawBusStopApiIntegrationTest {

    private static final int SERVER_PORT = 1111;
    private static final int MAX_EXECUTION_TIME = 14000;

    @ClassRule
    public static WireMockRule wireMockRule = new WireMockRule(options()
        .port(SERVER_PORT)
        .usingFilesUnderClasspath("src/integrationTests/resources"));

    @Autowired
    private WarsawBusStopApi warsawBusStopApi;

    @Before
    public void setUp() {
    }

    @Test(expected = WarsawApiUnauthorizedException.class, timeout = MAX_EXECUTION_TIME)
    public void shouldThrowWrongApiKey() {
        warsawBusStopApi.getAllBusStops("wrong-api-key");
    }

    @Test(expected = WarsawApiServerUnavailableException.class, timeout = MAX_EXECUTION_TIME)
    public void shouldThrowServiceUnavailable() {
        warsawBusStopApi.getAllBusStops("service-unavailable");
    }

    @Test(timeout = MAX_EXECUTION_TIME)
    public void shouldThrowGeneralExceptionWhenErrorMessage() {
        assertThatExceptionThrownBy(() ->
            warsawBusStopApi.getAllBusStops("general-error-1")
        )
            .isInstanceOf(WarsawApiGeneralException.class)
            .hasMessageContaining("{ \"result\": \"false\", \"error\": \"Kabanosy nie są dobre do kawy ?!\" }")
            .is(correctWarsawApiGeneralException(500, "WarsawBusStopApi#getAllBusStops(String)", "Kabanosy"));
    }

    @Test(timeout = MAX_EXECUTION_TIME)
    public void shouldThrowGeneralExceptionWhenStatusIncorrect() {
        assertThatExceptionThrownBy(() ->
            warsawBusStopApi.getAllBusStops("general-error-2")
        )
            .isInstanceOf(WarsawApiGeneralException.class)
            .hasMessageContaining("{ \"result\": \"false\", \"error\": \"Nie wiem....\" }")
            .is(correctWarsawApiGeneralException(501, "WarsawBusStopApi#getAllBusStops(String)", "Nie wiem..."));
    }

    @Test(expected = WarsawApiBadMethodOrParamsException.class, timeout = MAX_EXECUTION_TIME)
    public void shouldThrowResourceIncorrectException() {
        warsawBusStopApi.getAllBusStops("wrong-resource-name-1");
    }

    @Test(timeout = MAX_EXECUTION_TIME)
    public void shouldThrowGeneralEx() {
        assertThatExceptionThrownBy(() ->
            warsawBusStopApi.getAllBusStops("wrong-content")
        )
            .isInstanceOf(WarsawApiGeneralException.class)
            .hasMessageContaining("{ \"result\": \"I'm not a json\" }")
            .is(correctWarsawApiGeneralException(500, "WarsawBusStopApi#getAllBusStops(String)", "I'm not a json"));
    }

    @Test(timeout = MAX_EXECUTION_TIME)
    public void should_getAllBusStops_loadCorrectBusStops() {
        List<BusStopDTO> busStopDTOList = warsawBusStopApi.getAllBusStops("correct-busStops-1");

        assertThat(busStopDTOList).hasSize(2);
        assertThat(busStopDTOList).have(nonNullFieldsOnly());
    }

    private <T extends Object> Condition<T> nonNullFieldsOnly() {
        Predicate<T> p1 = busStop -> !busStop.toString().contains("null");
        return new Condition<>(p1, "does not contain null values");
    }

    @Test(timeout = MAX_EXECUTION_TIME, expected = WarsawApiTimeoutException.class)
    public void shouldHandleServiceTimeout() {
        warsawBusStopApi.getAllBusStops("huge-read-delay-1");
    }

    @Test(timeout = MAX_EXECUTION_TIME, expected = WarsawApiCorruptedResponseException.class)
    public void shouldHandle_malformedResponse() {
        warsawBusStopApi.getAllBusStops("malformedResponse");
    }

    @Test(timeout = MAX_EXECUTION_TIME, expected = WarsawApiCorruptedResponseException.class)
    public void shouldHandle_emptyResponse() {
        warsawBusStopApi.getAllBusStops("emptyResponse");
    }

    @Test(timeout = MAX_EXECUTION_TIME, expected = WarsawApiCorruptedResponseException.class)
    public void shouldHandle_randomResponse() {
        warsawBusStopApi.getAllBusStops("randomResponse");
    }

    @Test(timeout = MAX_EXECUTION_TIME, expected = WarsawApiCorruptedResponseException.class)
    public void shouldHandle_connectionError() {
        wireMockRule.stop();
        try {
            warsawBusStopApi.getAllBusStops("correct-locations-2");
        } finally {
            wireMockRule.start();
        }
    }

    @Test
    public void should_getLinesForBusStop() {
        List<LineDTO> busStopLines = warsawBusStopApi.getLinesForBusStop("1001", "01", "correct-api-key");

        assertThat(busStopLines).hasSize(6);
        assertThat(busStopLines).extracting(LineDTO::getLine).containsExactly("135" ,"138" ,"166" ,"509" ,"517" ,"N21");
    }

    @Test
    public void should_getTimetablesForLineAndBusStop() {
        List<TimetableDTO> timetablesForLineAndBusStop = warsawBusStopApi.getTimetablesForLineAndBusStop("1001", "01", "135", "correct-api-key");

        assertThat(timetablesForLineAndBusStop).hasSize(61);
        assertThat(timetablesForLineAndBusStop).have(nonNullFieldsOnly());
    }

    private Condition<? super Throwable> correctWarsawApiGeneralException(int status, String methodName, String bodyPart) {
        Predicate<? super Throwable> cond = err -> {
            WarsawApiGeneralException ex = (WarsawApiGeneralException) err;
            boolean correctStatus = ex.getStatus() == status;
            boolean correctMethod = ex.getMethodKey().equals(methodName);
            boolean bodyContains = ex.getBody().contains(bodyPart);

            return correctStatus && correctMethod && bodyContains;
        };
        return new Condition<>(cond, "Exception not matching all requirements from correctWarsawApiGeneralException");
    }

}