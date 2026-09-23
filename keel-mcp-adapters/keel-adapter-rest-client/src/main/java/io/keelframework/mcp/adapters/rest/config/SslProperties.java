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
package io.keelframework.mcp.adapters.rest.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * SSL properties for the outbound HTTP client (RestClient).
 *
 * keel:
 *   adapters:
 *     rest-client:
 *       ssl:
 *         enabled:              true
 *         trust-store:          classpath:ssl/truststore.jks
 *         trust-store-password: ${TRUSTSTORE_PASSWORD:changeit}
 *         trust-store-type:     JKS
 */
@ConfigurationProperties(prefix = "keel.adapters.rest-client.ssl")
public record SslProperties(

        /** Enables SSL/TLS on the HTTP client. Default: false */
        @DefaultValue("false") boolean enabled,

        /** Path to the truststore containing corporate certificates. */
        String trustStore,

        /** Password del truststore. Default: changeit */
        @DefaultValue("changeit") String trustStorePassword,

        /** Truststore type. Default: JKS */
        @DefaultValue("JKS") String trustStoreType

) {}
