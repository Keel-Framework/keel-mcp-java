package io.keelframework.mcp.adapters.rest.client;

import io.keelframework.mcp.adapters.rest.config.RestClientProperties;
import io.keelframework.mcp.adapters.rest.exception.RestClientExceptionMapper;
import io.keelframework.mcp.adapters.rest.model.RestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import java.util.Map;


/**
 * Cliente HTTP base para tools MCP.
 *
 * Responsabilidad unica: ejecutar peticiones HTTP
 * GET / POST / PUT / DELETE al servicio backend.
 *
 * Los headers estandar SCA (JWT, propagacion) son
 * gestionados automaticamente por McpHeadersInterceptor
 * registrado en el RestClient — transparente para el developer.
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
     * REQUEST - TIPO::  GET
     * get(path) → String, sin headers
     * get(path, extraHeaders) → String, con headers
     * get(path, responseType) → T, sin headers
     * get(path, extraHeaders, responseType) → T, con headers
     * get(path, responseType, uriVariables...) → T, con path variables
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


    // ================================================
    // POST
    // ================================================

    public RestResponse<String> post(String path, Object body) {
        return post(path, body, Map.of());
    }

    public RestResponse<String> post(String path, Object body, Map<String, String> extraHeaders) {
        logRequest("POST", path);
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
    // PRIVADO
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


}
