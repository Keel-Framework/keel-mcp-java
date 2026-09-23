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

/**
 * Defines the origin of the transaction's trace ID.
 *
 * <p>{@link #INTERNAL} — the MCP Server generated the trace ID because it
 * is the origin of the transaction. This is the normal case when invoked
 * by an MCP Client or LLM that does not participate in the SCA tracing scheme.</p>
 *
 * <p>{@link #PROPAGATED} — the trace ID was propagated from an upstream
 * system, such as another SCA microservice that is already part of a
 * broader transaction.</p>
 */
public enum TraceOrigin {
    INTERNAL,
    PROPAGATED
}
