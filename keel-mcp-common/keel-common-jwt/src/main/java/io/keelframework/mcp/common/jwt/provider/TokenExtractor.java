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


import io.keelframework.mcp.common.jwt.model.Jwt;
import io.keelframework.mcp.common.jwt.model.Role;
import io.keelframework.mcp.common.jwt.model.Scope;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;


public class TokenExtractor {

    public Jwt extract(JwtClaims claims) {
        Set<Role> roles = claims.realmRoles() == null
                ? Set.of()
                : claims.realmRoles().stream()
                  .map(Role::of)
                  .collect(Collectors.toUnmodifiableSet());

        Set<Scope> scopes = parseScopes(claims.scopes());

        // client_id claim — identifies the OAuth2 client for
        // machine-to-machine tokens (client_credentials grant), where
        // "subject" is typically absent. Not a first-class field on
        // JwtClaims, so read it from the raw claims map.
        String clientId = claims.rawClaims() != null
                ? (String) claims.rawClaims().get("client_id")
                : null;

        return new Jwt(
                claims.subject(),
                clientId,
                claims.username(),
                claims.email(),
                roles,
                scopes,
                claims.issuer(),
                claims.audience(),
                claims.expiresAt()
        );
    }


    private Set<Scope> parseScopes(String scopeClaim) {
        if (scopeClaim == null || scopeClaim.isEmpty()) {
            return Set.of();
        }
        return Arrays.stream(scopeClaim.split(" "))
                .filter(s -> !s.isBlank())
                .map(Scope::of)
                .collect(Collectors.toUnmodifiableSet());
    }
}
