package org.gemini.core.client.model_config.tool;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.util.Map;

/**
 * FunctionDeclaration for defining available functions
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record FunctionDeclaration(
        String name,
        String description,
        Map<String, Object> parameters
) {
}
