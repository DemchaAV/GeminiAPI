package org.gemini.request_response.response.candidate.safety_rating;

/**
 * Enum for harm probability levels
 */
public enum HarmProbability {
    /**
     * Unspecified harm probability.
     */
    HARM_PROBABILITY_UNSPECIFIED,

    /**
     * Negligible probability of harmful content.
     */
    NEGLIGIBLE,

    /**
     * Low probability of harmful content.
     */
    LOW,

    /**
     * Medium probability of harmful content.
     */
    MEDIUM,

    /**
     * High probability of harmful content.
     */
    HIGH
}
