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
package io.keelframework.mcp.adapters.auth.idp.jwks;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.SignedJWT;
import lombok.extern.slf4j.Slf4j;

import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Resolves an RSAPublicKey from the {@code kid} in the JWT header.
 * Maintains its own internal cache implemented with ConcurrentHashMap
 * and a per-entry TTL.
 */
@Slf4j
public class JwksKeyResolver {

    private final JwksRemoteLoader remoteLoader;
    private final String jwksUri;
    private final Duration ttl;

    /** Main cache: kid → public key */
    private final ConcurrentHashMap<String, RSAPublicKey> keyCache = new ConcurrentHashMap<>();

    /** Per-entry TTL: kid → expiration timestamp */
    private final ConcurrentHashMap<String, Instant> expiryCache = new ConcurrentHashMap<>();

    /** Lock por kid para evitar stampede en cache miss simultáneo */
    private final ConcurrentHashMap<String, ReentrantLock> locks = new ConcurrentHashMap<>();

    public JwksKeyResolver(JwksRemoteLoader remoteLoader, String jwksUri, Duration ttl) {
        this.remoteLoader = remoteLoader;
        this.jwksUri = jwksUri;
        this.ttl = ttl;
    }

    /**
     * Dado un JWT raw, extrae el kid del header y resuelve la RSAPublicKey.
     *
     * @param rawJwt token JWT en formato compacto
     * @return RSAPublicKey para verificar la firma
     * @throws JwksKeyResolutionException si el kid no existe o no se puede resolver
     */
    public RSAPublicKey resolve(String rawJwt) {
        String kid = extractKid(rawJwt);
        log.debug("Resolving public key for kid: {}", kid);

        return resolveByKid(kid)
                .orElseThrow(() -> new JwksKeyResolutionException(
                        "No public key found for kid: " + kid));
    }

    private Optional<RSAPublicKey> resolveByKid(String kid) {
        // Lectura sin bloqueo — caso más frecuente
        if (isCached(kid)) {
            log.debug("Cache HIT for kid: {}", kid);
            return Optional.of(keyCache.get(kid));
        }

        ReentrantLock lock = locks.computeIfAbsent(kid, k -> new ReentrantLock());
        lock.lock();
        try {
            // Double-check: otro hilo puede haberlo cargado mientras esperábamos
            if (isCached(kid)) {
                log.debug("Cache HIT after lock for kid: {}", kid);
                return Optional.of(keyCache.get(kid));
            }

            log.debug("Cache MISS for kid: {}. Loading from remote: {}", kid, jwksUri);
            loadAndCacheAll();

            RSAPublicKey resolved = keyCache.get(kid);
            if (resolved == null) {
                log.warn("kid '{}' not found in JWKS after remote load", kid);
                return Optional.empty();
            }
            return Optional.of(resolved);

        } finally {
            lock.unlock();
            locks.remove(kid); // Limpiar lock para no acumular entradas
        }
    }

    private boolean isCached(String kid) {
        Instant expiry = expiryCache.get(kid);
        if (expiry == null || Instant.now().isAfter(expiry)) {
            if (expiry != null) {
                log.debug("Cache EXPIRED for kid: {}", kid);
                keyCache.remove(kid);
                expiryCache.remove(kid);
            }
            return false;
        }
        return keyCache.containsKey(kid);
    }

    @SuppressWarnings("unchecked")
    private void loadAndCacheAll() {
        JWKSet jwkSet = remoteLoader.load(jwksUri);
        Instant expiry = Instant.now().plus(ttl);

        jwkSet.getKeys().stream()
                .filter(k -> k instanceof RSAKey)
                .map(k -> (RSAKey) k)
                .forEach(rsaKey -> {
                    try {
                        RSAPublicKey publicKey = rsaKey.toRSAPublicKey();
                        keyCache.put(rsaKey.getKeyID(), publicKey);
                        expiryCache.put(rsaKey.getKeyID(), expiry);
                        log.debug("Cached RSA key kid: {} expires at: {}", rsaKey.getKeyID(), expiry);
                    } catch (Exception e) {
                        log.warn("Could not extract RSA public key for kid: {}", rsaKey.getKeyID(), e);
                    }
                });

        log.info("JWKS loaded and cached. Keys: {}. TTL: {}", jwkSet.getKeys().size(), ttl);
    }

    private String extractKid(String rawJwt) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(rawJwt);
            String kid = signedJWT.getHeader().getKeyID();
            if (kid == null || kid.isBlank()) {
                throw new JwksKeyResolutionException("JWT header missing 'kid' claim");
            }
            return kid;
        } catch (ParseException e) {
            throw new JwksKeyResolutionException("Failed to parse JWT for kid extraction", e);
        }
    }

    /** Invalida toda la caché forzando recarga en la siguiente petición. */
    public void invalidateAll() {
        keyCache.clear();
        expiryCache.clear();
        log.info("JWKS internal cache invalidated");
    }

    public int cachedKeyCount() {
        return keyCache.size();
    }



}
