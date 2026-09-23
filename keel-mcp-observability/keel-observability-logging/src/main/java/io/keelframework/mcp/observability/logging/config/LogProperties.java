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

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import io.keelframework.mcp.observability.logging.model.McpLogType;
/**
 * Configuration properties for MCP observability.
 *
 * <p>External configuration can be provided through {@code application.yml}
 * or environment variables.</p>
 *
 * <p>Example:</p>
 * <pre>
 * keel:
 *   mcp:
 *     observability:
 *       enabled: ${MCP_OBSERVABILITY_ENABLED:true}
 *       technical: ${MCP_OBSERVABILITY_TECHNICAL:true}
 *       functional: ${MCP_OBSERVABILITY_FUNCTIONAL:true}
 *       security: ${MCP_OBSERVABILITY_SECURITY:true}
 * </pre>
 *
 * <p>All observability features are enabled by default.</p>
 */
@ConfigurationProperties(prefix = "keel.mcp.observability")
public record LogProperties(

        /** Enables/disables all traces. Default: true */
        @DefaultValue("true")
        boolean enabled,

        /** Enables TECHNICAL traces. Default: true */
        @DefaultValue("true")
        boolean technical,

        /** Enables FUNCTIONAL traces. Default: true */
        @DefaultValue("true")
        boolean functional,

        /** Enables SECURITY traces. Default: true */
        @DefaultValue("true")
        boolean security
) {
    /**
     * Checks whether a trace type is enabled.
     */
    public boolean isActive(McpLogType type) {
        if (!enabled) return false;
        return switch (type) {
            case TECHNICAL  -> technical;
            case FUNCTIONAL -> functional;
            case SECURITY   -> security;
        };
    }
}
