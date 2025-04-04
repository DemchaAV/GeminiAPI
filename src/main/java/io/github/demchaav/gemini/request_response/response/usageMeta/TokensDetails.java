package io.github.demchaav.gemini.request_response.response.usageMeta;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record TokensDetails(String modality,Integer tokenCount ) {
}
