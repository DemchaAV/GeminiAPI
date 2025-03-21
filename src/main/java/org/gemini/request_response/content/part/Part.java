package org.gemini.request_response.content.part;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import org.gemini.model_config.tool.Function;
import org.gemini.request_response.content.part.video_metadata.VideoMetadata;

/**
 * Part representing a segment of content in various formats
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record Part(
        String text,
        Blob inlineData,
        FileData fileData,
        Function Function,
        FunctionResponse functionResponse,
        VideoMetadata videoMetadata
) {
}

