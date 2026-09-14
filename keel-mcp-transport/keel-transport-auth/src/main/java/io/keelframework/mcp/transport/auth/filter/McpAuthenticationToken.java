package io.keelframework.mcp.transport.auth.filter;

import io.keelframework.mcp.common.jwt.model.Authority;
import io.keelframework.mcp.common.jwt.model.Jwt;
import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.util.stream.Collectors;

/**
 * Token de autenticación de Spring Security que envuelve el Jwt.
 * Se establece en el SecurityContextHolder tras una validación exitosa del JWT.
 *
 * Siempre está autenticado (authenticated=true) porque solo se crea
 * después de que KeycloakTokenValidator confirma la validez del JWT.
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
