package org.gemini.request_response.content;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import org.gemini.request_response.content.part.Part;

import java.util.ArrayList;
import java.util.List;

/**
 * Content structure representing a message with its role and parts
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record Content(
        String role,
        List<Part> parts
) {    public Content(Part part) {
    this(null, new ArrayList<>());
    parts.add(part);
}

public Content(String role, Part part) {
    this(role, new ArrayList<>());
    parts.add(part);
}


public Content(String prompt) {
    this(Part.builder().text(prompt).build());
}

public Content(String role, String prompt) {
    this(role, Part.builder().text(prompt).build());
}
public static class ContentBuilder{
    public ContentBuilder addPart(Part part){
        if(part==null){
            return this;
        }
        if (parts==null){
            parts = new ArrayList<>();
            parts.add(part);
        }else {
            parts.add(part);
        }
        return this;
    }
}

}