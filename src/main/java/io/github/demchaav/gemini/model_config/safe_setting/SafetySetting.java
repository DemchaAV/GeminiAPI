package io.github.demchaav.gemini.model_config.safe_setting;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

/**
 * SafetySetting for content moderation
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record SafetySetting(
        HarmCategory category,
        HarmBlockThreshold threshold,
        HarmBlockMethod method
) {
}
