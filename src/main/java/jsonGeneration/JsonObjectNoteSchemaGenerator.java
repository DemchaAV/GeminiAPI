package jsonGeneration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.List;

public class JsonObjectNoteSchemaGenerator {

    public static ObjectNode generateJsonSchema(Class<?> clazz) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode schema = objectMapper.createObjectNode();

        schema.put("type", "object");
        schema.set("properties", getPropertiesNode(clazz, objectMapper));
        schema.set("required", getRequiredNode(clazz, objectMapper));

        return schema;
    }

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

            // Получаем тип элементов списка
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
        }else {
            fieldNode.put("type", "object");
            ObjectNode propertiesNode = getPropertiesNode(fieldType, objectMapper);
            ArrayNode requiredNode = getRequiredNode(fieldType, objectMapper);

            if (propertiesNode.size() > 0) { // Проверка, есть ли свойства
                fieldNode.set("properties", propertiesNode);
            }
            if (requiredNode.size() > 0) { // Проверка, есть ли обязательные поля
                fieldNode.set("required", requiredNode);
            }
            if(propertiesNode.size() == 0){
                fieldNode.put("additionalProperties", false);
            }
        }

        return fieldNode;
    }

    private static ObjectNode getPropertiesNode(Class<?> clazz, ObjectMapper objectMapper) {
        ObjectNode propertiesNode = objectMapper.createObjectNode();
        for (Field field : clazz.getDeclaredFields()) {
            propertiesNode.set(field.getName(), getTypeNode(field, objectMapper));
        }
        return propertiesNode;
    }

    private static ArrayNode getRequiredNode(Class<?> clazz, ObjectMapper objectMapper) {
        ArrayNode requiredNode = objectMapper.createArrayNode();
        for (Field field : clazz.getDeclaredFields()) {
            requiredNode.add(field.getName());
        }
        return requiredNode;
    }
}
