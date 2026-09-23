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
package io.keelframework.mcp.transport.auth.aop;

import io.keelframework.mcp.common.jwt.model.Jwt;
import io.keelframework.mcp.transport.auth.filter.McpAuthenticationToken;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * AOP advice that intercepts methods annotated with {@literal @}McpAuthorized
 * and verifies that the authenticated user has the required roles and scopes.
 *
 * Precondition: the SecurityContext already contains a McpAuthenticationToken
 * established by McpAuthenticationFilter. If no authentication is present,
 * an AccessDeniedException is thrown.
 *
 * A class-level annotation acts as the default for all its methods.
 * A method-level annotation takes precedence over the class-level annotation.
 */
@Aspect
public class McpAuthorizationAdvisor {

    private static final Logger log =
            LoggerFactory.getLogger(McpAuthorizationAdvisor.class);

    @Around("@annotation(com.sca.framework.adapters.auth.idp.aop.McpAuthorized)" +
            " || @within(com.sca.framework.adapters.auth.idp.aop.McpAuthorized)")
    public Object authorize(ProceedingJoinPoint joinPoint) throws Throwable {
        McpAuthorized annotation = resolveAnnotation(joinPoint);
        Jwt tokenJwt = resolvePrincipal();

        checkRoles(annotation, tokenJwt);
        checkScopes(annotation, tokenJwt);

        return joinPoint.proceed();
    }

    private McpAuthorized resolveAnnotation(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // Precedencia: anotación en método > anotación en clase
        McpAuthorized methodAnnotation = method.getAnnotation(McpAuthorized.class);
        if (methodAnnotation != null) {
            return methodAnnotation;
        }
        return joinPoint.getTarget().getClass().getAnnotation(McpAuthorized.class);
    }

    private Jwt resolvePrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof McpAuthenticationToken token) {
            return token.getPrincipal();
        }

        log.warn("No KeycloakAuthenticationToken in SecurityContext — access denied");
        throw new AccessDeniedException("Authentication required");
    }

    private void checkRoles(McpAuthorized annotation, Jwt jwtToken) {
        String[] requiredRoles = annotation.roles();
        if (requiredRoles.length == 0) return;

        boolean granted = annotation.requireAllRoles()
                ? Arrays.stream(requiredRoles).allMatch(jwtToken::hasRole)
                : Arrays.stream(requiredRoles).anyMatch(jwtToken::hasRole);

        if (!granted) {
            log.warn("Access denied for user '{}'. Required roles: {} (requireAll={}). Has: {}",
                    jwtToken.username(),
                    Arrays.toString(requiredRoles),
                    annotation.requireAllRoles(),
                    jwtToken.realmRoles());
            throw new AccessDeniedException(
                    "Insufficient roles. Required: " + Arrays.toString(requiredRoles));
        }
    }

    private void checkScopes(McpAuthorized annotation, Jwt jwtToken) {
        String[] requiredScopes = annotation.scopes();
        if (requiredScopes.length == 0) return;

        boolean granted = annotation.requireAllScopes()
                ? Arrays.stream(requiredScopes).allMatch(jwtToken::hasScope)
                : Arrays.stream(requiredScopes).anyMatch(jwtToken::hasScope);

        if (!granted) {
            log.warn("Access denied for user '{}'. Required scopes: {} (requireAll={}). Has: {}",
                    jwtToken.username(),
                    Arrays.toString(requiredScopes),
                    annotation.requireAllScopes(),
                    jwtToken.scopes());
            throw new AccessDeniedException(
                    "Insufficient scopes. Required: " + Arrays.toString(requiredScopes));
        }
    }
}
