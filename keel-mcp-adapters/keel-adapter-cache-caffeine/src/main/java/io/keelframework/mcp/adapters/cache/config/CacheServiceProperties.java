package io.keelframework.mcp.adapters.cache.config;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "keel.mcp.adapters.cache")
public record CacheServiceProperties(

    /*
     * TTL tras escritura, entrada expira aunque se acceda.
     * Default: 10 minutos
     */
    Duration expireAfterWrite,

    /*
     * TTL tras último acceso. renueva con cada lectura —
     * Utilizado para las sesiones de MCP .
     * Default: 10 minutos
     */
    Duration expireAfterAccess,


    /*
     * Máximo de entradas en cache, cuando se supera Cafeína elimina las menos usadas (LRU).
     * Default: 1000
     */
    long maximumSize,

    /*
     * Activa estadísticas de Cafeína, será expuesta para Micrometer — hits, misses, evictions.
     * Default: true
     */
    boolean recordStats,

    /*
     * Activa logging de eventos del ciclo de vida.
     * Default: false
     */
    boolean logLifecycleEvents
){
    public CacheServiceProperties{
        if (expireAfterWrite == null) expireAfterWrite = Duration.ofMinutes(10);
        if (expireAfterAccess == null) expireAfterAccess = Duration.ofMinutes(10);
        if (maximumSize == 0) maximumSize = 1000;
    }
}
