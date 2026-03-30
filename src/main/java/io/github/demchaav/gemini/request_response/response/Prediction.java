/*
 * Copyright (c) 2025 Artem Demchyshyn
 *
 * Licensed under the MIT License. See LICENSE file in the project root.
 */
package io.github.demchaav.gemini.request_response.response;

public record Prediction(String mimeType, String prompt, String bytesBase64Encoded) {}
