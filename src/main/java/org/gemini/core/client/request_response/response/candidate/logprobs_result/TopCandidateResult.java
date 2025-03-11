package org.gemini.core.client.request_response.response.candidate.logprobs_result;

import lombok.Builder;

import java.util.List;

/**
 * Container for top candidate tokens at a generation step
 */
@Builder
public record TopCandidateResult(
        /**
         * List of candidate tokens and their probabilities.
         */
        List<TokenProbability> candidates
) {
}
