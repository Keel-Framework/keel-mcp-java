package io.keelframework.mcp.adapters.rest.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * Propiedades SSL del cliente HTTP saliente (RestClient).
 *
 * sca:
 *   mcp:
 *     adapters:
 *       rest:
 *         ssl:
 *           enabled:              true
 *           trust-store:          classpath:ssl/truststore.jks
 *           trust-store-password: ${TRUSTSTORE_PASSWORD:changeit}
 *           trust-store-type:     JKS
 */
@ConfigurationProperties(prefix = "sca.mcp.adapters.rest.ssl")
public record SslProperties(

        /** Activa SSL/TLS en el cliente HTTP. Default: false */
        @DefaultValue("false") boolean enabled,

        /** Ruta al truststore con certificados corporativos. */
        String trustStore,

        /** Password del truststore. Default: changeit */
        @DefaultValue("changeit") String trustStorePassword,

        /** Tipo del truststore. Default: JKS */
        @DefaultValue("JKS") String trustStoreType

) {}
