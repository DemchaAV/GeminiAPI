package anki.data;

import java.util.List;

public record TestClass(String lessonName, String description, List<QuestionTest>questions) {
}
