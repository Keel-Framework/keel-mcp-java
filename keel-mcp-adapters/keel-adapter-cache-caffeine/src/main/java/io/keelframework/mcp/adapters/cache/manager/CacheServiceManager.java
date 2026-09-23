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
package io.keelframework.mcp.adapters.cache.manager;

import io.keelframework.mcp.adapters.cache.config.CacheServiceProperties;
import io.keelframework.mcp.adapters.cache.exception.CacheServiceException;
import io.keelframework.mcp.adapters.cache.model.CacheModel;           // ← tu modelo
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;

import java.util.Optional;
import java.util.function.Supplier;

/*
 * Lifecycle:
 * Create     → instantiate cache with configuration
 * Read       → get with miss/hit
 * Write      → put
 * Invalidate → evict specific entry
 * Clear      → clear all entries
 * Expire     → automatic Caffeine TTL expiration
 * Destroy    → release resources on shutdown
 */
public final class CacheServiceManager {

    private static final Logger log =
            LoggerFactory.getLogger(CacheServiceManager.class);

    private final CacheManager cacheManager;
    private final CacheServiceProperties properties;

    public CacheServiceManager(CacheManager cacheManager,
                               CacheServiceProperties properties) {
        this.cacheManager = cacheManager;
        this.properties   = properties;
    }

    // ================================================
    // GET — read with hit/miss
    // ================================================
    public <T> Optional<T> get(String cacheName, Object key, Class<T> type) {
        try {
            Cache.ValueWrapper wrapper = resolveCache(cacheName).get(key);

            if (wrapper == null) {
                log.debug("CACHE MISS cache={} key={}", cacheName, key);
                return Optional.empty();
            }

            log.debug("CACHE HIT  cache={} key={}", cacheName, key);
            return Optional.ofNullable(type.cast(wrapper.get()));

        } catch (Exception ex) {
            log.warn("CACHE GET error cache={} key={} error={}",
                    cacheName, key, ex.getMessage());
            return Optional.empty();
        }
    }

    // ================================================
    // PUT — write
    // ================================================

    public void put(String cacheName, Object key, Object value) {
        try {
            resolveCache(cacheName).put(key, value);
            log.debug("CACHE PUT  cache={} key={}", cacheName, key);

        } catch (Exception ex) {
            log.error("CACHE PUT error cache={} key={} error={}",
                    cacheName, key, ex.getMessage());
            throw new CacheServiceException(
                    "Error storing in cache", cacheName, ex);
        }
    }

    // ================================================
    // GET OR LOAD — cache lookup or load
    // ================================================
    public <T> T getOrLoad(String cacheName,
                           Object key,
                           Class<T> type,
                           Supplier<T> loader) {
        return get(cacheName, key, type)
                .orElseGet(() -> {
                    log.debug("CACHE LOAD cache={} key={}", cacheName, key);
                    T value = loader.get();
                    if (value != null) {
                        put(cacheName, key, value);
                    }
                    return value;
                });
    }

    // ================================================
    // EVICT — invalidate specific entry
    // ================================================
    public void evict(String cacheName, Object key) {
        try {
            resolveCache(cacheName).evict(key);
            log.debug("CACHE EVICT cache={} key={}", cacheName, key);

        } catch (Exception ex) {
            log.error("CACHE EVICT error cache={} key={} error={}",
                    cacheName, key, ex.getMessage());
            throw new CacheServiceException(
                    "Error evicting from cache", cacheName, ex);
        }
    }

    // ================================================
    // CLEAR — clear all entries
    // ================================================
    public void clear(String cacheName) {
        try {
            resolveCache(cacheName).clear();
            log.info("CACHE CLEAR cache={}", cacheName);

        } catch (Exception ex) {
            log.error("CACHE CLEAR error cache={} error={}",
                    cacheName, ex.getMessage());
            throw new CacheServiceException(
                    "Error clearing cache", cacheName, ex);
        }
    }

    // ================================================
    // STATS — returns your CacheModel
    // ================================================
    public CacheModel stats(String cacheName) {
        try {
            var caffeineMgr   = (CaffeineCacheManager) cacheManager;
            var caffeineCache = caffeineMgr.getCache(cacheName);

            if (caffeineCache == null) {
                return CacheModel.empty(cacheName);
            }

            // Cast explícito al tipo nativo de Cafeína
            @SuppressWarnings("unchecked")
            var nativeCache =
                    (com.github.benmanes.caffeine.cache.Cache<Object, Object>)
                            caffeineCache.getNativeCache();

            // Cafeína CacheStats — fully qualified para evitar conflicto
            com.github.benmanes.caffeine.cache.stats.CacheStats nativeStats =
                    nativeCache.stats();

            // Mapea a tu modelo CacheModel
            return new CacheModel(
                    cacheName,
                    nativeStats.hitCount(),
                    nativeStats.missCount(),
                    nativeStats.hitRate(),
                    nativeStats.evictionCount(),
                    nativeCache.estimatedSize()
            );

        } catch (Exception ex) {
            log.warn("CACHE STATS error cache={} error={}",
                    cacheName, ex.getMessage());
            return CacheModel.empty(cacheName);
        }
    }

    // ================================================
    // DESTROY — release resources on shutdown
    // ================================================
    @PreDestroy
    public void destroy() {
        log.info("CACHE DESTROY — liberando todas las caches");
        cacheManager.getCacheNames().forEach(name -> {
            try {
                cacheManager.getCache(name).clear();
                log.debug("CACHE DESTROY cache={}", name);
            } catch (Exception ex) {
                log.warn("CACHE DESTROY error cache={} error={}",
                        name, ex.getMessage());
            }
        });
    }

    private Cache resolveCache(String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            throw new CacheServiceException(
                    "Cache not found", cacheName);
        }
        return cache;
    }
}