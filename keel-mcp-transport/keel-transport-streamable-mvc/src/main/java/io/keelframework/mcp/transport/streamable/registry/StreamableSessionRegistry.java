package io.keelframework.mcp.transport.streamable.registry;

import io.keelframework.mcp.transport.session.model.StreamableProtocolSession;
import io.keelframework.mcp.transport.session.repository.SessionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;


/**
 * Registry for active Streamable HTTP sessions.
 * Persists sessions exclusively through the SessionRepository.
 */
public final class StreamableSessionRegistry {

    private static final Logger log =
            LoggerFactory.getLogger(StreamableSessionRegistry.class);

    private final SessionRepository repository;
    private final int maxSessions;

    public StreamableSessionRegistry(SessionRepository repository, int maxSessions) {
        this.repository = repository;
        this.maxSessions = maxSessions;
    }

    /**
     * Registers a Streamable HTTP session.
     * @param session the session to register
     * @return {@code true} if the session was registered,
     *          {@code false} if the maximum number of sessions was reached
     */
    public boolean register(StreamableProtocolSession session) {
        if (isOverloaded()) {
            log.warn("STREAMABLE MAX SESSIONS reached max={}",
                    maxSessions);
            return false;
        }
        repository.save(session);

        log.debug("STREAMABLE REGISTER sessionId={}...",
                session.sessionId().substring(0, 8));

        return true;
    }

    /**
     * Removes a session from the repository.
     * @param sessionId the session identifier
    */
    public void unregister(String sessionId) {
        repository.delete(sessionId);

        log.debug("STREAMABLE UNREGISTER sessionId={}...",
                sessionId.substring(0, 8));
    }

    /**
     * Finds a Streamable HTTP session by its identifier.
     * @param sessionId the session identifier
     * @return the Streamable session if it exists, otherwise {@code null}
     */
    public StreamableProtocolSession findById(String sessionId) {
        return repository.findById(sessionId)
                .filter(s -> s instanceof StreamableProtocolSession)
                .map(s -> (StreamableProtocolSession) s)
                .orElse(null);
    }

    /**
     * Retrieves the JWT of an existing session.
     * @param sessionId the session identifier
     * @return the session JWT, or {@code null} if the session does not exist
     */
    public String getJwt(String sessionId) {
        return repository.findById(sessionId)
                .filter(s -> s instanceof StreamableProtocolSession)
                .map(s -> (StreamableProtocolSession) s)
                .map(StreamableProtocolSession::jwt)
                .orElse(null);
    }

    /**
     * Renews the session activity and updates its JWT.
     * @param sessionId the session identifier
     * @param newJwt the new JWT
     */
    public void renewActivityAndJwt(String sessionId, String newJwt) {
        repository.findById(sessionId)
                .filter(s -> s instanceof StreamableProtocolSession)
                .map(s -> (StreamableProtocolSession) s)
                .map(s -> s.renewActivityAndJwt(newJwt))
                .ifPresent(repository::save);

        log.debug("STREAMABLE ACTIVITY+JWT RENEWED sessionId={}...",
                sessionId.substring(0, 8));
    }

    /**
     * Checks whether a session exists in the repository.
     * @param sessionId the session identifier
     * @return {@code true} if the session exists
     */
    public boolean exists(String sessionId) {
        return repository.exists(sessionId);
    }

    /**
     * Checks whether the maximum number of sessions has been reached.
     * @return {@code true} if the session limit has been reached
     */
    public boolean isOverloaded() {
        return repository.count() >= maxSessions;
    }
}
