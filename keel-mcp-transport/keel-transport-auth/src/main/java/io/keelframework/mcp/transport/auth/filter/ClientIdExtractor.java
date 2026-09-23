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
package io.keelframework.mcp.transport.auth.filter;

import io.keelframework.mcp.common.jwt.model.Jwt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

/**
 * Extrae el clientId (subject) del Authentication del SecurityContext.
 *
 * Uso en StreamableController:
 *   Authentication auth = SecurityContextHolder.getContext().getAuthentication();
 *   String clientId = ClientIdExtractor.extract(auth);
 *
 * El controller no necesita conocer McpAuthenticationToken ni Jwt directamente.
 */

public class ClientIdExtractor {

    private static final Logger log = LoggerFactory.getLogger(ClientIdExtractor.class);

    private static final String ANONYMOUS = "anonymous";

    public ClientIdExtractor() {
    }

    /**
     * Extracts the client identifier from the JWT.
     *
     * Prefers the "sub" (subject) claim, which is populated for
     * user-based grants (authorization_code, password). Falls back to
     * the "client_id" claim for machine-to-machine tokens issued via
     * the client_credentials grant, where "sub" is typically absent
     * (there's no end user, only the calling client itself) — this is
     * the case for the public Duende IdentityServer demo used by the
     * Petstore sample, and for many OAuth2/OIDC providers in general.
     *
     * @param authentication Authentication from the SecurityContextHolder
     * @return the client identifier, or "anonymous" if none could be resolved
     */
    public static String extract(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            log.debug("No authenticated principal found — returning anonymous");
            return ANONYMOUS;
        }

        if (authentication instanceof McpAuthenticationToken mcpToken) {
            Jwt jwt = mcpToken.getPrincipal();

            // "sub" for user-based grants; fall back to "client_id"
            // for machine-to-machine tokens (client_credentials),
            // which typically have no "sub" claim at all.
            String clientId = jwt.subject() != null ? jwt.subject() : jwt.clientId();

            if (clientId == null || clientId.isBlank()) {
                log.warn("Neither 'sub' nor 'client_id' claim present in JWT — returning anonymous");
                return ANONYMOUS;
            }

            log.debug("Extracted clientId: {}", clientId);
            return clientId;
        }

        // Fallback — otros tipos de Authentication (tests, actuator, etc.)
        String name = authentication.getName();
        log.debug("Non-MCP authentication — using name as clientId: {}", name);
        return name != null ? name : ANONYMOUS;
    }

    /**
     * Extrae el Jwt completo del Authentication.
     * Útil cuando el controller necesita más que el subject (roles, scopes, etc.)
     *
     * @return Jwt completo, o null si no hay McpAuthenticationToken
     */
    public static Jwt extractJwt(Authentication authentication) {
        if (authentication instanceof McpAuthenticationToken mcpToken) {
            return mcpToken.getPrincipal();
        }
        return null;
    }

}
