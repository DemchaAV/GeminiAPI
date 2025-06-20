package io.github.demchaav.gemini.request_response.response.candidate;

/**
 * Enumeration of reasons why token generation stopped
 */
public enum FinishReason {
    /**
     * The finish reason is unspecified.
     */
    FINISH_REASON_UNSPECIFIED,

    /**
     * Natural stop point of the model or provided stop sequence.
     */
    FINISH_REASON_STOP,

    /**
     * The maximum number of tokens as specified in the request was reached.
     */
    FINISH_REASON_MAX_TOKENS,

    /**
     * Token generation was stopped because the response was flagged for safety reasons.
     * Note that Candidate.content is empty if content filters block the output.
     */
    FINISH_REASON_SAFETY,

    /**
     * The token generation was stopped because the response was flagged for unauthorized citations.
     */
    FINISH_REASON_RECITATION,

    /**
     * Token generation was stopped because the response includes blocked terms.
     */
    FINISH_REASON_BLOCKLIST,

    /**
     * Token generation was stopped because the response was flagged for prohibited content,
     * such as child sexual abuse material (CSAM).
     */
    FINISH_REASON_PROHIBITED_CONTENT,

    /**
     * Token generation was stopped because the response was flagged for
     * sensitive personally identifiable information (SPII).
     */
    FINISH_REASON_SPII,

    /**
     * Candidates were blocked because of malformed and unparsable function call.
     */
    FINISH_REASON_MALFORMED_FUNCTION_CALL,

    /**
     * All other reasons that stopped the token generation.
     */
    FINISH_REASON_OTHER,
    /**
     * Natural stop point of the model or provided stop sequence.
     */
    STOP,
    /**
     * The maximum number of tokens as specified in the request was reached.
     */
    MAX_TOKENS,
    /**
     * The response candidate content was flagged for safety reasons.
     */
    SAFETY,
    /**
     * The response candidate content was flagged for recitation reasons.
     */
    RECITATION,
    /**
     * The response candidate content was flagged for using an unsupported language.
     */
    LANGUAGE,
    /**
     * Unknown reason.
     */
    OTHER,
    /**
     * Token generation stopped because the content contains forbidden terms.
     */
    BLOCKLIST,
    /**
     * Token generation stopped for potentially containing prohibited content.
     */
    PROHIBITED_CONTENT,
    /**
     * Token generation stopped because the content potentially contains Sensitive Personally Identifiable Information (SPII).
     */
    SPII,
    /**
     * The function call generated by the model is invalid.
     */
    MALFORMED_FUNCTION_CALL,
    /**
     * Token generation stopped because generated images contain safety violations.
     */
    IMAGE_SAFETY
}
