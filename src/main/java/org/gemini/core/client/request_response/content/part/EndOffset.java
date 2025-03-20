package org.gemini.core.client.request_response.content.part;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record EndOffset(Integer seconds, Integer nanos) {
}
