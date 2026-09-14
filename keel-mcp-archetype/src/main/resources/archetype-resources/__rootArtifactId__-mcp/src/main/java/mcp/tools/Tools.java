#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.mcp.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import io.keelframework.mcp.adapters.rest.client.McpRestClient;
import io.keelframework.mcp.adapters.rest.client.McpRestClientFactory;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.HashMap;
import java.time.LocalDateTime;
import io.keelframework.mcp.observability.logging.handler.McpAuthLoggingHandler;
import io.keelframework.mcp.common.exceptions.McpToolException;
/**
 * Tools MCP — acciones que el LLM puede ejecutar contra el backend.
 *
 * GUÍA PARA EL DESARROLLADOR:
 * ─────────────────────────────────────────────────────────────────
 * 1. Añade un método por cada acción de negocio
 * 2. Anota cada método con @Tool y una description clara:
 *
 *    @Tool(description = """
 *        [QUÉ HACE]    — acción concreta en una línea.
 *        [CUÁNDO USAR] — situaciones donde el LLM debe invocarla.
 *        [QUÉ DEVUELVE]— formato y contenido de la respuesta.
 *        """)
 *
 * 3. Anota cada parámetro con @ToolParam:
 *
 *    @ToolParam(description = "Tipo + valores válidos + ejemplo")
 *
 * 4. Usa McpRestClientFactory para llamar al backend:
 *
 *    McpRestClient client = restClientFactory.getClient("nombre-servicio");
 *    RestResponse<MiResponse> response = client.get("/ruta/{id}", MiResponse.class, id);
 *
 * EJEMPLOS:
 * ─────────────────────────────────────────────────────────────────
 * Ver métodos comentados abajo como referencia.
 * Elimina los ejemplos cuando implementes tus tools reales.
 */
@Component
public class Tools {

    // ── Inyecta el factory para comunicación HTTP-REST / Logging con el backend ────────
    private final McpRestClientFactory restClientFactory;
    private final McpAuthLoggingHandler mcplog; // Dev posibilidad generar trazas custom en la tools

    public Tools(McpRestClientFactory restClientFactory, McpAuthLoggingHandler mcplog) {
        this.restClientFactory = restClientFactory;
        this.mcplog = mcplog;
    }

    @Tool(description = "Suma dos números enteros y retorna el resultado")
    public Map<String, Object> suma(
            @ToolParam(description = "Primer número") int a,
            @ToolParam(description = "Segundo número") int b) {
        long durationMs = (System.nanoTime() - System.nanoTime()) / 1_000_000;
        long sumaResultado = (long) a + b;

        if (sumaResultado > Integer.MAX_VALUE || sumaResultado < Integer.MIN_VALUE) {
            throw new McpToolException(
                    "suma-tool",
                    "suma(" + a + "," + b + ")",
                    400,
                    "El resultado de la suma excede el rango soportado"
            );
        }

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("operacion", a + " + " + b);
        resultado.put("resultado", sumaResultado);
        resultado.put("timestamp", LocalDateTime.now().toString());
        mcplog.logTool(
                "suma-services",
                "/suma/a/b",
                200,
                durationMs
        );
        return resultado;
    }



    /**
    * Ejemplo 1 — Tool para sumar dos numeros INT.
    *
    * @Tool(description = "Suma dos números enteros y retorna el resultado")
    * public Map<String, Object> suma(
    *        @ToolParam(description = "Primer número") int a,
    *        @ToolParam(description = "Segundo número") int b) {
    *    Map<String, Object> resultado = new HashMap<>();
    *    resultado.put("operacion",  a + " + " + b);
    *    resultado.put("resultado",  a + b);
    *    resultado.put("timestamp",  LocalDateTime.now().toString());
    *    return resultado;
    }
     */
    /**
    * Ejemplo 2 — Tool Busca una entidad por ID en el backend REST.
    *
    * @Tool(description = """
    *         Busca una entidad por su identificador ID.
    *         Usar cuando el usuario pregunte por una entidad específica
    *         o mencione un ID concreto.
    *         Devuelve los datos completos de la entidad.
    *         """)
    * public MiResponse busca_entidad(
    *         @ToolParam(description = "Identificador numérico de la entidad. Ejemplo: 1, 2, 3")
    *         int id) {
    *
    *     McpRestClient client = restClientFactory.getClient("nombre-servicio");
    *
    *     long start = System.nanoTime();
     *    MiResponse<> response = client.get("/product/{id}", PolicyMockFactory.ProductResponse.class, productId);
     *    mcplog.mcplog(
     *                 "scaapa-service",
     *                 "/product/" + productId,
     *                 response.httpStatus(),
     *                 durationMs);
    *     return response.body();;
    * }
    */

    /**
     * Ejemplo 3 — Tool Crea una nueva entidad en el backend REST.
     *
     * @Tool(description = """
     *         Registra una nueva entidad en el sistema.
     *         Usar cuando el usuario quiera crear o añadir una nueva entidad.
     *         Requiere nombre y descripción — solicitar al usuario si faltan.
     *         Devuelve confirmación con el ID asignado.
     *         """)
     * public MiResponse registra_entidad(
     *         @ToolParam(description = "Nombre de la entidad. Debe ser único.")
     *         String nombre,
     *         @ToolParam(description = "Descripción detallada. Mínimo 10 caracteres.")
     *         String descripcion) {
     *
     *     McpRestClient client = restClientFactory.getClient("nombre-servicio");
     *     MiRequest request = new MiRequest(nombre, descripcion);
     *     return client.post("/entidad/", request, MiResponse.class).body();
     * }
     */


}
