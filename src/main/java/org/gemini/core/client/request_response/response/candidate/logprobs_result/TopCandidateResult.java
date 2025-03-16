package org.gemini.core.client.request_response.response.candidate.logprobs_result;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.util.List;

/**
 * Container for top candidate tokens at a generation step
 */
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record TopCandidateResult(
        /**
         * List of candidate tokens and their probabilities.
         */
        List<TokenProbability> candidates
) {
}
