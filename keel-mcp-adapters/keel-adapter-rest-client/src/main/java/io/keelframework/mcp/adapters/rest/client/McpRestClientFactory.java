package io.keelframework.mcp.adapters.rest.client;

import io.keelframework.mcp.adapters.rest.config.HttpClientProperties;
import io.keelframework.mcp.adapters.rest.config.RestClientProperties;
import io.keelframework.mcp.adapters.rest.config.SslProperties;
import io.keelframework.mcp.adapters.rest.interceptor.McpHeadersInterceptor;
import io.keelframework.mcp.common.jwt.provider.JwtProvider;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import javax.net.ssl.SSLContext;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Factoría de McpRestClient por servicio.
 *
 * Configura cada cliente con:
 *
 * 1. Apache HttpClient 5 con pool de conexiones
 *    (architecture.httpclient.*)
 *    - socketTimeout, connectTimeout, requestTimeout
 *    - maxTotalConnections, maxConnectionsPerRoute
 *    - keepAlive strategy
 *
 * 2. SSL/TLS corporativo
 *    (server.ssl.*)
 *    - trustStore, trustStorePassword, trustStoreType
 *
 * 3. McpHeadersInterceptor — automatico e invisible al desarrollador
 *    - Authorization: Bearer <JWT>   ← SIEMPRE
 *    - channel, applicationid, traceid, spanid, x-adeslas-device
 */
public final class McpRestClientFactory {

    private static final Logger log =
            LoggerFactory.getLogger(McpRestClientFactory.class);

    private final JwtProvider jwtProvider;

    private final RestClientProperties restProperties;
    private final HttpClientProperties httpProperties;
    private final SslProperties sslProperties;

    private final Map<String, McpRestClient> clients = new ConcurrentHashMap<>();

    private final HttpClient httpClient;

    private final ResourceLoader resourceLoader;

    public McpRestClientFactory(RestClientProperties restProperties,
                                HttpClientProperties httpProperties,
                                SslProperties sslProperties,
                                ResourceLoader resourceLoader,
                                JwtProvider jwtProvider) {
        this.restProperties = restProperties;
        this.httpProperties = httpProperties;
        this.sslProperties = sslProperties;
        this.resourceLoader = resourceLoader;
        this.httpClient = buildHttpClient();
        this.jwtProvider = jwtProvider;

        log.info("REST CLIENT FACTORY initialized " +
                        "socketTimeout={}ms connectTimeout={}ms " +
                        "requestTimeout={}ms maxTotal={} " +
                        "maxPerRoute={} keepAlive={}ms ssl={}",
                httpProperties.socketTimeout(),
                httpProperties.connectTimeout(),
                httpProperties.requestTimeout(),
                httpProperties.maxTotalConnections(),
                httpProperties.maxConnectionsPerRoute(),
                httpProperties.keepAlive(),
                sslProperties.enabled() ? "enabled" : "disabled");
    }

    /**
     * Obtiene o crea el McpRestClient para el servicio dado.
     *
     * @param serviceName nombre configurado en sca.mcp.adapters.rest.services
     */
    public McpRestClient getClient(String serviceName) {
        return clients.computeIfAbsent(serviceName, this::createClient);
    }

    // ================================================
    // PRIVADO
    // ================================================
    private McpRestClient createClient(String serviceName) {
        RestClientProperties.ServiceConfig config =
                restProperties.services().get(serviceName);

        if (config == null){
            throw  new IllegalArgumentException( "Service '%s' not configured in " +
                    "sca.adapters.rest.services".formatted(serviceName));
        }

        HttpComponentsClientHttpRequestFactory requestFactory =
                new HttpComponentsClientHttpRequestFactory(httpClient);
        requestFactory.setConnectionRequestTimeout(
                httpProperties.requestTimeout()
        );

        // RestClient con McpHeadersInterceptor
        RestClient restClient = RestClient.builder()
                .baseUrl(config.baseUrl())
                .requestFactory(requestFactory)
                .requestInterceptor(new McpHeadersInterceptor(jwtProvider))
                .build();

        log.info("REST CLIENT CREATED service={} baseUrl={}",
                serviceName, config.baseUrl());

        return  new McpRestClient(restClient, serviceName, config);
    }

    /**
     * Construye el HttpClient de Apache con pool de conexiones.
     */
    private HttpClient buildHttpClient(){
        try{
            SSLContext sslContext = buildSslContext();

            // ConnectionConfig — idle timeout + TTL
            ConnectionConfig connectionConfig = ConnectionConfig.custom()
                    .setConnectTimeout( //connect timeout
                            httpProperties.connectTimeout(),
                            TimeUnit.MILLISECONDS)
                    .setSocketTimeout(  // read/socket timeout
                            httpProperties.socketTimeout(),
                            TimeUnit.MILLISECONDS)
                    .setValidateAfterInactivity(  // revalida idle
                            httpProperties.keepAlive(),
                            TimeUnit.MILLISECONDS)
                    .setTimeToLive(//TTL maximo
                            5, TimeUnit.MINUTES)
                    .build();

            // Pool de conexiones
            HttpClientConnectionManager connectionManager =
                    PoolingHttpClientConnectionManagerBuilder.create()
                            .setSSLSocketFactory(
                                    SSLConnectionSocketFactoryBuilder.create()
                                            .setSslContext(sslContext)
                                            .build())
                            .setDefaultConnectionConfig(connectionConfig)
                            .setMaxConnTotal(httpProperties.maxTotalConnections())
                            .setMaxConnPerRoute(httpProperties.maxConnectionsPerRoute())
                            .build();

            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectionRequestTimeout(
                            Timeout.ofMilliseconds(httpProperties.requestTimeout()))
                    .build();

            return HttpClients.custom()
                    .setConnectionManager(connectionManager)
                    .evictExpiredConnections()
                    .evictIdleConnections(
                            TimeValue.of(httpProperties.keepAlive(),
                                    TimeUnit.MILLISECONDS)
                    )
                    .setDefaultRequestConfig(requestConfig)
                    .setKeepAliveStrategy((response, context) ->
                            TimeValue.ofMilliseconds(httpProperties.keepAlive()))
                    .build();


        } catch (Exception ex) {
            log.error("HTTP CLIENT BUILD ERROR: {}", ex.getMessage());
            throw new RuntimeException(
                    "Failed to build HttpClient: " + ex.getMessage(), ex);
        }
    }

    /**
     * Construye el SSLContext segun server.ssl.* properties.
     */
    private SSLContext buildSslContext() throws Exception {

        if (!sslProperties.enabled()){
            log.debug("SSL disabled — using JVM default truststore");
            return SSLContext.getDefault();
        }

        if (sslProperties.trustStore() == null
                || sslProperties.trustStore().isBlank()){
            throw new IllegalStateException("Truststore path is required when SSL is enabled");
        }

        try {

            log.info("SSL TRUSTSTORE loading path={} type={}",
                    sslProperties.trustStore(),
                    sslProperties.trustStoreType());

            Resource resource = resolveResource(sslProperties.trustStore());

            if (!resource.exists()) {
                throw new IllegalStateException(
                        "Truststore not found: " + sslProperties.trustStore());
            }

            KeyStore trustStore = KeyStore.getInstance(
                    sslProperties.trustStoreType());

            char[] pwd = Optional.ofNullable(sslProperties.trustStorePassword())
                    .filter(p -> !p.isBlank())
                    .orElseThrow(() -> new IllegalStateException("Truststore password required"))
                    .toCharArray();

            try (InputStream is = resource.getInputStream()) {
                trustStore.load(is, pwd);
            }
            return SSLContextBuilder.create()
                    .loadTrustMaterial(trustStore, null)
                    .build();
        } catch (Exception ex) {
            throw new IllegalStateException("Error building SSLContext", ex);
        }
    }

    private Resource resolveResource(String path) {
        if (path == null || path.isBlank()) return null;

        // Ya tiene prefijo — usa directamente
        if (path.startsWith("classpath:")
                || path.startsWith("file:")
                || path.startsWith("http:")
                || path.startsWith("https:")) {
            return resourceLoader.getResource(path);
        }

        // Ruta absoluta — añade file: automáticamente
        if (path.startsWith("/") || path.matches("^[A-Za-z]:\\\\.*")) {
            return resourceLoader.getResource("file:" + path);
        }

        // Ruta relativa — asume classpath
        return resourceLoader.getResource("classpath:" + path);
    }



}
