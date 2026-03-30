/*
 * Copyright (c) 2025 Artem Demchyshyn
 *
 * Licensed under the MIT License. See LICENSE file in the project root.
 */
package io.github.demchaav.gemini.model_config.safe_setting;

/** Enum for harm blocking thresholds */
public enum HarmBlockThreshold {
  HARM_BLOCK_THRESHOLD_UNSPECIFIED,
  BLOCK_LOW_AND_ABOVE,
  BLOCK_MEDIUM_AND_ABOVE,
  BLOCK_ONLY_HIGH,
  BLOCK_NONE,
  OFF
}
