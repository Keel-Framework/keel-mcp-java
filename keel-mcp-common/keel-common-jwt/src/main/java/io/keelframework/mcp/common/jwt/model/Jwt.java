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
 * Modelo propio del adaptador que representa al usuario autenticado por Keycloak.
 * Es un record inmutable construido a partir de los claims del JWT validado.
 */
public record Jwt(
        /** sub claim — identificador único del usuario en Keycloak */
        String subject,

        /** client_id claim — identifica al cliente OAuth2 que solicitó
         *  el token. Es el identificador fiable para tokens
         *  client_credentials, donde "subject" es null. */
        String clientId,

        /** preferred_username claim */
        String username,

        /** email claim */
        String email,

        /** Roles de realm extraídos de realm_access.roles */
        Set<Role> realmRoles,

        /** Scopes extraídos del claim scope (espacio-separados) */
        Set<Scope> scopes,

        /** Issuer del token — URL del realm de Keycloak */
        String issuer,

        /** JWT Audience claim — aud claim */
        List<String> audience,

        /** Expiración del token — exp claim, para auditoría de seguridad */
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
