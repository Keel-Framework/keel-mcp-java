package io.keelframework.mcp.observability.logging.context;


/**
 * Contexto del request HTTP disponible durante todo el ciclo de vida
 * del thread — se puebla una vez en McpRequestLoggingFilter y se lee
 * automáticamente en McpAuthLoggingHandler.emit() sin necesidad de
 * pasarlo explícitamente en cada traza.
 *
 * Mismo patrón que MDC pero para campos del request HTTP (remoteAddr,
 * userAgent, method, uri) que no son String simples o no encajan en MDC.
 *
 * Ciclo de vida:
 *   1. McpRequestLoggingFilter.doFilterInternal() → McpLogContext.get().populate(request)
 *   2. Cualquier componente del thread → McpLogContext.get().remoteAddr() etc.
 *   3. McpRequestLoggingFilter.finally → McpLogContext.clear()
 */
public class McpLogContext {

    private static final ThreadLocal<McpLogContext> CONTEXT =
            ThreadLocal.withInitial(McpLogContext::new);

    private String remoteAddr;
    private String userAgent;
    private String method;
    private String uri;

    private McpLogContext() {}

    public static McpLogContext get() {
        return CONTEXT.get();
    }

    public static void clear() {
        CONTEXT.remove();
    }

    // ── Populate ─────────────────────────────────────────────────────────────
    public McpLogContext remoteAddr(String v) { this.remoteAddr = v; return this; }
    public McpLogContext userAgent(String v)  { this.userAgent = v;  return this; }
    public McpLogContext method(String v)     { this.method = v;     return this; }
    public McpLogContext uri(String v)        { this.uri = v;        return this; }

    // ── Getters ───────────────────────────────────────────────────────────────
    public String remoteAddr() { return remoteAddr; }
    public String userAgent()  { return userAgent; }
    public String method()     { return method; }
    public String uri()        { return uri; }
}

