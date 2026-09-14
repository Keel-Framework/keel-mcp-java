package io.keelframework.mcp.adapters.rest.config;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * Propiedades del pool de conexiones HTTP.
 *
 * Estandar de arquitectura SCA — architecture.httpclient.*
 *
 * architecture:
 *   mcp:
 *   httpclient:
 *     socket-timeout:            60000
 *     connect-timeout:           60000
 *     request-timeout:           60000
 *     max-total-connections:     200
 *     max-connections-per-route: 20
 *     keep-alive:                10000
 */
@ConfigurationProperties(prefix = "keel.mcp.http-client")
public record HttpClientProperties(


    @DefaultValue("60000") int connectTimeout,
    @DefaultValue("60000") int socketTimeout,
    @DefaultValue("60000") int requestTimeout,
    @DefaultValue("200") int maxTotalConnections,
    @DefaultValue("20") int maxConnectionsPerRoute,
    @DefaultValue("300000") long connectionTimeToLive,
    @DefaultValue("10000") long keepAlive

) {}
