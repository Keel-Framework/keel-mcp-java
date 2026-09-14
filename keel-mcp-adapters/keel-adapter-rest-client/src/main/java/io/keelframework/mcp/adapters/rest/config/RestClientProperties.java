package io.keelframework.mcp.adapters.rest.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * Propiedades de servicios backend para tools MCP.
 *
 * El desarrollador solo configura las URLs de sus servicios.
 * Los headers de propagacion son internos del ecosistema SCA.
 *
 * keel:
 *   mcp:
 *     adapters:
 *     rest-client:
 *       services:
 *         pagos:
 *           base-url:     https://api.pagos.sca.es
 *           log-requests: true
 *         clientes:
 *           base-url:     https://api.clientes.sca.es
 */

@ConfigurationProperties(prefix = "keel.mcp.adapters.rest-client")
public record RestClientProperties(
    /*
     * Mapa de servicios backend Microservice.
     * Clave = nombre del servicio usado en McpRestClientFactory.getClient()
     */
    Map<String, ServiceConfig> services
){
    public RestClientProperties{
        if (services == null)
            services = Map.of();
    }
    public record ServiceConfig(
            /* URL base del service */
            String baseUrl,
            /* Activa logging detallado de requests. Default: false */
            boolean logRequests
    ){}
}
