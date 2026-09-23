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
package io.keelframework.mcp.transport.session.manager;

import io.keelframework.mcp.transport.session.config.SessionProperties;
import io.keelframework.mcp.transport.session.exception.SessionException;
import io.keelframework.mcp.transport.session.model.ProtocolSession;
import io.keelframework.mcp.transport.session.model.StreamableProtocolSession;
import io.keelframework.mcp.transport.session.repository.SessionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Optional;

/**
 * Gestor del ciclo de vida de sesiones de transporte MCP.
 *
 * Responsabilidades:
 * - Validar sesión en cada invocación de tool
 * - Renovar TTL con cada actividad
 * - Destruir sesión en desconexión
 *
 * Agnóstico del protocolo via métodos de fábrica específicos por tipo.
 *
 * La persistencia delega en TransportSessionRepository
 * que usa CacheServiceManager de cache-service.jar.
 */
public final class SessionManager
        implements SessionManagerPort {

    private static final Logger log =
            LoggerFactory.getLogger(SessionManager.class);

    private final SessionRepository repository;
    private final SessionProperties properties;

    public SessionManager(SessionRepository repository,
                          SessionProperties properties) {
        this.repository = repository;
        this.properties = properties;
    }

    @Override
    public ProtocolSession validate(String sessionId, String jwt) {
        ProtocolSession session = repository.findById(sessionId)
                .orElseThrow(() ->
                        new SessionException
                                .SessionNotFoundException(sessionId));

        if (session.status() == ProtocolSession.SessionStatus.TERMINATED){
            throw new SessionException
                    .SessionExpiredException(sessionId);
        }

        // Validate session inactivity timeout.
        if (isExpired(session)) {
            repository.delete(sessionId);

            log.debug(
                    "SESSION EXPIRED sessionId={} type={} lastActivityAt={}",
                    sessionId,
                    session.transportType(),
                    session.lastActivityAt()
            );

            throw new SessionException
                    .SessionExpiredException(sessionId);
        }

        // Renew activity — updates lastActivityAt.
        ProtocolSession renewed = renewActivityAndJwt(session, jwt);
        repository.save(renewed);

        log.debug("SESSION VALIDATE sessionId={} type={}",
                sessionId, session.transportType());

        return renewed;
    }

    @Override
    public void destroy(String sessionId) {
        repository.findById(sessionId).ifPresent(session -> {
            //Define como terminada
            ProtocolSession terminated = terminateSession(session);
            repository.save(terminated);

            // Delete Cache
            repository.delete(sessionId);

            log.info("SESSION DESTROY sessionId={} type={} duration={}ms",
                    sessionId,
                    session.transportType(),
                    calculateDuration(session));
        });

    }

    @Override
    public Optional<ProtocolSession> findById(String sessionId) {
        return repository.findById(sessionId);
    }

    @Override
    public boolean isActive(String sessionId) {
        return repository.exists(sessionId);
    }


    /** ==========================================================================  **/

    private boolean isExpired(ProtocolSession session) {
        return session.lastActivityAt()
                .plus(properties.timeout())
                .isBefore(Instant.now());
    }

    private ProtocolSession renewActivityAndJwt(
            ProtocolSession session,
            String jwt) {
        return switch (session) {
            case StreamableProtocolSession streamable ->
                    streamable.renewActivityAndJwt(jwt);
        };
    }

    private ProtocolSession terminateSession(ProtocolSession sesion){
        return switch (sesion){
            case StreamableProtocolSession streamable -> streamable.terminate();
        };
    }

    /** Calcula duración de la sesión en milisegundos para añadir en la traza*/
    private long calculateDuration(ProtocolSession session) {
        return java.time.Duration
                .between(session.connectedAt(), session.lastActivityAt())
                .toMillis();
    }

}
