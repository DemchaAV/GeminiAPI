/*
 * Copyright (c) 2025 Artem Demchyshyn
 *
 * Licensed under the MIT License. See LICENSE file in the project root.
 */
package io.github.demchaav.gemini.request_response.response.candidate.logprobs_result;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.Builder;

/** Log probability results for token generation */
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record LogprobsResult(
    /** The top candidate tokens at each step. */
    List<TopCandidateResult> topCandidates,

    /** The actual chosen tokens at each step. */
    List<TokenProbability> chosenCandidates) {}
