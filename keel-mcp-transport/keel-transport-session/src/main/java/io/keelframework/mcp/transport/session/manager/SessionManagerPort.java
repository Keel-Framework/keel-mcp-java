package io.keelframework.mcp.transport.session.manager;

import io.keelframework.mcp.transport.session.model.ProtocolSession;

import java.util.Optional;

public sealed interface SessionManagerPort
        permits SessionManager {

    /**
    * Validates an existing session.
    *
    * @param sessionId the session identifier
    * @return the validated session
    */
    ProtocolSession validate(String sessionId, String jwt);

    /**
    * Destroys an existing session.
    *
    * @param sessionId the session identifier
    */
    void destroy(String sessionId);

    /**
    * Finds a session by its identifier.
    *
    * @param sessionId the session identifier
    * @return the session if it exists
    */
    Optional<ProtocolSession> findById(String sessionId);

    /**
    * Checks whether a session exists and is active.
    *
    * @param sessionId the session identifier
    * @return {@code true} if the session is active
    */
    boolean isActive(String sessionId);
}