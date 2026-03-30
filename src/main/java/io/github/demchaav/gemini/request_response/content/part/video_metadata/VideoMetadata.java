/*
 * Copyright (c) 2025 Artem Demchyshyn
 *
 * Licensed under the MIT License. See LICENSE file in the project root.
 */
package io.github.demchaav.gemini.request_response.content.part.video_metadata;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

/** VideoMetadata for video timing information */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record VideoMetadata(Duration startOffset, Duration endOffset) {}
