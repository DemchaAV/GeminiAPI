import io.github.demchaav.gemini.GeminiClient;
import io.github.demchaav.gemini.request_schema_generation.SchemaGenerator;

public class TestJson {
    public static void main(String[] args) throws Exception {
        // Выводим итоговую схему
        SchemaGenerator.generateJsonNode(GeminiClient.class);
        System.out.println( SchemaGenerator.generateAsString(GeminiClient.class));
    }
}