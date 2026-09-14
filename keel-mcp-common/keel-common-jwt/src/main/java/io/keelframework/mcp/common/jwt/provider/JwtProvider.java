package io.keelframework.mcp.common.jwt.provider;

/**
 * Contrato para obtener el JWT del contexto de la request actual.
 */
public interface JwtProvider {
    String getJwt();
}
