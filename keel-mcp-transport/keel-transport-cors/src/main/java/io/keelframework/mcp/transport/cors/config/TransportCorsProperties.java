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
package io.keelframework.mcp.transport.cors.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * CORS configuration properties for the MCP endpoint.
 *
 * <pre>
 * keel:
 *   mcp:
 *     transport:
 *       cors:
 *         enabled: true
 *         path-pattern: /mcp/**
 *         allowed-origins:
 *           - https://mcp-inspector-route-domain-demo.apps.example.cloud
 *         allowed-methods: [GET, POST, OPTIONS]
 *         allowed-headers: ["*"]
 *         exposed-headers: [Mcp-Session-Id]
 *         allow-credentials: true
 *         max-age: 3600
 * </pre>
 */
@Data
@ConfigurationProperties(prefix = "keel.mcp.transport.cors")
public class TransportCorsProperties {

    /** Enables or disables CORS configuration for the MCP endpoint. Default: false */
    private boolean enabled = false;

    /** Path pattern to which CORS applies. Default: /mcp/** */
    private String pathPattern = "/mcp/**";

    /**
     * Allowed origins (scheme + host + port, without path).
     * Supports patterns (e.g. "https://*.keel.es") via allowedOriginPatterns.
     * Required when enabled = true.
     */
    private List<String> allowedOrigins = List.of();

    /** Allowed HTTP methods. Default: GET, POST, OPTIONS */
    private List<String> allowedMethods = List.of("GET", "POST", "OPTIONS");

    /** Allowed request headers. Default: all ("*") */
    private List<String> allowedHeaders = List.of("*");

    /**
     * Response headers exposed to browser JavaScript.
     * IMPORTANT: must include "Mcp-Session-Id", otherwise the browser-based
     * MCP client cannot read the session ID returned during initialization.
     */
    private List<String> exposedHeaders = List.of("Mcp-Session-Id");

    /**
     * Allows credentials to be sent (cookies, Authorization header).
     * If true, allowedOrigins cannot contain the literal "*" — use
     * explicit origins or patterns instead.
     */
    private boolean allowCredentials = true;

    /** Time in seconds that the browser caches the preflight response. Default: 3600 */
    private long maxAge = 3600;
}
