package io.keelframework.mcp.common.exceptions;

/**
 * Excepción estándar para errores producidos dentro de una tool MCP
 * (por ejemplo, al invocar un backend externo vía adapter-rest-client).
 *
 * Al ser un RuntimeException, Spring AI la captura automáticamente al
 * invocar el método anotado con @Tool y devuelve su mensaje al modelo
 * como resultado de error de la tool (isError=true), sin necesidad de
 * usar org.springframework.ai.tool.execution.ToolExecutionException
 * directamente (esa clase exige un ToolDefinition en el constructor y
 * está pensada para uso interno del framework, no para lanzarla a mano).
 */
public class McpToolException extends RuntimeException {

    private final String serviceName;
    private final String endpoint;
    private final int httpStatus;

    public McpToolException(String serviceName, String endpoint, int httpStatus,
                            String message, Throwable cause) {
        super(message, cause);
        this.serviceName = serviceName;
        this.endpoint = endpoint;
        this.httpStatus = httpStatus;
    }

    public McpToolException(String serviceName, String endpoint, int httpStatus, String message) {
        this(serviceName, endpoint, httpStatus, message, null);
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

}
