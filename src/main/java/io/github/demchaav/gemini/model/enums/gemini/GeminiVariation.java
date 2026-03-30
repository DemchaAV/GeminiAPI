/*
 * Copyright (c) 2025 Artem Demchyshyn
 *
 * Licensed under the MIT License. See LICENSE file in the project root.
 */
package io.github.demchaav.gemini.model.enums.gemini;

public enum GeminiVariation {
  _1_0("1.0"),
  _1_5("1.5"),
  _2_0("2.0"),
  _2_5("2.5");

  final String variation;

  GeminiVariation(String variation) {
    this.variation = variation;
  }

  @Override
  public String toString() {
    return variation;
  }
}
