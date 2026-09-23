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
package io.keelframework.mcp.transport.session.config;
import io.keelframework.mcp.adapters.cache.manager.CacheServiceManager;
import io.keelframework.mcp.transport.session.manager.SessionManager;
import io.keelframework.mcp.transport.session.repository.SessionRepository;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Auto-configuration for the MCP transport session service.
 *
 * Automatically enabled when:
 * - transport-session-service.jar is on the classpath
 * - cache-service.jar is on the classpath (CacheServiceManager available)
 *
 * Registered beans:
 * - TransportSessionRepository → persists sessions using Caffeine via CacheServiceManager
 * - TransportSessionManager    → manages the session lifecycle
 *
 * The MCP Server project requires no additional configuration.
 * Simply add the dependency to pom.xml and configure application.yml.
 */

@AutoConfiguration
@EnableConfigurationProperties(SessionProperties.class)
@ConditionalOnBean(CacheServiceManager.class)
public class SessionAutoConfig {

    /**
     * Session repository.
     * Persists sessions in Caffeine via CacheServiceManager from cache-service.jar.
     */
    @Bean
    @ConditionalOnMissingBean(SessionRepository.class)
    public SessionRepository sessionRepository(
            CacheServiceManager cacheServiceManager){
        return new SessionRepository(cacheServiceManager);
    }

    /**
     * Session lifecycle manager.
     * Exposes create, validate, and destroy operations for SSE, WebSocket,
     * and Stdio/Local transports.
     */
    @Bean
    @ConditionalOnMissingBean(SessionManager.class)
    public SessionManager sessionManager(
            SessionRepository repository,
            SessionProperties properties){
        return new SessionManager(repository, properties);
    }

}
