package io.keelframework.mcp.transport.session.config;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;


/**
 * Propiedades configurables del servicio de sesión de transporte MCP.
 *
 * Ejemplo de configuración en el proyecto MCP Server:
 *
 * keel:
 *   transport:
 *     session:
 *       max-sessions: 500
 *       log-events:   false
 */

@ConfigurationProperties(prefix = "keel.mcp.transport.session")
public record SessionProperties(

    /**
     * Máximo de sesiones simultáneas permitidas.
     * Protege contra sobrecarga del servidor MCP.
     * Default: 500
     */
    int maxSessions,

    /**
     * Session inactivity timeout.
     * Default: 30 minutes.
     */
    Duration timeout,

    /**
     * Activa logging detallado de eventos de sesión.
     * Solo para desarrollo — desactivar en producción.
     * Default: false
     */
    boolean logEvents
){
    public SessionProperties {
        if (maxSessions == 0){
            maxSessions = 500;
        }
        if (timeout == null) {
            timeout = Duration.ofMinutes(30);
        }
    }
}
