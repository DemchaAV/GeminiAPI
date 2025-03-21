import anki.data.Lesson;
import org.gemini.request_schema_generation.SchemaGenerator;

public class TestJson {
    public static void main(String[] args) throws Exception {
        // Выводим итоговую схему
        SchemaGenerator.generateJsonNode(Lesson.class);
        System.out.println( SchemaGenerator.generateAsString(Lesson.class));
    }
}