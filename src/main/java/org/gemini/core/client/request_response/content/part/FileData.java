package org.gemini.core.client.request_response.content.part;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

/**
 * FileData for URI or URL-based content
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record FileData(
        String mimeType,
        String fileUri
) {
}
