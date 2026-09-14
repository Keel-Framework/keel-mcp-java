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
 * Filtro de trazas TECHNICAL — cada request HTTP entrante.
 *
 * Responsabilidades:
 *  1. Puebla McpMdcPopulator (traceId, spanId, requestId, sessionId)
 *  2. Puebla McpLogContext (remoteAddr, userAgent, method, uri) — UNA VEZ
 *     para que todos los componentes del thread los lean automáticamente
 *  3. Emite traza TECHNICAL via handler.technical() al final del request
 *  4. Limpia MDC y McpLogContext en el finally
 *
 * El traceId/spanId raíz se capturan en variables locales antes de
 * chain.doFilter() porque el MDC será mutado por los componentes hijos
 * (auth, session, tools) vía newChildSpan() — no se puede leer del
 * MDC en el finally ya que pertenecería al último span hijo.
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

        // ── Propagación de trazas distribuidas ────────────────────────────
        // Captura traceId/spanId/traceOrigin RAÍZ en variables locales —
        // son el spanId de ESTE filtro, no se deben leer del MDC al final
        // porque para entonces ya fueron mutados por los componentes hijos.
        McpLogContext.get()
                .remoteAddr(request.getRemoteAddr())
                .userAgent(request.getHeader("User-Agent"))
                .method(request.getMethod())
                .uri(request.getRequestURI());

        // ── traceId/spanId raíz capturados en variables LOCALES ──────────────
        // el MDC será mutado por los hijos vía newChildSpan()
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
                // ── Emite traza TECHNICAL — isActive() verificado en emit() ──
                handler.technical(McpLogEntry.builder()
                        // sobreescribe los campos MDC con los valores RAÍZ locales
                        // porque emit() leerá el MDC ya mutado por los hijos
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
