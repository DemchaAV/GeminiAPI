package org.gemini.core.client.request_response.response.candidate.citation_metadata;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.util.List;

/**
 * Metadata about citations in the response
 */
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CitationMetadata(
        /**
         * List of citations found in the response.
         */
        List<Citation> citations,
        List<CitationSources> citationSources
) {
}
