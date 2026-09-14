package io.keelframework.mcp.transport.streamable.provider;
import io.keelframework.mcp.transport.auth.filter.McpAuthenticationToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.keelframework.mcp.common.jwt.provider.JwtProvider;
import io.keelframework.mcp.transport.streamable.registry.StreamableSessionRegistry;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;


/**
 * Retrieves the JWT from the current context.
 * First attempts to retrieve it from the MCP session,
 * then falls back to the SecurityContext.
 */
public class McpJwtProvider implements JwtProvider {

    private static final Logger log =
            LoggerFactory.getLogger(McpJwtProvider.class);

    private final StreamableSessionRegistry registry;
    private final HttpServletRequest request;

    public McpJwtProvider(StreamableSessionRegistry registry,
                          HttpServletRequest request) {
        this.registry = registry;
        this.request = request;
    }

    @Override
    public String getJwt() {
        // 1. Intenta desde sesión MCP
        String sessionId = request.getHeader("Mcp-Session-Id");
        if (sessionId != null && !sessionId.isBlank()) {
            String jwt = registry.getJwt(sessionId);
            if (jwt != null) {
                log.debug("JWT from session sessionId={}...", sessionId.substring(0, 8));
                return jwt;
            }
        }

        // 2. Fallback — SecurityContext
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof McpAuthenticationToken mcpAuth) {
            log.debug("JWT from SecurityContext");
            return (String) mcpAuth.getCredentials();
        }

        log.warn("JWT not found");
        return null;
    }
}
