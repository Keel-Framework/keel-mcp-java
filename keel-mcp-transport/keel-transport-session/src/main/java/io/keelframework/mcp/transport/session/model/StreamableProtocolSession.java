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
     * Creates a new active Streamable session.
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
     * Updates the JWT while keeping the remaining fields immutable.
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
     * Renews activity and updates the JWT in a single operation.
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
     * Renews session activity.
     * Returns a new record with the updated lastActivityAt.
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
     * Terminates the session.
     * Returns a new record with status TERMINATED.
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
