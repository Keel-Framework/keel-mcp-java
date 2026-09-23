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
package io.keelframework.mcp.transport.streamable.config;

import io.keelframework.mcp.common.jwt.provider.JwtProvider;
import io.keelframework.mcp.observability.logging.handler.McpAuthLoggingHandler;
import io.keelframework.mcp.transport.session.config.SessionProperties;
import io.keelframework.mcp.transport.session.repository.SessionRepository;
import io.keelframework.mcp.transport.streamable.interceptor.McpSessionInterceptor;
import io.keelframework.mcp.transport.streamable.provider.McpJwtProvider;
import io.keelframework.mcp.transport.streamable.registry.StreamableSessionRegistry;
import io.keelframework.mcp.transport.session.manager.SessionManagerPort;
import jakarta.servlet.http.HttpServletRequest;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Auto-configuration for the Keel Streamable HTTP session module: registers
 * the {@link McpSessionInterceptor}, the session registry, and existing
 * session validation on top of Spring AI's MCP Streamable HTTP transport.
 *
 * <p><b>{@code keel.mcp.transport.streamable.enabled}</b><br>
 * Enables or disables this entire module (interceptor, session registry,
 * existing-session validation).
 * <ul>
 *   <li>Default: {@code true} — no need to declare it explicitly.</li>
 *   <li>Set to {@code false} to fully disable {@code StreamableAutoConfig}.
 *       Useful only in exceptional scenarios (e.g. unit tests that don't
 *       need session management, or a project implementing its own
 *       mechanism).</li>
 * </ul>
 */
@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnBean({
        SessionManagerPort.class,
        SessionRepository.class
})
@ConditionalOnProperty(
        prefix       = "keel.mcp.transport.streamable",
        name         = "enabled",
        havingValue  = "true",
        matchIfMissing = true     //  enabled by default
)
public class StreamableAutoConfig {

    /**
     * Registro de sesiones Streamable HTTP activas.
     */
    @Bean
    @ConditionalOnMissingBean(StreamableSessionRegistry.class)
    public StreamableSessionRegistry streamableSessionRegistry(
            SessionRepository repository,
            SessionProperties properties) {
        return new StreamableSessionRegistry(
                repository,
                properties.maxSessions()
        );
    }

    /**
     * Endpoint Streamable HTTP del servidor MCP.
     * Interceptor /mcp.
     */
    @Bean
    @ConditionalOnMissingBean
    public McpSessionInterceptor mcpSessionInterceptor(
            SessionManagerPort sessionManager,
            StreamableSessionRegistry registry,
            McpAuthLoggingHandler authLoggingHandler
            ) {
        return new McpSessionInterceptor(sessionManager, registry, authLoggingHandler);
    }

    @Bean
    public WebMvcConfigurer mcpInterceptorConfigurer(
            McpSessionInterceptor interceptor,
            @Value("${spring.ai.mcp.server.streamable-http.mcp-endpoint:/mcp}") String endpoint){
        return new WebMvcConfigurer() {
            @Override
            public void addInterceptors(@NonNull InterceptorRegistry registry) {
                registry.addInterceptor(interceptor)
                        .addPathPatterns(endpoint, endpoint + "/**");
            }
        };
    }

    @Bean
    @ConditionalOnMissingBean(JwtProvider.class)
    public McpJwtProvider mcpJwtProvider(
            StreamableSessionRegistry registry,
            HttpServletRequest request) {
        return new McpJwtProvider(registry, request);
    }
}
