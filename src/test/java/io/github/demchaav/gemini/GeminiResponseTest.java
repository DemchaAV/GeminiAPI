package io.github.demchaav.gemini;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.demchaav.gemini.request_response.response.GeminiResponse;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GeminiResponseTest {
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void responseCanBeDeserializedAndFlattened() throws IOException {
        try (InputStream inputStream = getClass().getResourceAsStream("/fixtures/single-response.json")) {
            String json = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            GeminiResponse response = mapper.readValue(json, GeminiResponse.class);

            assertEquals("Hello world!", response.asString());
            assertTrue(response.contentAsString().contains("role: model"));
            assertTrue(response.contentAsString().contains("Hello world!"));
        }
    }
}
