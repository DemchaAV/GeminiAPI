package org.gemini.request_response.response.candidate.logprobs_result;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

/**
 * Token and its probability information
 */
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record TokenProbability(
        /**
         * Generative AI models break down text data into tokens for processing,
         * which can be characters, words, or phrases.
         */
        String token,

        /**
         * A log probability value that indicates the model's confidence for a particular token.
         */
        Float logProbability
) {
}
