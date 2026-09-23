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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestClient;
import java.text.ParseException;

@Slf4j
@RequiredArgsConstructor
public class JwksRemoteLoader {

    private final RestClient restClient;

    /**
     * Downloads the JWKS from the endpoint and parses it as a Nimbus JWKSet.
     *
     * @param jwksUri URI del endpoint JWKS
     * @return JWKSet parseado
     * @throws JwksLoadException si la descarga o el parseo falla
     */
    public JWKSet load(String jwksUri) {
        log.debug("Loading JWKS from remote endpoint: {}", jwksUri);
        try {
            String rawJson = restClient.get()
                    .uri(jwksUri)
                    .retrieve()
                    .body(String.class);

            if (rawJson == null || rawJson.isBlank()) {
                throw new JwksLoadException("Empty JWKS response from: " + jwksUri);
            }

            JWKSet jwkSet = JWKSet.parse(rawJson);
            log.info("JWKS loaded successfully from {}. Keys: {}", jwksUri, jwkSet.getKeys().size());
            return jwkSet;

        } catch (ParseException e) {
            throw new JwksLoadException("Failed to parse JWKS from: " + jwksUri, e);
        } catch (Exception e) {
            throw new JwksLoadException("Failed to load JWKS from: " + jwksUri, e);
        }
    }


}
