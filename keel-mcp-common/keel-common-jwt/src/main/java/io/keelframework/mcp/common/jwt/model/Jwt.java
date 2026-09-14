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
