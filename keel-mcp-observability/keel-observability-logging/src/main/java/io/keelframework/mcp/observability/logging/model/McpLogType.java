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
 * Defines the categories of MCP observability traces.
 *
 * <p>Each trace type represents a different observability concern:</p>
 * <ul>
 *     <li>{@link #TECHNICAL} — technical and infrastructure-related events.</li>
 *     <li>{@link #FUNCTIONAL} — business and MCP operation-related events.</li>
 *     <li>{@link #SECURITY} — authentication, authorization, and security events.</li>
 * </ul>
 */
public enum McpLogType {
    TECHNICAL,
    FUNCTIONAL,
    SECURITY
}
