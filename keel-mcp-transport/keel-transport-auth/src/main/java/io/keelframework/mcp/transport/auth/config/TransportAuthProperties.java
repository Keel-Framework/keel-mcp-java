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
package io.keelframework.mcp.transport.auth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import java.time.Duration;
import java.util.List;

/**
 * Configuration properties for the transport-auth-service module.
 *
 * Example application.yml:
 *
 * keel:
 *   mcp:
 *     transport:
 *       auth:
 *         enabled: true
 *         jwt:
 *           issuer:
 *           audience:       # optional
 *           jwks-ttl: 1h
 *           jwks-uri:
 *         excluded-paths:
 *           - /actuator/health
 *           - /actuator/info
 */
@Data
@ConfigurationProperties(prefix = "keel.mcp.transport.auth")
public class TransportAuthProperties {

    /** Activa o desactiva el filtro de autenticación JWT. Default: false */
    private boolean enabled = false;

    private Jwt jwt = new Jwt();
    private List<String> excludedPaths = List.of("/actuator/health", "/actuator/info");

    @Data
    public static class Jwt {

        /** Expected 'iss' claim in the token — Keycloak/Red Hat SSO realm URL */
        private String issuer;

        /** Expected 'aud' claim in the token. Null or empty = validation disabled */
        private String audience;

        /** JWKS endpoint URI. If not configured, it is inferred from the issuer. */
        private String jwksUri;

        /** TTL for cached keys. Default: 1 hour */
        private Duration jwksTtl = Duration.ofHours(1);
    }

    /**
     * Builds the JWKS URI from the issuer if not explicitly configured.
     */
    public String resolveJwksUri() {
        if (jwt.getJwksUri() == null || jwt.getJwksUri().isBlank()) {
            throw new IllegalStateException(
                    "keel.mcp.transport.auth.jwt.jwks-uri is required when auth is enabled. " +
                            "Note: jwks-uri is usually different from issuer (e.g. Keycloak appends " +
                            "/protocol/openid-connect/certs to the realm URL).");
        }
        return jwt.getJwksUri();
    }

    /** Devuelve el audience o null si no está configurado */
    public String resolveAudience() {
        String aud = jwt.getAudience();
        return (aud == null || aud.isBlank()) ? null : aud;
    }
}

