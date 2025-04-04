package io.github.demchaav.gemini.model_config;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import io.github.demchaav.gemini.request_response.content.part.Part;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record SystemInstruction(
        String role,
        List<Part> parts
) {
    public SystemInstruction(Part part) {
        this(null, new ArrayList<>());
        parts.add(part);
    }

    public SystemInstruction(String role, Part part) {
        this(role, new ArrayList<>());
        parts.add(part);
    }


    public SystemInstruction(String prompt) {
        this(Part.builder().text(prompt).build());
    }

    public SystemInstruction(String role, String prompt) {
        this(role, Part.builder().text(prompt).build());
    }

    public static class SystemInstructionBuilder {
        public SystemInstructionBuilder addPart(Part part) {
            if (part == null) {
                return this;
            }
            if (this.parts == null) {
                parts = new ArrayList<>();
                parts.add(part);
            } else {
                parts.add(part);
            }
            return this;
        }
    }
}