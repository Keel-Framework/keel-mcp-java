package io.keelframework.mcp.transport.streamable.interceptor;

import io.keelframework.mcp.observability.logging.handler.McpAuthLoggingHandler;
import io.keelframework.mcp.observability.logging.mdc.McpMdcPopulator;
import io.keelframework.mcp.observability.logging.model.McpLogEntry;
import io.keelframework.mcp.transport.auth.filter.ClientIdExtractor;
import io.keelframework.mcp.transport.auth.filter.McpAuthenticationToken;
import io.keelframework.mcp.transport.session.manager.SessionManagerPort;
import io.keelframework.mcp.transport.session.model.StreamableProtocolSession;
import io.keelframework.mcp.transport.streamable.registry.StreamableSessionRegistry;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * MCP session interceptor — executes BEFORE the Spring AI MCP handler.
 *
 * Flow in preHandle():
 *   1. Extracts clientId from the SecurityContext
 *      (McpAuthenticationFilter has already authenticated the request)
 *   2. Retrieves an existing session or creates a new one
 *   3. Validates that the session is active
 *   4. Renews session activity in Caffeine
 *   5. Adds Mcp-Session-Id to the response header
 *
 * If it returns false → Spring AI MCP does not process the request.
 * If it returns true  → Spring AI MCP processes the JSON-RPC request directly.
 */

public class McpSessionInterceptor implements HandlerInterceptor {

    private static final Logger log =
            LoggerFactory.getLogger(McpSessionInterceptor.class);

    private static final String SESSION_HEADER = "Mcp-Session-Id";

    private final SessionManagerPort sessionManager;
    private final StreamableSessionRegistry   registry;
    private final McpAuthLoggingHandler logMcp;   // ← inyecta

    private final ThreadLocal<String> clientIdHolder = new ThreadLocal<>();
    private final ThreadLocal<String> jwtHolder = new ThreadLocal<>();

    public McpSessionInterceptor(SessionManagerPort sessionManager, StreamableSessionRegistry registry, McpAuthLoggingHandler logMcp) {
        this.sessionManager = sessionManager;
        this.registry = registry;
        this.logMcp = logMcp;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        // 1. Extract clientId from the SecurityContext.
        // McpAuthenticationFilter has already validated the token.
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        String clientId = ClientIdExtractor.extract(authentication);
        McpMdcPopulator.putClientId(clientId);
        String jwt = extractRawJwt(authentication);
        McpMdcPopulator.putClientId(clientId);

        // 2. Check for an existing MCP session.
        String sessionId = request.getHeader(SESSION_HEADER);
        if (sessionId != null && !sessionId.isBlank()) {
            // Existing session
            if (!registry.exists(sessionId)) {

                // Security log — session not found
                logMcp.security(McpLogEntry.builder()
                        .level("WARN")
                        .component(McpAuthLoggingHandler.COMPONENT_SESSION)
                        .sessionId(sessionId)
                        .mcpOperation("session/invalid")
                        .authResult(McpAuthLoggingHandler.RESULT_FAILURE)
                        .authFailReason("Session not found")
                        .errorCode("SESSION_001"));

                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"Session not found\"}");
                return false;
            }
            // Validate the session and renew its activity.
            try {
                sessionManager.validate(sessionId,jwt);
            } catch (Exception ex) {
                // Security log — invalid session
                logMcp.security(McpLogEntry.builder()
                        .level("WARN")
                        .component(McpAuthLoggingHandler.COMPONENT_SESSION)
                        .sessionId(sessionId)
                        .mcpOperation("session/invalid")
                        .authResult(McpAuthLoggingHandler.RESULT_FAILURE)
                        .authFailReason(ex.getMessage())
                        .errorCode(logMcp.resolveErrorCode(ex.getMessage())));


                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"Session invalid\"}");
                return false;
            }
            log.debug("SESSION OK sessionId={}... clientId={}", sessionId.substring(0, 8), clientId);
        } else {
            // New session.
            // Do not create the session here.
            // Spring AI generates the Mcp-Session-Id.
            // The session is registered in afterCompletion()
            // using the identifier generated by Spring AI.
            if (registry.isOverloaded()) {
                logMcp.technical(McpLogEntry.builder()
                        .level("WARN")
                        .component(McpAuthLoggingHandler.COMPONENT_SESSION)
                        .clientId(clientId)
                        .httpStatus(503)
                        .errorCode("SESSION_002")
                        .errorMessage("Max sessions reached"));

                response.setStatus(HttpStatus.SERVICE_UNAVAILABLE.value());
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"Max sessions reached\"}");
                return false;
            }
            // Store clientId and JWT for afterCompletion().
            clientIdHolder.set(clientId);
            jwtHolder.set(jwt);
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) {

        String clientId = clientIdHolder.get();
        String jwt = jwtHolder.get();

        // Prevent ThreadLocal memory leaks.
        clientIdHolder.remove();
        jwtHolder.remove();

        // Do not register a session when the request failed.
        if (ex != null || response.getStatus() >= 400) {
            log.debug(
                    "MCP request failed — session will not be registered status={}",
                    response.getStatus()
            );
            return;
        }

        // Existing session — nothing to register.
        if (clientId == null) return;

        // Read the Mcp-Session-Id generated by Spring AI.
        String mcpSessionId = response.getHeader(SESSION_HEADER);
        if (mcpSessionId == null || mcpSessionId.isBlank()) {
            log.warn("Spring AI did not set Mcp-Session-Id — session not registered");
            return;
        }

        // Register the session using the identifier generated by Spring AI.
        StreamableProtocolSession session =
                StreamableProtocolSession.create(mcpSessionId, clientId, jwt);
        boolean registered = registry.register(session);

        if (registered) {
            // Technical log — session registered.
            logMcp.technical(McpLogEntry.builder()
                    .level("INFO")
                    .component(McpAuthLoggingHandler.COMPONENT_SESSION)
                    .sessionId(mcpSessionId)
                    .clientId(clientId)
                    .httpStatus(response.getStatus())
                    .mcpOperation("session/registered"));
        }
    }

    private String extractRawJwt(Authentication authentication) {
        if (authentication instanceof McpAuthenticationToken mcpAuth) {
            return (String) mcpAuth.getCredentials();
        }
        return null;
    }
}
