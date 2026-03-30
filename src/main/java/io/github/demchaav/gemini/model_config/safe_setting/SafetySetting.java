/*
 * Copyright (c) 2025 Artem Demchyshyn
 *
 * Licensed under the MIT License. See LICENSE file in the project root.
 */
package io.github.demchaav.gemini.model_config.safe_setting;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

/** SafetySetting for content moderation */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record SafetySetting(
    HarmCategory category, HarmBlockThreshold threshold, HarmBlockMethod method) {}
