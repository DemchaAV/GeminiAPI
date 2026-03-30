/*
 * Copyright (c) 2025 Artem Demchyshyn
 *
 * Licensed under the MIT License. See LICENSE file in the project root.
 */
package io.github.demchaav.gemini.request_response.response.candidate.citation_metadata;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.Builder;

/** Metadata about citations in the response */
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CitationMetadata(
    /** List of citations found in the response. */
    List<Citation> citations, List<CitationSources> citationSources) {}
