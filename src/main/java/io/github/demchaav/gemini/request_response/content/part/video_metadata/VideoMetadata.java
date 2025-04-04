package io.github.demchaav.gemini.request_response.content.part.video_metadata;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

/**
 * VideoMetadata for video timing information
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record VideoMetadata(
        Duration startOffset,
        Duration endOffset
) {
}
