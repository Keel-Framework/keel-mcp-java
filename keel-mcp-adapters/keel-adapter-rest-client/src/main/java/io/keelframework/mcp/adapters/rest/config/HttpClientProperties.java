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
 * Propiedades del pool de conexiones HTTP.
 *
 * Estandar de arquitectura SCA — architecture.httpclient.*
 *
 * architecture:
 *   mcp:
 *   httpclient:
 *     socket-timeout:            60000
 *     connect-timeout:           60000
 *     request-timeout:           60000
 *     max-total-connections:     200
 *     max-connections-per-route: 20
 *     keep-alive:                10000
 */
@ConfigurationProperties(prefix = "keel.adapters.rest-client.http-client")
public record HttpClientProperties(


    @DefaultValue("60000") int connectTimeout,
    @DefaultValue("60000") int socketTimeout,
    @DefaultValue("60000") int requestTimeout,
    @DefaultValue("200") int maxTotalConnections,
    @DefaultValue("20") int maxConnectionsPerRoute,
    @DefaultValue("300000") long connectionTimeToLive,
    @DefaultValue("10000") long keepAlive

) {}
