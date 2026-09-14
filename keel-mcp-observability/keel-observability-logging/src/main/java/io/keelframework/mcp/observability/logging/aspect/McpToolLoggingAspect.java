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
 * Aspecto de trazas FUNCTIONAL — cada tool call MCP.
 *
 * Usa McpAuthLoggingHandler.functional() para centralizar la lógica
 * de contexto (traceId, spanId, parentSpanId, remoteAddr, etc.) —
 * solo pasa los campos específicos de la tool call.
 *
 * Activación: sca.mcp.observability.functional=true
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
