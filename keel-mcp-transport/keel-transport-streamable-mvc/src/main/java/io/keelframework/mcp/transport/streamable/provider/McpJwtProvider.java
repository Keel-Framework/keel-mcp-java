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
package io.keelframework.mcp.transport.streamable.provider;
import io.keelframework.mcp.transport.auth.filter.McpAuthenticationToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.keelframework.mcp.common.jwt.provider.JwtProvider;
import io.keelframework.mcp.transport.streamable.registry.StreamableSessionRegistry;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;


/**
 * Retrieves the JWT from the current context.
 * First attempts to retrieve it from the MCP session,
 * then falls back to the SecurityContext.
 */
public class McpJwtProvider implements JwtProvider {

    private static final Logger log =
            LoggerFactory.getLogger(McpJwtProvider.class);

    private final StreamableSessionRegistry registry;
    private final HttpServletRequest request;

    public McpJwtProvider(StreamableSessionRegistry registry,
                          HttpServletRequest request) {
        this.registry = registry;
        this.request = request;
    }

    @Override
    public String getJwt() {
        // 1. Intenta desde sesión MCP
        String sessionId = request.getHeader("Mcp-Session-Id");
        if (sessionId != null && !sessionId.isBlank()) {
            String jwt = registry.getJwt(sessionId);
            if (jwt != null) {
                log.debug("JWT from session sessionId={}...", sessionId.substring(0, 8));
                return jwt;
            }
        }

        // 2. Fallback — SecurityContext
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof McpAuthenticationToken mcpAuth) {
            log.debug("JWT from SecurityContext");
            return (String) mcpAuth.getCredentials();
        }

        log.warn("JWT not found");
        return null;
    }
}
