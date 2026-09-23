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
package io.keelframework.mcp.adapters.auth.idp.validator;

import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import io.keelframework.mcp.adapters.auth.idp.jwks.JwksKeyResolutionException;
import io.keelframework.mcp.adapters.auth.idp.jwks.JwksKeyResolver;
import io.keelframework.mcp.common.jwt.provider.JwtClaims;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.stream.Collectors;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class TokenValidator {

    private static final Logger log =
            LoggerFactory.getLogger(TokenValidator.class);

    private final JwksKeyResolver keyResolver;
    private final String expectedIssuer;
    private final String expectedAudience; // null = no se valida audience

    /**
     * Valida el JWT y retorna un TokenValidationResult sellado.
     * Nunca lanza excepción — los errores se modelan como Invalid.
     *
     * @param rawJwt token JWT en formato compacto
     * @return Valid con JwtClaims, o Invalid con el motivo del rechazo
     */
    public TokenValidationResult validate(String rawJwt) {
        try {
            // 1. Parseo
            SignedJWT signedJWT = SignedJWT.parse(rawJwt);

            // 2. Resolución de clave pública
            RSAPublicKey publicKey = keyResolver.resolve(rawJwt);

            // 3. Verificación de firma
            JWSVerifier verifier = new RSASSAVerifier(publicKey);
            if (!signedJWT.verify(verifier)) {
                log.warn("JWT signature verification failed");
                return TokenValidationResult.invalid("Invalid JWT signature");
            }

            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

            // 4. Expiración
            Date expiration = claims.getExpirationTime();
            if (expiration == null || expiration.before(new Date())) {
                log.debug("JWT expired at: {}", expiration);
                return TokenValidationResult.invalid("JWT token has expired");
            }

            // 5. Issuer
            String issuer = claims.getIssuer();
            if (!expectedIssuer.equals(issuer)) {
                log.warn("JWT issuer mismatch. Expected: {}, got: {}", expectedIssuer, issuer);
                return TokenValidationResult.invalid("JWT issuer mismatch");
            }

            // 6. Audience (opcional)
            if (expectedAudience != null) {
                List<String> audiences = claims.getAudience();
                if (audiences == null || !audiences.contains(expectedAudience)) {
                    log.warn("JWT audience mismatch. Expected: {}, got: {}", expectedAudience, audiences);
                    return TokenValidationResult.invalid("JWT audience mismatch");
                }
            }

            // 7. Extracción de claims
            JwtClaims jwtClaims = extractClaims(claims);
            log.debug("JWT validated successfully for subject: {}", jwtClaims.subject());
            return TokenValidationResult.valid(jwtClaims);

        } catch (JwksKeyResolutionException e) {
            log.warn("JWKS key resolution failed: {}", e.getMessage());
            return TokenValidationResult.invalid("Could not resolve signing key: " + e.getMessage());
        } catch (Exception e) {
            log.warn("JWT validation error: {}", e.getMessage());
            return TokenValidationResult.invalid("JWT validation error: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private JwtClaims extractClaims(JWTClaimsSet claims) throws Exception {
        // realm_access.roles
        List<String> realmRoles = List.of();
        Map<String, Object> realmAccess = (Map<String, Object>) claims.getClaim("realm_access");
        if (realmAccess != null && realmAccess.get("roles") instanceof List<?> roles) {
            realmRoles = roles.stream()
                    .filter(r -> r instanceof String)
                    .map(r -> (String) r)
                    .toList();
        }

        // scope → some IdPs return a single space-separated String
        // (e.g. "api read write"), others return a JSON array
        // (e.g. ["api", "read"]). Handle both.
        String scopes = extractScope(claims.getClaim("scope"));

        Date iat = claims.getIssueTime();
        Date exp = claims.getExpirationTime();

        return new JwtClaims(
                claims.getSubject(),
                (String) claims.getClaim("preferred_username"),
                (String) claims.getClaim("email"),
                claims.getIssuer(),
                claims.getJWTID(),
                claims.getAudience(),
                realmRoles,
                scopes,          // String, space-separated
                iat != null ? iat.toInstant() : null,
                exp != null ? exp.toInstant() : null,
                claims.toJSONObject()
        );
    }

    /**
     * Normalizes the "scope" claim regardless of how the IdP represents
     * it: as a single space-separated String (most common, per RFC
     * 6749/8693 convention), or as a JSON array of individual scope
     * strings (used by some providers, e.g. Duende IdentityServer).
     */
    private String extractScope(Object rawScope) {
        if (rawScope == null) {
            return null;
        }
        if (rawScope instanceof String s) {
            return s;
        }
        if (rawScope instanceof List<?> list) {
            return list.stream()
                    .filter(item -> item instanceof String)
                    .map(item -> (String) item)
                    .collect(Collectors.joining(" "));
        }
        log.warn("Unexpected type for 'scope' claim: {}", rawScope.getClass());
        return null;
    }
}
