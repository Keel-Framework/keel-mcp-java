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
package io.keelframework.mcp.adapters.cache.config;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "keel.adapters.cache")
public record CacheServiceProperties(

    /*
     * TTL tras escritura, entrada expira aunque se acceda.
     * Default: 10 minutos
     */
    Duration expireAfterWrite,

    /*
     * TTL tras último acceso. renueva con cada lectura —
     * Utilizado para las sesiones de MCP .
     * Default: 10 minutos
     */
    Duration expireAfterAccess,


    /*
     * Máximo de entradas en cache, cuando se supera Cafeína elimina las menos usadas (LRU).
     * Default: 1000
     */
    long maximumSize,

    /*
     * Activa estadísticas de Cafeína, será expuesta para Micrometer — hits, misses, evictions.
     * Default: true
     */
    boolean recordStats,

    /*
     * Activa logging de eventos del ciclo de vida.
     * Default: false
     */
    boolean logLifecycleEvents
){
    public CacheServiceProperties{
        if (expireAfterWrite == null) expireAfterWrite = Duration.ofMinutes(10);
        if (expireAfterAccess == null) expireAfterAccess = Duration.ofMinutes(10);
        if (maximumSize == 0) maximumSize = 1000;
    }
}
