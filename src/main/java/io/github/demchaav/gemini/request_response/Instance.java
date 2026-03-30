package io.github.demchaav.gemini.request_response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

/**
 * Text prompt wrapper used for image generation requests.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record Instance(
        /**
         * Required text prompt for image generation.
         *
         * <p>Prompt token limits depend on the selected model:
         * `imagen-3.0-generate-002` and `imagen-3.0-generate-001` support up to 480 tokens,
         * `imagen-3.0-fast-generate-001` supports up to 480 tokens,
         * `imagegeneration@006` and `imagegeneration@005` support up to 128 tokens,
         * and `imagegeneration@002` supports up to 64 tokens.</p>
         */
        String prompt
) {
}
