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
package io.keelframework.mcp.adapters.rest.client;

import io.keelframework.mcp.adapters.rest.config.RestClientProperties;
import io.keelframework.mcp.adapters.rest.exception.RestClientExceptionMapper;
import io.keelframework.mcp.adapters.rest.model.RestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;


/**
 * Base HTTP client for MCP tools.
 *
 * Single responsibility: execute HTTP GET / POST / PUT / DELETE
 * requests against the backend service.
 *
 * Standard Keel headers (JWT, propagation) are handled automatically
 * by McpHeadersInterceptor, registered on the RestClient —
 * transparent to the developer.
 */

public class McpRestClient {

    private static final Logger log = LoggerFactory.getLogger(McpRestClient.class);

    private final RestClient restClient;
    private final String serviceName;
    private final  RestClientProperties.ServiceConfig config;

    public McpRestClient(RestClient restClient, String serviceName,
                         RestClientProperties.ServiceConfig config) {
        this.restClient = restClient;
        this.serviceName = serviceName;
        this.config = config;
    }

    /** ================================================
     * REQUEST TYPE::  GET
     * get(path) → String, no headers
     * get(path, extraHeaders) → String, with headers
     * get(path, responseType) → T, no headers
     * get(path, extraHeaders, responseType) → T, with headers
     * get(path, responseType, uriVariables...) → T, with path variables
     * getWithQueryParams(path, queryParams, responseType) → T, with safe (URL-encoded) query params
     * getList(path, arrayType, uriVariables...) → List<T>, with path variables
     * getList(path, extraHeaders, arrayType) → List<T>, with headers
     * getListWithQueryParams(path, queryParams, arrayType) → List<T>, with safe query params
     */
    public RestResponse<String> get(String path){
        return get(path, Map.of());
    }

    public RestResponse<String> get(String path, Map<String, String> extraHeaders){
        logRequest("GET", path);
        try{
            String body = restClient.get()
                    .uri(path)
                    .headers(h -> extraHeaders.forEach(h::add))
                    .retrieve()
                    .body(String.class);
            return RestResponse.ok(body);
        }
        catch (Exception ex) {
            log.warn("REST GET ERROR service={} path={} error={}", serviceName, path, ex.getMessage());
            throw RestClientExceptionMapper.map(
                    serviceName,
                    path,
                    ex);
        }
    }

    public <T> RestResponse<T> get(String path, Class<T> responseType) {
        return get(path, Map.of(), responseType);
    }

    public <T> RestResponse<T> get(String path, Map<String, String> extraHeaders, Class<T> responseType) {
        logRequest("GET", path);
        try {
            T body = restClient.get()
                    .uri(path)
                    .headers(h -> extraHeaders.forEach(h::add))
                    .retrieve()
                    .body(responseType);

            return RestResponse.ok(body);

        } catch (Exception ex) {
            log.warn("REST GET ERROR service={} path={} error={}", serviceName, path, ex.getMessage());
            throw RestClientExceptionMapper.map(
                    serviceName,
                    path,
                    ex);
        }
    }

    public <T> RestResponse<T> get(String path, Class<T> responseType, Object... uriVariables) {
        logRequest("GET", path);
        try{
            T body = restClient.get()
                    .uri(path, uriVariables)
                    .retrieve()
                    .body(responseType);
            return  RestResponse.ok(body);
        } catch (Exception ex) {
            log.warn("REST GET ERROR service={} path={} error={}", serviceName, path, ex.getMessage());
            throw RestClientExceptionMapper.map(
                    serviceName,
                    path,
                    ex);
        }
    }

    /**
     * GET with query parameters built safely via UriComponentsBuilder,
     * instead of manually concatenating "?name={name}" into the path.
     * Handles URL-encoding automatically and scales cleanly to
     * multiple parameters, without depending on positional argument
     * order like get(path, responseType, uriVariables...) does.
     *
     * Named getWithQueryParams (not an overload of get) because
     * Map&lt;String, Object&gt; erases to the same raw type as the
     * existing get(path, Map&lt;String, String&gt; extraHeaders, Class)
     * overload — Java cannot disambiguate them by generic type alone.
     */
    public <T> RestResponse<T> getWithQueryParams(String path, Map<String, Object> queryParams, Class<T> responseType) {
        String uri = buildUriWithQueryParams(path, queryParams);
        logRequest("GET", uri);
        try {
            T body = restClient.get()
                    .uri(uri)
                    .retrieve()
                    .body(responseType);
            return RestResponse.ok(body);
        } catch (Exception ex) {
            log.warn("REST GET ERROR service={} path={} error={}", serviceName, uri, ex.getMessage());
            throw RestClientExceptionMapper.map(
                    serviceName,
                    uri,
                    ex);
        }
    }

    /**
     * Like {@link #get(String, Class, Object...)}, but for endpoints
     * that return a JSON array. Java generics erase List&lt;T&gt; at
     * runtime, so a plain get(path, List.class) cannot deserialize
     * element types correctly — this method works around that by
     * requesting the response as a T[] array (which Jackson
     * deserializes correctly) and wrapping the result in an
     * immutable List.
     */
    public <T> RestResponse<List<T>> getList(String path, Class<T[]> arrayType, Object... uriVariables) {
        logRequest("GET", path);
        try {
            T[] body = restClient.get()
                    .uri(path, uriVariables)
                    .retrieve()
                    .body(arrayType);
            return RestResponse.ok(body == null ? List.of() : List.of(body));
        } catch (Exception ex) {
            log.warn("REST GET ERROR service={} path={} error={}", serviceName, path, ex.getMessage());
            throw RestClientExceptionMapper.map(
                    serviceName,
                    path,
                    ex);
        }
    }

    /**
     * Like {@link #getList(String, Class, Object...)}, but lets you
     * add extra HTTP headers, mirroring get(path, extraHeaders, responseType).
     */
    public <T> RestResponse<List<T>> getList(String path, Map<String, String> extraHeaders, Class<T[]> arrayType) {
        logRequest("GET", path);
        try {
            T[] body = restClient.get()
                    .uri(path)
                    .headers(h -> extraHeaders.forEach(h::add))
                    .retrieve()
                    .body(arrayType);
            return RestResponse.ok(body == null ? List.of() : List.of(body));
        } catch (Exception ex) {
            log.warn("REST GET ERROR service={} path={} error={}", serviceName, path, ex.getMessage());
            throw RestClientExceptionMapper.map(
                    serviceName,
                    path,
                    ex);
        }
    }

    /**
     * Like {@link #getList(String, Class, Object...)}, but with safe
     * query params via UriComponentsBuilder (see {@link #getWithQueryParams(String, Map, Class)}).
     *
     * Named getListWithQueryParams for the same erasure reason
     * documented on {@link #getWithQueryParams(String, Map, Class)}.
     */
    public <T> RestResponse<List<T>> getListWithQueryParams(String path, Map<String, Object> queryParams, Class<T[]> arrayType) {
        String uri = buildUriWithQueryParams(path, queryParams);
        logRequest("GET", uri);
        try {
            T[] body = restClient.get()
                    .uri(uri)
                    .retrieve()
                    .body(arrayType);
            return RestResponse.ok(body == null ? List.of() : List.of(body));
        } catch (Exception ex) {
            log.warn("REST GET ERROR service={} path={} error={}", serviceName, uri, ex.getMessage(), ex);
            throw RestClientExceptionMapper.map(
                    serviceName,
                    uri,
                    ex);
        }
    }


    // ================================================
    // POST
    // ================================================

    public RestResponse<String> post(String path, Object body) {
        return post(path, body, Map.of());
    }

    public RestResponse<String> post(String path, Object body, Map<String, String> extraHeaders) {
        logRequest("POST", path);
        log.info("REST POST body={}", body);
        try {
            String response = restClient.post()
                    .uri(path)
                    .contentType(MediaType.APPLICATION_JSON)
                    .headers(h -> extraHeaders.forEach(h::add))
                    .body(body)
                    .retrieve()
                    .body(String.class);

            return RestResponse.ok(response);

        } catch (Exception ex) {
            log.warn("REST POST ERROR service={} path={} error={}", serviceName, path, ex.getMessage());
            throw RestClientExceptionMapper.map(
                    serviceName,
                    path,
                    ex);
        }
    }

    public <T> RestResponse<T> post(String path, Object body, Class<T> responseType) {
        return post(path, body, Map.of(), responseType);
    }

    public <T> RestResponse<T> post(String path,
                                    Object body,
                                    Map<String, String> extraHeaders,
                                    Class<T> responseType) {
        logRequest("POST", path);
        log.info("REST POST body={}", body);
        try {
            T response = restClient.post()
                    .uri(path)
                    .contentType(MediaType.APPLICATION_JSON)
                    .headers(h -> extraHeaders.forEach(h::add))
                    .body(body)
                    .retrieve()
                    .body(responseType);

            return RestResponse.ok(response);

        } catch (Exception ex) {
            log.warn("REST POST ERROR service={} path={} error={}",
                    serviceName, path, ex.getMessage());
            throw RestClientExceptionMapper.map(
                    serviceName,
                    path,
                    ex);
        }
    }

    // ================================================
    // PUT
    // ================================================

    public RestResponse<String> put(String path, Object body) {
        return put(path, body, Map.of());
    }

    public RestResponse<String> put(String path,
                                    Object body,
                                    Map<String, String> extraHeaders) {
        logRequest("PUT", path);
        try {
            String response = restClient.put()
                    .uri(path)
                    .contentType(MediaType.APPLICATION_JSON)
                    .headers(h -> extraHeaders.forEach(h::add))
                    .body(body)
                    .retrieve()
                    .body(String.class);

            return RestResponse.ok(response);

        } catch (Exception ex) {
            log.warn("REST PUT ERROR service={} path={} error={}",
                    serviceName, path, ex.getMessage());
            throw RestClientExceptionMapper.map(
                    serviceName,
                    path,
                    ex);
        }
    }

    // ================================================
    // DELETE
    // ================================================

    public RestResponse<Void> delete(String path) {
        return delete(path, Map.of());
    }

    public RestResponse<Void> delete(String path, Map<String, String> extraHeaders) {
        logRequest("DELETE", path);
        try {
            restClient.delete()
                    .uri(path)
                    .headers(h -> extraHeaders.forEach(h::add))
                    .retrieve()
                    .toBodilessEntity();

            return RestResponse.ok(null);

        } catch (Exception ex) {
            log.warn("REST DELETE ERROR service={} path={} error={}",
                    serviceName, path, ex.getMessage());
            throw RestClientExceptionMapper.map(
                    serviceName,
                    path,
                    ex);
        }
    }

    // ================================================
    // PRIVATE
    // ================================================

    private void logRequest(String method, String path) {
        if (config.logRequests()) {
            log.info("REST {} service={} path={}",
                    method, serviceName, path);
        } else {
            log.debug("REST {} service={} path={}",
                    method, serviceName, path);
        }
    }

    private String buildUriWithQueryParams(String path, Map<String, Object> queryParams) {
        MultiValueMap<String, String> multiValueParams = new LinkedMultiValueMap<>();
        queryParams.forEach((key, value) -> multiValueParams.add(key, String.valueOf(value)));
        return UriComponentsBuilder.fromPath(path)
                .queryParams(multiValueParams)
                .build()
                .toUriString();
    }

}