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
package io.keelframework.mcp.observability.logging.filter;

import io.keelframework.mcp.observability.logging.config.LogProperties;
import io.keelframework.mcp.observability.logging.context.McpLogContext;
import io.keelframework.mcp.observability.logging.handler.McpAuthLoggingHandler;
import io.keelframework.mcp.observability.logging.mdc.McpMdcPopulator;
import io.keelframework.mcp.observability.logging.model.McpLogEntry;
import io.keelframework.mcp.observability.logging.model.TraceOrigin;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * TECHNICAL tracing filter for each incoming HTTP request.
 *
 * <p>Responsibilities:</p>
 * <ol>
 *     <li>Populates {@code McpMdcPopulator} with trace, span, request,
 *         and session identifiers.</li>
 *     <li>Populates {@code McpLogContext} with HTTP request data
 *         (remote address, user agent, method, and URI) once, making it
 *         automatically available to all components running in the current thread.</li>
 *     <li>Emits a TECHNICAL trace through {@code handler.technical()}
 *         when the request completes.</li>
 *     <li>Clears the MDC and {@code McpLogContext} in the {@code finally} block.</li>
 * </ol>
 *
 * <p>The root trace and span identifiers are captured in local variables
 * before {@code chain.doFilter()} is invoked. The MDC may be modified by
 * child components such as authentication, session handling, and tools
 * through {@code newChildSpan()}. Therefore, reading the identifiers from
 * the MDC in the {@code finally} block would return the identifiers of the
 * last child span rather than those of the root request.</p>
 */

@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class McpRequestLoggingFilter extends OncePerRequestFilter {

    private final LogProperties properties;
    private final McpAuthLoggingHandler handler;

    public McpRequestLoggingFilter(LogProperties properties, McpAuthLoggingHandler handler) {
        this.properties = properties;
        this.handler = handler;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        long startNs = System.nanoTime();
        String requestId = McpMdcPopulator.generateRequestId();
        McpMdcPopulator.putRequestId(requestId);

        /**
         * Distributed trace propagation.
         *
         * <p>Captures the root {@code traceId}, {@code spanId}, and {@code traceOrigin}
         * in local variables. These identifiers belong to this filter's span and must
         * not be read from the MDC at the end of the request, as they may have been
         * modified by child components in the meantime.</p>
         */
        McpLogContext.get()
                .remoteAddr(request.getRemoteAddr())
                .userAgent(request.getHeader("User-Agent"))
                .method(request.getMethod())
                .uri(request.getRequestURI());

        /**
         * Root trace and span IDs captured in LOCAL variables.
         *
         * <p>The MDC will be mutated by child components through
         * {@code newChildSpan()}.</p>
         */
        String rootTraceId    = resolveTraceId(request);
        TraceOrigin rootOrigin = resolveTraceOrigin(request);
        String rootSpanId     = resolveSpanId(request);

        McpMdcPopulator.putTraceId(rootTraceId);
        McpMdcPopulator.putTraceOrigin(rootOrigin.name());
        McpMdcPopulator.putSpanId(rootSpanId);

        String sessionId = request.getHeader("Mcp-Session-Id");
        if (sessionId != null && !sessionId.isBlank()) {
            McpMdcPopulator.putSessionId(sessionId);
        }

        try {
            chain.doFilter(request, response);
        } finally {
            long durationMs =
                    (System.nanoTime() - startNs) / 1_000_000;
            try {
                /**
                 * Emits the TECHNICAL trace — {@code isActive()} is checked in {@code emit()}.
                 */
                handler.technical(McpLogEntry.builder()
                        .traceId(rootTraceId)
                        .traceOrigin(rootOrigin)
                        .spanId(rootSpanId)
                        .level(resolveLevel(response.getStatus()))
                        .component("McpRequestLoggingFilter")
                        .requestId(requestId)
                        .httpStatus(response.getStatus())
                        .durationMs(durationMs));
            } finally {
                McpMdcPopulator.clear();
                McpLogContext.clear();
            }
        }
    }
    // ── Helpers ───────────────────────────────────────────────────────────────

    private String resolveTraceId(HttpServletRequest request) {
        String traceId = request.getHeader("traceid");
        return (traceId == null || traceId.isBlank())
                ? McpMdcPopulator.generateTraceId()
                : traceId;
    }

    private TraceOrigin resolveTraceOrigin(HttpServletRequest request) {
        String traceId = request.getHeader("traceid");
        return (traceId == null || traceId.isBlank())
                ? TraceOrigin.INTERNAL
                : TraceOrigin.PROPAGATED;
    }

    private String resolveSpanId(HttpServletRequest request) {
        String spanId = request.getHeader("spanid");
        return (spanId == null || spanId.isBlank())
                ? McpMdcPopulator.generateSpanId()
                : spanId;
    }

    private String resolveLevel(int status) {
        if (status >= 500) return "ERROR";
        if (status >= 400) return "WARN";
        return "INFO";
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getServletPath().startsWith("/actuator");
    }

}
