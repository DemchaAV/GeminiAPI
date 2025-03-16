import anki.data.Lesson;
import jsonGeneration.JsonObjectNoteSchemaGenerator;

public class ReusesTest {
    public static void main(String[] args) {
        System.out.println(JsonObjectNoteSchemaGenerator.generateJsonSchema(Lesson.class).toPrettyString());
    }
}
