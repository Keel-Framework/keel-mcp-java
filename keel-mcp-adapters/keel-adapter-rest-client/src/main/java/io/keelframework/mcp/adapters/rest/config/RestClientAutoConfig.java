package io.keelframework.mcp.adapters.rest.config;

import io.keelframework.mcp.common.jwt.provider.JwtProvider;

import org.springframework.core.io.ResourceLoader;
import io.keelframework.mcp.adapters.rest.client.McpRestClientFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Autoconfiguracion del adaptador REST para tools MCP.
 *
 * El desarrollador solo necesita:
 * 1. Añadir adapter-rest-client como dependencia
 * 2. Configurar servicios en application.yml:
 *    sca.adapters.rest.services.{nombre}.base-url
 * 3. Inyectar McpRestClientFactory en sus tools
 *
 * Todo lo demas (JWT, headers SCA, pool, SSL) es automatico e invisible.
 */
@AutoConfiguration
@EnableConfigurationProperties({
        RestClientProperties.class,
        HttpClientProperties.class,
        SslProperties.class
})
public class RestClientAutoConfig implements EnvironmentAware {

    private static final Logger log =
            LoggerFactory.getLogger(RestClientAutoConfig.class);

    @Override
    public void setEnvironment(Environment environment) {
        // Verifica si virtual threads ya está activado
        String enabled = environment.getProperty(
                "spring.threads.virtual.enabled");
        if (!"true".equalsIgnoreCase(enabled)) {
            log.warn("VIRTUAL THREADS not enabled — " +
                    "add spring.threads.virtual.enabled=true " +
                    "to application.yml for better MCP performance ");
        }
    }

    @Bean
    @ConditionalOnMissingBean(McpRestClientFactory.class)
    public McpRestClientFactory mcpRestClientFactory(
            RestClientProperties restProperties,
            HttpClientProperties httpProperties,
            SslProperties sslProperties,
            ResourceLoader resourceLoader,
            JwtProvider jwtProvider){
        return new McpRestClientFactory(
                restProperties,
                httpProperties,
                sslProperties,
                resourceLoader,
                jwtProvider);
    }
}
