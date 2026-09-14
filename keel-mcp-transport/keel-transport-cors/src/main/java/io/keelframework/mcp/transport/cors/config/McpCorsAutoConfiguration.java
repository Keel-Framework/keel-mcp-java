package io.keelframework.mcp.transport.cors.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.util.CollectionUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.servlet.DispatcherType;
/**
 * Auto-configuración CORS para el endpoint MCP Streamable HTTP.
 * Se activa únicamente si {@code keel.mcp.transport.cors.enabled=true}.
 *
 * IMPORTANTE: se registra explícitamente como FilterRegistrationBean con
 * DispatcherType.ASYNC incluido. El endpoint /mcp usa dispatch asíncrono
 * (Streamable HTTP), y el mecanismo estándar de Spring MVC
 * (WebMvcConfigurer.addCorsMappings) delega en un filtro que extiende
 * OncePerRequestFilter, el cual por defecto NO se re-ejecuta en el
 * redispatch async — resultando en respuestas reales sin el header
 * Access-Control-Allow-Origin, aunque el preflight OPTIONS sí lo tenga.
 *
 * Agnóstico de autenticación y sesión — únicamente controla qué orígenes
 * de navegador pueden interactuar con el endpoint MCP.
 */

@AutoConfiguration
@EnableConfigurationProperties(TransportCorsProperties.class)
@ConditionalOnProperty(prefix = "keel.mcp.transport.cors", name = "enabled", havingValue = "true")
public class McpCorsAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(McpCorsAutoConfiguration.class);

    private final TransportCorsProperties properties;

    public McpCorsAutoConfiguration(TransportCorsProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    void validateConfiguration() {
        if (CollectionUtils.isEmpty(properties.getAllowedOrigins())) {
            throw new IllegalStateException(
                    "keel.mcp.transport.cors.allowed-origins is required when " +
                            "keel.mcp.transport.cors.enabled=true. Refusing to start with CORS " +
                            "enabled but no origins configured, to avoid an insecure default.");
        }

        if (properties.isAllowCredentials() && properties.getAllowedOrigins().contains("*")) {
            throw new IllegalStateException(
                    "keel.mcp.transport.cors.allowed-origins cannot contain \"*\" when " +
                            "allow-credentials=true. List origins explicitly.");
        }
    }

    @Bean
    public FilterRegistrationBean<CorsFilter> mcpCorsConfigurer() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(properties.getAllowedOrigins());
        config.setAllowedMethods(properties.getAllowedMethods());
        config.setAllowedHeaders(properties.getAllowedHeaders());
        config.setExposedHeaders(properties.getExposedHeaders());
        config.setAllowCredentials(properties.isAllowCredentials());
        config.setMaxAge(properties.getMaxAge());

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration(properties.getPathPattern(), config);

        FilterRegistrationBean<CorsFilter> registration =
                new FilterRegistrationBean<>(new CorsFilter(source));

        // Clave: incluir ASYNC además de REQUEST, o el header CORS
        // desaparece en la respuesta real de un endpoint Streamable HTTP.
        registration.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.ASYNC);
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        registration.addUrlPatterns(toUrlPattern(properties.getPathPattern()));

        return registration;
    }

    /**
     * Convierte un Ant-style pattern (ej. "/mcp/**") al formato de
     * servlet URL pattern esperado por FilterRegistrationBean (ej. "/mcp/*").
     */
    private static String toUrlPattern(String antPattern) {
        return antPattern.endsWith("/**")
                ? antPattern.substring(0, antPattern.length() - 1)
                : antPattern;
    }}
