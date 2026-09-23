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
package io.keelframework.mcp.observability.logging.handler;

import java.time.Instant;
import java.util.List;

/**
 * Data context for a successful JWT authentication.
 *
 * <p>Groups authentication and audit fields into a single object,
 * avoiding method signatures with a large number of parameters.</p>
 *
 * <p>Usage from {@code McpAuthenticationFilter}:</p>
 *
 * <pre>
 * AuthSuccessContext ctx = AuthSuccessContext.builder()
 *         .subject(token.subject())
 *         .issuer(token.issuer())
 *         .audience(token.audience())
 *         .expiresAt(token.expiresAt())
 *         .sessionId(request.getHeader("Mcp-Session-Id"))
 *         .clientId(clientId)
 *         .uri(request.getRequestURI())
 *         .method(request.getMethod())
 *         .remoteAddr(request.getRemoteAddr())
 *         .userAgent(request.getHeader("User-Agent"))
 *         .build();
 *
 * authLoggingHandler.logAuthSuccess(ctx);
 * </pre>
 */
public record AuthSuccessContext(
        String subject,
        String issuer,
        List<String> audience,
        Instant expiresAt,
        String sessionId,
        String clientId
) {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String subject;
        private String issuer;
        private List<String> audience;
        private Instant expiresAt;
        private String sessionId;
        private String clientId;

        public Builder subject(String v)    { this.subject = v; return this; }
        public Builder issuer(String v)     { this.issuer = v; return this; }
        public Builder audience(List<String> v)   { this.audience = v; return this; }
        public Builder expiresAt(Instant v) { this.expiresAt = v; return this; }
        public Builder sessionId(String v)  { this.sessionId = v; return this; }
        public Builder clientId(String v)   { this.clientId = v; return this; }

        public AuthSuccessContext build() {
            return new AuthSuccessContext(
                    subject, issuer, audience, expiresAt,
                    sessionId, clientId);
        }
    }
}