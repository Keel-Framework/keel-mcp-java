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
package io.keelframework.mcp.observability.logging.mdc;

import org.slf4j.MDC;

import java.security.SecureRandom;
import java.util.HexFormat;
import java.util.UUID;

/**
 * Manages the MDC for MCP tracing.
 *
 * <p>Tracing model:</p>
 * <ul>
 *     <li>{@code traceId} — unique across the entire transaction and remains
 *         unchanged between components.</li>
 *     <li>{@code traceOrigin} — {@code INTERNAL} if the MCP Server generated
 *         the trace ID (transaction origin), or {@code PROPAGATED} if it was
 *         received from an external source.</li>
 *     <li>{@code spanId} — unique to each component or operation within
 *         the transaction.</li>
 *     <li>{@code parentSpanId} — the span ID of the component that preceded
 *         the current one, allowing the complete hierarchical tree to be
 *         reconstructed in Kibana.</li>
 * </ul>
 *
 * <p>ID format — W3C Trace Context, without an OpenTelemetry dependency:</p>
 * <ul>
 *     <li>{@code traceId} — 32 hexadecimal characters (128 bits).</li>
 *     <li>{@code spanId} — 16 hexadecimal characters (64 bits).</li>
 * </ul>
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
     * Opens a new child span: the current span ID, if present, becomes the
     * {@code parentSpanId}, and a new W3C-compliant span ID is generated and
     * stored as the active span in the MDC.
     *
     * <p>Each component (authentication, session, tool call, backend request)
     * should call this method when starting its work and use the returned value
     * as the span ID of its own {@code McpLogEntry}, together with
     * {@code McpMdcPopulator.getParentSpanId()} for the {@code parentSpanId}
     * field.</p>
     *
     * @return the newly generated span ID, now active in the MDC
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
