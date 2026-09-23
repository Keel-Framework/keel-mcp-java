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
package io.keelframework.mcp.adapters.cache.exception;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;

public final class CacheServiceErrorHandler implements CacheErrorHandler {

    private static final Logger log =
            LoggerFactory.getLogger(CacheServiceErrorHandler.class);

    @Override
    public void handleCacheGetError(RuntimeException ex,
                                    Cache cache,
                                    Object key) {
        log.warn("CACHE GET error cache={} key={} error={}",
                cache.getName(), key, ex.getMessage());
        // No lanza excepción — miss aceptable
    }

    @Override
    public void handleCachePutError(RuntimeException ex,
                                    Cache cache,
                                    Object key,
                                    Object value) {
        log.error("CACHE PUT error cache={} key={} error={}",
                cache.getName(), key, ex.getMessage());
        throw new CacheServiceException(
                "Error storing in cache", cache.getName(), ex);
    }

    @Override
    public void handleCacheEvictError(RuntimeException ex,
                                      Cache cache,
                                      Object key) {
        log.error("CACHE EVICT error cache={} key={} error={}",
                cache.getName(), key, ex.getMessage());
        throw new CacheServiceException(
                "Error evicting from cache", cache.getName(), ex);
    }

    @Override
    public void handleCacheClearError(RuntimeException ex,
                                      Cache cache) {
        log.error("CACHE CLEAR error cache={} error={}",
                cache.getName(), ex.getMessage());
        throw new CacheServiceException(
                "Error clearing cache", cache.getName(), ex);
    }
}

