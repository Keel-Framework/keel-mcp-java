package io.keelframework.mcp.adapters.cache.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import io.keelframework.mcp.adapters.cache.exception.CacheServiceErrorHandler;
import io.keelframework.mcp.adapters.cache.key.CacheServiceKeyGenerator;
import io.keelframework.mcp.adapters.cache.manager.CacheServiceManager;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;

/**
 * Autoconfiguracion del starter de cache basado en Cafeína.
 *
 * Se activa automáticamente cuando Cafeína está en el classpath.
 * El proyecto MCP Server no necesita ninguna configuración extra.
 *
 * * Registrado en:
 *  * META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports
*/
@AutoConfiguration
@EnableCaching
@EnableConfigurationProperties(CacheServiceProperties.class)
@ConditionalOnClass(Caffeine.class)
public class CacheServiceConfig {
    /**
     * CacheManager basado en Cafeína.
     * Solo se registra si no existe otro CacheManager en el contexto.
     */
    @Bean
    @ConditionalOnMissingBean(CacheManager.class)
    public CacheManager cacheManager(CacheServiceProperties properties) {

        CaffeineCacheManager manager = new CaffeineCacheManager();
        manager.setCaffeine(buildCaffeine(properties));
        manager.setAllowNullValues(false);

        return manager;
    }

    /**
     * Instancia de Cafeína con la configuración estándar del ecosistema.
     * Solo se registra si no existe otra instancia en el contexto.
     */
    @Bean
    @ConditionalOnMissingBean(Caffeine.class)
    public Caffeine<Object, Object> caffeine(CacheServiceProperties properties) {
        return buildCaffeine(properties);
    }

    /**
     * Gestor del ciclo de vida de cache.
     * Provee operaciones estándar: get, put, evict, clear, stats.
     */
    @Bean
    @ConditionalOnMissingBean(CacheServiceManager.class)
    public CacheServiceManager cacheServiceManager(
            CacheManager cacheManager,
            CacheServiceProperties properties) {
        return new CacheServiceManager(cacheManager, properties);
    }

    /**
     * Manejador estándar de errores de cache.
     * GET fallido → warning, no excepción.
     * PUT/EVICT/CLEAR fallido → CacheServiceException.
     */
    @Bean
    @ConditionalOnMissingBean(CacheServiceErrorHandler.class)
    public CacheServiceErrorHandler cacheServiceErrorHandler() {
        return new CacheServiceErrorHandler();
    }

    /**
     * Generador estándar de claves de cache.
     * Formato: ClassName:methodName:param1:param2
     * Sin colisiones entre distintos adapters del ecosistema.
     */
    @Bean("cacheServiceKeyGenerator")
    @ConditionalOnMissingBean(name = "cacheServiceKeyGenerator")
    public CacheServiceKeyGenerator cacheServiceKeyGenerator() {
        return new CacheServiceKeyGenerator();
    }

    // ================================================
    // Privado — construcción de Cafeína
    // ================================================

    private Caffeine<Object, Object> buildCaffeine(CacheServiceProperties properties) {

        Caffeine<Object, Object> builder = Caffeine.newBuilder()
                .maximumSize(properties.maximumSize())
                .expireAfterWrite(properties.expireAfterWrite())
                .expireAfterAccess(properties.expireAfterAccess());

        if (properties.recordStats()) {
            builder.recordStats();
        }

        if (properties.logLifecycleEvents()) {
            builder.removalListener((key, value, cause) ->
                    System.out.printf(
                            "CACHE EVICT key=%s cause=%s%n", key, cause));
        }

        return builder;
    }


}
