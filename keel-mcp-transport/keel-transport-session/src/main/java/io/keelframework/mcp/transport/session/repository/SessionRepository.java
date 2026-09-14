package io.keelframework.mcp.transport.session.repository;

import io.keelframework.mcp.adapters.cache.manager.CacheServiceManager;
import io.keelframework.mcp.transport.session.model.ProtocolSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * MCP transport session repository.
 *
 * Persists sessions in Caffeine through cache-service.jar.
 * Transport-type agnostic — supports SSE, WebSocket, and Local sessions.
 *
 * Cache key: "transport:session:{sessionId}"
 * TTL: configurable via sca.transport.session.ttl
 *      managed by CacheServiceManager from cache-service.jar
 */
public final class SessionRepository {

    private static final Logger log =
            LoggerFactory.getLogger(SessionRepository.class);

    private static final String CACHE_NAME = "transport-sessions";
    private static final String KEY_PREFIX = "transport:session";

    private final CacheServiceManager cacheServiceManager;

    public SessionRepository(CacheServiceManager cacheServiceManager){
        this.cacheServiceManager = cacheServiceManager;
    }

    /**
     * SAVE- Crear o Actualizar sesion
     */
    public void save(ProtocolSession session){
        String key = buildKey(session.sessionId());
        cacheServiceManager.put(CACHE_NAME, key, session);
        log.debug("SESSION SAVE sessionId={} type={}",
                session.sessionId(), session.transportType());
    }

    /**
     * FIND -leer sesion
     * Return Optional.empty()
     */
    public Optional<ProtocolSession> findById(String sessionId){
        String key = buildKey(sessionId);
        return cacheServiceManager.get(CACHE_NAME, key, ProtocolSession.class);
    }

    /**
     * CHECK EXISTs -Existencia Sesion
     * Return Bool
     */
    public boolean exists(String sessionId){
        return findById(sessionId)
                .map(session -> session.status() ==
                        ProtocolSession.SessionStatus.ACTIVE)
                .orElse(false);
    }

    /**
     * DELETE SESION - Eliminar Sesion
     * Return
     */
    public void delete(String sessionId){
        String key = buildKey(sessionId);
        cacheServiceManager.evict(CACHE_NAME,key);
        log.debug("SESSION DELETE sessionId={}", sessionId);
    }

    private String buildKey(String sessionId){
        return KEY_PREFIX + sessionId;
    }

    /**
     * Retorna el total de sesiones activas en Cafeína.
     * Usa estimatedSize() de Cafeína via CacheModel.
     */
    public int count() {
        return (int) cacheServiceManager
                .stats(CACHE_NAME)
                .estimatedSize();
    }




}
