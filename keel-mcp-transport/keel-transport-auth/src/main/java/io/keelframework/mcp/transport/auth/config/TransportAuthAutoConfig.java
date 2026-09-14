package io.keelframework.mcp.transport.auth.config;
import io.keelframework.mcp.adapters.auth.idp.jwks.JwksKeyResolver;
import io.keelframework.mcp.adapters.auth.idp.jwks.JwksRemoteLoader;
import io.keelframework.mcp.adapters.auth.idp.validator.TokenValidator;
import io.keelframework.mcp.common.jwt.provider.TokenExtractor;
import io.keelframework.mcp.observability.logging.handler.McpAuthLoggingHandler;
import io.keelframework.mcp.transport.auth.aop.McpAuthorizationAdvisor;
import io.keelframework.mcp.transport.auth.filter.McpAuthenticationFilter;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.tomcat.servlet.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.RestClient;

import java.util.Set;

/**
 * AutoConfiguration del módulo transport-auth-service.
 *
 * Se activa SOLO si sca.transport.auth.enabled=true.
 * Con enabled=false (default) el servidor MCP arranca sin autenticación —
 * Cadena de beans:
 *   RestClient → JwksRemoteLoader → JwksKeyResolver → TokenValidator
 *        → TokenExtractor → McpAuthenticationFilter → SecurityFilterChain
 *
 * Los beans de validación JWT (JwksRemoteLoader, JwksKeyResolver, TokenValidator)
 * vienen de adapter-auth-idp. Los beans de Spring Security (filtro, cadena)
 * se definen aquí en transport-auth-service.
 */

@AutoConfiguration
@EnableAspectJAutoProxy
@EnableWebSecurity
@EnableConfigurationProperties(TransportAuthProperties.class)
@ConditionalOnProperty(
        prefix      = "keel.mcp.transport.auth",
        name        = "enabled",
        havingValue = "true",
        matchIfMissing = false   // ← default OFF, sin auth si no se configura
)
public class TransportAuthAutoConfig {


    // =========================================================
    // INFRAESTRUCTURA JWT — delega en adapter-auth-idp
    // =========================================================

    @Bean
    @ConditionalOnMissingBean(name = "mcpRestClient")
    public RestClient mcpRestClient() {
        return RestClient.builder().build();
    }

    @Bean
    @ConditionalOnMissingBean
    public JwksRemoteLoader jwksRemoteLoader(RestClient mcpRestClient) {
        return new JwksRemoteLoader(mcpRestClient);
    }

    @Bean
    @ConditionalOnMissingBean
    public JwksKeyResolver jwksKeyResolver(
            JwksRemoteLoader loader,
            TransportAuthProperties properties) {
        return new JwksKeyResolver(
                loader,
                properties.resolveJwksUri(),
                properties.getJwt().getJwksTtl()
        );
    }

    @Bean
    @ConditionalOnMissingBean
    public TokenValidator tokenValidator(
            JwksKeyResolver resolver,
            TransportAuthProperties properties) {
        return new TokenValidator(
                resolver,
                properties.getJwt().getIssuer(),
                properties.resolveAudience()
        );
    }

    // =========================================================
    // FILTRO HTTP — responsabilidad de transport-auth-service
    // =========================================================

    @Bean
    @ConditionalOnMissingBean
    public TokenExtractor tokenExtractor() {

        return new TokenExtractor();
    }

    @Bean
    @ConditionalOnMissingBean
    public McpAuthenticationFilter mcpAuthenticationFilter(
            TokenValidator validator,
            TokenExtractor extractor,
            McpAuthLoggingHandler authLoggingHandler) {
        return new McpAuthenticationFilter(validator, extractor, authLoggingHandler);
    }

    // =========================================================
    // AOP — autorización por rol/scope
    // =========================================================

    @Bean
    @ConditionalOnMissingBean
    public McpAuthorizationAdvisor mcpAuthorizationAdvisor() {

        return new McpAuthorizationAdvisor();
    }

// =========================================================
// TOMCAT ERROR PAGES
// Elimina el redirect de Tomcat a /error cuando Spring Security
// lanza AuthorizationDeniedException con el response ya committed.
// Sin esto, Tomcat intenta procesar ErrorPage /error → Spring Security
// lo bloquea de nuevo → loop de error en consola.
// El cliente recibe 200 correctamente — es ruido interno del servidor.
// =========================================================

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> mcpErrorPageCustomizer() {
        return factory -> factory.setErrorPages(Set.of());
    }

    // =========================================================
    // SPRING SECURITY FILTER CHAIN
    // =========================================================
  /*
    @Bean
    @ConditionalOnMissingBean(SecurityFilterChain.class)
    public SecurityFilterChain mcpSecurityFilterChain(
            HttpSecurity http,
            McpAuthenticationFilter mcpFilter,
            TransportAuthProperties properties) throws Exception {

        http
                .securityMatcher("/mcp", "/mcp/**")  // ← solo aplica a /mcp
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((req, res, e) -> {
                            if (!res.isCommitted())
                                res.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                        })
                        .accessDeniedHandler((req, res, e) -> {
                            if (!res.isCommitted())
                                res.sendError(HttpServletResponse.SC_FORBIDDEN);
                        })
                )
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().authenticated())
                .addFilterBefore(mcpFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
 */
/*
    @Bean
    @ConditionalOnMissingBean(SecurityFilterChain.class)
    public SecurityFilterChain mcpSecurityFilterChain(
            HttpSecurity http,
            McpAuthenticationFilter mcpFilter,
            TransportAuthProperties properties) throws Exception {

        String[] publicPaths = properties.getExcludedPaths()
                .toArray(new String[0]);

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            // si el response ya fue escrito al cliente, Spring Security no intenta redirigir a /error
                            if (!response.isCommitted()) {
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                            }
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            if (!response.isCommitted()) {
                                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                            }
                        })
                )
                .authorizeHttpRequests(auth -> auth
                        .dispatcherTypeMatchers(DispatcherType.FORWARD).permitAll()
                        .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                        .requestMatchers(publicPaths).permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(mcpFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }*/
    @Bean
    @ConditionalOnMissingBean(SecurityFilterChain.class)
    public SecurityFilterChain mcpSecurityFilterChain(
            HttpSecurity http,
            McpAuthenticationFilter mcpFilter,
            TransportAuthProperties properties) throws Exception {

        String[] publicPaths = properties.getExcludedPaths()
                .toArray(new String[0]);

        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            if (!response.isCommitted()) {
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                            }
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            if (!response.isCommitted()) {
                                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                            }
                        })
                )
                .authorizeHttpRequests(auth -> auth
                        // 1. Preflight CORS — nunca debe exigir JWT
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // 1. MCP y endpoints explícitamente excluidos
                        .requestMatchers(publicPaths).permitAll()
                        // 2. SOLO necesario para evitar doble evaluación en async MCP
                        .dispatcherTypeMatchers(DispatcherType.ASYNC).permitAll()
                        // 3. TODO lo demás protegido
                        .anyRequest().authenticated()
                )
                .addFilterBefore(
                        mcpFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }


}
