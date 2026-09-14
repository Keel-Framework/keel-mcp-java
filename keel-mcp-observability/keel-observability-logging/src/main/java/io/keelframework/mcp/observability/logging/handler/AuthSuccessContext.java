package io.keelframework.mcp.observability.logging.handler;

import java.time.Instant;
import java.util.List;

/**
 * Contexto de datos para una autenticación JWT exitosa — agrupa los campos
 * de auditoría en un solo objeto para evitar firmas de método con muchos
 * parámetros
 *
 * Uso desde McpAuthenticationFilter:
 *
 *   AuthSuccessContext ctx = AuthSuccessContext.builder()
 *           .subject(token.subject())
 *           .issuer(token.issuer())
 *           .audience(token.audience())
 *           .expiresAt(token.expiresAt())
 *           .sessionId(request.getHeader("Mcp-Session-Id"))
 *           .clientId(clientId)
 *           .uri(request.getRequestURI())
 *           .method(request.getMethod())
 *           .remoteAddr(request.getRemoteAddr())
 *           .userAgent(request.getHeader("User-Agent"))
 *           .build();
 *
 *   authLoggingHandler.logAuthSuccess(ctx);
 */
public record AuthSuccessContext(
        String subject,
        String issuer,
        List<String> audience,
        Instant expiresAt,
        String sessionId,
        String clientId
) {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String subject;
        private String issuer;
        private List<String> audience;
        private Instant expiresAt;
        private String sessionId;
        private String clientId;

        public Builder subject(String v)    { this.subject = v; return this; }
        public Builder issuer(String v)     { this.issuer = v; return this; }
        public Builder audience(List<String> v)   { this.audience = v; return this; }
        public Builder expiresAt(Instant v) { this.expiresAt = v; return this; }
        public Builder sessionId(String v)  { this.sessionId = v; return this; }
        public Builder clientId(String v)   { this.clientId = v; return this; }

        public AuthSuccessContext build() {
            return new AuthSuccessContext(
                    subject, issuer, audience, expiresAt,
                    sessionId, clientId);
        }
    }
}