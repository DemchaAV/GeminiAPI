package io.github.demchaav.gemini.request_response.response.candidate.logprobs_result;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.util.List;

/**
 * Log probability results for token generation
 */
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
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
