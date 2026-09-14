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
 * Advice AOP que intercepta métodos anotados con {@literal @}McpAuthorized
 * y verifica que el usuario autenticado tenga los roles y scopes requeridos.
 *
 * Precondición: el SecurityContext ya tiene un McpAuthenticationToken
 * establecido por McpAuthenticationFilter. Si no hay autenticación,
 * se lanza AccessDeniedException.
 *
 * La anotación en clase actúa como default para todos sus métodos.
 * La anotación en método tiene precedencia sobre la de clase.
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
