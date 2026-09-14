#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.mcp.prompts;

import org.springframework.ai.mcp.annotation.McpArg;
import org.springframework.ai.mcp.annotation.McpPrompt;
import org.springframework.stereotype.Component;

/**
 * Prompts MCP — plantillas de conversación reutilizables.
 *
 * GUÍA PARA EL DESARROLLADOR:
 * ─────────────────────────────────────────────────────────────────
 * Un Prompt es una plantilla que guía al LLM en flujos específicos.
 * El MCP Client lo invoca para estructurar la conversación.
 *
 * CUÁNDO USAR PROMPTS:
 *  - Flujos complejos con varios pasos transacionales (alta simulacion, contratación)
 *  - Cuando necesitas que el LLM siga instrucciones exactas
 *  - Procesos que requieren validación antes de ejecutar una tool
 *  - Respuestas que deben tener un formato estructurado específico
 *
 * ANOTACIÓN @McpPrompt:
 * ─────────────────────────────────────────────────────────────────
 *  name        → identificador único del prompt
 *                formato: "mcp-server6-rest-poc-nombre-accion"
 *  description → CRÍTICO — describe cuándo el MCP Client debe invocar
 *                este prompt. Sé específico:
 *                "Usar cuando el usuario quiera X"
 *
 * ANOTACIÓN @McpArg:
 * ─────────────────────────────────────────────────────────────────
 *  description → explica qué dato se espera en este parámetro
 *                incluye formato y valores válidos si aplica
 *
 * ESTRUCTURA DEL MENSAJE:
 * ─────────────────────────────────────────────────────────────────
 * Solo USER    → el LLM decide cómo responder = casos simples
 * USER+ASSISTANT → el LLM sigue el formato exacto  = respuestas estructuradas
 *
 * NOTA: Esta clase es OPCIONAL.
 * Elimínala si no necesitas guiar al LLM en flujos específicos.
 * ─────────────────────────────────────────────────────────────────
 */


@Component
public class Prompts {

    // ================================================
    // EJEMPLOS — eliminar cuando implementes tus promtps
    // ================================================

    /**
     * Ejemplo 1 — Prompt de consulta con un parámetro, guía al LLM para consultar una entidad por ID.
     * PATRÓN: consulta por identificador
     * TOOL que invocará: busca_[entidad](id)
     *
     * @McpPrompt(
     *       name = "mcp-server6-rest-poc-consulta",
     *       description = "Plantilla para consultar una entidad por su identificador ID. " +
     *               "Usar cuando el usuario quiera información detallada de un elemento específico."
     * )
     * public McpSchema.GetPromptResult consultaEntidad(
     *       @McpArg(description = "Identificador numérico de la entidad. Ejemplo: 1, 2, 3")
     *       String id) {
     *
     *   return McpSchema.GetPromptResult.builder()
     *           .description("Consulta entidad por ID")
     *           .messages(List.of(
     *                   McpSchema.PromptMessage.builder()
     *                           .role(McpSchema.Role.USER)
     *                           .content(new McpSchema.TextContent(
     *                                   "Dame información completa de la entidad con ID " + id + ".\n" +
     *                                           "Incluye todos los detalles y datos relacionados disponibles."))
     *                           .build()
     *           ))
     *           .build();
     * }*/

    /**
     * Ejemplo 2 — Prompt de registro con múltiples parámetros.
     * Guía al LLM para registrar una nueva entidad validando los datos.
     *
     * PATRÓN: creación con validación previa
     * TOOL que invocará: registra_[entidad](params)
     *
     * @McpPrompt(
     *       name = "mcp-server6-rest-poc-registrar",
     *       description = "Plantilla para registrar una nueva entidad en el sistema. " +
     *               "Usar cuando el usuario quiera crear o añadir un nuevo elemento. " +
     *               "Valida los datos antes de invocar la tool de registro."
     * )
     * public McpSchema.GetPromptResult registrarEntidad(
     *       @McpArg(description = "Nombre de la entidad a registrar. Debe ser único en el sistema.")
     *       String nombre,
     *       @McpArg(description = "Descripción detallada de la entidad. Mínimo 10 caracteres.")
     *       String descripcion) {
     *
     *   return McpSchema.GetPromptResult.builder()
     *           .description("Registro de nueva entidad")
     *           .messages(List.of(
     *                   McpSchema.PromptMessage.builder()
     *                           .role(McpSchema.Role.USER)
     *                           .content(new McpSchema.TextContent(
     *                                   "Quiero registrar una nueva entidad con los siguientes datos:\n" +
     *                                           "- Nombre:      " + nombre + "\n" +
     *                                           "- Descripción: " + descripcion + "\n\n" +
     *                                           "Antes de registrar:\n" +
     *                                           "1. Verifica que no existe una entidad con el mismo nombre\n" +
     *                                           "2. Valida que la descripción tiene mínimo 10 caracteres\n" +
     *                                           "3. Si todo es correcto, procede con el registro\n" +
     *                                           "4. Confirma al usuario el ID asignado"))
     *                           .build()
     *           ))
     *           .build();
     * }*/

    /**
     * Ejemplo 3 —  Define también la respuesta esperada del LLM - Prompt con mensaje ASSISTANT (comentado).
     * OPCIONAL — usar cuando necesites controlar el formato de respuesta.
     *
     * @McpPrompt(
     *         name = "mcp-server6-rest-poc-consulta-estructurada",
     *         description = "Consulta con respuesta en formato estructurado. " +
     *                 "Usar cuando necesites una respuesta con formato específico."
     * )
     * public McpSchema.GetPromptResult consultaEstructurada(
     *         @McpArg(description = "ID de la entidad") String id) {
     *
     *     return McpSchema.GetPromptResult.builder()
     *             .description("Consulta con respuesta estructurada")
     *             .messages(List.of(
     *                     McpSchema.PromptMessage.builder()
     *                             .role(McpSchema.Role.USER)
     *                             .content(new McpSchema.TextContent(
     *                                     "Dame información de la entidad con ID " + id))
     *                             .build(),
     *                     McpSchema.PromptMessage.builder()
     *                             .role(McpSchema.Role.ASSISTANT)
     *                             .content(new McpSchema.TextContent(
     *                                     "Aquí están los datos de la entidad:\n" +
     *                                     "- ID: ...\n" +
     *                                     "- Nombre: ...\n" +
     *                                     "- Descripción: ..."))
     *                             .build()
     *             ))
     *             .build();
     * }
     */


}
