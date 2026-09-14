package io.keelframework.mcp.transport.auth.filter;

import io.keelframework.mcp.common.jwt.model.Jwt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

/**
 * Extrae el clientId (subject) del Authentication del SecurityContext.
 *
 * Uso en StreamableController:
 *   Authentication auth = SecurityContextHolder.getContext().getAuthentication();
 *   String clientId = ClientIdExtractor.extract(auth);
 *
 * El controller no necesita conocer McpAuthenticationToken ni Jwt directamente.
 */

public class ClientIdExtractor {

    private static final Logger log = LoggerFactory.getLogger(ClientIdExtractor.class);

    private static final String ANONYMOUS = "anonymous";


    public ClientIdExtractor() {
    }

    /**
     * Extrae el subject del JWT como clientId.
     *
     * @param authentication Authentication del SecurityContextHolder
     * @return subject del JWT, o "anonymous" si no hay autenticación válida
     */
    public static String extract(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            log.debug("No authenticated principal found — returning anonymous");
            return ANONYMOUS;
        }

        if (authentication instanceof McpAuthenticationToken mcpToken) {
            Jwt jwt = mcpToken.getPrincipal();
            String clientId = jwt.subject();
            log.debug("Extracted clientId: {}", clientId);
            return clientId;
        }

        // Fallback — otros tipos de Authentication (tests, actuator, etc.)
        String name = authentication.getName();
        log.debug("Non-MCP authentication — using name as clientId: {}", name);
        return name != null ? name : ANONYMOUS;
    }

    /**
     * Extrae el Jwt completo del Authentication.
     * Útil cuando el controller necesita más que el subject (roles, scopes, etc.)
     *
     * @return Jwt completo, o null si no hay McpAuthenticationToken
     */
    public static Jwt extractJwt(Authentication authentication) {
        if (authentication instanceof McpAuthenticationToken mcpToken) {
            return mcpToken.getPrincipal();
        }
        return null;
    }


}
