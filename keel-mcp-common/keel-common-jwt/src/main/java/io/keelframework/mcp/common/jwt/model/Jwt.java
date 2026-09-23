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
package io.keelframework.mcp.common.jwt.model;

import java.time.Instant;
import java.util.List;
import java.util.Set;

/**
 * Adapter-specific model representing the user authenticated by Keycloak.
 * This is an immutable record built from the claims of the validated JWT.
 */
public record Jwt(

    /** sub claim — unique user identifier in Keycloak */
    String subject,

    /**
     * client_id claim — identifies the OAuth2 client that requested
     * the token. This is the reliable identifier for
     * client_credentials tokens, where "subject" is null.
     */
    String clientId,

    /** preferred_username claim */
    String username,

    /** email claim */
    String email,

    /** Realm roles extracted from realm_access.roles */
    Set<Role> realmRoles,

    /** Scopes extracted from the space-separated scope claim */
    Set<Scope> scopes,

    /** Token issuer — Keycloak realm URL */
    String issuer,

    /** JWT Audience claim — aud claim */
    List<String> audience,

    /** Token expiration — exp claim, for security auditing */
    Instant expiresAt


) {

    public boolean hasRole(String roleName) {
        return realmRoles.stream()
                .anyMatch(r -> r.name().equalsIgnoreCase(roleName));
    }

    public boolean hasScope(String scopeName) {
        return scopes.stream()
                .anyMatch(s -> s.name().equalsIgnoreCase(scopeName));
    }

    public boolean hasAnyRole(String... roleNames) {
        for (String role : roleNames) {
            if (hasRole(role)) return true;
        }
        return false;
    }
}
