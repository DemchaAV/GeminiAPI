import anki.data.Lesson;
import jsonGeneration.JsonObjectNoteSchemaGenerator;

public class TestJson {
    public static void main(String[] args) {
        var json = JsonObjectNoteSchemaGenerator.generateJsonSchema(Lesson.class);
        System.out.println(json.toPrettyString());

    }
}
