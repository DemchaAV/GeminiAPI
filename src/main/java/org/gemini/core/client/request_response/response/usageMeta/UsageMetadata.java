package org.gemini.core.client.request_response.response.usageMeta;

import lombok.Builder;

import java.util.List;

/**
 * Metadata about token usage for this request and response
 */
@Builder
public record UsageMetadata(
    /**
     * Number of tokens in the request.
     */
    Integer promptTokenCount,
    
    /**
     * Number of tokens in the response(s).
     */
    Integer candidatesTokenCount,
    
    /**
     * Number of tokens in the request and response(s).
     */
    Integer totalTokenCount,

    List<TokensDetails> promptTokensDetails,
    List<TokensDetails> candidatesTokensDetails
) {}
