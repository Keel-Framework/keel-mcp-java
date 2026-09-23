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
package io.keelframework.mcp.observability.logging.handler;

/**
 * Data context for a failed JWT authentication attempt.
 *
 * <p>Contains only the failure reason. The remote address, user agent,
 * HTTP method, and URI are automatically read from {@code McpLogContext}
 * when {@code emit()} is invoked.</p>
 */
public record AuthFailureContext(
        String reason
) {
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String reason;

        public Builder reason(String v) { this.reason = v; return this; }

        public AuthFailureContext build() {
            return new AuthFailureContext(reason);
        }
    }
}
