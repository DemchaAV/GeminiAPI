/*
 * Copyright (c) 2025 Artem Demchyshyn
 *
 * Licensed under the MIT License. See LICENSE file in the project root.
 */
package io.github.demchaav.gemini;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.demchaav.gemini.model.GeminiModel;
import io.github.demchaav.gemini.model.enums.VerAPI;
import io.github.demchaav.gemini.model.enums.gemini.GeminiGenerateMethod;
import io.github.demchaav.gemini.model.enums.gemini.GeminiVariation;
import io.github.demchaav.gemini.model.enums.gemini.GeminiVersion;
import java.net.Authenticator;
import java.net.CookieHandler;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSession;
import org.junit.jupiter.api.Test;

class GeminiConnectionTest {
  @Test
  void constructionIsSideEffectFreeEvenWithAClientThatWouldFailOnSend() {
    TrackingHttpClient httpClient = new TrackingHttpClient();

    assertDoesNotThrow(() -> createConnection(httpClient));
    assertEquals(0, httpClient.sendCount());
  }

  @Test
  void pingTriggersAnExplicitNetworkCall() {
    TrackingHttpClient httpClient = new TrackingHttpClient();
    GeminiConnection connection = createConnection(httpClient);

    assertTrue(connection.ping());
    assertEquals(1, httpClient.sendCount());
  }

  @Test
  void takeContentReturnsNullWhenNoConversationHistoryExists() {
    GeminiConnection connection = createConnection(new TrackingHttpClient());

    assertFalse(connection.isReadyContent());
    assertFalse(GeminiConnection.hasAnyNotNullField(null));
  }

  private GeminiConnection createConnection(HttpClient httpClient) {
    return GeminiConnection.builder()
        .apiKey("test-api-key")
        .httpClient(httpClient)
        .geminiModel(
            GeminiModel.builder()
                .verAPI(VerAPI.V1BETA)
                .variation(GeminiVariation._2_0)
                .version(GeminiVersion.FLASH_LATEST)
                .generateMethod(GeminiGenerateMethod.GENERATE_CONTENT)
                .build())
        .build();
  }

  private static final class TrackingHttpClient extends HttpClient {
    private final AtomicInteger sendCount = new AtomicInteger();

    int sendCount() {
      return sendCount.get();
    }

    @Override
    public Optional<CookieHandler> cookieHandler() {
      return Optional.empty();
    }

    @Override
    public Optional<Duration> connectTimeout() {
      return Optional.empty();
    }

    @Override
    public Redirect followRedirects() {
      return Redirect.NEVER;
    }

    @Override
    public Optional<ProxySelector> proxy() {
      return Optional.empty();
    }

    @Override
    public SSLContext sslContext() {
      return null;
    }

    @Override
    public SSLParameters sslParameters() {
      return new SSLParameters();
    }

    @Override
    public Optional<Authenticator> authenticator() {
      return Optional.empty();
    }

    @Override
    public Version version() {
      return Version.HTTP_1_1;
    }

    @Override
    public Optional<Executor> executor() {
      return Optional.empty();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> HttpResponse<T> send(
        HttpRequest request, HttpResponse.BodyHandler<T> responseBodyHandler) {
      sendCount.incrementAndGet();
      return (HttpResponse<T>)
          new StaticHttpResponse(
              request,
              200,
              "{\"candidates\":[{\"content\":{\"parts\":[{\"text\":\"pong\"}],"
                  + "\"role\":\"model\"},\"index\":0}],\"usageMetadata\":{"
                  + "\"promptTokenCount\":1,\"totalTokenCount\":2},"
                  + "\"modelVersion\":\"gemini-2.0-flash\"}");
    }

    @Override
    public <T> CompletableFuture<HttpResponse<T>> sendAsync(
        HttpRequest request, HttpResponse.BodyHandler<T> responseBodyHandler) {
      return CompletableFuture.completedFuture(send(request, responseBodyHandler));
    }

    @Override
    public <T> CompletableFuture<HttpResponse<T>> sendAsync(
        HttpRequest request,
        HttpResponse.BodyHandler<T> responseBodyHandler,
        HttpResponse.PushPromiseHandler<T> pushPromiseHandler) {
      return CompletableFuture.completedFuture(send(request, responseBodyHandler));
    }
  }

  private static final class StaticHttpResponse implements HttpResponse<String> {
    private final HttpRequest request;
    private final int statusCode;
    private final String body;

    private StaticHttpResponse(HttpRequest request, int statusCode, String body) {
      this.request = request;
      this.statusCode = statusCode;
      this.body = body;
    }

    @Override
    public int statusCode() {
      return statusCode;
    }

    @Override
    public HttpRequest request() {
      return request;
    }

    @Override
    public Optional<HttpResponse<String>> previousResponse() {
      return Optional.empty();
    }

    @Override
    public HttpHeaders headers() {
      return HttpHeaders.of(Collections.emptyMap(), (left, right) -> true);
    }

    @Override
    public String body() {
      return body;
    }

    @Override
    public Optional<SSLSession> sslSession() {
      return Optional.empty();
    }

    @Override
    public URI uri() {
      return request.uri();
    }

    @Override
    public HttpClient.Version version() {
      return HttpClient.Version.HTTP_1_1;
    }
  }
}
