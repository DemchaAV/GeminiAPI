package org.gemini.core.client.request_response.response.candidate;

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

    STOP,
    MAX_TOKENS
}
