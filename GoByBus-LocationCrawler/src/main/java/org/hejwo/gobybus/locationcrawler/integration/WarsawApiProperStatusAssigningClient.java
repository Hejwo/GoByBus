package org.hejwo.gobybus.locationcrawler.integration;

import feign.Client;
import feign.Request;
import feign.Response;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.CharEncoding;
import org.hejwo.gobybus.locationcrawler.integration.exceptions.WarsawApiCorruptedResponseException;
import org.hejwo.gobybus.locationcrawler.integration.exceptions.WarsawApiInnerCauseException;
import org.hejwo.gobybus.locationcrawler.integration.exceptions.WarsawApiNotRetryableException;
import org.hejwo.gobybus.locationcrawler.integration.exceptions.WarsawApiRetryableException;
import org.hejwo.gobybus.locationcrawler.integration.exceptions.WarsawApiTimeoutException;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;

class WarsawApiProperStatusAssigningClient implements Client {

    private final Client rootClient;

    WarsawApiProperStatusAssigningClient() {
        this(new Client.Default(null, null));
    }

    WarsawApiProperStatusAssigningClient(Client rootClient) {
        this.rootClient = rootClient;
    }


    @Override
    public Response execute(Request request, Request.Options options) throws IOException {
        try {
            Response response = rootClient.execute(request, options);

            int responseStatus = response.status();
            String responseBody = IOUtils.toString(response.body().asInputStream(), CharEncoding.UTF_8);
            Response newResponse = createResponse(response, responseBody);
            if (responseStatus == 200 && responseBody.equalsIgnoreCase("[ ]")) {
                return createResponse(HttpStatus.SERVICE_UNAVAILABLE, response, responseBody);
            }

            if (responseStatus == 200 && isErrorContent(responseBody)) {
                HttpStatus httpStatus = resolveErrorStatus(responseBody);
                return createResponse(httpStatus, response, responseBody);
            }

            if (responseStatus == 200 && isWrongContent(responseBody)) {
                HttpStatus httpStatus = resolveErrorStatus(responseBody);
                return createResponse(httpStatus, response, responseBody);
            }

            return newResponse;
        } catch (SocketTimeoutException timeoutEx) {
            throw new WarsawApiTimeoutException(timeoutEx);
        } catch (ConnectException connectException) {
            throw new WarsawApiCorruptedResponseException(connectException);
        } catch (IOException ioEx) {
            throw new WarsawApiCorruptedResponseException(ioEx);
        } catch (WarsawApiRetryableException | WarsawApiNotRetryableException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new WarsawApiInnerCauseException(ex);
        }
    }

    private boolean isErrorContent(String responseBody) {
        String responseBeginning = responseBody.substring(0, 15).replace(" ", "");
        return responseBeginning.contains("error\":");
    }

    private boolean isWrongContent(String responseBody) {
        String responseBeginning = responseBody.substring(0, 15).replace(" ", "");
        return !responseBeginning.startsWith("{\"result\":[");
    }

    private Response createResponse(HttpStatus httpStatus, Response oldResponse, String responseBody) {
        return Response.builder()
            .headers(oldResponse.headers())
            .body(responseBody.getBytes())
            .reason(oldResponse.reason())
            .request(oldResponse.request())
            .status(httpStatus.value())
            .build();
    }

    private Response createResponse(Response oldResponse, String responseBody) {
        return Response.builder()
            .headers(oldResponse.headers())
            .body(responseBody.getBytes())
            .reason(oldResponse.reason())
            .request(oldResponse.request())
            .status(oldResponse.status())
            .build();
    }

    private HttpStatus resolveErrorStatus(String responseBody) {
        if (responseBody.contains("apikey lub jego brak")) {
            return HttpStatus.UNAUTHORIZED;
        }
        if (responseBody.contains("na metoda lub parametry wywo")) {
            return HttpStatus.BAD_REQUEST;
        }

        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
