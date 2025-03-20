package org.gemini.core.client.request_schema_generation;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * Utility class for generating a JSON Schema representation for a given Java class.
 *
 * <p>This class uses reflection and the Jackson library to inspect the fields of the provided class
 * and generate a JSON Schema. The generated schema includes the type definition ("object"), properties,
 * and required fields based on the class's declared fields.</p>
 *
 * <p>The following field types are supported:</p>
 * <ul>
 *     <li><strong>String</strong>: Mapped to JSON type "string".</li>
 *     <li><strong>Numeric types</strong> (primitives and wrappers): Mapped to JSON type "number".</li>
 *     <li><strong>Boolean</strong> (primitive and wrapper): Mapped to JSON type "boolean".</li>
 *     <li><strong>Enum</strong>: Mapped to JSON type "string" with an "enum" array containing possible values.</li>
 *     <li><strong>List</strong>: Mapped to JSON type "array". The item type is determined by the generic parameter.
 *         If the item type is a complex object, its properties and required fields are also generated.</li>
 *     <li><strong>Other objects</strong>: Mapped to JSON type "object", with nested properties and required fields.</li>
 * </ul>
 *
 * <p>If an object has no declared properties, the generated schema will set "additionalProperties" to false.</p>
 *
 * @author
 */
public class JsonObjectNoteSchemaGenerator {

    /**
     * Generates a JSON Schema for the specified Java class.
     *
     * @param clazz the class for which the JSON Schema should be generated
     * @return an {@link ObjectNode} representing the JSON Schema for the given class
     */
    public static ObjectNode generateJsonSchema(Class<?> clazz) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode schema = objectMapper.createObjectNode();

        schema.put("type", "object");
        schema.set("properties", getPropertiesNode(clazz, objectMapper));
        schema.set("required", getRequiredNode(clazz, objectMapper));

        return schema;
    }

    /**
     * Generates a JSON Schema node representing the type definition for a specific field.
     *
     * <p>This method inspects the field's type and creates an appropriate JSON Schema definition.
     * It handles basic types (String, Number, Boolean), enumerations (by listing possible values),
     * collections (by determining the type of list elements), and nested objects.</p>
     *
     * @param field         the field to generate the schema for
     * @param objectMapper  the {@link ObjectMapper} instance used to create JSON nodes
     * @return a {@link JsonNode} representing the JSON Schema for the field
     */
    private static JsonNode getTypeNode(Field field, ObjectMapper objectMapper) {
        ObjectNode fieldNode = objectMapper.createObjectNode();
        Class<?> fieldType = field.getType();

        if (fieldType == String.class) {
            fieldNode.put("type", "string");
        } else if (Number.class.isAssignableFrom(fieldType) || fieldType.isPrimitive()) {
            fieldNode.put("type", "number");
        } else if (Boolean.class.isAssignableFrom(fieldType) || fieldType == boolean.class) {
            fieldNode.put("type", "boolean");
        } else if (fieldType.isEnum()) {
            fieldNode.put("type", "string");
            ArrayNode enumValues = objectMapper.createArrayNode();
            for (Object constant : fieldType.getEnumConstants()) {
                enumValues.add(constant.toString());
            }
            fieldNode.set("enum", enumValues);
        } else if (List.class.isAssignableFrom(fieldType)) {
            fieldNode.put("type", "array");

            // Retrieve the item type of the list using generics
            ParameterizedType listType = (ParameterizedType) field.getGenericType();
            Class<?> itemType = (Class<?>) listType.getActualTypeArguments()[0];

            ObjectNode itemsNode = objectMapper.createObjectNode();
            if (itemType == String.class) {
                itemsNode.put("type", "string");
            } else if (Number.class.isAssignableFrom(itemType) || itemType.isPrimitive()) {
                itemsNode.put("type", "number");
            } else if (Boolean.class.isAssignableFrom(itemType)) {
                itemsNode.put("type", "boolean");
            } else {
                itemsNode.put("type", "object");
                itemsNode.set("properties", getPropertiesNode(itemType, objectMapper));
                itemsNode.set("required", getRequiredNode(itemType, objectMapper));
            }

            fieldNode.set("items", itemsNode);
        } else {
            fieldNode.put("type", "object");
            ObjectNode propertiesNode = getPropertiesNode(fieldType, objectMapper);
            ArrayNode requiredNode = getRequiredNode(fieldType, objectMapper);

            if (propertiesNode.size() > 0) { // If there are properties, add them
                fieldNode.set("properties", propertiesNode);
            }
            if (requiredNode.size() > 0) { // If there are required fields, add them
                fieldNode.set("required", requiredNode);
            }
            if (propertiesNode.size() == 0) {
                fieldNode.put("additionalProperties", false);
            }
        }

        return fieldNode;
    }

    /**
     * Generates an {@link ObjectNode} containing JSON Schema definitions for all declared fields of the given class.
     *
     * @param clazz         the class whose fields will be processed as schema properties
     * @param objectMapper  the {@link ObjectMapper} instance used to create JSON nodes
     * @return an {@link ObjectNode} where each field name is mapped to its JSON Schema definition
     */
    private static ObjectNode getPropertiesNode(Class<?> clazz, ObjectMapper objectMapper) {
        ObjectNode propertiesNode = objectMapper.createObjectNode();
        for (Field field : clazz.getDeclaredFields()) {
            propertiesNode.set(field.getName(), getTypeNode(field, objectMapper));
        }
        return propertiesNode;
    }

    /**
     * Generates an {@link ArrayNode} listing all declared fields of the given class as required properties.
     *
     * <p>Every field declared in the class is considered required in the generated JSON Schema.</p>
     *
     * @param clazz         the class whose field names will be added as required properties
     * @param objectMapper  the {@link ObjectMapper} instance used to create JSON nodes
     * @return an {@link ArrayNode} containing the names of all declared fields
     */
    private static ArrayNode getRequiredNode(Class<?> clazz, ObjectMapper objectMapper) {
        ArrayNode requiredNode = objectMapper.createArrayNode();
        for (Field field : clazz.getDeclaredFields()) {
            requiredNode.add(field.getName());
        }
        return requiredNode;
    }
}
