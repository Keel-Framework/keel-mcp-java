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
package io.keelframework.mcp.common.jwt.model;

public record Scope(String name) {
    public Scope {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("KeycloakScope name must not be blank");
        }
    }

    public static Scope of(String name) {
        return new Scope(name.trim().toLowerCase());
    }
}
