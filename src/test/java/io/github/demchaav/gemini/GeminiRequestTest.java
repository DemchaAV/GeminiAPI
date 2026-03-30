/*
 * Copyright (c) 2025 Artem Demchyshyn
 *
 * Licensed under the MIT License. See LICENSE file in the project root.
 */
package io.github.demchaav.gemini;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.demchaav.gemini.request_response.content.Content;
import io.github.demchaav.gemini.request_response.content.Message;
import io.github.demchaav.gemini.request_response.request.GeminiRequest;
import org.junit.jupiter.api.Test;

class GeminiRequestTest {
  private final ObjectMapper mapper = new ObjectMapper();

  @Test
  void requestMessageCreatesSingleUserContent() {
    GeminiRequest request = GeminiRequest.requestMessage(new Message("Hello, Gemini!"));

    assertEquals(1, request.contents().size());
    assertEquals("user", request.contents().getFirst().role());
    assertEquals("Hello, Gemini!", request.contents().getFirst().parts().getFirst().text());
  }

  @Test
  void requestMessageRejectsBlankMessages() {
    assertThrows(
        IllegalArgumentException.class, () -> GeminiRequest.requestMessage(new Message("  ")));
  }

  @Test
  void requestBuilderAppendsMultipleContents() throws JsonProcessingException {
    GeminiRequest request =
        GeminiRequest.builder()
            .addContent(new Content("user", "First"))
            .addContent(new Content("user", "Second"))
            .build();

    String json = mapper.writeValueAsString(request);

    assertEquals(2, request.contents().size());
    assertTrue(json.contains("\"text\":\"First\""));
    assertTrue(json.contains("\"text\":\"Second\""));
  }
}
