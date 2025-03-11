package anki.data;

import java.util.List;

public record Lesson(String lessonName, String description, List<Question> questions) {
}
