package org.gemini.core.client.request_response.response.candidate.logprobs_result;

import lombok.Builder;

import java.util.List;

/**
 * Log probability results for token generation
 */
@Builder
public record LogprobsResult(
        /**
         * The top candidate tokens at each step.
         */
        List<TopCandidateResult> topCandidates,

        /**
         * The actual chosen tokens at each step.
         */
        List<TokenProbability> chosenCandidates
) {
}
