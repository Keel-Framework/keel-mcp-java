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
package ${package}.mcp.tools;

import io.keelframework.mcp.adapters.rest.client.McpRestClient;
import io.keelframework.mcp.adapters.rest.client.McpRestClientFactory;
import io.keelframework.mcp.adapters.rest.exception.RestClientException;
import io.keelframework.mcp.adapters.rest.model.RestResponse;
import io.keelframework.mcp.common.exceptions.McpToolException;
import jakarta.annotation.PostConstruct;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import io.keelframework.mcp.observability.logging.handler.McpAuthLoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Map;
/**
 * MCP Tools — actions the LLM can execute against the backend.
 *
 * DEVELOPER GUIDE:
 * ─────────────────────────────────────────────────────────────────
 * 1. Add one method per business action
 * 2. Annotate each method with @Tool and a clear description:
 *
 *    @Tool(description = """
 *        [WHAT IT DOES]  — the concrete action, in one line.
 *        [WHEN TO USE]   — situations where the LLM should call it.
 *        [WHAT IT RETURNS] — format and content of the response.
 *        """)
 *
 * 3. Annotate each parameter with @ToolParam:
 *
 *    @ToolParam(description = "Type + valid values + example")
 *
 * 4. Use McpRestClientFactory to call the backend:
 *
 *    McpRestClient client = restClientFactory.getClient("service-name");
 *    RestResponse<MyResponse> response = client.get("/path/{id}", MyResponse.class, id);
 *
 * EXAMPLES:
 * ─────────────────────────────────────────────────────────────────
 * See the commented-out methods below for reference.
 * Remove the examples once you implement your actual tools.
 */
@Component
public class Tools {
    private static final Logger log = LoggerFactory.getLogger(Tools.class);

    private static final String SERVICE_NAME = "SERVICE_NAME";

    private McpRestClient name_service;
    private final McpRestClientFactory restClientFactory;
    private final McpAuthLoggingHandler mcplog;


    public Tools(McpRestClientFactory restClientFactory, McpAuthLoggingHandler mcplog) {
        this.restClientFactory = restClientFactory;
        this.mcplog = mcplog;
    }

    @PostConstruct
    void init(){

        //this.name_service = restClientFactory.getClient(SERVICE_NAME);

    }

    @Tool(description = "Returns a simple greeting. Use this to verify the MCP server is reachable and tools are working.")
    public String ping() {
        return "ping";
    }


    // ================================================
    // EXAMPLES TOOLS — remove when you implement your own prompts
    // ================================================
    /**
     * Example — look up a single entity by its identifier.
     * PATTERN: single-parameter lookup, typed DTO return, path variable
     *
     * @Tool(description = """
     *       Find a pet by its identifier.
     *       Use when the user asks about a specific pet or mentions an ID.
     *       Returns the pet's name, category and status.
     *       """)
     * public PetstoreDTO.PetResponse findPet(
     *       @ToolParam(description = "Numeric ID of the pet. Example: 1, 2, 3")
     *       long petId) {
     *
     *   long start = System.nanoTime();
     *   String path = "/pet/" + petId;
     *   try {
     *       RestResponse<PetstoreDTO.PetResponse> response = petClient.get(
     *               "/pet/{petId}", PetstoreDTO.PetResponse.class, petId);
     *
     *       long durationMs = (System.nanoTime() - start) / 1_000_000;
     *       mcplog.logTool(SERVICE_NAME, path, response.httpStatus(), durationMs);
     *       return response.body();
     *   }
     *   catch (RestClientException e) {
     *       long durationMs = (System.nanoTime() - start) / 1_000_000;
     *       mcplog.logTool(SERVICE_NAME, path, 500, durationMs);
     *       log.warn(PetstoreErrorsMsg.PET_NOT_FOUND_MSG, petId, path, e.getMessage());
     *       throw new McpToolException(SERVICE_NAME, path, 500,
     *               "Could not retrieve pet " + petId, e);
     *   }
     * }
     */

    /**
     * Example — list entities filtered by a query parameter, with
     * input validation before calling the backend.
     * PATTERN: GET with safe query params, upfront validation,
     * collection wrapped in a dedicated response record
     *
     * @Tool(description = """
     *       List pets filtered by status.
     *       Use when the user wants to see available, pending or sold pets.
     *       Returns the list with name, category, status and the total number of results.
     *       """)
     * public PetstoreDTO.PetListResponse listPets(
     *       @ToolParam(description = "Status to filter by. Valid values: available, pending, sold.")
     *       String status) {
     *
     *   if (!VALID_STATUSES.contains(status)) {
     *       log.warn(PetstoreErrorsMsg.PET_INVALID_STATUS_MSG, status);
     *       throw new McpToolException(SERVICE_NAME, "/pet/findByStatus", 400,
     *               "Invalid status '" + status + "'. Valid values: available, pending, sold.");
     *   }
     *
     *   long start = System.nanoTime();
     *   String path = "/pet/findByStatus";
     *   try {
     *       RestResponse<PetstoreDTO.PetResponse[]> response = petClient.getListWithQueryParams(
     *               path, Map.of("status", status), PetstoreDTO.PetResponse[].class);
     *
     *       long durationMs = (System.nanoTime() - start) / 1_000_000;
     *       mcplog.logTool(SERVICE_NAME, path, response.httpStatus(), durationMs);
     *
     *       List<PetstoreDTO.PetResponse> pets = response.body();
     *       return new PetstoreDTO.PetListResponse(pets, pets.size());
     *   }
     *   catch (RestClientException e) {
     *       long durationMs = (System.nanoTime() - start) / 1_000_000;
     *       mcplog.logTool(SERVICE_NAME, path, 500, durationMs);
     *       log.warn(PetstoreErrorsMsg.PET_LIST_FAILED_MSG, status, path, e.getMessage());
     *       throw new McpToolException(SERVICE_NAME, path, 500,
     *               "Could not retrieve the pet list for status '" + status + "'", e);
     *   }
     * }
     */

    /**
     * Example — create a new entity from several required parameters.
     * PATTERN: POST with a request DTO, upfront validation, returns
     * the created entity with its assigned ID
     *
     * @Tool(description = """
     *       Register a new pet in the system.
     *       Use when the user wants to add a new pet.
     *       Requires name, category and status — ask the user if any are missing.
     *       Returns the registered pet with its assigned ID.
     *       """)
     * public PetstoreDTO.PetResponse registerPet(
     *       @ToolParam(description = "Pet's name. Required.")
     *       String name,
     *       @ToolParam(description = "Pet's category. Example: Dog, Cat, Bird.")
     *       String category,
     *       @ToolParam(description = "Pet's status. Valid values: available, pending, sold.")
     *       String status) {
     *
     *   if (!VALID_STATUSES.contains(status)) {
     *       log.warn(PetstoreErrorsMsg.PET_INVALID_STATUS_MSG, status);
     *       throw new McpToolException(SERVICE_NAME, "/pet", 400,
     *               "Invalid status '" + status + "'. Valid values: available, pending, sold.");
     *   }
     *
     *   long start = System.nanoTime();
     *   String path = "/pet";
     *   PetstoreDTO.PetRequest request = new PetstoreDTO.PetRequest(
     *           name, new PetstoreDTO.Category(null, category), status);
     *
     *   try {
     *       RestResponse<PetstoreDTO.PetResponse> response =
     *               petClient.post(path, request, PetstoreDTO.PetResponse.class);
     *
     *       long durationMs = (System.nanoTime() - start) / 1_000_000;
     *       mcplog.logTool(SERVICE_NAME, path, response.httpStatus(), durationMs);
     *       return response.body();
     *   }
     *   catch (RestClientException e) {
     *       long durationMs = (System.nanoTime() - start) / 1_000_000;
     *       mcplog.logTool(SERVICE_NAME, path, 500, durationMs);
     *       log.warn(PetstoreErrorsMsg.PET_CREATION_FAILED_MSG, name, path, e.getMessage(), e.getMessage());
     *       throw new McpToolException(SERVICE_NAME, path, 500,
     *               "Could not register pet '" + name + "'", e);
     *   }
     * }
     */






}
