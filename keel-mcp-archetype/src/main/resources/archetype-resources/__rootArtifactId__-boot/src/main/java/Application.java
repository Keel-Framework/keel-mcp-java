#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

import ${package}.mcp.tools.Tools;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
/**
 * MCP Server — ${artifactId}
 *
 * Generado por mcp-server-archetype  SCA.
 *
 * Capacidades incluidas via transport-sse-mvc:
 * - Autenticación JWT          (transport-auth-service)
 * - Gestión de sesión SSE      (transport-session-service)
 * - Canal SSE con heartbeat    (transport-sse-mvc)
 * - Cache Cafeína              (cache-service)
 *
 * El desarrollador implementa:
 * - tools/     → lógica de negocio expuesta al Agente IA
 * - schemas/   → schemas de validación
 * - prompts/   → prompts del servidor MCP
 */
@SpringBootApplication(scanBasePackages = "${package}")
public class Application extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    // ── Tools — @Bean explícito necesario ───────────────────────────────────
    @Bean
    public ToolCallbackProvider exampleTools(Tools tool) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(tool)
                .build();
    }

    // ── Resources y Prompts — NO necesitan @Bean ────────────────────────────
    // Spring AI los escanea automáticamente via @Component + @McpResource/@McpPrompt

}