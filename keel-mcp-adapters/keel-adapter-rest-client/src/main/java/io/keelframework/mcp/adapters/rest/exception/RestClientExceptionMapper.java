/*
 * Copyright 2026 Keel Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
