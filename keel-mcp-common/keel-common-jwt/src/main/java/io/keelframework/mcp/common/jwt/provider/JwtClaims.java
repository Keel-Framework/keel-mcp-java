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
package io.keelframework.mcp.common.jwt.provider;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Record inmutable con los claims extraídos del JWT ya validado.
 */
public record JwtClaims(

        String subject,
        String username,
        String email,
        String issuer,
        String jwtId,
        List<String> audience,

        /* realm_access.roles — lista de roles de realm */
        List<String> realmRoles,

        /* scope claim — string espacio-separado */
        String scopes,

        Instant issuedAt,
        Instant expiresAt,

        /* Claims completos para extensibilidad */
        Map<String, Object> rawClaims


) {
    public boolean hasRealmRole(String role) {
        return realmRoles != null && realmRoles.contains(role);
    }

    // En JwtClaims — añadir método de conveniencia
    // Nuevo — consistente con hasRealmRole
    public boolean hasScope(String scope) {
        return scopes != null && scopes.contains(scope);
    }
}
