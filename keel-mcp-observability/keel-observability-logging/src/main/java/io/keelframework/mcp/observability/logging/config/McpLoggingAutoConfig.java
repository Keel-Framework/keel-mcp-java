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
package io.keelframework.mcp.observability.logging.config;

import io.keelframework.mcp.observability.logging.aspect.McpToolLoggingAspect;
import io.keelframework.mcp.observability.logging.filter.McpRequestLoggingFilter;
import io.keelframework.mcp.observability.logging.handler.McpAuthLoggingHandler;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;


/**
 * Auto-configuration for MCP observability.
 *
 * <p>Automatically configures and registers the following observability components:</p>
 * <ul>
 *     <li>{@code McpRequestLoggingFilter} for TECHNICAL traces</li>
 *     <li>{@code McpToolLoggingAspect} for FUNCTIONAL traces</li>
 *     <li>{@code McpAuthLoggingHandler} for SECURITY traces</li>
 * </ul>
 *
 * <p>Observability is enabled by default and can be globally controlled through:</p>
 * <pre>
 * keel.mcp.observability.enabled=true
 * </pre>
 *
 * <p>The following environment variables can be used to configure individual
 * observability categories:</p>
 * <ul>
 *     <li>{@code MCP_OBSERVABILITY_ENABLED} — true/false</li>
 *     <li>{@code MCP_OBSERVABILITY_TECHNICAL} — true/false</li>
 *     <li>{@code MCP_OBSERVABILITY_FUNCTIONAL} — true/false</li>
 *     <li>{@code MCP_OBSERVABILITY_SECURITY} — true/false</li>
 * </ul>
 */
@AutoConfiguration
@EnableAspectJAutoProxy
@EnableConfigurationProperties(LogProperties.class)
@ConditionalOnProperty(
        prefix = "keel.mcp.observability",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true)
public class McpLoggingAutoConfig {

    /**
     * 1. Handler first — the filter and aspect depend on it.
     */
    @Bean
    @ConditionalOnMissingBean
    public McpAuthLoggingHandler mcpAuthLoggingHandler(
            LogProperties logProperties) {
        return new McpAuthLoggingHandler(logProperties);
    }

    /**
     * 2. Filter — receives the handler.
     */
    @Bean
    @ConditionalOnMissingBean
    public McpRequestLoggingFilter mcpRequestLoggingFilter(
            LogProperties logProperties,
            McpAuthLoggingHandler handler) {          // ← añade handler
        return new McpRequestLoggingFilter(logProperties, handler);
    }

    /**
     * 3. Aspect — receives the handler.
     */    @Bean
    @ConditionalOnMissingBean
    public McpToolLoggingAspect mcpToolLoggingAspect(
            LogProperties logProperties,
            McpAuthLoggingHandler handler) {          // ← añade handler
        return new McpToolLoggingAspect(logProperties, handler);
    }
}