/*
 * Copyright (c) 2025 Artem Demchyshyn
 *
 * Licensed under the MIT License. See LICENSE file in the project root.
 */
package io.github.demchaav.gemini;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.demchaav.gemini.request_response.response.GeminiResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class GeminiResponseTest {
  private final ObjectMapper mapper = new ObjectMapper();

  @Test
  void responseCanBeDeserializedAndFlattened() throws IOException {
    try (InputStream inputStream =
        getClass().getResourceAsStream("/fixtures/single-response.json")) {
      String json = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
      GeminiResponse response = mapper.readValue(json, GeminiResponse.class);

      assertEquals("Hello world!", response.asString());
      assertTrue(response.contentAsString().contains("role: model"));
      assertTrue(response.contentAsString().contains("Hello world!"));
    }
  }

  @Test
  void printContentCanRenderToACustomStream() throws IOException {
    try (InputStream inputStream =
        getClass().getResourceAsStream("/fixtures/single-response.json")) {
      String json = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
      GeminiResponse response = mapper.readValue(json, GeminiResponse.class);
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

      response.printContent(new PrintStream(outputStream, true, StandardCharsets.UTF_8));

      assertEquals("Hello world!", outputStream.toString(StandardCharsets.UTF_8));
    }
  }
}
