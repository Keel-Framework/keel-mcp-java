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
package io.keelframework.mcp.observability.logging.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import java.time.Instant;

/**
 * Standard MCP trace model.
 *
 * <p>Serialized as JSON for ELK.
 * Null fields are excluded from the serialized JSON.</p>
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
public record McpLogEntry(
        // ── Identification ───────────────────────────────
        String traceId,
        TraceOrigin traceOrigin,
        String spanId,
        String parentSpanId,

        String requestId,
        String sessionId,
        String clientId,

        // ── Temporal fields ───────────────────────────────
        Instant timestamp,
        Long durationMs,

        // ── Classification ───────────────────────────────
        McpLogType eventType,
        String level,
        String component,

        // ── HTTP Method ───────────────────────────────
        String method,
        String uri,
        Integer httpStatus,
        String remoteAddr,
        String userAgent,

        // ── MCP ───────────────────────────────
        String mcpOperation,
        String toolName,
        JsonNode toolParams,
        JsonNode toolResult,
        Boolean toolSuccess,

        // ── Security ───────────────────────────────
        String authJwtSubject,
        String authJwtIssuer,
        String authJwtAudience,
        Instant authJwtExpiresAt,
        String authResult,
        String authFailReason,

        // ── Errores ───────────────────────────────
        String errorCode,
        String errorType,
        String errorMessage,

        // ── Backend ───────────────────────────────
        String serviceName,
        String serviceUrl,
        Integer serviceStatus,
        Long serviceDurationMs
) {
    public static Builder builder(){
        return new Builder();
    }

    public static class Builder {
        private String traceId;
        private TraceOrigin traceOrigin;
        private String spanId;
        private String parentSpanId;
        private String requestId;
        private String sessionId;
        private String clientId;
        private Instant timestamp = Instant.now();
        private Long durationMs;
        private McpLogType eventType;
        private String level = "INFO";
        private String component;
        private String method;
        private String uri;
        private Integer httpStatus;
        private String remoteAddr;
        private String userAgent;
        private String mcpOperation;
        private String toolName;
        private JsonNode  toolParams;
        private JsonNode toolResult;
        private Boolean toolSuccess;
        private String authJwtSubject;
        private String authJwtIssuer;
        private String authJwtAudience;
        private Instant authJwtExpiresAt;
        private String authResult;
        private String authFailReason;
        private String errorCode;
        private String errorType;
        private String errorMessage;
        private String serviceName;
        private String serviceUrl;
        private Integer serviceStatus;
        private Long serviceDurationMs;

        public Builder traceId(String v) {
            this.traceId = v;
            return this;
        }

        public Builder traceOrigin(TraceOrigin v) {
            this.traceOrigin = v;
            return this;
        }

        public Builder spanId(String v) {
            this.spanId = v;
            return this;
        }

        public Builder parentSpanId(String v) {
            this.parentSpanId = v;
            return this;
        }

        public Builder requestId(String v) {
            this.requestId = v;
            return this;
        }

        public Builder sessionId(String v) {
            this.sessionId = v;
            return this;
        }

        public Builder clientId(String v) {
            this.clientId = v;
            return this;
        }

        public Builder timestamp(Instant v) {
            this.timestamp = v;
            return this;
        }

        public Builder durationMs(long v) {
            this.durationMs = v;
            return this;
        }

        public Builder eventType(McpLogType v) {
            this.eventType = v;
            return this;
        }

        public Builder level(String v) {
            this.level = v;
            return this;
        }

        public Builder component(String v) {
            this.component = v;
            return this;
        }

        public Builder method(String v) {
            this.method = v;
            return this;
        }

        public Builder uri(String v) {
            this.uri = v;
            return this;
        }

        public Builder httpStatus(int v) {
            this.httpStatus = v;
            return this;
        }

        public Builder remoteAddr(String v) {
            this.remoteAddr = v;
            return this;
        }

        public Builder userAgent(String v) {
            this.userAgent = v;
            return this;
        }

        public Builder mcpOperation(String v) {
            this.mcpOperation = v;
            return this;
        }

        public Builder toolName(String v) {
            this.toolName = v;
            return this;
        }

        public Builder toolParams(JsonNode v) {
            this.toolParams = v;
            return this;
        }

        public Builder toolResult(JsonNode v) {
            this.toolResult = v;
            return this;
        }

        public Builder toolSuccess(boolean v) {
            this.toolSuccess = v;
            return this;
        }

        public Builder authJwtSubject(String v) {
            this.authJwtSubject = v;
            return this;
        }

        public Builder authJwtIssuer(String v) {
            this.authJwtIssuer = v;
            return this;
        }
        public Builder authJwtAudience(String v) {
            this.authJwtAudience = v;
            return this;
        }

        public Builder authJwtExpiresAt(Instant v) {
            this.authJwtExpiresAt = v;
            return this;
        }

        public Builder authResult(String v) {
            this.authResult = v;
            return this;
        }

        public Builder authFailReason(String v) {
            this.authFailReason = v;
            return this;
        }

        public Builder errorCode(String v) {
            this.errorCode = v;
            return this;
        }

        public Builder errorType(String v) {
            this.errorType = v;
            return this;
        }

        public Builder errorMessage(String v) {
            this.errorMessage = v;
            return this;
        }

        public Builder serviceName(String v) {
            this.serviceName = v;
            return this;
        }

        public Builder serviceUrl(String v) {
            this.serviceUrl = v;
            return this;
        }

        public Builder serviceStatus(int v) {
            this.serviceStatus = v;
            return this;
        }

        public Builder serviceDurationMs(long v) {
            this.serviceDurationMs = v;
            return this;
        }

        public McpLogEntry build() {
            return new McpLogEntry(
                    traceId, traceOrigin, spanId, parentSpanId, requestId, sessionId, clientId,
                    timestamp, durationMs, eventType, level, component,
                    method, uri, httpStatus, remoteAddr, userAgent,
                    mcpOperation, toolName, toolParams, toolResult, toolSuccess,
                    authJwtSubject, authJwtIssuer, authJwtAudience, authJwtExpiresAt,
                    authResult, authFailReason, errorCode, errorType, errorMessage,
                    serviceName, serviceUrl, serviceStatus, serviceDurationMs
            );
        }
    }
}
