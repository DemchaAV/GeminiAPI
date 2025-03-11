package jsonGeneration;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public class JsonSimpleStringSchemaGenerator {

    // Public method to get JSON pattern without formatting
    public static String getJsonAsString(Class<?> clazz) {
        return getJsonAsString(clazz, false);
    }

    // Public method with formatting option (with line breaks)
    public static String getJsonAsString(Class<?> clazz, boolean formatted) {
        return getJsonAsString(clazz, formatted, 0, new HashSet<>());
    }

    // Private method with additional parameters for recursion control
    private static String getJsonAsString(Class<?> clazz, boolean formatted, int indentLevel, Set<Class<?>> visitedClasses) {
        if (visitedClasses.contains(clazz)) {
            // Prevent infinite recursion
            return "\"%sReference\"".formatted(clazz.getSimpleName());
        }

        // Add current class to visited set to prevent cycles
        visitedClasses.add(clazz);

        String indent = formatted ? getIndent(indentLevel) : "";
        String newline = formatted ? "\n" : "";
        StringBuilder sb = new StringBuilder("\"")
                .append(clazz.getSimpleName())
                .append("\": {")
                .append(newline);

        Field[] fields = clazz.getDeclaredFields();
        List<String> fieldPatterns = new ArrayList<>();

        for (Field field : fields) {
            String fieldIndent = formatted ? getIndent(indentLevel + 1) : "";
            String generalizedType = getGeneralizedType(field, formatted, indentLevel + 1, visitedClasses);
            fieldPatterns.add("%s\"%s\": %s".formatted(fieldIndent, field.getName(), generalizedType));
        }

        sb.append(String.join(",%s".formatted(newline), fieldPatterns));

        if (formatted && !fieldPatterns.isEmpty()) {
            sb.append(newline).append(indent);
        }

        sb.append("}");

        // Remove class from a visited set when done with this branch
        visitedClasses.remove(clazz);

        return sb.toString();
    }

    // Helper method to generate indentation
    private static String getIndent(int level) {
        StringBuilder indent = new StringBuilder();
        indent.append("  ".repeat(Math.max(0, level))); // Two spaces per level
        return indent.toString();
    }

    // Method to get generalized type from Field object
    private static String getGeneralizedType(Field field, boolean formatted, int indentLevel, Set<Class<?>> visitedClasses) {
        Class<?> fieldType = field.getType();
        Type genericType = field.getGenericType();
        return getGeneralizedType(fieldType, genericType, formatted, indentLevel, visitedClasses);
    }

    // Main method for type conversion
    private static String getGeneralizedType(Class<?> type, Type genericType, boolean formatted, int indentLevel, Set<Class<?>> visitedClasses) {
        String indent = formatted ? getIndent(indentLevel) : "";
        String newline = formatted ? "\n" : "";

        // Handle primitive types
        if (type.isPrimitive()) {
            if (type == int.class || type == long.class || type == float.class ||
                type == double.class || type == short.class || type == byte.class) {
                return "number";
            } else if (type == boolean.class) {
                return "boolean";
            } else if (type == char.class) {
                return "string";
            }
        }

        // Handle wrapper classes and common types
        if (Number.class.isAssignableFrom(type)) {
            return "number";
        } else if (type == String.class || type == Character.class) {
            return "string";
        } else if (type == Boolean.class) {
            return "boolean";
        } else if (Date.class.isAssignableFrom(type)) {
            return "date";
        } else if (Enum.class.isAssignableFrom(type)) {
            return "enum";
        }

        // Handle arrays and collections
        else if (type.isArray() || Collection.class.isAssignableFrom(type)) {
            StringBuilder sb = new StringBuilder("[");
            sb.append(newline);

            String itemIndent = formatted ? getIndent(indentLevel + 1) : "";
            sb.append(itemIndent);

            // Process arrays
            if (type.isArray()) {
                sb.append(getGeneralizedType(type.getComponentType(), null, formatted, indentLevel + 1, visitedClasses));
            }
            // Process collections with generic parameters
            else if (genericType instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) genericType;
                Type[] typeArguments = parameterizedType.getActualTypeArguments();
                if (typeArguments.length > 0) {
                    Type elementType = typeArguments[0];
                    sb.append(processGenericType(elementType, formatted, indentLevel + 1, visitedClasses));
                }
            } else {
                sb.append("\"Object\"");
            }

            sb.append(newline).append(indent).append("]");
            return sb.toString();
        }

        // Handle Maps
        else if (Map.class.isAssignableFrom(type)) {
            StringBuilder sb = new StringBuilder("{");
            sb.append(newline);

            // Try to get generic key and value types
            if (genericType instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) genericType;
                Type[] typeArguments = parameterizedType.getActualTypeArguments();
                if (typeArguments.length > 1) {
                    String keyIndent = formatted ? getIndent(indentLevel + 1) : "";

                    // Process key type
                    sb.append(keyIndent);
                    sb.append("\"key\": ");
                    sb.append(processGenericType(typeArguments[0], formatted, indentLevel + 1, visitedClasses));
                    sb.append(",").append(newline);

                    // Process value type
                    sb.append(keyIndent);
                    sb.append("\"value\": ");
                    sb.append(processGenericType(typeArguments[1], formatted, indentLevel + 1, visitedClasses));
                }
            } else {
                String keyIndent = formatted ? getIndent(indentLevel + 1) : "";
                sb.append(keyIndent).append("\"key\": \"Object\",").append(newline);
                sb.append(keyIndent).append("\"value\": \"Object\"");
            }

            sb.append(newline).append(indent).append("}");
            return sb.toString();
        }

        // Handle custom complex types (recursively)
        else if (!type.isPrimitive() && !type.getName().startsWith("java.") && !type.isEnum()) {
            return getJsonAsString(type, formatted, indentLevel, visitedClasses);
        }

        // Default fallback
        return "\"%s\"".formatted(type.getSimpleName());
    }

    // Helper method to process generic types (including nested ones)
    private static String processGenericType(Type type, boolean formatted, int indentLevel, Set<Class<?>> visitedClasses) {
        String indent = formatted ? getIndent(indentLevel) : "";
        String newline = formatted ? "\n" : "";

        if (type instanceof Class) {
            // Simple class type (String, Integer, etc.)
            Class<?> classType = (Class<?>) type;
            return getGeneralizedType(classType, null, formatted, indentLevel, visitedClasses);
        }
        else if (type instanceof ParameterizedType) {
            // Generic type (List<String>, Map<String, Integer>, etc.)
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type rawType = parameterizedType.getRawType();

            if (rawType instanceof Class) {
                Class<?> rawClass = (Class<?>) rawType;

                // Handle nested collections (List<List<String>>, etc.)
                if (Collection.class.isAssignableFrom(rawClass)) {
                    StringBuilder sb = new StringBuilder("[");
                    sb.append(newline);

                    String itemIndent = formatted ? getIndent(indentLevel + 1) : "";
                    sb.append(itemIndent);

                    Type[] typeArguments = parameterizedType.getActualTypeArguments();
                    if (typeArguments.length > 0) {
                        // Recursively process the nested generic type
                        sb.append(processGenericType(typeArguments[0], formatted, indentLevel + 1, visitedClasses));
                    } else {
                        sb.append("\"Object\"");
                    }

                    sb.append(newline).append(indent).append("]");
                    return sb.toString();
                }
                // Handle nested maps
                else if (Map.class.isAssignableFrom(rawClass)) {
                    StringBuilder sb = new StringBuilder("{");
                    sb.append(newline);

                    Type[] typeArguments = parameterizedType.getActualTypeArguments();
                    if (typeArguments.length > 1) {
                        String keyIndent = formatted ? getIndent(indentLevel + 1) : "";

                        sb.append(keyIndent);
                        sb.append("\"key\": ");
                        sb.append(processGenericType(typeArguments[0], formatted, indentLevel + 1, visitedClasses));
                        sb.append(",").append(newline);

                        sb.append(keyIndent);
                        sb.append("\"value\": ");
                        sb.append(processGenericType(typeArguments[1], formatted, indentLevel + 1, visitedClasses));
                    }

                    sb.append(newline).append(indent).append("}");
                    return sb.toString();
                }

                // Default handling for other parameterized types
                return getGeneralizedType(rawClass, type, formatted, indentLevel, visitedClasses);
            }
        }

        // Fallback for unknown generic types
        return "\"%s\"".formatted(type.getTypeName());
    }


}