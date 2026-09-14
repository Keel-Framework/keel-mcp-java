package io.keelframework.mcp.observability.logging.model;
/**
 * Origen del traceId de la transacción.
 *
 * INTERNAL    → el MCP Server generó el traceId porque es el origen de la
 *               transacción (caso normal: invocado por un MCP Client/LLM
 *               que no participa del esquema de trazabilidad SCA).
 *
 * PROPAGATED  → el traceId vino propagado desde un sistema aguas arriba
 *               (otro microservicio SCA que ya formaba parte de una
 *               transacción más amplia).
 */
public enum TraceOrigin {
    INTERNAL,
    PROPAGATED
}
