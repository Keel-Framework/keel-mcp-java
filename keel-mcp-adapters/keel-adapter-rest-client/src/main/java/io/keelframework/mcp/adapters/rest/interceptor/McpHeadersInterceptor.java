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
package io.keelframework.mcp.adapters.rest.interceptor;


import io.keelframework.mcp.common.jwt.provider.JwtProvider;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

/**
 * Interceptor de RestClient que propaga headers estandar SCA.
 *
 * TRANSPARENTE para el desarrollador — registrado automaticamente
 * via RestClientAutoConfig. No requiere configuracion.
 *
 * En cada request HTTP saliente añade:
 *
 * 1. Authorization: Bearer <JWT>   ← JWT del cliente MCP SIEMPRE
 *
 * 2. Headers de propagacion SCA (desde MDC):
 *    - channel          ← canal de origen
 *    - applicationid    ← ID de aplicacion
 *    - traceid          ← ID de traza distribuida
 *    - spanid           ← ID de span
 *    - x-adeslas-device ← dispositivo del cliente
 *
 * Los valores se extraen del MDC que fue poblado por el
 * filtro/interceptor del servidor MCP al recibir el request.
 */
public class McpHeadersInterceptor implements ClientHttpRequestInterceptor {


    private static final Logger log =
            LoggerFactory.getLogger(McpHeadersInterceptor.class);

    private final JwtProvider jwtProvider;

    public McpHeadersInterceptor(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }


    @Override
    public ClientHttpResponse intercept(HttpRequest request,
                                        byte[] body,
                                        ClientHttpRequestExecution execution)
            throws IOException {

        // 1. JWT — SIEMPRE propagado
        String jwt = jwtProvider.getJwt();
        if (jwt != null){
            request.getHeaders().set(
                    McpHeaders.AUTHORIZATION,
                    McpHeaders.BEARER_PREFIX + jwt);
            log.debug("JWT propagated service={}", request.getURI().getHost());

        } else{
            log.warn("JWT not found in SecurityContext uri={}",request.getURI());
        }

        // 2. Headers estandar SCA
        McpHeaders.PROPAGATION_HEADERS.forEach(header ->{
            String value = MDC.get(header);
            if (value != null && !value.isBlank()){
                request.getHeaders().set(header, value);
                log.debug("Header propagated name={} uri={}",header, request.getURI());
            }
        });
        return execution.execute(request, body);
    }


    // ================================================
    // PRIVADO
    // ================================================

    /**
     * Extrae el raw JWT del SecurityContextHolder.
     * El JwtAuthenticationFilter ya lo validó y guardó
     * en credentials del Authentication.
     */
    private String extractJwt() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) return null;

        // Raw JWT en credentials ← puesto por JwtAuthenticationFilter
        Object credentials = authentication.getCredentials();
        if (credentials instanceof String token
                && !token.isBlank()) {
            return token;
        }

        // Fallback en principal
        Object principal = authentication.getPrincipal();
        if (principal instanceof String token
                && !token.isBlank()) {
            return token;
        }
        return null;
    }
}
