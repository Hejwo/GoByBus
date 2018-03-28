package org.hejwo.gobybus.locationcrawler.integration;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.assertj.core.api.Condition;
import org.hejwo.gobybus.commons.domain.LocationData;
import org.hejwo.gobybus.locationcrawler.integration.exceptions.WarsawApiBadMethodOrParamsException;
import org.hejwo.gobybus.locationcrawler.integration.exceptions.WarsawApiCorruptedResponseException;
import org.hejwo.gobybus.locationcrawler.integration.exceptions.WarsawApiGeneralException;
import org.hejwo.gobybus.locationcrawler.integration.exceptions.WarsawApiServerUnavailableException;
import org.hejwo.gobybus.locationcrawler.integration.exceptions.WarsawApiTimeoutException;
import org.hejwo.gobybus.locationcrawler.integration.exceptions.WarsawApiUnauthorizedException;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Predicate;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static java.util.Collections.singleton;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionThrownBy;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = WarsawLocationApiConfig.class)
@Configuration
public class WarsawLocationApiIntegrationTest {

    private static final int SERVER_PORT = 1111;
    private static final int MAX_EXECUTION_TIME = 5500;

    @ClassRule
    public static WireMockRule wireMockRule = new WireMockRule(options()
        .port(SERVER_PORT)
        .usingFilesUnderClasspath("src/integrationTests/resources"), false);

    @Autowired
    private WarsawLocationApi warsawLocationApi;

    @Test(expected = WarsawApiUnauthorizedException.class, timeout = MAX_EXECUTION_TIME)
    public void shouldThrowWrongApiKey() {
        warsawLocationApi.getAllLines("wrong-api-key");
    }

    @Test(expected = WarsawApiServerUnavailableException.class, timeout = MAX_EXECUTION_TIME)
    public void shouldThrowServiceUnavailable() {
        warsawLocationApi.getAllLines("service-unavailable");
    }

    @Test(timeout = MAX_EXECUTION_TIME)
    public void shouldThrowGeneralExceptionWhenErrorMessage() {
        assertThatExceptionThrownBy(() ->
            warsawLocationApi.getAllLines("general-error-1")
        )
            .isInstanceOf(WarsawApiGeneralException.class)
            .hasMessageContaining("{ \"result\": \"false\", \"error\": \"Kabanosy nie są dobre do kawy ?!\" }")
            .is(correctWarsawApiGeneralException(500, "WarsawLocationApi#getAllLines(String)", "Kabanosy"));
    }

    @Test(timeout = MAX_EXECUTION_TIME)
    public void shouldThrowGeneralExceptionWhenStatusIncorrect() {
        assertThatExceptionThrownBy(() ->
            warsawLocationApi.getAllLines("general-error-2")
        )
            .isInstanceOf(WarsawApiGeneralException.class)
            .hasMessageContaining("{ \"result\": \"false\", \"error\": \"Nie wiem....\" }")
            .is(correctWarsawApiGeneralException(501, "WarsawLocationApi#getAllLines(String)", "Nie wiem..."));
    }

    @Test(expected = WarsawApiBadMethodOrParamsException.class, timeout = MAX_EXECUTION_TIME)
    public void shouldThrowResourceIncorrectException() {
        warsawLocationApi.getAllLines("wrong-resource-name-1");
    }

    @Test(timeout = MAX_EXECUTION_TIME)
    public void shouldThrowGeneralEx() {
        assertThatExceptionThrownBy(() ->
            warsawLocationApi.getAllLines("wrong-content")
        )
            .isInstanceOf(WarsawApiGeneralException.class)
            .hasMessageContaining("{ \"result\": \"I'm not a json\" }")
            .is(correctWarsawApiGeneralException(500, "WarsawLocationApi#getAllLines(String)", "I'm not a json"));
    }

    @Test(timeout = MAX_EXECUTION_TIME)
    public void shouldLoadCorrectLocations() {
        List<LocationData> loadedLocations = warsawLocationApi.getAllLines("correct-locations-1");

        assertThat(loadedLocations).hasSize(2);
        assertThat(loadedLocations.get(0).getStatus()).isEqualTo("RUNNING");

        assertThat(loadedLocations.get(0).getFirstLine()).isEqualTo("9");
        assertThat(loadedLocations.get(0).getLines()).hasSameElementsAs(singleton("9"));
        assertThat(loadedLocations.get(0).getBrigade()).isEqualTo("1");

        assertThat(loadedLocations.get(0).getLongitude()).isEqualTo(20.9438667);
        assertThat(loadedLocations.get(0).getLatitude()).isEqualTo(52.1761093);
        assertThat(loadedLocations.get(0).getTime()).isEqualTo(LocalDateTime.of(2017, 9, 4, 12, 40, 59));

        assertThat(loadedLocations.get(0).getLowFloor()).isEqualTo(false);

        assertThat(loadedLocations.get(1).getStatus()).isEqualTo("RUNNING");
        assertThat(loadedLocations.get(1).getFirstLine()).isEqualTo("26");
        assertThat(loadedLocations.get(1).getLines()).hasSameElementsAs(singleton("26"));
    }

    @Test(timeout = MAX_EXECUTION_TIME)
    public void shouldLoadCorrectLocations_anotherFormat() {
        List<LocationData> loadedLocations = warsawLocationApi.getAllLines("correct-locations-2");

        assertThat(loadedLocations).hasSize(12);
    }

    @Test(timeout = MAX_EXECUTION_TIME, expected = WarsawApiTimeoutException.class)
    public void shouldHandleServiceTimeout() {
        warsawLocationApi.getAllLines("huge-read-delay-1");
    }

    @Test(timeout = MAX_EXECUTION_TIME, expected = WarsawApiCorruptedResponseException.class)
    public void shouldHandle_malformedResponse() {
        warsawLocationApi.getAllLines("malformedResponse");
    }

    @Test(timeout = MAX_EXECUTION_TIME, expected = WarsawApiCorruptedResponseException.class)
    public void shouldHandle_emptyResponse() {
        warsawLocationApi.getAllLines("emptyResponse");
    }

    @Test(timeout = MAX_EXECUTION_TIME, expected = WarsawApiCorruptedResponseException.class)
    public void shouldHandle_randomResponse() {
        warsawLocationApi.getAllLines("randomResponse");
    }

    @Test(timeout = MAX_EXECUTION_TIME, expected = WarsawApiCorruptedResponseException.class)
    public void shouldHandle_connectionError() {
        wireMockRule.stop();
        try {
            warsawLocationApi.getAllLines("correct-locations-2");
        } finally {
            wireMockRule.start();
        }
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