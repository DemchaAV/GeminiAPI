package anki.data;

import java.util.List;

public record Question(String question, String answer, List<String>tags) {
}
