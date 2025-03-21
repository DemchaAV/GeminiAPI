package org.gemini.request_response.content.part;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.util.Map;

/**
 * FunctionResponse for results from function calls
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record FunctionResponse(
        String name,
        Map<String, Object> response
) {
}
