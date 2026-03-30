/*
 * Copyright (c) 2025 Artem Demchyshyn
 *
 * Licensed under the MIT License. See LICENSE file in the project root.
 */
package io.github.demchaav.gemini;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.github.demchaav.gemini.error.ApiErrorHandler;
import io.github.demchaav.gemini.error.GeminiApiException;
import io.github.demchaav.gemini.model.GeminiModel;
import io.github.demchaav.gemini.model.ImagenModel;
import io.github.demchaav.gemini.model.enums.VerAPI;
import io.github.demchaav.gemini.model.enums.gemini.GeminiGenerateMethod;
import io.github.demchaav.gemini.model.enums.gemini.GeminiVariation;
import io.github.demchaav.gemini.model.enums.gemini.GeminiVersion;
import io.github.demchaav.gemini.model_config.GenerationConfig;
import io.github.demchaav.gemini.model_config.SystemInstruction;
import io.github.demchaav.gemini.model_config.safe_setting.SafetySetting;
import io.github.demchaav.gemini.model_config.tool.Tool;
import io.github.demchaav.gemini.request_response.content.Content;
import io.github.demchaav.gemini.request_response.content.Message;
import io.github.demchaav.gemini.request_response.content.part.Part;
import io.github.demchaav.gemini.request_response.request.GeminiRequest;
import io.github.demchaav.gemini.request_response.request.ImgGenRequest;
import io.github.demchaav.gemini.request_response.response.GeminiResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Core HTTP client for Google Gemini and Imagen requests.
 *
 * <p>Instances are side-effect free during construction. Network calls happen only when one of the
 * response methods is invoked, or when {@link #ping()} is called explicitly.
 *
 * @since 1.0.4
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@AllArgsConstructor
@Setter
@Slf4j
public class GeminiConnection {

  /**
   * Default HTTP client used by convenience constructors and examples.
   *
   * @since 1.0.4
   */
  public static final HttpClient DEFAULT_HTTP_CLIENT =
      HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();

  private static final int MAX_RETRIES = 3;
  private static final int RETRY_DELAY_MS = 2000;
  private final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
  @NonNull private final HttpClient httpClient;
  @NonNull private final String apiKey;
  private final GeminiModel geminiModel;
  private final ImagenModel imagenModel;
  private final SystemInstruction systemInstruction;
  private final List<Tool> tools;
  private final List<SafetySetting> safetySettings;
  private final Map<String, String> labels;
  private final AtomicInteger totalTokens = new AtomicInteger(0);
  private HttpRequest httpRequest;
  private GenerationConfig generationConfig;
  private List<Content> contents;
  private List<Content> lastContent;
  private GeminiResponse response;
  private GeminiRequest request;
  private ImgGenRequest imageRequest;
  private String bodyHttpRequest;
  private String url;

  /**
   * Creates a connection configured for the current default Gemini flash model.
   *
   * @param apiKey Gemini API key
   * @since 1.0.4
   */
  public GeminiConnection(String apiKey) {
    this(
        apiKey,
        GeminiModel.builder()
            .verAPI(VerAPI.V1BETA)
            .variation(GeminiVariation._2_0)
            .version(GeminiVersion.FLASH_LATEST)
            .generateMethod(GeminiGenerateMethod.GENERATE_CONTENT)
            .build(),
        null);
  }

  /**
   * Creates a connection configured for a specific Gemini text model.
   *
   * @param apiKey Gemini API key
   * @param model Gemini model selection
   * @param config optional generation configuration
   * @since 1.0.4
   */
  public GeminiConnection(String apiKey, @NonNull GeminiModel model, GenerationConfig config) {
    this(
        GeminiConnection.DEFAULT_HTTP_CLIENT,
        apiKey,
        model,
        null,
        null,
        null,
        null,
        null,
        null,
        config,
        null,
        null,
        null,
        null,
        null,
        null,
        null);
  }

  /**
   * Creates a connection configured for a specific Imagen model.
   *
   * @param apiKey Gemini API key
   * @param model Imagen model selection
   * @param config optional generation configuration
   * @since 1.0.4
   */
  public GeminiConnection(String apiKey, @NonNull ImagenModel model, GenerationConfig config) {
    this(
        GeminiConnection.DEFAULT_HTTP_CLIENT,
        apiKey,
        null,
        model,
        null,
        null,
        null,
        null,
        null,
        config,
        null,
        null,
        null,
        null,
        null,
        null,
        null);
  }

  /**
   * Deserializes a JSON request payload and prepares it for execution.
   *
   * @param jsonGeminiRequest serialized request payload
   * @return the current connection for chaining
   * @since 1.0.4
   */
  public GeminiConnection sendRequest(String jsonGeminiRequest) {
    log.debug("Parsing JSON request payload");
    try {
      this.request = mapper.readValue(jsonGeminiRequest, GeminiRequest.class);
    } catch (JsonProcessingException e) {
      log.error("Failed to parse GeminiRequest JSON payload", e);
      throw new GeminiApiException("Failed to parse GeminiRequest JSON payload", e);
    }
    return sendRequest(request);
  }

  /**
   * Stores a structured Gemini request for execution.
   *
   * @param request request payload to send on the next response call
   * @return the current connection for chaining
   * @since 1.0.4
   */
  public GeminiConnection sendRequest(GeminiRequest request) {
    log.debug("Preparing request: {}", request);
    request =
        GeminiRequest.builder()
            .contents(request.contents())
            .systemInstruction(systemInstruction)
            .cachedContent(request.cachedContent())
            .tools(tools)
            .safetySettings(safetySettings)
            .generationConfig(generationConfig)
            .labels(labels)
            .build();
    this.request = request;

    return this;
  }

  /**
   * Stores an Imagen request for execution.
   *
   * @param request image-generation request payload
   * @return the current connection for chaining
   * @since 1.0.4
   */
  public GeminiConnection sendRequest(ImgGenRequest request) {
    this.imageRequest = request;
    return this;
  }

  /**
   * Performs a lightweight connectivity check using a default prompt.
   *
   * @return {@code true} when the API responds successfully
   * @since 1.0.4
   */
  public boolean ping() {
    return ping("Reply with the single word pong.");
  }

  /**
   * Performs a lightweight connectivity check using the supplied prompt.
   *
   * @param prompt prompt to send during the health check
   * @return {@code true} when the API responds successfully
   * @since 1.0.4
   */
  public boolean ping(String prompt) {
    log.info("Running explicit connectivity check");
    return sendRequest(GeminiRequest.requestMessage(new Message(prompt))).getResponse().isPresent();
  }

  private String generateStringHttpJsonFromRequest() {
    if (request == null) {
      log.error("Request is null. Call sendRequest(...) before requesting a response.");
      throw new GeminiApiException(
          "Request is null. Call sendRequest(...) before requesting a response.");
    }
    return getStringJson(request);
  }

  private void createHttpRequest(boolean asStream) {
    createHttpRequest(asStream, false);
  }

  private void createHttpRequest(boolean asStream, boolean isImageGeneration) {
    if (request == null) {
      if (isImageGeneration) {
        if (imageRequest == null) {
          log.error("Image request is null");
          throw new GeminiApiException(
              "Image request is null. Call sendRequest(ImgGenRequest) first.");
        }
        bodyHttpRequest = getStringJson(imageRequest);
      } else {
        throw new GeminiApiException(
            "Request object is null. Call sendRequest(GeminiRequest) first.");
      }
    }
    this.bodyHttpRequest =
        !isImageGeneration ? generateStringHttpJsonFromRequest() : bodyHttpRequest;

    log.info("Sending request to API{}", asStream ? " as stream" : "");

    String url = null;
    if (isImageGeneration) {
      if (imagenModel == null) {
        throw new GeminiApiException(
            "Imagen model is null. Initialize GeminiConnection with an ImagenModel.");
      }
      this.url = imagenModel.getUrl();
      url = this.url + apiKey;
    } else {
      if (geminiModel == null) {
        throw new GeminiApiException(
            "Gemini model is null. Initialize GeminiConnection with a GeminiModel.");
      }
      if (asStream) {
        this.url =
            GeminiModel.builder()
                .copyModelAndSetGenerateMethod(
                    geminiModel, GeminiGenerateMethod.STREAM_GENERATE_CONTENT)
                .build()
                .getUrl();
        url = this.url + apiKey;
      } else {
        this.url =
            GeminiModel.builder()
                .copyModelAndSetGenerateMethod(geminiModel, GeminiGenerateMethod.GENERATE_CONTENT)
                .build()
                .getUrl();
        url = this.url + apiKey;
      }
    }
    processHttpRequest(url);
  }

  private void processHttpRequest(String url) {
    this.httpRequest =
        HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(bodyHttpRequest, StandardCharsets.UTF_8))
            .build();

    log.debug("HTTP Request built: {}", this.httpRequest);
  }

  private <T> String getStringJson(T request) {
    String stringRequest;
    try {
      stringRequest = mapper.writeValueAsString(request);
      log.debug("Serialized request: {}", stringRequest);
    } catch (JsonProcessingException e) {
      log.error("Error serializing request", e);
      throw new RuntimeException(e);
    }
    return stringRequest;
  }

  /**
   * Executes the previously prepared text request and returns the parsed response.
   *
   * @return the parsed Gemini response when the API returns content
   * @since 1.0.4
   */
  public Optional<GeminiResponse> getResponse() {
    return generateResponse(false);
  }

  private Optional<GeminiResponse> generateResponse(boolean isImage) {
    createHttpRequest(false, isImage);

    HttpResponse<String> httpResponse = fetchHttpResponse(HttpResponse.BodyHandlers.ofString());
    if (isImage) {
      log.debug(httpResponse.body());
    }

    response = parseJson(httpResponse.body());
    if (!isImage) {
      if (response != null
          && response.usageMetadata() != null
          && response.usageMetadata().totalTokenCount() != null) {
        totalTokens.getAndAdd(response.usageMetadata().totalTokenCount());
      }
      if (addHistoryContent()) {
        log.info("Conversation history updated");
      } else {
        log.warn("Conversation history was not updated");
      }
    }
    return Optional.ofNullable(!hasAnyNotNullField(response) ? null : response);
  }

  /**
   * Executes the previously prepared image request and returns the parsed response.
   *
   * @return the parsed image-generation response when available
   * @since 1.0.4
   */
  public Optional<GeminiResponse> getImageResponse() {
    if (imagenModel == null) {
      log.warn(
          "{} is null. Initialize GeminiConnection with an ImagenModel before calling getImageResponse().",
          "imagenModel");
      return Optional.empty();
    }
    return generateResponse(true);
  }

  /**
   * Executes the previously prepared text request in streaming mode.
   *
   * @param responseConsumer callback invoked for each parsed stream chunk
   * @throws IOException when the stream cannot be consumed
   * @since 1.0.4
   */
  public void getResponseAsStream(Consumer<GeminiResponse> responseConsumer) throws IOException {
    createHttpRequest(true);
    HttpResponse<InputStream> httpResponse =
        fetchHttpResponse(HttpResponse.BodyHandlers.ofInputStream());

    try (InputStream inputStream = httpResponse.body();
        BufferedReader reader =
            new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

      String line;
      GeminiResponse geminiResponse;
      int totalTokens = 0;
      ResponseStreamProcessor processor = new ResponseStreamProcessor();
      while ((line = reader.readLine()) != null) {
        log.trace(line);
        processor.addChunk(line);
        if (!processor.getResponseQueue().isEmpty()) {
          log.trace("Object response ready in the queue");
          geminiResponse = processor.getResponseQueue().poll();
          if (geminiResponse != null) {
            log.trace("Put an object \"GeminiResponse\" in to the queue");
            if (geminiResponse.usageMetadata() != null
                && geminiResponse.usageMetadata().totalTokenCount() != null) {
              totalTokens = geminiResponse.usageMetadata().totalTokenCount();
            }
            responseConsumer.accept(geminiResponse);
          }
        }
      }
      this.totalTokens.getAndAdd(totalTokens);
    } catch (IOException e) {
      log.error("Error reading stream from Gemini API", e);
      throw new GeminiApiException("Error reading stream from Gemini API", e);
    } finally {
      this.bodyHttpRequest = null;
    }
  }

  private <T> HttpResponse<T> sendWithRetries(
      HttpRequest httpRequest, HttpResponse.BodyHandler<T> bodyHandler)
      throws IOException, InterruptedException {

    int attempt = 0;
    IOException lastException = null;

    while (attempt < MAX_RETRIES) {
      try {
        return httpClient.send(httpRequest, bodyHandler);
      } catch (IOException e) {
        lastException = e;
        log.warn("Attempt {} failed. Retrying in {} ms", attempt + 1, RETRY_DELAY_MS);
        Thread.sleep(RETRY_DELAY_MS);
        attempt++;
      }
    }
    throw new GeminiApiException("All retry attempts failed.", lastException);
  }

  private <T> HttpResponse<T> fetchHttpResponse(HttpResponse.BodyHandler<T> bodyHandler) {
    log.info("Fetching response from API");

    if (httpRequest == null) {
      throw new GeminiApiException("httpRequest is null. You must call sendRequest first.");
    }

    HttpResponse<T> httpResponse;
    try {
      httpResponse = sendWithRetries(httpRequest, bodyHandler);
    } catch (IOException | InterruptedException e) {
      if (e instanceof InterruptedException) {
        Thread.currentThread().interrupt();
      }
      log.error("Failed to fetch response from Gemini API", e);
      throw new GeminiApiException("Failed to fetch response from Gemini API", e);
    }

    if (httpResponse.statusCode() != 200) {
      handleErrorResponse(httpResponse);
    }
    bodyHttpRequest = null;
    return httpResponse;
  }

  private <T> void handleErrorResponse(HttpResponse<T> response) {
    String responseBody;

    if (response.body() instanceof InputStream) {
      try (InputStream errorStream = (InputStream) response.body();
          BufferedReader errorReader =
              new BufferedReader(new InputStreamReader(errorStream, StandardCharsets.UTF_8))) {
        responseBody = errorReader.lines().collect(Collectors.joining("\n"));
      } catch (IOException e) {
        log.error("Failed to read error stream", e);
        responseBody = "Failed to read error message";
      }
    } else {
      responseBody = response.body().toString();
    }
    int responseErrorCode = response.statusCode();
    var error = ApiErrorHandler.createError(responseErrorCode);
    log.error(
        "API request failed with status code: {}, message: {} \nrequestUrl:  {} \n response body: \n{} request body: {}",
        responseErrorCode,
        error.getDetailedErrorMessage(),
        this.url,
        responseBody,
        this.bodyHttpRequest);
    throw new GeminiApiException(
        "API request failed with status code: %d  %s %nrequestUrl:  %s %nresponse body: %s %n request body: %s"
            .formatted(responseErrorCode, error, this.url, responseBody, this.bodyHttpRequest));
  }

  /**
   * Returns whether the supplied object has at least one non-static, non-transient field set.
   *
   * @param obj object to inspect
   * @return {@code true} when at least one relevant field is non-null
   * @since 1.0.4
   */
  public static boolean hasAnyNotNullField(Object obj) {
    if (obj == null) {
      return false;
    }

    for (Field field : obj.getClass().getDeclaredFields()) {
      if (Modifier.isStatic(field.getModifiers()) || Modifier.isTransient(field.getModifiers())) {
        continue;
      }

      field.setAccessible(true);
      try {
        if (field.get(obj) != null) {
          return true;
        }
      } catch (IllegalAccessException e) {
        throw new RuntimeException("Error to access field: " + field.getName(), e);
      }
    }
    return false;
  }

  private GeminiResponse parseJson(String jsonObject) {
    GeminiResponse geminiResponse = null;
    try {
      geminiResponse = mapper.readValue(jsonObject, GeminiResponse.class);
    } catch (JsonProcessingException e) {
      log.error("Failed to parse response JSON", e);
      throw new GeminiApiException("Failed to parse response JSON", e);
    }

    log.info("Response successfully parsed");
    return geminiResponse;
  }

  private boolean addHistoryContent() {
    if (request == null || response == null) {
      if (request == null) {
        log.warn("Variable {} is null", "request");
      } else {
        log.warn("Variable {} is null", "response");
      }
      return false;
    }
    if (request.contents() == null
        || response.candidates() == null
        || response.candidates().isEmpty()) {
      log.warn("Cannot update history because request contents or response candidates are missing");
      return false;
    }
    contents = new ArrayList<>();
    var contentsRequest = request.contents();
    var contentsResponse = response.candidates().getFirst().content();
    this.contents.addAll(contentsRequest);
    log.trace("Request {} has been added to content", contentsRequest);
    this.contents.add(contentsResponse);
    log.trace("Response {} has been added to content", contentsResponse);
    return true;
  }

  /**
   * Returns the current conversation history and clears the internal history buffer.
   *
   * @return a copy of the buffered conversation history, or {@code null} when none exists
   * @since 1.0.4
   */
  public List<Content> takeContent() {
    List<Content> contentList = null;
    if (isReadyContent()) {
      contentList = new ArrayList<>(this.contents);
      this.contents = null;
      return contentList;
    }
    log.warn("Content is not ready because the history buffer is empty");
    response = null;
    request = null;
    return contentList;
  }

  /**
   * Returns the current buffered conversation history as a printable string and clears it.
   *
   * @return a printable conversation transcript, or {@code null} when none exists
   * @since 1.0.4
   */
  public String takeContentAsString() {
    List<Content> contentList = takeContent();
    if (contentList == null) {
      return null;
    }

    return contentList.stream()
        .map(
            content ->
                String.format(
                    "role: %s%nmessage: \"%s\"%n",
                    content.role(),
                    content.parts().stream().map(Part::text).collect(Collectors.joining()).trim()))
        .collect(Collectors.joining());
  }

  /**
   * Returns whether buffered conversation history is available.
   *
   * @return {@code true} when history can be consumed via {@link #takeContent()}
   * @since 1.0.4
   */
  public boolean isReadyContent() {
    return contents != null;
  }
}
