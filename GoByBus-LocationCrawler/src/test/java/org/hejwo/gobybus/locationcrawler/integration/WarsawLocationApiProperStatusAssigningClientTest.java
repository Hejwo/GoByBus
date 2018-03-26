package org.hejwo.gobybus.locationcrawler.integration;

import com.google.common.collect.Maps;
import feign.Client;
import feign.Request;
import feign.Response;
import org.hejwo.gobybus.locationcrawler.integration.exceptions.WarsawApiConnectException;
import org.hejwo.gobybus.locationcrawler.integration.exceptions.WarsawApiCorruptedResponseException;
import org.hejwo.gobybus.locationcrawler.integration.exceptions.WarsawApiInnerCauseException;
import org.hejwo.gobybus.locationcrawler.integration.exceptions.WarsawApiNotRetryableException;
import org.hejwo.gobybus.locationcrawler.integration.exceptions.WarsawApiRetryableException;
import org.hejwo.gobybus.locationcrawler.integration.exceptions.WarsawApiTimeoutException;
import org.hejwo.gobybus.locationcrawler.integration.exceptions.WarsawApiUnauthorizedException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionThrownBy;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WarsawLocationApiProperStatusAssigningClientTest {

    private Client warsawApiProperStatusAssigningClient;

    @Mock
    private Client rootClient;

    @Before
    public void setUp() {
        warsawApiProperStatusAssigningClient = new WarsawApiProperStatusAssigningClient(rootClient);
    }

    @Test
    public void execute_shouldAssign503_whenEmptyResponse() throws IOException {
        String body = "[ ]";
        Request request = mockResponseAndCreateRequest(body);

        Response response = warsawApiProperStatusAssigningClient.execute(request, new Request.Options());

        assertThat(response.status()).isEqualTo(503);
        assertThat(response.body().toString()).isEqualToIgnoringCase(body);
    }

    @Test
    public void execute_shouldAssign401_whenUnAuthResponse() throws IOException {
        String body = "{ \"error\": \"bład apikey lub jego brak\" }";
        Request request = mockResponseAndCreateRequest(body);

        Response response = warsawApiProperStatusAssigningClient.execute(request, new Request.Options());

        assertThat(response.status()).isEqualTo(401);
        assertThat(response.body().toString()).isEqualToIgnoringCase(body);
    }

    @Test
    public void execute_shouldAssign400_whenError() throws IOException {
        String body = "{ \"error\": \"błędna metoda lub parametry wywołania\" }";
        Request request = mockResponseAndCreateRequest(body);

        Response response = warsawApiProperStatusAssigningClient.execute(request, new Request.Options());

        assertThat(response.status()).isEqualTo(400);
        assertThat(response.body().toString()).isEqualToIgnoringCase(body);
    }

    @Test
    public void execute_shouldAssign500_whenError() throws IOException {
        String body = "{ \"error\": \"something went wrong\" }";
        Request request = mockResponseAndCreateRequest(body);

        Response response = warsawApiProperStatusAssigningClient.execute(request, new Request.Options());

        assertThat(response.status()).isEqualTo(500);
        assertThat(response.body().toString()).isEqualToIgnoringCase(body);
    }

    @Test
    public void execute_shouldAssign200_whenOk() throws IOException {
        String body = "{ \"result\": [\"[success !!!\"] }";
        Request request = mockResponseAndCreateRequest(body);

        Response response = warsawApiProperStatusAssigningClient.execute(request, new Request.Options());

        assertThat(response.status()).isEqualTo(200);
        assertThat(response.body().toString()).isEqualToIgnoringCase(body);
    }

    @Test
    public void execute_shouldAssign500_whenWeirdContent() throws IOException {
        String body = "{ \"wyniki\": [\"[success !!!\"] }";
        Request request = mockResponseAndCreateRequest(body);

        Response response = warsawApiProperStatusAssigningClient.execute(request, new Request.Options());

        assertThat(response.status()).isEqualTo(500);
        assertThat(response.body().toString()).isEqualToIgnoringCase(body);
    }

    @Test
    public void execute_shouldHandleSocketTimeout() throws IOException {
        Request request = mockThrowAndCreateRequest(new SocketTimeoutException());

        assertThatExceptionThrownBy(() -> warsawApiProperStatusAssigningClient.execute(request, new Request.Options()))
                .isInstanceOf(WarsawApiTimeoutException.class)
                .hasMessageContaining("Connection has timed out. SocketTimeoutException");
    }

    @Test
    public void execute_shouldHandleConnectException() throws IOException {
        Request request = mockThrowAndCreateRequest(new ConnectException());

        assertThatExceptionThrownBy(() -> warsawApiProperStatusAssigningClient.execute(request, new Request.Options()))
                .isInstanceOf(WarsawApiCorruptedResponseException.class)
                .hasMessageContaining("Server response is corrupted ConnectException");
    }

    @Test
    public void execute_shouldHandleIOException() throws IOException {
        Request request = mockThrowAndCreateRequest(new IOException());

        assertThatExceptionThrownBy(() -> warsawApiProperStatusAssigningClient.execute(request, new Request.Options()))
                .isInstanceOf(WarsawApiCorruptedResponseException.class)
                .hasMessageContaining("Server response is corrupted IOException: ");
    }

    @Test
    public void execute_shouldHandleOtherExceptionTypes() throws IOException {
        Request request = mockThrowAndCreateRequest(new RuntimeException("sad thing happened ! ;("));

        assertThatExceptionThrownBy(() -> warsawApiProperStatusAssigningClient.execute(request, new Request.Options()))
                .isInstanceOf(WarsawApiInnerCauseException.class)
                .hasMessageContaining("Inner exception has been thrown : RuntimeException: sad thing happened ! ;(");
    }

    @Test
    public void execute_shouldThrowRetryableExceptionUp() throws IOException {
        Request request = mockThrowAndCreateRequest(new WarsawApiConnectException());

        assertThatExceptionThrownBy(() -> warsawApiProperStatusAssigningClient.execute(request, new Request.Options()))
                .isInstanceOf(WarsawApiRetryableException.class)
                .hasMessageContaining("Server is not responding");
    }

    @Test
    public void execute_shouldThrowNotRetryableExceptionUp() throws IOException {
        Request request = mockThrowAndCreateRequest(new WarsawApiUnauthorizedException());

        assertThatExceptionThrownBy(() -> warsawApiProperStatusAssigningClient.execute(request, new Request.Options()))
                .isInstanceOf(WarsawApiNotRetryableException.class)
                .hasMessageContaining("Missing API key or invalid one");
    }

    private Request mockResponseAndCreateRequest(String body) throws IOException {
        Response response = Response.builder()
                .status(200)
                .headers(Maps.newHashMap())
                .body(body.getBytes())
                .build();
        when(rootClient.execute(any(), any())).thenReturn(response);
        return Request.create("GET", "http://test:1234", Maps.newHashMap(), body.getBytes(), Charset.defaultCharset());
    }

    private Request mockThrowAndCreateRequest(Exception exceptionToThrow) throws IOException {
        String body = "{ \"result\": [\"[success !!!\"] }";
        doThrow(exceptionToThrow).when(rootClient).execute(any(), any());
        return Request.create("GET", "http://test:1234", Maps.newHashMap(), body.getBytes(), Charset.defaultCharset());
    }
}