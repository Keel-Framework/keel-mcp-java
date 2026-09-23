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
package io.keelframework.mcp.adapters.rest.interceptor;

import java.util.List;

/**
 * Standard KeelFramework headers for propagation between the MCP Server and microservices.
 *
 * INTERNAL to the ecosystem — not configurable by the developer.
 * Defined by the SCA Architecture team as a mandatory standard.
 *
 * These headers are automatically propagated on every outbound HTTP request
 * from the REST adapter to backend microservices.
 *
 * McpHeadersInterceptor manages them transparently.
 */

public final class McpHeaders {

    private McpHeaders() {}

    // ================================================
    // JWT — always propagated
    // ================================================
    /**
     * JWT authentication header.
     * ALWAYS propagated from the SecurityContextHolder.
     * Value: "Bearer <jwt-token>"
     */
    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    // ================================================
    // SCA PROPAGATION HEADERS
    // Architecture standard — not configurable
    // ================================================
    /**
     * Request source channel.
     * Example: WEB, MOBILE, API, BATCH
     */
    public static final String CHANNEL = "channel";

    /**
     * Request origin identifier.
     */
    public static final String LOCALE = "locale";

    /**
     * Origin application identifier.
     */
    public static final String APPLICATION_ID = "applicationid";


    /**
     * Distributed trace identifier.
     */
    public static final String TRACE_ID = "traceid";

    /**
     * Current span identifier within the trace.
     */
    public static final String SPAN_ID = "spanid";


    /**
     * Lista ordenada de headers a propagar en cada request saliente.
     * Se leen del MDC y se añaden automaticamente via McpHeadersInterceptor.
     *
     * El MDC es poblado por el interceptor o filtro del servidor MCP
     * al recibir el request del cliente.
     */
    public static final List<String> PROPAGATION_HEADERS = List.of(
            CHANNEL,
            APPLICATION_ID,
            TRACE_ID,
            SPAN_ID
    );

}
