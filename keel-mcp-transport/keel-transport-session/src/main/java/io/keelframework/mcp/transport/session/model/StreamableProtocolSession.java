/*
 * Copyright 2026 Keel Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
