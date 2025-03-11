package org.gemini.core.client.request_response.response.candidate.citation_metadata;

import lombok.Builder;

/**
 * Information about a specific citation
 */
@Builder
public record Citation(
        /**
         * An integer that specifies where a citation starts in the content.
         */
        Integer startIndex,

        /**
         * An integer that specifies where a citation ends in the content.
         */
        Integer endIndex,

        /**
         * The URL of a citation source.
         * Examples of a URL source might be a news website or a GitHub repository.
         */
        String uri,

        /**
         * The title of a citation source.
         * Examples of source titles might be that of a news article or a book.
         */
        String title,

        /**
         * The license associated with a citation.
         */
        String license,

        /**
         * The date a citation was published.
         * Valid formats are YYYY, YYYY-MM, and YYYY-MM-DD.
         */
        PublicationDate publicationDate
) {
}
