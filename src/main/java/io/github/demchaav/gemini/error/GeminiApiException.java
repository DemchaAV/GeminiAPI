/*
 * Copyright (c) 2025 Artem Demchyshyn
 *
 * Licensed under the MIT License. See LICENSE file in the project root.
 */
package io.github.demchaav.gemini.error;

/**
 * Runtime exception thrown when Gemini request processing fails.
 *
 * @since 1.0.4
 */
public class GeminiApiException extends RuntimeException {
  /**
   * Creates an exception with a message.
   *
   * @param message exception message
   * @since 1.0.4
   */
  public GeminiApiException(String message) {
    super(message);
  }

  /**
   * Creates an exception with a message and cause.
   *
   * @param message exception message
   * @param cause root cause
   * @since 1.0.4
   */
  public GeminiApiException(String message, Throwable cause) {
    super(message, cause);
  }
}
