package io.keelframework.mcp.observability.logging.mdc;

import org.slf4j.MDC;

import java.security.SecureRandom;
import java.util.HexFormat;
import java.util.UUID;

/**
 * Gestiona el MDC para trazas MCP.
 *
 * Modelo de tracing:
 *  - traceId       → ÚNICO por toda la transacción, no cambia entre componentes
 *  - traceOrigin   → INTERNAL si el MCP Server generó el traceId (origen de
 *                     la transacción), PROPAGATED si vino de fuera
 *  - spanId        → propio de CADA componente/operación dentro de la transacción
 *  - parentSpanId  → el spanId del componente que precedió a este, permite
 *                     reconstruir el árbol jerárquico completo en Kibana
 *
 *  Formato de IDs — W3C Trace Context (sin dependencia de OpenTelemetry):
 *  *  - traceId → 32 caracteres hex (128 bits)
 *  *  - spanId  → 16 caracteres hex (64 bits)
 */
public class McpMdcPopulator {

    public static final String TRACE_ID   = "traceId";
    public static final String TRACE_ORIGIN    = "traceOrigin";
    public static final String SPAN_ID    = "spanId";
    public static final String PARENT_SPAN_ID  = "parentSpanId";
    public static final String REQUEST_ID = "requestId";
    public static final String SESSION_ID = "sessionId";
    public static final String CLIENT_ID  = "clientId";

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final HexFormat HEX = HexFormat.of();

    private McpMdcPopulator() {}


    public static String generateRequestId() {
        return UUID.randomUUID().toString();
    }


    /**
     * Genera un traceId con formato W3C Trace Context — 32 caracteres hex
     * (128 bits).
     */
    public static String generateTraceId() {
        byte[] bytes = new byte[16];
        SECURE_RANDOM.nextBytes(bytes);
        return HEX.formatHex(bytes);
    }

    /**
     * Genera un spanId con formato W3C Trace Context — 16 caracteres hex
     * (64 bits).
     */
    public static String generateSpanId() {
        byte[] bytes = new byte[8];
        SECURE_RANDOM.nextBytes(bytes);
        return HEX.formatHex(bytes);
    }

    public static void put(String key, String value) {

        if (value != null && !value.isBlank())
            MDC.put(key, value);
    }

    public static void putRequestId(String v)     { put(REQUEST_ID, v); }
    public static void putSessionId(String v)     { put(SESSION_ID, v); }
    public static void putClientId(String v)      { put(CLIENT_ID, v); }
    public static void putTraceId(String v)       { put(TRACE_ID, v); }
    public static void putTraceOrigin(String v)   { put(TRACE_ORIGIN, v); }
    public static void putSpanId(String v)        { put(SPAN_ID, v); }
    public static void putParentSpanId(String v)  { put(PARENT_SPAN_ID, v); }

    public static String getTraceId()       { return MDC.get(TRACE_ID); }
    public static String getTraceOrigin()   { return MDC.get(TRACE_ORIGIN); }
    public static String getSpanId()        { return MDC.get(SPAN_ID); }
    public static String getParentSpanId()  { return MDC.get(PARENT_SPAN_ID); }
    public static String getRequestId()     { return MDC.get(REQUEST_ID); }
    public static String getSessionId()     { return MDC.get(SESSION_ID); }
    public static String getClientId()      { return MDC.get(CLIENT_ID); }

    /**
     * Abre un nuevo span hijo: el spanId actual (si existe) pasa a ser
     * parentSpanId, y se genera un spanId nuevo (formato W3C) que queda
     * activo en el MDC.
     *
     * Cada componente (auth, session, tool call, llamada a backend) debe
     * llamar a este método al iniciar su trabajo, y usar el valor devuelto
     * como spanId de su propio McpLogEntry, junto con
     * McpMdcPopulator.getParentSpanId() para el campo parentSpanId.
     *
     * @return el nuevo spanId generado, ya activo en el MDC
     */
    public static String newChildSpan() {
        String currentSpanId = getSpanId();
        if (currentSpanId != null && !currentSpanId.isBlank()) {
            putParentSpanId(currentSpanId);
        }
        String newSpanId = generateSpanId();
        putSpanId(newSpanId);
        return newSpanId;
    }


    public static void clear() {
        MDC.remove(TRACE_ID);
        MDC.remove(TRACE_ORIGIN);
        MDC.remove(SPAN_ID);
        MDC.remove(PARENT_SPAN_ID);
        MDC.remove(REQUEST_ID);
        MDC.remove(SESSION_ID);
        MDC.remove(CLIENT_ID);
    }
}
