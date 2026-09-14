package io.keelframework.mcp.transport.streamable.config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import java.time.Duration;

/**
 * Configurable properties for the Streamable HTTP transport.
 * Configuration in the MCP Server project's application.yml:
 *
 * keel:
 *   transport:
 *     streamable:
 *       endpoint:         /mcp
 *       keep-alive:
 *         enabled:  true
 *         interval: 30s
 */
@ConfigurationProperties(prefix = "keel.mcp.transport.streamable")
public record StreamableProperties(

    /**
     * Streamable HTTP endpoint of the MCP server.
     * Handles POST, GET and DELETE requests.
     * Default: /mcp
     */
    String endpoint,

    /**
     * Keep-alive configuration for streaming.
     */
    KeepAlive keepAlive

) {
    public StreamableProperties {
        if (endpoint == null || endpoint.isBlank()) {
            endpoint = "/mcp";
        }
        if (keepAlive == null) {
            keepAlive = new KeepAlive(true, Duration.ofSeconds(30));
        }

    }
    /**
     * Configuración del keep-alive SSE para streaming.
     */
    public record KeepAlive(
            boolean  enabled,
            Duration interval
    ) {
        public KeepAlive {
            if (interval == null) interval = Duration.ofSeconds(30);
        }
    }

}
