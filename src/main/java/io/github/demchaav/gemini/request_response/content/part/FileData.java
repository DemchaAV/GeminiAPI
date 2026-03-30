/*
 * Copyright (c) 2025 Artem Demchyshyn
 *
 * Licensed under the MIT License. See LICENSE file in the project root.
 */
package io.github.demchaav.gemini.request_response.content.part;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

/** FileData for URI or URL-based content */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record FileData(String mimeType, String fileUri) {}
