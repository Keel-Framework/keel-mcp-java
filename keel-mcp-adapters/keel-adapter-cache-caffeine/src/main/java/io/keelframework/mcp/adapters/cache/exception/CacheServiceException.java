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
package io.keelframework.mcp.adapters.cache.exception;

public final class CacheServiceException extends RuntimeException {

    private final String cacheName;

    public CacheServiceException(String message, String cacheName) {
        super(buildMessage(message, cacheName));
        this.cacheName = cacheName;
    }

    public CacheServiceException(String message,
                                 String cacheName,
                                 Throwable cause) {
        super(buildMessage(message, cacheName), cause);
        this.cacheName = cacheName;
    }

    public String cacheName() {
        return cacheName;
    }

    private static String buildMessage(String message, String cacheName) {
        return "%s [cache=%s]".formatted(message, cacheName);
    }

}
