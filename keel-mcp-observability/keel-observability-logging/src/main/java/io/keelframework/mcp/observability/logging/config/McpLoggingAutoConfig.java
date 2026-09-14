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
 * Autoconfiguración de observabilidad MCP.
 *
 * Registra automáticamente:
 *  - McpRequestLoggingFilter → trazas TECHNICAL
 *  - McpToolLoggingAspect    → trazas FUNCTIONAL
 *  - McpAuthLoggingHandler   → trazas SECURITY
 *
 * Activación global:
 *  sca.mcp.observability.enabled=true (default)
 *
 * Variables de entorno:
 *  MCP_OBSERVABILITY_ENABLED    → true/false
 *  MCP_OBSERVABILITY_TECHNICAL  → true/false
 *  MCP_OBSERVABILITY_FUNCTIONAL → true/false
 *  MCP_OBSERVABILITY_SECURITY   → true/false
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

    // ── 1. Handler primero — filtro y aspecto dependen de él ─────────────────
    @Bean
    @ConditionalOnMissingBean
    public McpAuthLoggingHandler mcpAuthLoggingHandler(
            LogProperties logProperties) {
        return new McpAuthLoggingHandler(logProperties);
    }

    // ── 2. Filtro — recibe handler ───────────────────────────────────────────
    @Bean
    @ConditionalOnMissingBean
    public McpRequestLoggingFilter mcpRequestLoggingFilter(
            LogProperties logProperties,
            McpAuthLoggingHandler handler) {          // ← añade handler
        return new McpRequestLoggingFilter(logProperties, handler);
    }

    // ── 3. Aspecto — recibe handler ──────────────────────────────────────────
    @Bean
    @ConditionalOnMissingBean
    public McpToolLoggingAspect mcpToolLoggingAspect(
            LogProperties logProperties,
            McpAuthLoggingHandler handler) {          // ← añade handler
        return new McpToolLoggingAspect(logProperties, handler);
    }
}