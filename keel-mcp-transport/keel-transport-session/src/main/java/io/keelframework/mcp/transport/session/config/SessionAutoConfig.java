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
 * Autoconfiguracion del servicio de sesión de transporte MCP.
 *
 * Se activa automáticamente cuando:
 * - transport-session-service.jar está en el classpath
 * - cache-service.jar está en el classpath (CacheServiceManager disponible)

 * Beans registrados:
 * - TransportSessionRepository → persiste en Cafeína via CacheServiceManager
 * - TransportSessionManager    → gestiona ciclo de vida de sesión
 *
 * El proyecto MCP Server NO necesita configuración extra.
 * Solo añade la dependencia en pom.xml y configura application.yml.
 */

@AutoConfiguration
@EnableConfigurationProperties(SessionProperties.class)
@ConditionalOnBean(CacheServiceManager.class)
public class SessionAutoConfig {

    /**
     * Repositorio de sesiones.
     * Persiste en Cafeína via CacheServiceManager de cache-service.jar.
     */
    @Bean
    @ConditionalOnMissingBean(SessionRepository.class)
    public SessionRepository sessionRepository(
            CacheServiceManager cacheServiceManager){
        return new SessionRepository(cacheServiceManager);
    }

    /**
     * Gestor del ciclo de vida de sesiones.
     * Expone -> create, validate, destroy para SSE, WS y Stdio/Local.
     */
    @Bean
    @ConditionalOnMissingBean(SessionManager.class)
    public SessionManager sessionManager(
            SessionRepository repository,
            SessionProperties properties){
        return new SessionManager(repository, properties);
    }

}
