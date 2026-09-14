package io.keelframework.mcp.transport.session.model;

import java.time.Instant;

public record StreamableProtocolSession(
        String sessionId,
        String clientId,
        String jwt,
        Instant connectedAt,
        Instant lastActivityAt,
        Instant lastRequestAt,
        SessionStatus status
) implements ProtocolSession {

    /**
     * Crea una nueva sesión Streamable activa.
     */
    public static StreamableProtocolSession create(
            String sessionId,
            String clientId,
            String jwt){
        Instant now = Instant.now();
        return new StreamableProtocolSession(
                sessionId,
                clientId,
                jwt,
                now,
                now,
                now,
                SessionStatus.ACTIVE
        );
    }

    /**
     * Actualiza el JWT manteniendo el resto de campos inmutables.
     */
    public StreamableProtocolSession withJwt(String newJwt) {
        return new StreamableProtocolSession(
                sessionId,
                clientId,
                newJwt,
                connectedAt,
                lastActivityAt,
                lastRequestAt,
                status
        );
    }
    /**
     * Renueva actividad y actualiza JWT en una sola operación.
     */
    public StreamableProtocolSession renewActivityAndJwt(String newJwt) {
        Instant now = Instant.now();
        return new StreamableProtocolSession(
                sessionId,
                clientId,
                newJwt,
                connectedAt,
                now,
                now,
                status
        );
    }


    @Override
    public TransportType transportType() {
        return TransportType.STREAMABLE;
    }

    /**
     * Renueva la actividad de la sesión.
     * Retorna un nuevo record con lastActivityAt actualizado.
     */
    @Override
    public ProtocolSession renewActivity() {
        return new StreamableProtocolSession(
                sessionId,
                clientId,
                jwt,
                connectedAt,
                Instant.now(),
                Instant.now(),
                status
        );
    }

    /**
     * Termina la sesión.
     * Retorna un nuevo record con status TERMINATED.
     */
    @Override
    public ProtocolSession terminate() {
        return new StreamableProtocolSession(
                sessionId,
                clientId,
                jwt,
                connectedAt,
                Instant.now(),
                Instant.now(),
                SessionStatus.TERMINATED
        );
    }
}
