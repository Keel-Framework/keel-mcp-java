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
 * Serializa McpLogEntry a JSON.
 */
public class McpLogSerializer {

    private static final Logger log = LoggerFactory.getLogger(McpLogSerializer.class);

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private McpLogSerializer() {}

    /**
     * Serializa una McpLogEntry a JSON string.
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
     * Convierte un objeto a JsonNode para indexación en ELK.
     * Devuelve null si el objeto es null.
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
