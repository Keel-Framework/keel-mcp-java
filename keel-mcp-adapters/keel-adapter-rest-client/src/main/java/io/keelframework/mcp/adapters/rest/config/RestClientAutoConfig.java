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
package io.keelframework.mcp.adapters.rest.config;

import io.keelframework.mcp.common.jwt.provider.JwtProvider;

import org.springframework.core.io.ResourceLoader;
import io.keelframework.mcp.adapters.rest.client.McpRestClientFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Auto-configuration for the REST adapter for MCP tools.
 *
 * The developer only needs to:
 * 1. Add the adapter-rest-client dependency
 * 2. Configure services in application.yml:
 *    sca.adapters.rest.services.{name}.base-url
 * 3. Inject McpRestClientFactory into their tools
 *
 * Everything else (JWT, SCA headers, connection pool, SSL) is automatic
 * and transparent to the developer.
 */

@AutoConfiguration
@EnableConfigurationProperties({
        RestClientProperties.class,
        HttpClientProperties.class,
        SslProperties.class
})
public class RestClientAutoConfig implements EnvironmentAware {

    private static final Logger log =
            LoggerFactory.getLogger(RestClientAutoConfig.class);

    @Override
    public void setEnvironment(Environment environment) {
        // Verifica si virtual threads ya está activado
        String enabled = environment.getProperty(
                "spring.threads.virtual.enabled");
        if (!"true".equalsIgnoreCase(enabled)) {
            log.warn("VIRTUAL THREADS not enabled — " +
                    "add spring.threads.virtual.enabled=true " +
                    "to application.yml for better MCP performance ");
        }
    }

    @Bean
    @ConditionalOnMissingBean(McpRestClientFactory.class)
    public McpRestClientFactory mcpRestClientFactory(
            RestClientProperties restProperties,
            HttpClientProperties httpProperties,
            SslProperties sslProperties,
            ResourceLoader resourceLoader,
            JwtProvider jwtProvider){
        return new McpRestClientFactory(
                restProperties,
                httpProperties,
                sslProperties,
                resourceLoader,
                jwtProvider);
    }
}
