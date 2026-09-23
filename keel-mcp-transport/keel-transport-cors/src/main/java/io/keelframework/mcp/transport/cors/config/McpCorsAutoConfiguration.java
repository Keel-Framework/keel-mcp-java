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
package io.keelframework.mcp.transport.cors.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.util.CollectionUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.servlet.DispatcherType;
/**
 * CORS auto-configuration for the MCP Streamable HTTP endpoint.
 * Enabled only when {@code keel.mcp.transport.cors.enabled=true}.
 *
 * IMPORTANT: explicitly registered as a FilterRegistrationBean with
 * DispatcherType.ASYNC included. The /mcp endpoint uses asynchronous
 * dispatching (Streamable HTTP), and the standard Spring MVC mechanism
 * (WebMvcConfigurer.addCorsMappings) delegates to a filter extending
 * OncePerRequestFilter, which by default is NOT re-executed on an
 * async redispatch — resulting in actual responses without the
 * Access-Control-Allow-Origin header, even though the preflight
 * OPTIONS response includes it.
 *
 * Authentication- and session-agnostic — it only controls which
 * browser origins can interact with the MCP endpoint.
 */

@AutoConfiguration
@EnableConfigurationProperties(TransportCorsProperties.class)
@ConditionalOnProperty(prefix = "keel.mcp.transport.cors", name = "enabled", havingValue = "true")
public class McpCorsAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(McpCorsAutoConfiguration.class);

    private final TransportCorsProperties properties;

    public McpCorsAutoConfiguration(TransportCorsProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    void validateConfiguration() {
        if (CollectionUtils.isEmpty(properties.getAllowedOrigins())) {
            throw new IllegalStateException(
                    "keel.mcp.transport.cors.allowed-origins is required when " +
                            "keel.mcp.transport.cors.enabled=true. Refusing to start with CORS " +
                            "enabled but no origins configured, to avoid an insecure default.");
        }

        if (properties.isAllowCredentials() && properties.getAllowedOrigins().contains("*")) {
            throw new IllegalStateException(
                    "keel.mcp.transport.cors.allowed-origins cannot contain \"*\" when " +
                            "allow-credentials=true. List origins explicitly.");
        }
    }

    @Bean
    public FilterRegistrationBean<CorsFilter> mcpCorsConfigurer() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(properties.getAllowedOrigins());
        config.setAllowedMethods(properties.getAllowedMethods());
        config.setAllowedHeaders(properties.getAllowedHeaders());
        config.setExposedHeaders(properties.getExposedHeaders());
        config.setAllowCredentials(properties.isAllowCredentials());
        config.setMaxAge(properties.getMaxAge());

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration(properties.getPathPattern(), config);

        FilterRegistrationBean<CorsFilter> registration =
                new FilterRegistrationBean<>(new CorsFilter(source));

        // Clave: incluir ASYNC además de REQUEST, o el header CORS
        // desaparece en la respuesta real de un endpoint Streamable HTTP.
        registration.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.ASYNC);
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        registration.addUrlPatterns(toUrlPattern(properties.getPathPattern()));

        return registration;
    }

    /**
     * Converts an Ant-style pattern (e.g. "/mcp/**") to the servlet URL pattern
     * expected by FilterRegistrationBean (e.g. "/mcp/*").
     */
    private static String toUrlPattern(String antPattern) {
        return antPattern.endsWith("/**")
                ? antPattern.substring(0, antPattern.length() - 1)
                : antPattern;
    }}
