package io.keelframework.mcp.observability.logging.handler;

import io.keelframework.mcp.observability.logging.config.McpLogSerializer;
import io.keelframework.mcp.observability.logging.config.LogProperties;
import io.keelframework.mcp.observability.logging.context.McpLogContext;
import io.keelframework.mcp.observability.logging.mdc.McpMdcPopulator;
import io.keelframework.mcp.observability.logging.model.McpLogEntry;
import io.keelframework.mcp.observability.logging.model.McpLogType;
import io.keelframework.mcp.observability.logging.model.TraceOrigin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.Optional;

/**
 * Handler central de trazas MCP — 3 métodos genéricos reutilizables.
 *
 * Los campos comunes (traceId, spanId, parentSpanId, requestId, sessionId,
 * clientId, remoteAddr, userAgent, method, uri) se obtienen automáticamente
 * desde McpMdcPopulator y McpLogContext — no hay que pasarlos en cada llamada.
 *
 * Solo se pasan los campos ESPECÍFICOS de cada evento:
 *
 *   // Auth SUCCESS
 *   handler.security(McpLogEntry.builder()
 *           .level("INFO")
 *           .component(COMPONENT_AUTH_FILTER)
 *           .authJwtSubject(ctx.subject())
 *           .authResult("SUCCESS"));
 *
 *   // Tool call
 *   handler.functional(McpLogEntry.builder()
 *           .level("INFO")
 *           .component(COMPONENT_AOP_TOOLS)
 *           .toolName(toolName)
 *           .toolSuccess(true)
 *           .durationMs(durationMs));
 *
 *   // Session registered
 *   handler.technical(McpLogEntry.builder()
 *           .level("INFO")
 *           .component(COMPONENT_SESSION)
 *           .mcpOperation("session/registered"));
 */

public class McpAuthLoggingHandler {
    private static final Logger log =
            LoggerFactory.getLogger(McpAuthLoggingHandler.class);

    // ── Componentes ────────────────────────────────────────────────────────
    public static final String COMPONENT_AUTH_FILTER = "McpAuthenticationFilter";
    public static final String COMPONENT_SESSION      = "McpSessionInterceptor";
    public static final String COMPONENT_AOP_TOOLS    = "McpToolLoggingAspect";
    private static final String FRAMEWORK_PACKAGE = "com.sca.framework.observability";
    // ── Auth results ───────────────────────────────────────────────────────
    public static final String RESULT_SUCCESS = "SUCCESS";
    public static final String RESULT_FAILURE = "FAILURE";

    private final LogProperties properties;

    public McpAuthLoggingHandler(LogProperties properties) {
        this.properties = properties;
    }

    // ── 3 métodos genéricos públicos ────────────────────────────────────────

    public void security(McpLogEntry.Builder builder) {

        emit(McpLogType.SECURITY, builder);
    }

    public void functional(McpLogEntry.Builder builder) {

        emit(McpLogType.FUNCTIONAL, builder);
    }

    public void technical(McpLogEntry.Builder builder) {

        emit(McpLogType.TECHNICAL, builder);
    }

    // ── Núcleo común — puebla contexto automáticamente ────────────────────

    private void emit(McpLogType type, McpLogEntry.Builder builder) {
        if (!properties.isActive(type)) return;

        String spanId = McpMdcPopulator.newChildSpan();
        McpLogContext ctx = McpLogContext.get();

        McpLogEntry entry = builder
                .eventType(type)
                // ── del MDC ──────────────────────────────────────────────
                .traceId(safe(McpMdcPopulator.getTraceId()))
                .traceOrigin(resolveTraceOrigin())
                .spanId(spanId)
                .parentSpanId(McpMdcPopulator.getParentSpanId())
                .requestId(McpMdcPopulator.getRequestId())
                .sessionId(McpMdcPopulator.getSessionId())
                .clientId(McpMdcPopulator.getClientId())
                // ── del McpLogContext ────────────────────────────────────
                .remoteAddr(ctx.remoteAddr())
                .userAgent(ctx.userAgent())
                .method(ctx.method())
                .uri(ctx.uri())
                .build();

        String json = McpLogSerializer.toJson(entry);

        switch (entry.level()) {
            case "ERROR" -> log.error("{}", json);
            case "WARN"  -> log.warn("{}", json);
            default      -> log.info("{}", json);
        }
    }

    // ── Helpers ────────────────────────────────────────────────────────────

    public String resolveErrorCode(String reason) {
        if (reason == null) return "AUTH_000";
        String normalized = reason.toLowerCase(Locale.ROOT);
        if (normalized.contains("expired"))   return "AUTH_001";
        if (normalized.contains("signature")) return "AUTH_002";
        if (normalized.contains("issuer"))    return "AUTH_003";
        if (normalized.contains("audience"))  return "AUTH_004";
        if (normalized.contains("key"))       return "AUTH_005";
        if (normalized.contains("session"))   return "SESSION_001";
        return "AUTH_999";
    }

    private String safe(String value) {
        return Optional.ofNullable(value).orElse(null);
    }

    private TraceOrigin resolveTraceOrigin() {
        String origin = McpMdcPopulator.getTraceOrigin();
        if (origin == null) return null;
        try {
            return TraceOrigin.valueOf(origin);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    // ── StackWalker — detecta la clase que llama al framework ────────────────

    /**
     * Traza TECHNICAL para llamadas a servicios backend desde tools MCP.
     *
     * El componente (nombre de la clase que llama) se detecta automáticamente
     * via StackWalker — igual que LoggerFactory.getLogger() obtiene el nombre
     * de la clase sin que el desarrollador lo pase explícitamente.
     *
     * @param serviceName     nombre del servicio backend (ej: "product-service")
     * @param serviceUrl      URL o path invocado (ej: "/product/3")
     * @param serviceStatus   HTTP status devuelto por el backend
     * @param serviceDurationMs duración de la llamada en milisegundos
     */
    public void logTool(String serviceName,
                            String serviceUrl,
                            int serviceStatus,
                            long serviceDurationMs) {

        // component detectado automáticamente — el dev no lo pasa
        String component = resolveCallerClass();

        // level inferido del status — el dev no lo pasa
        String level = serviceStatus >= 400 ? "WARN" : "INFO";

        technical(McpLogEntry.builder()
                .level(level)
                .component(component)
                .serviceName(serviceName)
                .serviceUrl(serviceUrl)
                .serviceStatus(serviceStatus)
                .serviceDurationMs(serviceDurationMs));
    }

    /**
     * Obtiene el nombre simple de la clase que invocó al framework,
     * saltando los frames internos del paquete de observabilidad.
     */
    private static String resolveCallerClass() {
        return StackWalker.getInstance()
                .walk(frames -> frames
                        .filter(f -> !f.getClassName()
                                .startsWith(FRAMEWORK_PACKAGE))
                        .findFirst()
                        .map(f -> {
                            String fqcn = f.getClassName();
                            return fqcn.substring(fqcn.lastIndexOf('.') + 1);
                        })
                        .orElse("unknown"));
    }
}
