package anki.data;

import java.util.List;

public record QuestionTest(String question, String answer, List<QuestionTest>questionTests) {
}

