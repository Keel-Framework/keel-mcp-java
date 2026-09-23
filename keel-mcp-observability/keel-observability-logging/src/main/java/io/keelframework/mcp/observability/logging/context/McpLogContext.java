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
package io.keelframework.mcp.observability.logging.context;

/**
 * Holds HTTP request context data throughout the lifecycle of the current thread.
 *
 * <p>The context is populated once by {@code McpRequestLoggingFilter} and can
 * then be accessed automatically by {@code McpAuthLoggingHandler.emit()}
 * without explicitly passing request information to each log entry.</p>
 *
 * <p>This follows a similar pattern to MDC, but is intended for HTTP request
 * fields such as the remote address, user agent, HTTP method, and URI that
 * represent request context rather than simple log correlation values.</p>
 *
 * <p>Lifecycle:</p>
 * <ol>
 *     <li>{@code McpRequestLoggingFilter.doFilterInternal()} populates the context.</li>
 *     <li>Any component in the current thread can access the request context.</li>
 *     <li>{@code McpRequestLoggingFilter.finally} clears the context.</li>
 * </ol>
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

