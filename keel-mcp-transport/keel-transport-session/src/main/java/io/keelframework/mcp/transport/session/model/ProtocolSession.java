package io.keelframework.mcp.transport.session.model;

import java.time.Instant;

/**
 * Contrato común para cualquier sesión de transporte MCP.
*/

public sealed interface ProtocolSession
        permits
        StreamableProtocolSession {

    String sessionId();
    String clientId();
    String jwt();
    Instant connectedAt();
    Instant lastActivityAt();
    SessionStatus status();
    TransportType transportType();

    ProtocolSession renewActivity();
    ProtocolSession terminate();

    enum SessionStatus {
        ACTIVE,
        INACTIVE,
        TERMINATED
    }

    enum TransportType {
        STREAMABLE,
        WEBSOCKET,
        STDIO
    }
}
