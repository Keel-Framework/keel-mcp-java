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
 * Auto-configuration for the Caffeine-based cache starter.
 *
 * Automatically enabled when Caffeine is available on the classpath.
 * The MCP Server project requires no additional configuration.
 *
 * Registered in:
 * META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports
 */
@AutoConfiguration
@EnableCaching
@EnableConfigurationProperties(CacheServiceProperties.class)
@ConditionalOnClass(Caffeine.class)
public class CacheServiceConfig {
    /**
     * Caffeine-based CacheManager.
     * Registered only if no other CacheManager exists in the context.
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
     * Caffeine instance with the ecosystem's standard configuration.
     * Registered only if no other instance exists in the context.
     */
    @Bean
    @ConditionalOnMissingBean(Caffeine.class)
    public Caffeine<Object, Object> caffeine(CacheServiceProperties properties) {
        return buildCaffeine(properties);
    }

    /**
     * Cache lifecycle manager.
     * Provides standard operations: get, put, evict, clear, and stats.
     */
    @Bean
    @ConditionalOnMissingBean(CacheServiceManager.class)
    public CacheServiceManager cacheServiceManager(
            CacheManager cacheManager,
            CacheServiceProperties properties) {
        return new CacheServiceManager(cacheManager, properties);
    }

    /**
     * Standard cache error handler.
     * Failed GET → warning, no exception.
     * Failed PUT/EVICT/CLEAR → CacheServiceException.
     */
    @Bean
    @ConditionalOnMissingBean(CacheServiceErrorHandler.class)
    public CacheServiceErrorHandler cacheServiceErrorHandler() {
        return new CacheServiceErrorHandler();
    }

    /**
     * Standard cache key generator.
     * Format: ClassName:methodName:param1:param2
     * Prevents collisions between different adapters in the ecosystem.
     */
    @Bean("cacheServiceKeyGenerator")
    @ConditionalOnMissingBean(name = "cacheServiceKeyGenerator")
    public CacheServiceKeyGenerator cacheServiceKeyGenerator() {
        return new CacheServiceKeyGenerator();
    }


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
