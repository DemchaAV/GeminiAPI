package org.gemini.core.client.request_schema_generation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.factories.SchemaFactoryWrapper;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;
import java.util.Map;

/**
 * SchemaGenerator is responsible for generating a JSON schema from a given Java class.
 * It removes the "id" fields from the generated schema and adds "required" fields to each object.
 * Exception handling and logging (via SLF4J) are integrated into the process.
 */
@Slf4j
public class SchemaGenerator {

    /**
     * Recursively removes the "id" field from the provided JsonNode.
     *
     * @param node the JsonNode from which "id" fields should be removed.
     */
    private static void removeIdFromJsonNode(JsonNode node) {
        if (node.isObject()) {
            ObjectNode objNode = (ObjectNode) node;
            objNode.remove("id"); // Remove the "id" key
            Iterator<Map.Entry<String, JsonNode>> fields = objNode.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                removeIdFromJsonNode(entry.getValue());
            }
        } else if (node.isArray()) {
            for (JsonNode item : node) {
                removeIdFromJsonNode(item);
            }
        }
    }

    /**
     * Recursively adds a "required" array to object nodes based on their properties.
     *
     * @param node the JsonNode to process.
     */
    private static void addRequiredRecursively(JsonNode node) {
        if (node.isObject()) {
            ObjectNode objNode = (ObjectNode) node;
            // If "properties" exists, add a "required" array based on the property keys
            if (objNode.has("properties") && objNode.get("properties").isObject()) {
                ObjectNode props = (ObjectNode) objNode.get("properties");
                if (!objNode.has("required")) {
                    ArrayNode requiredArray = objNode.putArray("required");
                    Iterator<String> fieldNames = props.fieldNames();
                    while (fieldNames.hasNext()) {
                        String fieldName = fieldNames.next();
                        requiredArray.add(fieldName);
                    }
                }
                // Recursively process each property
                Iterator<Map.Entry<String, JsonNode>> fields = props.fields();
                while (fields.hasNext()) {
                    Map.Entry<String, JsonNode> entry = fields.next();
                    addRequiredRecursively(entry.getValue());
                }
            }
            // If "items" exists (for arrays), process them recursively
            if (objNode.has("items")) {
                addRequiredRecursively(objNode.get("items"));
            }
        } else if (node.isArray()) {
            for (JsonNode item : node) {
                addRequiredRecursively(item);
            }
        }
    }

    /**
     * Generates a JSON schema as a JsonNode for the provided Java class.
     * It removes all "id" fields and adds "required" arrays based on properties.
     *
     * @param clazz        the Class for which the JSON schema is generated.
     * @param withIdFields Remove all "id" fields.
     * @param required     Add "required" arrays to all object nodes (if applicable).
     * @return a JsonNode representing the generated JSON schema.
     * @throws RuntimeException if the schema generation fails.
     */
    public static JsonNode generateJsonNode(Class<?> clazz, boolean withIdFields, boolean required) {
        ObjectMapper mapper = new ObjectMapper();
        SchemaFactoryWrapper visitor = new SchemaFactoryWrapper();
        try {
            log.info("Generating JSON schema for class: {}", clazz.getName());
            mapper.acceptJsonFormatVisitor(clazz, visitor);
            JsonSchema schema = visitor.finalSchema();

            // Convert the generated schema to a JsonNode
            JsonNode schemaNode = mapper.valueToTree(schema);

            if (withIdFields) {
                removeIdFromJsonNode(schemaNode);
            }

            if (required) {
                addRequiredRecursively(schemaNode);
            }
            log.info("Successfully generated JSON schema for class: {}", clazz.getName());
            return schemaNode;
        } catch (Exception e) {
            log.error("Error generating JSON schema for class: {}", clazz.getName(), e);
            throw new RuntimeException("Failed to generate JSON schema", e);
        }
    }
    
    public static JsonNode generateJsonNode(Class<?> clazz) {
        return generateJsonNode(clazz,true,true);
    }

    /**
     * Generates a formatted JSON schema string for the provided Java class.
     *
     * @param clazz the Class for which the JSON schema is generated.
     * @return a pretty-printed JSON string representing the schema.
     * @throws RuntimeException if the JSON string generation fails.
     */
    public static String generateAsString(Class<?> clazz) {
        try {
            log.info("Generating JSON schema string for class: {}", clazz.getName());
            JsonNode schemaNode = generateJsonNode(clazz);
            ObjectMapper mapper = new ObjectMapper();
            String result = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(schemaNode);
            log.info("Successfully generated JSON schema string for class: {}", clazz.getName());
            return result;
        } catch (JsonProcessingException e) {
            log.error("Error processing JSON schema string for class: {}", clazz.getName(), e);
            throw new RuntimeException("Failed to generate JSON schema string", e);
        } catch (Exception e) {
            log.error("Unexpected error generating JSON schema string for class: {}", clazz.getName(), e);
            throw new RuntimeException("Unexpected error while generating JSON schema string", e);
        }
    }
}
