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
package io.keelframework.mcp.observability.logging.aspect;

import io.keelframework.mcp.observability.logging.config.McpLogSerializer;
import io.keelframework.mcp.observability.logging.config.LogProperties;
import io.keelframework.mcp.observability.logging.handler.McpAuthLoggingHandler;
import io.keelframework.mcp.observability.logging.model.McpLogEntry;
import io.keelframework.mcp.observability.logging.model.McpLogType;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FUNCTIONAL tracing aspect for each MCP tool call.
 *
 * <p>Uses {@code McpAuthLoggingHandler.functional()} to centralize
 * trace context handling, including {@code traceId}, {@code spanId},
 * {@code parentSpanId}, remote address, and other common fields.
 * Only tool call-specific fields are provided by the aspect.</p>
 *
 * <p>Activation: {@code keel.mcp.observability.functional=true}</p>
 */
@Aspect
public class McpToolLoggingAspect {

    private static final Logger log =
            LoggerFactory.getLogger(McpToolLoggingAspect.class);
    private final LogProperties properties;
    private final McpAuthLoggingHandler handler;

    public McpToolLoggingAspect(LogProperties properties,
                                McpAuthLoggingHandler handler) {
        this.properties = properties;
        this.handler = handler;
    }

    @Around("@annotation(org.springframework.ai.tool.annotation.Tool)")
    public Object logToolCall(ProceedingJoinPoint pjp) throws Throwable {

        if (!properties.isActive(McpLogType.FUNCTIONAL)) {
            return pjp.proceed();
        }

        String toolName = pjp.getSignature().getName();
        long start = System.nanoTime();

        try {
            Object result   = pjp.proceed();
            long durationMs = (System.nanoTime() - start) / 1_000_000;

            handler.functional(McpLogEntry.builder()
                    .level("INFO")
                    .component(McpAuthLoggingHandler.COMPONENT_AOP_TOOLS)
                    .mcpOperation("tools/call")
                    .toolName(toolName)
                    .toolParams(McpLogSerializer.toJsonNode(pjp.getArgs()))
                    .toolResult(McpLogSerializer.toJsonNode(result))
                    .toolSuccess(true)
                    .durationMs(durationMs));

            return result;

        } catch (Exception ex) {
            long durationMs = (System.nanoTime() - start) / 1_000_000;

            handler.functional(McpLogEntry.builder()
                    .level("ERROR")
                    .component(McpAuthLoggingHandler.COMPONENT_AOP_TOOLS)
                    .mcpOperation("tools/call")
                    .toolName(toolName)
                    .toolParams(McpLogSerializer.toJsonNode(pjp.getArgs()))
                    .toolSuccess(false)
                    .durationMs(durationMs)
                    .errorType(ex.getClass().getSimpleName())
                    .errorMessage(ex.getMessage()));

            throw ex;
        }
    }

}
