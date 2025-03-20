import anki.data.Lesson;
import org.gemini.core.client.request_response.request.GeminiRequest;
import org.gemini.core.client.request_schema_generation.JsonObjectNoteSchemaGenerator;

public class TestJson {
    public static void main(String[] args) {
        var json = JsonObjectNoteSchemaGenerator.generateJsonSchema(GeminiRequest.class);
        System.out.println(json.toPrettyString());

    }
}
