/*
 * Copyright (c) 2025 Artem Demchyshyn
 *
 * Licensed under the MIT License. See LICENSE file in the project root.
 */
package io.github.demchaav.gemini.model.enums.gemini;

public enum GeminiGenerateMethod {
  GENERATE_CONTENT("generateContent"),
  STREAM_GENERATE_CONTENT("streamGenerateContent");

  final String method;

  GeminiGenerateMethod(String method) {
    this.method = method;
  }

  @Override
  public String toString() {
    return method;
  }
}
