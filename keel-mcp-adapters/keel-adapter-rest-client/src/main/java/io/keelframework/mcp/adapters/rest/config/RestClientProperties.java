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

import java.util.Map;

/**
 * Backend service properties for MCP tools.
 *
 * The developer only configures the URLs of their services.
 * Propagation headers are managed internally by the SCA ecosystem.
 *
 * keel:
 *     adapters:
 *     rest-client:
 *       services:
 *         pagos:
 *           base-url:     https://api.pagos.sca.es
 *           log-requests: true
 *         clientes:
 *           base-url:     https://api.clientes.sca.es
 */

@ConfigurationProperties(prefix = "keel.adapters.rest-client")
public record RestClientProperties(

    /*
    * Map of backend microservices.
    * Key = service name used in McpRestClientFactory.getClient()
    */
    Map<String, ServiceConfig> services
){
    public RestClientProperties{
        if (services == null)
            services = Map.of();
    }
    public record ServiceConfig(
            /* URL base del service */
            String baseUrl,
            /* Activa logging detallado de requests. Default: false */
            boolean logRequests
    ){}
}
