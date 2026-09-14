package io.keelframework.mcp.observability.logging.handler;

/**
 * Contexto de datos para una autenticación JWT fallida.
 * Solo contiene el motivo del fallo — remoteAddr, userAgent, method y uri
 * se leen automáticamente desde McpLogContext en emit().
 */
public record AuthFailureContext(
        String reason
) {
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String reason;

        public Builder reason(String v) { this.reason = v; return this; }

        public AuthFailureContext build() {
            return new AuthFailureContext(reason);
        }
    }
}
