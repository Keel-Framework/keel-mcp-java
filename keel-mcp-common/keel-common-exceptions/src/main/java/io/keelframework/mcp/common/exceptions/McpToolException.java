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
