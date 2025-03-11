package org.gemini.core.client.request_response.response.candidate.safety_rating;

import lombok.Builder;

/**
 * Safety rating for a specific harm category
 */
@Builder
public record SafetyRating(
        /**
         * The safety category being rated.
         */
        HarmCategory category,

        /**
         * The harm probability level detected in the content.
         */
        HarmProbability probability,

        /**
         * A boolean flag that indicates if the model's input or output was blocked.
         */
        Boolean blocked
) {
}
