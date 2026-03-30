/*
 * Copyright (c) 2025 Artem Demchyshyn
 *
 * Licensed under the MIT License. See LICENSE file in the project root.
 */
package io.github.demchaav.gemini.model;

public enum GenerateMethod {
  /** For Gemini model */
  GENERATE_CONTENT("generateContent"),
  /** For Gemini model */
  STREAM_GENERATE_CONTENT("streamGenerateContent")
  /** For imagen model */
  ,
  PREDICT("predict");

  final String method;

  GenerateMethod(String method) {
    this.method = method;
  }

  @Override
  public String toString() {
    return method;
  }
}
