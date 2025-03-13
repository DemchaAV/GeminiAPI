import anki.data.Lesson;
import anki.data.TestClass;
import jsonGeneration.JsonObjectNoteSchemaGenerator;

import java.util.Arrays;
import java.util.LinkedList;

public class ReusesTest {
    public static void main(String[] args) {
        System.out.println(JsonObjectNoteSchemaGenerator.generateJsonSchema(TestClass.class).toPrettyString());
    }
}
