package io.keelframework.mcp.observability.logging.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import io.keelframework.mcp.observability.logging.model.McpLogType;
/**
 * Propiedades de observabilidad MCP.
 *
 * Configuración externa via application.yml o variables de entorno:
 *
 * sca:
 *   mcp:
 *     observability:
 *       enabled:           ${MCP_OBSERVABILITY_ENABLED:true}
 *       technical:         ${MCP_OBSERVABILITY_TECHNICAL:true}
 *       functional:        ${MCP_OBSERVABILITY_FUNCTIONAL:true}
 *       security:          ${MCP_OBSERVABILITY_SECURITY:true}
 */
@ConfigurationProperties(prefix = "sca.mcp.observability")
public record LogProperties(

        /** Activa/desactiva todas las trazas. Default: true */
        @DefaultValue("true")
        boolean enabled,

        /** Activa trazas TECHNICAL. Default: true */
        @DefaultValue("true")
        boolean technical,

        /** Activa trazas FUNCTIONAL. Default: true */
        @DefaultValue("true")
        boolean functional,

        /** Activa trazas SECURITY. Default: true */
        @DefaultValue("true")
        boolean security
) {
    /**
     * Verifica si un tipo de traza está activo.
     */
    public boolean isActive(McpLogType type) {
        if (!enabled) return false;
        return switch (type) {
            case TECHNICAL  -> technical;
            case FUNCTIONAL -> functional;
            case SECURITY   -> security;
        };
    }
}
