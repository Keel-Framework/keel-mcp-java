#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.mcp.resources;

import org.springframework.ai.mcp.annotation.McpResource;
import org.springframework.stereotype.Component;

/**
 * Resources MCP — contexto y datos expuestos al LLM.
 *
 * GUÍA PARA EL DESARROLLADOR:
 * ─────────────────────────────────────────────────────────────────
 * Un Resource es información que el LLM puede consultar
 * sin ejecutar acciones contra el backend.
 *
 * CUÁNDO USAR RESOURCES:
 *  - Catálogos de datos que cambian poco (ramos, tipos, estados)
 *  - Reglas de negocio que el LLM debe respetar
 *  - Guías de uso de las tools disponibles
 *  - Contexto del dominio que el LLM no conoce
 *
 * TIPOS DE RESOURCE:
 *
 *  1. ESTÁTICO — contenido hardcoded, no cambia
 *     public String miResource() {
 *         return "contenido fijo";
 *     }
 *
 *  2. DINÁMICO — contenido del backend en tiempo real
 *     public String miResource() {
 *         McpRestClient client = restClientFactory.getClient("servicio");
 *         return client.get("/catalogo/", String.class).body();
 *     }
 *
 *
 * ANOTACIÓN @McpResource (Estandar de Spring AI):
 * ─────────────────────────────────────────────────────────────────
 *  uri         → identificador único del resource
 *                formato: "${artifactId}://nombre-descriptivo"
 *  name        → nombre legible para el LLM
 *  description → CRÍTICO — el LLM decide si consultar este resource
 *                basándose en esta description. Sé específico:
 *                "Consultar cuando el usuario pregunte sobre X"
 *  mimeType    → "text/plain" o "application/json"
 *
 * NOTA: Esta clase es OPCIONAL.
 * Elimínala si no necesitas exponer contexto al LLM.
 * ─────────────────────────────────────────────────────────────────
 */
@Component
public class Resources {

    // ================================================
    // EJEMPLOS — eliminar cuando implementes tus resources
    // ================================================

    /**
     * Ejemplo 1 — Guía de tools disponibles para que el LLM sepa cuándo invocar cada una
     * Resource estático.
     * RECOMENDADO — mantener actualizado cuando añadas nuevas tools.
     *
     * @McpResource(
     *       uri = "mcpsca://tools-guide",
     *       name = "Guía de Tools disponibles",
     *       description = "Describe cuándo y cómo usar cada tool disponible. " +
     *               "Consultar cuando el LLM necesite decidir qué tool invocar.",
     *       mimeType = "text/plain"
     * )
     * public String toolsGuide() {
     *   return """
     *           TOOLS DISPONIBLES EN mcpsca:
     *
     *           // TODO: documenta aquí cada tool que implementes en HelloWorldItemMapper.java
     *           // Formato recomendado:
     *
     *           1. nombre_tool(parametros)
     *              - Cuándo usar: situación donde el LLM debe invocarla
     *              - Parámetros:  descripción de cada parámetro
     *              - Ejemplo:     frase del usuario que activa esta tool
     *              - Devuelve:    formato y contenido de la respuesta
     *
     *           REGLAS GENERALES:
     *           - Nunca inventar datos — usar siempre las tools para información real
     *           - Si faltan parámetros obligatorios, solicitarlos al usuario antes de invocar
     *           - Responde siempre en español
     *           """;
    * } */

    /**
     * Ejemplo 2  — atálogo obtenido del backend en tiempo real
     * Resource dinámico (comentado).
     *
     * Para usarlo:
     *  1. Inyecta McpRestClientFactory en el constructor
     *  2. Configura el servicio en application-dev.yml
     *  3. Descomenta el método
     *
     * @McpResource(
     *         uri = "${artifactId}://catalogo",
     *         name = "Catálogo dinámico",
     *         description = "Datos actualizados del backend en tiempo real. " +
     *                 "Consultar cuando el usuario pregunte por datos que cambian frecuentemente.",
     *         mimeType = "application/json"
     * )
     * public String catalogoDinamico() {
     *     McpRestClient client = restClientFactory.getClient("nombre-servicio");
     *     return client.get("/catalogo/", String.class).body();
     * }
     */

}