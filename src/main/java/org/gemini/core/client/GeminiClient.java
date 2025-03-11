package org.gemini.core.client;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.grpc.internal.JsonUtil;
import lombok.Builder;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.gemini.core.chat.Message;
import org.gemini.core.client.error.GeminiApiException;
import org.gemini.core.client.model_config.GenerationConfig;
import org.gemini.core.client.model_config.Model;
import org.gemini.core.client.model_config.SystemInstruction;
import org.gemini.core.client.model_config.safe_setting.SafetySetting;
import org.gemini.core.client.model_config.tool.Tool;
import org.gemini.core.client.request_response.content.Content;
import org.gemini.core.client.request_response.content.part.Part;
import org.gemini.core.client.request_response.request.GeminiRequest;
import org.gemini.core.client.request_response.response.GeminiResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;


@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@Setter
@Slf4j
public class GeminiClient {
    public static final HttpClient DEFAULT_HTTP_CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private static final String BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/";
    private static final int MAX_RETRIES = 3;
    private static final int RETRY_DELAY_MS = 2000;
    // Model constants
    @NonNull
    private final String apiKey;
    @NonNull
    private final HttpClient httpClient;
    private final ObjectMapper mapper = new ObjectMapper();
    @NonNull
    private final String defaultModel;
    private final SystemInstruction systemInstruction;
    private final List<Tool> tools;
    private final List<SafetySetting> safetySettings;
    private final Map<String, String> labels;
    private List<Content> contents;
    private GenerationConfig generationConfig;
    private HttpRequest httpRequest;
    private List<Content> lastContent;
    private GeminiResponse response;
    private GeminiRequest request;


    public GeminiClient(@NonNull String apiKey, String defaultModel, GenerationConfig config) {
        this(apiKey, GeminiClient.DEFAULT_HTTP_CLIENT, defaultModel,
                null, null, null, null, null, config, null, null, null, null);

    }

    public GeminiClient(@NonNull String apiKey, @NonNull HttpClient httpClient, @NonNull String defaultModel, SystemInstruction systemInstruction, List<Tool> tools, List<SafetySetting> safetySettings, Map<String, String> labels, List<Content> contents, GenerationConfig generationConfig, HttpRequest httpRequest, List<Content> lastContent, GeminiResponse response, GeminiRequest request) {
        this.apiKey = apiKey;
        this.httpClient = httpClient;
        this.defaultModel = defaultModel;
        this.systemInstruction = systemInstruction;
        this.tools = tools;
        this.safetySettings = safetySettings;
        this.labels = labels;
        this.contents = contents;
        this.generationConfig = generationConfig;
        this.httpRequest = httpRequest;
        this.lastContent = lastContent;
        this.response = response;
        this.request = request;
        if (checkConnection()) {
            log.info("Connection was successful!");
        } else {
            log.warn("Connection was failed");
        }
    }

    public GeminiClient(String apiKey) {
        this(apiKey, Model.GEMINI_2_0_FLASH_LATEST.getVersion(), null);
    }


    public GeminiClient(String apiKey, Model model, GenerationConfig config) {
        this(apiKey, model.getVersion(), config);
    }

    /**
     * Send a simple text prompt to the Gemini API
     */
    public GeminiResponse generateContent(String prompt) throws IOException, InterruptedException {
        log.info("Generating content for prompt: {}", prompt);
        return generateContent(GeminiRequest.builder()
                .addContent(Content.builder()
                        .addPart(Part.builder().text(prompt).build())
                        .build())
                .build(), defaultModel);
    }

    public GeminiClient sendRequest(GeminiRequest request,boolean asStream) {
        log.debug("Preparing request: {}", request);
        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

        request = GeminiRequest.builder()
                .contents(request.contents())
                .systemInstruction(systemInstruction)
                .cachedContent(request.cachedContent())
                .tools(tools)
                .safetySettings(safetySettings)
                .generationConfig(generationConfig)
                .labels(labels)
                .build();
        this.request = request;
        // Build the request
        try {
            String stringRequest = mapper.writeValueAsString(request);
            log.debug("Serialized request: {}", stringRequest);
            return sendRequest(stringRequest,asStream);
        } catch (JsonProcessingException e) {
            log.error("Error serializing request", e);
            throw new RuntimeException(e);
        }


    }
    public GeminiClient sendRequest(GeminiRequest request) {
        return sendRequest(request, false);
    }

    public GeminiClient sendRequest(String stringRequest) {
        return sendRequest(stringRequest, false);
    }

    private GeminiClient sendRequest(String stringRequest, boolean asStream) {
        log.info("Sending request to API");
        String content = asStream ? ":streamGenerateContent" : ":generateContent";
        String url = BASE_URL + defaultModel + content + "?key=" + apiKey;

        this.httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(stringRequest, StandardCharsets.UTF_8))
                .build();

        log.debug("HTTP Request built: {}", this.httpRequest);
        return this;
    }

    private HttpResponse<String> sendWithRetries(HttpRequest httpRequest) throws IOException, InterruptedException {
        int attempt = 0;
        IOException lastException = null;

        while (attempt < MAX_RETRIES) {
            try {
                return httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            } catch (IOException e) {
                lastException = e;
                log.warn("Attempt {} failed. Retrying in {} ms", attempt + 1, RETRY_DELAY_MS);
                Thread.sleep(RETRY_DELAY_MS);
                attempt++;
            }
        }
        throw new GeminiApiException("All retry attempts failed.", lastException);
    }

    public GeminiResponse getResponse() {
        log.info("Fetching response from API");
        if (httpRequest == null) {
            throw new GeminiApiException("httpRequest is null. You must call sendRequest first.");
        }

        HttpResponse<String> httpResponse;
        try {
            httpResponse = sendWithRetries(httpRequest);
        } catch (IOException | InterruptedException e) {
            throw new GeminiApiException("Failed to fetch response from Gemini API", e);
        }

        if (httpResponse.statusCode() != 200) {
            throw new GeminiApiException(
                    "API request failed with status: " + httpResponse.statusCode() + ", body: " + httpResponse.body()
            );
        }

        try {
            response = mapper.readValue(httpResponse.body(), GeminiResponse.class);
        } catch (JsonProcessingException e) {
            throw new GeminiApiException("Failed to parse response JSON", e);
        }

        log.info("Response successfully parsed");
        addHistoryContent();
        return response;
    }

    public void getResponseAsStream(Consumer<GeminiResponse> responseConsumer) throws IOException {
        log.info("Fetching response from API");
        if (httpRequest == null) {
            throw new GeminiApiException("httpRequest is null. You must call sendRequest first.");
        }

        HttpResponse<InputStream> httpResponse;
        try {
            httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofInputStream());
        } catch (IOException | InterruptedException e) {
            log.error("Failed to fetch response from Gemini API", e);
            throw new GeminiApiException("Failed to fetch response from Gemini API", e);
        }

        // Check for errors - only need to do this once
        if (httpResponse.statusCode() != 200) {
            try (InputStream errorStream = httpResponse.body();
                 BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorStream))) {
                String errorMessage = errorReader.lines().collect(Collectors.joining("\n"));
                log.error("API request failed with status code: {}, message: {}", httpResponse.statusCode(), errorMessage);
                throw new GeminiApiException("API request failed with status code: " + httpResponse.statusCode() + ", message: " + errorMessage);
            }
        }

        try (InputStream inputStream = httpResponse.body();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            String line;
            GeminiResponse geminiResponse;
           GeminiResponseProcessor processor = new GeminiResponseProcessor();
            while ((line = reader.readLine()) != null) {

                processor.addChunk(line);
                if (!processor.getResponseQueue().isEmpty()) {
                    responseConsumer.accept(processor. getResponseQueue().poll());
                }
            }
        } catch (IOException e) {
            log.error("Error reading stream from Gemini API", e);
            throw new GeminiApiException("Error reading stream from Gemini API", e);
        }

        addHistoryContent();
    }

    private GeminiResponse processJson(String jsonObject) throws IOException {
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


    /**
     * Send a customized request to the Gemini API
     */
    public GeminiResponse generateContent(GeminiRequest request, String model) throws IOException, InterruptedException {
        String url = BASE_URL + model + ":generateContent" + "?key=" + apiKey;

        // Serialize request to JSON
        String requestBody = mapper.writeValueAsString(request);

        // Build and send HTTP request
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        // Check for errors
        if (response.statusCode() != 200) {
            throw new IOException("API request failed with status code: %d, message: %s".formatted(response.statusCode(), response.body()));
        }

        // Parse response
        return mapper.readValue(response.body(), GeminiResponse.class);
    }

    //TODO response as stream
    public void generateContent(GeminiRequest request, Consumer<String> consumer) throws IOException, InterruptedException {
        String url = BASE_URL + defaultModel + ":streamGenerateContent" + "?key=" + apiKey; // Обратите внимание на streamGenerateContent

        // Serialize request to JSON
        String requestBody = mapper.writeValueAsString(request);

        // Build and send HTTP request
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                .build();

        HttpResponse<InputStream> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofInputStream());

        // Check for errors
        if (response.statusCode() != 200) {
            try (InputStream errorStream = response.body();
                 BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorStream))) {
                String errorMessage = errorReader.lines().reduce("", (a, b) -> a + b + "\n");
                log.error("API request failed with status code: {}, message: {}", response.body(), errorMessage);
                throw new IOException("API request failed with status code: " + response.statusCode() + ", message: " + errorMessage);
            }
        }

        try (InputStream inputStream = response.body();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            String line;
            GeminiResponse geminiResponse;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
//            geminiResponse = mapper.readValue(line, GeminiResponse.class);
                consumer.accept(line); // Отправляем каждую строку потребителю
            }
        }
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
        contents = new ArrayList<>();
        var contentsRequest = request.contents();
        var contentsResponse = response.candidates().getFirst().content();
        this.contents.addAll(contentsRequest);
        log.trace("Request {} has been added to content", contentsRequest);
        this.contents.add(contentsResponse);
        log.trace("Response {} has been added to content", contentsResponse);
        return true;
    }

    public List<Content> takeContent() {
        List<Content> contentList = null;
        if (isReadyContent()) {
            contentList = new ArrayList<>(this.contents);
            this.contents = null;
            return contentList;
        }
        response = null;
        request = null;
        return contentList;
    }

    public boolean isReadyContent() {
        return contents != null;
    }

    private boolean checkConnection() {
        String prompt = "Give me a short answer. did you just get my message ?";
        log.info("Checking the connection");
        GeminiResponse response = null;
        response = sendRequest(GeminiRequest.requestMessage(new Message(prompt))).getResponse();
        takeContent();
        return response != null;

    }

    /**
     * Additional methods for builder
     */
    public static class GeminiClientBuilder {
        public GeminiClientBuilder defaultModel(Model model) {
            return (defaultModel(model.getVersion()));
        }

        public GeminiClientBuilder defaultModel(String defaultModel) {
            this.defaultModel = defaultModel;
            return this;
        }
    }


}