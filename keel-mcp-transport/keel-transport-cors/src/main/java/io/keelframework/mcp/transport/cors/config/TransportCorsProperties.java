package io.keelframework.mcp.transport.cors.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * Propiedades de configuración CORS para el endpoint MCP.
 *
 * <pre>
 * keel:
 *   mcp:
 *     transport:
 *       cors:
 *         enabled: true
 *         path-pattern: /mcp/**
 *         allowed-origins:
 *           - https://mcp-inspector-route-domain-demo.apps.example.cloud
 *         allowed-methods: [GET, POST, OPTIONS]
 *         allowed-headers: ["*"]
 *         exposed-headers: [Mcp-Session-Id]
 *         allow-credentials: true
 *         max-age: 3600
 * </pre>
 */
@Data
@ConfigurationProperties(prefix = "keel.mcp.transport.cors")
public class TransportCorsProperties {

    /** Activa o desactiva la configuración CORS para el endpoint MCP. Default: false */
    private boolean enabled = false;

    /** Patrón de ruta al que se aplica CORS. Default: /mcp/** */
    private String pathPattern = "/mcp/**";

    /**
     * Orígenes permitidos (protocolo + host + puerto, sin path).
     * Soporta patrones (ej. "https://*.keel.es") vía allowedOriginPatterns.
     * Obligatorio si enabled = true.
     */
    private List<String> allowedOrigins = List.of();

    /** Métodos HTTP permitidos. Default: GET, POST, OPTIONS */
    private List<String> allowedMethods = List.of("GET", "POST", "OPTIONS");

    /** Headers de request permitidos. Default: todos ("*") */
    private List<String> allowedHeaders = List.of("*");

    /**
     * Headers de response expuestos al JavaScript del navegador.
     * IMPORTANTE: debe incluir "Mcp-Session-Id", o el cliente MCP en el
     * navegador no podrá leer el session-id devuelto en el initialize.
     */
    private List<String> exposedHeaders = List.of("Mcp-Session-Id");

    /**
     * Permite el envío de credenciales (cookies, Authorization header).
     * Si es true, allowedOrigins NO puede contener "*" literal — usar
     * orígenes explícitos o patrones.
     */
    private boolean allowCredentials = true;

    /** Tiempo en segundos que el navegador cachea la respuesta del preflight. Default: 3600 */
    private long maxAge = 3600;
}
