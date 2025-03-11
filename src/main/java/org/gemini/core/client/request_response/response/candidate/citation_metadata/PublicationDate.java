package org.gemini.core.client.request_response.response.candidate.citation_metadata;

import lombok.Builder;

/**
 * Publication date information for citations
 */
@Builder
public record PublicationDate(
        /**
         * The year of publication.
         */
        Integer year,

        /**
         * The month of publication.
         */
        Integer month,

        /**
         * The day of publication.
         */
        Integer day
) {
}
