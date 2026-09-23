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
package io.keelframework.mcp.adapters.cache.model;

public record CacheModel(
        String cacheName,
        long   hitCount,
        long   missCount,
        double hitRate,
        long   evictionCount,
        long   estimatedSize
) {
    /**
     * Empty instance when no statistics are available.
     */
    public static CacheModel empty(String cacheName) {
        return new CacheModel(cacheName, 0L, 0L, 0.0, 0L, 0L);
    }

    /**
     * Miss percentage — complement of the hit rate.
     */
    public double missRate() {
        return 1.0 - hitRate;
    }

    /**
     * Total number of requests — hits + misses.
     */
    public long totalRequests() {
        return hitCount + missCount;
    }

}
