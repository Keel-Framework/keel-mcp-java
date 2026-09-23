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
#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.mcp.prompts;

import org.springframework.ai.mcp.annotation.McpArg;
import org.springframework.ai.mcp.annotation.McpPrompt;
import org.springframework.stereotype.Component;

/**
 * MCP Prompts — reusable conversation templates.
 *
 * DEVELOPER GUIDE:
 * ─────────────────────────────────────────────────────────────────
 * A Prompt is a template that guides the LLM through specific flows.
 * The MCP Client invokes it to structure the conversation.
 *
 * WHEN TO USE PROMPTS:
 *  - Complex flows with multiple transactional steps (e.g. quote
 *    simulation, contracting)
 *  - When the LLM needs to follow exact instructions
 *  - Processes that require validation before executing a tool
 *  - Responses that must follow a specific structured format
 *
 * @McpPrompt ANNOTATION:
 * ─────────────────────────────────────────────────────────────────
 *  name        → unique identifier for the prompt
 *                format: "mcp-server-rest-poc-action-name"
 *  description → CRITICAL — describes when the MCP Client should
 *                invoke this prompt. Be specific:
 *                "Use when the user wants to X"
 *
 * @McpArg ANNOTATION:
 * ─────────────────────────────────────────────────────────────────
 *  description → explains what data is expected for this parameter,
 *                including format and valid values if applicable
 *
 * MESSAGE STRUCTURE:
 * ─────────────────────────────────────────────────────────────────
 * USER only        → the LLM decides how to respond = simple cases
 * USER + ASSISTANT → the LLM follows the exact format = structured responses
 *
 * NOTE: This class is OPTIONAL.
 * Remove it if you don't need to guide the LLM through specific flows.
 * ─────────────────────────────────────────────────────────────────
 */

@Component
public class Prompts {

    // ================================================
    // EXAMPLES — remove when you implement your own prompts
    // ================================================

    /**
     * Example — a prompt with a single parameter, guiding the LLM to
     * look up an entity by ID.
     * PATTERN: lookup by identifier
     * TOOL it will invoke: find[Entity](id)
     *
     * @McpPrompt(
     *       name = "petstore-find-pet",
     *       description = "Template for looking up a pet by its ID. " +
     *               "Use when the user wants full details about one specific pet."
     * )
     * public McpSchema.GetPromptResult findPetPrompt(
     *       @McpArg(description = "Numeric ID of the pet. Example: 1, 2, 3")
     *       String petId) {
     *
     *   return new McpSchema.GetPromptResult(
     *           "Look up a pet by ID",
     *           List.of(
     *                   new McpSchema.PromptMessage(
     *                           McpSchema.Role.USER,
     *                           new McpSchema.TextContent(
     *                                   "Give me the full details of the pet with ID " + petId + ".\n" +
     *                                           "Include its name, category and current status."))
     *           ));
     * }
     */

    /**
     * Example — a multi-step prompt (USER + ASSISTANT) that guides the
     * LLM to collect all required fields before invoking a creation
     * tool, instead of calling it with missing data.
     * PATTERN: guided data collection before a write operation
     * TOOL it will invoke: register[Entity](field1, field2, field3)
     *
     * @McpPrompt(
     *       name = "petstore-register-pet",
     *       description = "Template for registering a new pet, guiding the model to " +
     *               "collect name, category and status before calling the registerPet tool. " +
     *               "Use when the user wants to add a pet but hasn't given all the required fields yet."
     * )
     * public McpSchema.GetPromptResult registerPetPrompt() {
     *
     *   return new McpSchema.GetPromptResult(
     *           "Register a new pet, step by step",
     *           List.of(
     *                   new McpSchema.PromptMessage(
     *                           McpSchema.Role.USER,
     *                           new McpSchema.TextContent(
     *                                   "I want to register a new pet.")),
     *                   new McpSchema.PromptMessage(
     *                           McpSchema.Role.ASSISTANT,
     *                           new McpSchema.TextContent(
     *                                   "Sure! I need three things before I can register the pet:\n" +
     *                                           "1. Name\n" +
     *                                           "2. Category (e.g. Dog, Cat, Bird)\n" +
     *                                           "3. Status (available, pending, or sold)\n\n" +
     *                                           "Please provide all three, and I'll confirm the details " +
     *                                           "back to you before registering."))
     *           ));
     * }
     */


}
