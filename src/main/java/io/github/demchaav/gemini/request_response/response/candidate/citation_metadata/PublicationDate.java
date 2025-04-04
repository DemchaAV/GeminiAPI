package io.github.demchaav.gemini.request_response.response.candidate.citation_metadata;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

/**
 * Publication date information for citations
 */
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
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
