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
package io.keelframework.mcp.transport.session.manager;

import io.keelframework.mcp.transport.session.model.ProtocolSession;

import java.util.Optional;

public sealed interface SessionManagerPort
        permits SessionManager {

    /**
    * Validates an existing session.
    *
    * @param sessionId the session identifier
    * @return the validated session
    */
    ProtocolSession validate(String sessionId, String jwt);

    /**
    * Destroys an existing session.
    *
    * @param sessionId the session identifier
    */
    void destroy(String sessionId);

    /**
    * Finds a session by its identifier.
    *
    * @param sessionId the session identifier
    * @return the session if it exists
    */
    Optional<ProtocolSession> findById(String sessionId);

    /**
    * Checks whether a session exists and is active.
    *
    * @param sessionId the session identifier
    * @return {@code true} if the session is active
    */
    boolean isActive(String sessionId);
}