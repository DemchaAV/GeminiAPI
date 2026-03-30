/*
 * Copyright (c) 2025 Artem Demchyshyn
 *
 * Licensed under the MIT License. See LICENSE file in the project root.
 */
package io.github.demchaav.gemini;

import io.github.demchaav.gemini.request_response.content.Content;
import io.github.demchaav.gemini.request_response.content.Image;
import io.github.demchaav.gemini.request_response.content.Message;
import io.github.demchaav.gemini.request_response.content.part.Blob;
import io.github.demchaav.gemini.request_response.content.part.Part;
import io.github.demchaav.gemini.request_response.request.GeminiRequest;
import io.github.demchaav.gemini.request_response.response.GeminiResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.function.Consumer;
import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * High-level convenience client for prompt-oriented Gemini text generation.
 *
 * @since 1.0.4
 */
@RequiredArgsConstructor
@Slf4j
@Builder
public class GeminiClient {
  private final GeminiConnection connection;

  /**
   * Sends a text prompt with an optional inline image attachment.
   *
   * @param message prompt content to send to the model
   * @param image optional image to attach to the request
   * @return the parsed Gemini response when the API returns content
   * @since 1.0.4
   */
  public Optional<GeminiResponse> generateResponse(@NonNull Message message, Image image) {
    log.info("Processing message: '{}' from user", message.text());

    Content.ContentBuilder contentBuilder = Content.builder().role("user");

    if (image != null) {
      log.info("Message contains an image attachment.");
      contentBuilder.addPart(
          Part.builder().inlineData(Blob.builder().addBlobFromImage(image).build()).build());
    }
    contentBuilder.addPart(Part.builder().text(message.text()).build());

    log.info("Generate request");
    GeminiRequest request = GeminiRequest.builder().addContent(contentBuilder.build()).build();
    return connection.sendRequest(request).getResponse();
  }

  /**
   * Sends a prompt string with an optional image attachment.
   *
   * @param prompt prompt text to send
   * @param image optional image to attach to the request
   * @return the parsed Gemini response when the API returns content
   * @since 1.0.4
   */
  public Optional<GeminiResponse> generateResponse(String prompt, Image image) {
    prompt = (prompt == null) ? "" : prompt;
    return generateResponse(new Message(prompt), image);
  }

  /**
   * Sends a prompt string and returns the resulting model response.
   *
   * @param prompt prompt text to send
   * @return the parsed Gemini response when the API returns content
   * @since 1.0.4
   */
  public Optional<GeminiResponse> generateResponse(String prompt) {
    log.info("Generating content for prompt: {}", prompt);
    var request = GeminiRequest.requestMessage(new Message(prompt));
    return connection.sendRequest(request).getResponse();
  }

  /**
   * Sends a structured message payload and returns the resulting model response.
   *
   * @param message message payload to send
   * @return the parsed Gemini response when the API returns content
   * @since 1.0.4
   */
  public Optional<GeminiResponse> generateResponse(Message message) {
    log.info("Generating content for prompt: {}", message);
    var request = GeminiRequest.requestMessage(message);
    return connection.sendRequest(request).getResponse();
  }

  /**
   * Streams a response back to the provided consumer as chunks are parsed.
   *
   * @param message prompt text to send
   * @param responseConsumer callback invoked for each parsed response chunk
   * @since 1.0.4
   */
  public void generateResponseAsStream(String message, Consumer<GeminiResponse> responseConsumer) {
    var request = GeminiRequest.requestMessage(new Message(message));
    try {
      connection.sendRequest(request).getResponseAsStream(responseConsumer);
    } catch (IOException e) {
      log.error("Error during stream", e);
      throw new IllegalStateException("Failed to stream a response from the Gemini API.", e);
    }
  }
}
