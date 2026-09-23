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
package io.keelframework.mcp.adapters.cache.key;
import org.springframework.cache.interceptor.KeyGenerator;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Standard cache key generator for the MCP ecosystem.
 *
 * Generated key format:
 * {SimpleClassName}:{methodName}:{param1}:{param2}:...
 *
 * @Cacheable(
 *     cacheNames = "tipos-cambio",
 *     keyGenerator = "cacheServiceKeyGenerator"
 * )
 * public TipoCambio obtenerTipoCambio(String moneda) { ... }
 *
 * Registered as a bean named "cacheServiceKeyGenerator"
 * in CacheServiceConfig.
 */
public final class CacheServiceKeyGenerator implements KeyGenerator {

    private static final String SEPARATOR = ":";
    private static final String NULL_PARAM = "null";

    @Override
    public Object generate(Object target, Method method, Object... params) {

        String className  = target.getClass().getSimpleName();
        String methodName = method.getName();
        String paramsPart = buildParamsPart(params);

        return className + SEPARATOR + methodName + SEPARATOR + paramsPart;
    }

    private String buildParamsPart(Object[] params) {
        if (params == null || params.length == 0) {
            return "no-params";
        }

        return Arrays.stream(params)
                .map(p -> p != null ? p.toString() : NULL_PARAM)
                .collect(Collectors.joining(SEPARATOR));
    }
}