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
package io.keelframework.mcp.transport.auth.filter;
import io.keelframework.mcp.adapters.auth.idp.validator.TokenValidationResult;
import io.keelframework.mcp.adapters.auth.idp.validator.TokenValidator;
import io.keelframework.mcp.common.jwt.model.Jwt;
import io.keelframework.mcp.common.jwt.provider.TokenExtractor;
import io.keelframework.mcp.observability.logging.handler.McpAuthLoggingHandler;
import io.keelframework.mcp.observability.logging.mdc.McpMdcPopulator;
import io.keelframework.mcp.observability.logging.model.McpLogEntry;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro de autenticación HTTP que:
 *  1. Extrae el Bearer token del header Authorization
 *  2. Delega la validación a KeycloakTokenValidator
 *  3. Si válido → construye KeycloakPrincipal y puebla SecurityContextHolder
 *  4. Si inválido → responde 401 Unauthorized y corta la cadena
 *
 * Implementa OncePerRequestFilter para garantizar ejecución única por request,
 * incluyendo forwards y dispatches internos de Spring.
 */

@RequiredArgsConstructor
public class McpAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log =
            LoggerFactory.getLogger(McpAuthenticationFilter.class);

    private static final String BEARER_PREFIX = "Bearer ";

    private final TokenValidator tokenValidator;
    private final TokenExtractor tokenExtractor;
    private final McpAuthLoggingHandler logMcp;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            // Sin token — continúa la cadena (Spring Security decidirá si la ruta requiere auth)
            filterChain.doFilter(request, response);
            return;
        }

        String rawJwt = authHeader.substring(BEARER_PREFIX.length()).trim();
        TokenValidationResult result = tokenValidator.validate(rawJwt);

        switch (result) {
            case TokenValidationResult.Valid valid -> {
                Jwt token = tokenExtractor.extract(valid.claims());
                McpAuthenticationToken authToken = new McpAuthenticationToken(token, rawJwt);
                authToken.setDetails(request.getRemoteAddr());

                SecurityContext context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(authToken);
                SecurityContextHolder.setContext(context);

                // ── Traza SECURITY — auth SUCCESS ────────────────────
                String clientId = ClientIdExtractor.extract(authToken);
                McpMdcPopulator.putClientId(clientId);

                logMcp.security(McpLogEntry.builder()
                        .level("INFO")
                        .component(McpAuthLoggingHandler.COMPONENT_AUTH_FILTER)
                        .sessionId(request.getHeader("Mcp-Session-Id"))
                        .clientId(clientId)
                        .authJwtSubject(token.subject())
                        .authJwtIssuer(token.issuer())
                        .authJwtAudience(valid.claims().audience() != null
                                ? String.join(",", valid.claims().audience()) : null)
                        .authJwtExpiresAt(valid.claims().expiresAt())
                        .authResult(McpAuthLoggingHandler.RESULT_SUCCESS));
                try {
                    filterChain.doFilter(request, response);
                } finally {
                    // Limpia el contexto al terminar el request
                    // Evita que el thread reutilice el contexto anterior
                    SecurityContextHolder.clearContext();
                }
            }
            case TokenValidationResult.Invalid invalid -> {
                // ── Traza SECURITY — auth fallida
                logMcp.security(McpLogEntry.builder()
                        .level("WARN")
                        .component(McpAuthLoggingHandler.COMPONENT_AUTH_FILTER)
                        .authResult(McpAuthLoggingHandler.RESULT_FAILURE)
                        .authFailReason(invalid.reason())
                        .errorCode(logMcp.resolveErrorCode(invalid.reason())));

                SecurityContextHolder.clearContext();
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.setContentType("application/json");
                response.getWriter().write(
                        "{\"error\":\"unauthorized\",\"message\":\"%s\"}"
                                .formatted(invalid.reason())
                );
            }
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // Excluir endpoints públicos de actuator y JWKS discovery
        String path = request.getServletPath();
        return path.startsWith("/actuator") || path.startsWith("/public");
    }
}
