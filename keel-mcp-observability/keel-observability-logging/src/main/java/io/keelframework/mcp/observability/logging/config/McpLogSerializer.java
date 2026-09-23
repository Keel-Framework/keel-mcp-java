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
package io.keelframework.mcp.observability.logging.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.keelframework.mcp.observability.logging.model.McpLogEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for serializing MCP log entries to JSON.
 *
 * <p>Uses a Jackson {@link ObjectMapper} configured with Java time support
 * and ISO-8601 date/time serialization.</p>
 *
 * <p>This class is not intended to be instantiated.</p>
 */
public class McpLogSerializer {

    private static final Logger log = LoggerFactory.getLogger(McpLogSerializer.class);

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private McpLogSerializer() {}

    /**
     * Serializes an McpLogEntry to a JSON string.
     */
    public static String toJson(McpLogEntry entry) {
        try {
            return MAPPER.writeValueAsString(entry);
        } catch (JsonProcessingException e) {
            log.warn("Error serializing McpLogEntry: {}", e.getMessage());
            return "{\"error\":\"serialization_failed\"}";
        }
    }

    /**
     * Converts an object to a JsonNode for indexing in ELK.
     * Returns null if the object is null.
     */
    public static JsonNode toJsonNode(Object obj) {
        if (obj == null) return null;
        try {
            return MAPPER.valueToTree(obj);
        } catch (Exception e) {
            log.warn("Error converting to JsonNode: {}", e.getMessage());
            return MAPPER.valueToTree(obj.toString());
        }
    }

}
