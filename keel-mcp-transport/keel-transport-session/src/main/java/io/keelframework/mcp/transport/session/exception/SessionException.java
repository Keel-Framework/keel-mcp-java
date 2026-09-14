package io.keelframework.mcp.transport.session.exception;

/**
 * Excepción base del servicio de sesión de transporte MCP.
 *
 * Jerarquía:
 * TransportSessionException
 * ├── SessionNotFoundException     → sesión no existe o expiró
 * ├── SessionExpiredException      → sesión TTL expirado
 * └── SessionCreationException     → error al crear sesión
 */
public sealed class SessionException extends RuntimeException
        permits SessionException.SessionNotFoundException,
        SessionException.SessionExpiredException,
        SessionException.SessionCreationException {

    private final String sessionId;

    protected SessionException(String message, String sessionId) {
        super("%s [sessionId=%s]".formatted(message, sessionId));
        this.sessionId = sessionId;
    }

    protected SessionException(String message,
                               String sessionId,
                               Throwable cause) {
        super("%s [sessionId=%s]".formatted(message, sessionId), cause);
        this.sessionId = sessionId;
    }

    public String sessionId() {
        return sessionId;
    }

    public static final class SessionNotFoundException
            extends SessionException {

        public SessionNotFoundException(String sessionId) {
            super("Session not found or expired", sessionId);
        }
    }

    public static final class SessionExpiredException
            extends SessionException {

        public SessionExpiredException(String sessionId) {
            super("Session TTL expired", sessionId);
        }
    }

    public static final class SessionCreationException
            extends SessionException {

        public SessionCreationException(String sessionId, Throwable cause) {
            super("Error creating session", sessionId, cause);
        }
    }
}

