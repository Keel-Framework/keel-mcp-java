package io.keelframework.mcp.adapters.rest.exception;

import org.springframework.web.client.RestClientResponseException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;

public class RestClientExceptionMapper {

    public static RuntimeException map(
            String serviceName,
            String path,
            Exception ex) {

        if (ex instanceof RestClientResponseException rce) {

            return new RestClientException.HttpError(
                    serviceName,
                    path,
                    rce.getStatusCode().value(),
                    rce.getResponseBodyAsString(),
                    rce);
        }

        if (ex instanceof SocketTimeoutException
                || ex instanceof TimeoutException) {

            return new RestClientException.Timeout(
                    serviceName,
                    path,
                    ex);
        }

        if (ex instanceof UnknownHostException
                || ex instanceof ConnectException) {

            return new RestClientException.ServiceUnavailable(
                    serviceName,
                    ex);
        }

        return new RestClientException.ServiceUnavailable(
                serviceName,
                ex);
    }

    private RestClientExceptionMapper() {
        // utility class
    }
}
