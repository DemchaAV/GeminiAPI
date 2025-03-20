package anki.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record Lesson(String lessonName, String description,List<Question>questions) {
}
