package org.gemini.request_response.response.candidate.citation_metadata;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CitationSources(Integer startIndex, Integer endIndex, String uri, String license) {
}
