package io.github.demchaav.gemini.request_response.response.usageMeta;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.util.List;

/**
 * Metadata about token usage for this request and response
 */
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
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

    List<TokensDetails> candidatesTokensDetails,

    Integer thoughtsTokenCount
) {}
