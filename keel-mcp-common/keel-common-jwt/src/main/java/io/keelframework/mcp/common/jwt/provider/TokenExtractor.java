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

        return new Jwt(
                claims.subject(),
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
