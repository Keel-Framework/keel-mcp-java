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
     * Descarga el JWKS del endpoint y lo parsea como JWKSet Nimbus.
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
