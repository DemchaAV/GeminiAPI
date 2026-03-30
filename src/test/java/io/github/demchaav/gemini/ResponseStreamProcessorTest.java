/*
 * Copyright (c) 2025 Artem Demchyshyn
 *
 * Licensed under the MIT License. See LICENSE file in the project root.
 */
package io.github.demchaav.gemini;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.demchaav.gemini.request_response.response.GeminiResponse;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class ResponseStreamProcessorTest {
  @Test
  void processorBuildsResponsesFromChunkedFixtureData() throws IOException {
    String payload;
    try (InputStream inputStream =
        getClass().getResourceAsStream("/fixtures/response-stream-chunks.json")) {
      payload = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    }

    ResponseStreamProcessor processor = new ResponseStreamProcessor();
    boolean emitted = false;

    for (int index = 0; index < payload.length(); index += 17) {
      int endIndex = Math.min(index + 17, payload.length());
      emitted = processor.addChunk(payload.substring(index, endIndex)) || emitted;
    }

    List<GeminiResponse> responses = new ArrayList<>();
    while (!processor.getResponseQueue().isEmpty()) {
      responses.add(processor.getResponseQueue().poll());
    }

    assertTrue(emitted);
    assertEquals(3, responses.size());
    assertEquals("Hello", responses.get(0).asString());
    assertEquals(" world", responses.get(1).asString());
    assertEquals("!", responses.get(2).asString());
  }
}
