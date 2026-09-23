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
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;


/**
 * Configurable properties for the MCP transport session service.
 *
 * Example configuration in the MCP Server project:
 *
 * keel:
 *   transport:
 *     session:
 *       max-sessions: 500
 *       log-events:   false
 */

@ConfigurationProperties(prefix = "keel.mcp.transport.session")
public record SessionProperties(

    /**
     * Maximum number of concurrent sessions allowed.
     * Protects the MCP server from overload.
     * Default: 500
     */
    int maxSessions,

    /**
     * Session inactivity timeout.
     * Default: 30 minutes.
     */
    Duration timeout,

    /**
     * Enables detailed logging of session events.
     * For development only — disable in production.
     * Default: false
     */
    boolean logEvents
){
    public SessionProperties {
        if (maxSessions == 0){
            maxSessions = 500;
        }
        if (timeout == null) {
            timeout = Duration.ofMinutes(30);
        }
    }
}
