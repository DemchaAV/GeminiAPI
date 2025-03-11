package org.gemini.core.client.request_response.content.part;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.util.Map;

/**
 * FunctionCall for model-predicted function calls
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record FunctionCall(
        String name,
        Map<String, Object> args
) {
}
