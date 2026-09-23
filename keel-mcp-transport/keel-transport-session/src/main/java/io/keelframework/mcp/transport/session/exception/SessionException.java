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
package io.keelframework.mcp.transport.session.exception;

/**
 * Base exception for the MCP transport session service.
 *
 * Hierarchy:
 * TransportSessionException
 * ├── SessionNotFoundException  → session does not exist or has expired
 * ├── SessionExpiredException   → session TTL has expired
 * └── SessionCreationException  → error while creating the session
 */
public sealed class SessionException extends RuntimeException
        permits SessionException.SessionNotFoundException,
        SessionException.SessionExpiredException,
        SessionException.SessionCreationException {

    private final String sessionId;

    protected SessionException(String message, String sessionId) {
        super("%s [sessionId=%s]".formatted(message, sessionId));
        this.sessionId = sessionId;
    }

    protected SessionException(String message,
                               String sessionId,
                               Throwable cause) {
        super("%s [sessionId=%s]".formatted(message, sessionId), cause);
        this.sessionId = sessionId;
    }

    public String sessionId() {
        return sessionId;
    }

    public static final class SessionNotFoundException
            extends SessionException {

        public SessionNotFoundException(String sessionId) {
            super("Session not found or expired", sessionId);
        }
    }

    public static final class SessionExpiredException
            extends SessionException {

        public SessionExpiredException(String sessionId) {
            super("Session TTL expired", sessionId);
        }
    }

    public static final class SessionCreationException
            extends SessionException {

        public SessionCreationException(String sessionId, Throwable cause) {
            super("Error creating session", sessionId, cause);
        }
    }
}

