package org.gemini.core.client.request_response.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record Instance(String prompt) {
}
