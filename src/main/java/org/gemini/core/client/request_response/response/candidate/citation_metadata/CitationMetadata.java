package org.gemini.core.client.request_response.response.candidate.citation_metadata;

import lombok.Builder;

import java.util.List;

/**
 * Metadata about citations in the response
 */
@Builder
public record CitationMetadata(
        /**
         * List of citations found in the response.
         */
        List<Citation> citations,
        List<CitationSources> citationSources
) {
}
