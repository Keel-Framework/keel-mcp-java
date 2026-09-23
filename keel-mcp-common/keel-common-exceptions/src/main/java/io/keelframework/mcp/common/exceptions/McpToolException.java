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
 * Standard exception for errors occurring within an MCP tool
 * (for example, when invoking an external backend via the
 * adapter-rest-client).
 *
 * As a RuntimeException, Spring AI automatically handles it when
 * invoking a method annotated with @Tool and returns its message to
 * the model as a tool execution error (isError=true), without the need
 * to use org.springframework.ai.tool.execution.ToolExecutionException
 * directly. That class requires a ToolDefinition in its constructor and
 * is intended for internal framework use rather than being thrown manually.
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
