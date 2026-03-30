/*
 * Copyright (c) 2025 Artem Demchyshyn
 *
 * Licensed under the MIT License. See LICENSE file in the project root.
 */
package io.github.demchaav.gemini.request_response.response.candidate.logprobs_result;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.Builder;

/** Container for top candidate tokens at a generation step */
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record TopCandidateResult(
    /** List of candidate tokens and their probabilities. */
    List<TokenProbability> candidates) {}
