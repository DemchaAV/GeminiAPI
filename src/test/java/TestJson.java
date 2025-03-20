import anki.data.Lesson;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.gemini.core.client.request_schema_generation.SchemaGenerator;

import java.util.Iterator;
import java.util.Map;

public class TestJson {
    public static void main(String[] args) throws Exception {
        // Выводим итоговую схему
        SchemaGenerator.generateJsonNode(Lesson.class);
        System.out.println( SchemaGenerator.generateAsString(Lesson.class));
    }
}