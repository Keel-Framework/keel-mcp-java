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

import io.keelframework.mcp.common.jwt.model.Authority;
import io.keelframework.mcp.common.jwt.model.Jwt;
import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.util.stream.Collectors;

/**
 * Spring Security authentication token that wraps the Jwt.
 * Set in the SecurityContextHolder after successful JWT validation.
 *
 * Always authenticated (authenticated=true) because it is only created
 * after KeycloakTokenValidator confirms the JWT is valid.
 */

public class McpAuthenticationToken extends AbstractAuthenticationToken {

    private final Jwt tokenJwt;
    private final String rawToken;

    public McpAuthenticationToken(Jwt tokenJwt, String rawToken) {
        super(tokenJwt.realmRoles().stream()
                .map(r -> (Authority) Authority.of(r.name()))
                .collect(Collectors.toSet()));
        this.tokenJwt = tokenJwt;
        this.rawToken = rawToken;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return rawToken;
    }

    @Override
    public Jwt getPrincipal() {
        return tokenJwt;
    }

}
