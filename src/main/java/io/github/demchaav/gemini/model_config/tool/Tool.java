package io.github.demchaav.gemini.model_config.tool;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.util.List;

/**
 * Tool definition for function calling capabilities
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record Tool(String type, List<Function> functionDeclarations)
{
}
