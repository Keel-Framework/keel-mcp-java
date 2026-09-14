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
