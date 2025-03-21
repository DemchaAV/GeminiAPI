package org.gemini.core.client;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.gemini.core.client.error.ApiErrorHandler;
import org.gemini.core.client.error.GeminiApiException;
import org.gemini.core.client.model.GeminiModel;
import org.gemini.core.client.model.ImagenModel;
import org.gemini.core.client.model.VerAPI;
import org.gemini.core.client.model.enums.GeminiGenerateMethod;
import org.gemini.core.client.model.enums.GeminiVariation;
import org.gemini.core.client.model.enums.GeminiVersion;
import org.gemini.core.client.model_config.GenerationConfig;
import org.gemini.core.client.model_config.SystemInstruction;
import org.gemini.core.client.model_config.safe_setting.SafetySetting;
import org.gemini.core.client.model_config.tool.Tool;
import org.gemini.core.client.request_response.content.Content;
import org.gemini.core.client.request_response.content.Message;
import org.gemini.core.client.request_response.content.part.Part;
import org.gemini.core.client.request_response.request.GeminiRequest;
import org.gemini.core.client.request_response.request.ImgGenRequest;
import org.gemini.core.client.request_response.response.GeminiResponse;

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

/**
 * {@code GeminiConnection} Documentation
 *
 * <p>
 * The {@code GeminiConnection} class provides a simple and convenient way to interact with the Google Gemini API.
 * It allows you to easily send requests and receive responses from the Gemini models.
 * </p>
 *
 * <h2>Initialization</h2>
 * <p>
 * To start using the client, you need to initialize it with your Google API key.
 * You can obtain your API key from the
 * <a href="https://aistudio.google.com/app/apikey">Google Gemini API Keys</a> page.
 * </p>
 * <p>
 * <strong>Important:</strong> Ensure that the {@code API_KEY} is not null during client initialization.
 * </p>
 *
 * <h3>Basic Initialization</h3>
 * <p>
 * The simplest way to initialize the client is by providing just your API key.
 * This will use the default {@code GEMINI_2_0_FLASH_LATEST} model and default HTTP client settings.
 * </p>
 * <pre>{@code
 * GeminiConnection client = new GeminiConnection(API_KEY);
 * }</pre>
 *
 * <h3>Initialization with a Specific org.gemini.core.client.model.model_test.Model</h3>
 * <p>
 * You can specify a different Gemini model to use during initialization.
 * Refer to the {@link GeminiModel} enum for available model options.
 * </p>
 * <pre>{@code
 * GeminiConnection client = new GeminiConnection(API_KEY, org.gemini.core.client.model.model_test.Model.GEMINI_PRO, null);
 * }</pre>
 *
 * <h3>Initialization with Custom Configuration</h3>
 * <p>
 * For more advanced configurations, you can use the {@link GeminiConnectionBuilder} to customize the client.
 * This includes setting a custom HTTP client, default model, generation configuration, system instructions, tools,
 * safety settings, and labels.
 * </p>
 * <pre>{@code
 * GenerationConfig generationConfig = GenerationConfig.builder()
 *         .temperature(0.9f)
 *         .topK(1)
 *         .topP(1)
 *         .maxOutputTokens(2048)
 *         .build();
 *
 * GeminiConnection client = GeminiConnection.builder()
 *         .apiKey(API_KEY)
 *         .httpClient(GeminiConnection.DEFAULT_HTTP_CLIENT) // Or your custom HttpClient
 *         .defaultModel(org.gemini.core.client.model.model_test.Model.GEMINI_PRO_LATEST.getVersion())
 *         .generationConfig(generationConfig)
 *         // ... other configurations
 *         .build();
 * }</pre>
 *
 * <h2>Sending Requests</h2>
 * <p>
 * The {@code GeminiConnection} provides methods to send different types of requests to the Gemini API.
 * </p>
 *
 * <h3>Sending a Request with {@link GeminiRequest} Object</h3>
 * <p>
 * You can create a {@link GeminiRequest} object to encapsulate all request parameters, including contents,
 * system instructions, tools, safety settings, and generation configuration.
 * Use the {@link #sendRequest(GeminiRequest)} method to send this request to the client.
 * </p>
 * <pre>{@code
 * Message userMessage = new Message("Hello Gemini!");
 * GeminiRequest request = GeminiRequest.requestMessage(userMessage);
 * GeminiConnection client = new GeminiConnection(API_KEY);
 * client.sendRequest(request);
 * GeminiResponse response = client.getResponse();
 * }</pre>
 *
 * <h3>Sending a Request with a JSON String</h3>
 * <p>
 * Alternatively, you can send a request using a JSON string that represents a {@link GeminiRequest}.
 * This is useful when you have pre-formatted JSON requests or need to dynamically generate them as strings.
 * Use the {@link #sendRequest(String)} method for this purpose.
 * </p>
 * <pre>{@code
 * GeminiConnection client = GeminiConnection.builder()
 *         .apiKey(API_KEY)
 *         .httpClient(GeminiConnection.DEFAULT_HTTP_CLIENT)
 *         .defaultModel(org.gemini.core.client.model.model_test.Model.GEMINI_2_0_FLASH_LATEST)
 *         .build();
 * String simpleJsonRequest = "{"contents":[{"role":"user","parts":[{"text":"Hello Gemini!"}]}]}";
 * client.sendRequest(simpleJsonRequest);
 * GeminiResponse response = client.getResponse();
 * }</pre>
 *
 * <h2>Receiving Responses</h2>
 * <p>
 * After sending a request, you can retrieve the response from the Gemini API using the following methods:
 * </p>
 *
 * <h3>{@link #getResponse()}</h3>
 * <p>
 * This method sends the prepared request to the Gemini API and returns a {@link GeminiResponse} object.
 * The {@code GeminiResponse} object contains the API's response in a structured format, including generated content,
 * candidates, and other relevant information.
 * </p>
 * <pre>{@code
 * GeminiResponse response = client.getResponse();
 * if (response != null && response.candidates() != null && !response.candidates().isEmpty()) {
 *     String generatedText = client.takeContentAsString();
 *     System.out.println("Generated Text: " + generatedText);
 * } else {
 *     System.out.println("No response or candidates found.");
 * }
 * }</pre>
 *
 * <h3>{@link #getResponseAsStream(Consumer)}</h3>
 * <p>
 * For streaming responses, use this method to process the response as an asynchronous stream.
 * This is beneficial for handling large responses or when you want to process the response chunks as they become available.
 * Provide a {@link Consumer} functional interface to handle each {@link GeminiResponse} chunk received from the stream.
 * </p>
 * <pre>{@code
 *  try {
 *             client.getResponseAsStream(response -> {
 *                 response.candidates().forEach(candidate -> {
 *                     candidate.content().parts().forEach(part -> {
 *                         System.out.print(part.text()); //
 *                     });
 *                 });
 *             });
 *         } catch (IOException e) {
 *             throw new RuntimeException(e);
 *         }
 * }</pre>
 *
 * <h2>Generating Simple Text Content</h2>
 * <p>
 * For quick text generation, you can use the {@link #generateContent(String)} method.
 * This method simplifies the process of sending a text prompt and retrieving the generated text response as a String.
 * </p>
 * <pre>{@code
 * String prompt = "Write a short poem about the moon.";
 * String poem = client.generateContent(prompt);
 * System.out.println("Generated Poem:\n" + poem);
 * }</pre>
 *
 * <h2>Retrieving Content History</h2>
 * <p>
 * The client maintains a history of sent requests and received responses.
 * You can retrieve the content history as a list of {@link Content} objects using the {@link #takeContent()} method.
 * To get the history as a formatted String, use {@link #takeContentAsString()}.
 * </p>
 * <pre>{@code
 * List<Content> history = client.takeContent();
 * if (history != null) {
 *     String historyString = client.takeContentAsString();
 *     System.out.println("Content History:\n" + historyString);
 * }
 * }</pre>
 *
 * <p>
 * For further details and advanced usage, please refer to the
 * <a href="https://ai.google.dev/gemini-api/docs">Google Gemini Client Documentation</a>.
 * </p>
 *
 * @author Artem Demchyshyn
 * @see <a href="https://ai.google.dev/gemini-api/docs">Google Gemini Client Documentation</a>
 * @see <a href="https://aistudio.google.com/app/apikey">Google Gemini API Keys</a>
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@AllArgsConstructor
@Setter
@Slf4j
public class GeminiConnection {

    public static final HttpClient DEFAULT_HTTP_CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private static final String BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/";
    private static final int MAX_RETRIES = 3;
    private static final int RETRY_DELAY_MS = 2000;
    private final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    // HTTP client
    @NonNull
    private final HttpClient httpClient;
    //org.gemini.core.client.model.model_test.Model constants settings
    @NonNull
    private final String apiKey;
    @NonNull
    private final GeminiModel geminiModel;
    private final ImagenModel imagenModel;
    private final SystemInstruction systemInstruction;
    private final List<Tool> tools;
    private final List<SafetySetting> safetySettings;
    private final Map<String, String> labels;
    private final AtomicInteger totalTokens = new AtomicInteger(0);
    private HttpRequest httpRequest;
    private GenerationConfig generationConfig;
    //Generation content
    private List<Content> contents;
    private List<Content> lastContent;
    //Request and response
    private GeminiResponse response;
    private GeminiRequest request;
    private ImgGenRequest imageRequest;
    private String bodyHttpRequest;
    private String url;

    //Constructors


    public GeminiConnection(@NonNull String apiKey, @NonNull HttpClient httpClient, @NonNull GeminiModel geminiModel, ImagenModel imagenModel, SystemInstruction systemInstruction, List<Tool> tools, List<SafetySetting> safetySettings, Map<String, String> labels, List<Content> contents, GenerationConfig generationConfig, HttpRequest httpRequest, List<Content> lastContent, GeminiResponse response, GeminiRequest request) {
        this.apiKey = apiKey;
        this.httpClient = httpClient;
        this.geminiModel = geminiModel;
        this.imagenModel = imagenModel;
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
            log.info("Connection is successful!");
        } else {
            log.error("Connection is failed!");
        }

    }
    public GeminiConnection(String apiKey) {
        this(apiKey, GeminiModel.builder()
                .verAPI(VerAPI.V1BETA)
                .variation(GeminiVariation._2_0)
                .version(GeminiVersion.FLASH_LATEST)
                .generateMethod(GeminiGenerateMethod.GENERATE_CONTENT).build(), null);
    }

    public GeminiConnection(String apiKey, @NonNull GeminiModel model, GenerationConfig config) {
        this(GeminiConnection.DEFAULT_HTTP_CLIENT, apiKey, model, null, null, null, null, null, null, config, null, null, null, null, null, null,null);
    }

    public GeminiConnection(String apiKey, @NonNull ImagenModel model, GenerationConfig config) {
        this(GeminiConnection.DEFAULT_HTTP_CLIENT, apiKey, null, model, null, null, null, null, null, config, null, null, null, null, null, null,null);
    }

    //Methods

    public GeminiConnection sendRequest(GeminiRequest request) {
        log.debug("Preparing request: {}", request);
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

        return this;
    }

    public GeminiConnection sendRequest(ImgGenRequest request) {
        this.imageRequest = request;
        return this;
    }

    public static boolean hasAnyNotNullField(Object obj) {
        if (obj == null) {
            return false; // Если объект null, значит, все поля по умолчанию null
        }

        for (Field field : obj.getClass().getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers()) || Modifier.isTransient(field.getModifiers())) {
                continue; // Пропускаем static и transient поля
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
        return false; // Все поля null
    }


    private String generateStringHttpJsonFromRequest() {
        if (request == null) {
            log.error("This.request is null please sed the  request before");
            throw new GeminiApiException("This.request is null please sed the  request before");
        }
        return getStringJson(request);
    }

    private <T> String getStringJson(T request) {
        String stringRequest = null;
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
     * @param jsonGeminiRequest request with existing jsonObject as String
     * @return return client to execute a request uses a method {@code getResponse>()} or {@code getResponseAsStream()}
     * <pre>
     *     {@code client = GeminiConnection.builder()
     *                 .apiKey(System.getenv("API_KEY"))
     *                 .httpClient(GeminiConnection.DEFAULT_HTTP_CLIENT)
     *                 .defaultModel(org.gemini.core.client.model.model_test.Model.GEMINI_2_0_FLASH_LATEST)
     *                 .build();
     * String simpleJsonRequest = "{"contents":[{"role":"user","parts":[{"text":"Hello Gemini!"}]}]}"
     * client.sendRequest(simpleJsonRequest);
     * GeminiResponse response = client.getResponse;
     * </pre>
     */
    public GeminiConnection sendRequest(String jsonGeminiRequest) {
        log.debug("Parse string Json request: {}", jsonGeminiRequest);
        try {
            this.request = mapper.readValue(jsonGeminiRequest, GeminiRequest.class);
        } catch (JsonProcessingException e) {
            log.error("Error during parsing JsonString GeminiRequest:  {} failed!\n", jsonGeminiRequest, e);
            throw new RuntimeException(e);
        }
        return sendRequest(request);
    }

    private void createHttpRequest(boolean asStream) {
        createHttpRequest(asStream, false);
    }

    private void createHttpRequest(boolean asStream, boolean isImageGeneration) {
        if (request == null) {
            if (isImageGeneration) {
                bodyHttpRequest = getStringJson(imageRequest);
            } else {
                log.error("Request object is null. Cannot generate HTTP request.");
                return;
            }

        }
        this.bodyHttpRequest = !isImageGeneration ? generateStringHttpJsonFromRequest() : bodyHttpRequest;


        log.info("Sending request to API {}", asStream ? "as Stream" : "");
        
        String url = null;
        if (isImageGeneration) {
            this.url = imagenModel.getUrl();
            url = this.url + apiKey;
        } else {
            if (asStream) {
                this.url = GeminiModel.builder()
                        .copyModelAndSetGenerateMethod(geminiModel, GeminiGenerateMethod.STREAM_GENERATE_CONTENT)
                        .build()
                        .getUrl();
                url =this.url + apiKey;
            } else {
                this.url = GeminiModel.builder()
                        .copyModelAndSetGenerateMethod(geminiModel, GeminiGenerateMethod.GENERATE_CONTENT)
                        .build()
                        .getUrl();
                url =this.url + apiKey;

            }
        }
        processHttpRequest(url);

    }


    public Optional<GeminiResponse> getResponse() {
        return getResponse(false);
    }

    private void processHttpRequest(String url) {
        this.httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(bodyHttpRequest, StandardCharsets.UTF_8))
                .build();

        log.debug("HTTP Request built: {}", this.httpRequest);
    }

    public Optional<GeminiResponse> getResponse(boolean isImage) {
        // create HttpRequest
        createHttpRequest(false, isImage);

        HttpResponse<String> httpResponse = fetchHttpResponse(HttpResponse.BodyHandlers.ofString());
        if (isImage) {
            log.debug(httpResponse.body());
        }

        response = parseJson(httpResponse.body());
        if (!isImage) {
        totalTokens.getAndAdd(response.usageMetadata().totalTokenCount());
            if (addHistoryContent()) {
                log.info("History content has been added");
            }else {
                log.warn("History content hasn't been added!!");
            }
        }
        return Optional.ofNullable(!hasAnyNotNullField(response) ? null : response);
    }

    private <T> HttpResponse<T> sendWithRetries(HttpRequest httpRequest, HttpResponse.BodyHandler<T> bodyHandler)
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
                 BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorStream))) {
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
        log.error("API request failed with status code: {}, message: {} \nrequestUrl:  {} \n response body: \n{} request body: {}", responseErrorCode, error.getDetailedErrorMessage(),this.url, responseBody, this.bodyHttpRequest);
        throw new GeminiApiException("API request failed with status code: %d  %s \nrequestUrl:  %s \nresponse body: %s \n request body: %s".formatted(
                responseErrorCode, error,this.url, responseBody, this.bodyHttpRequest));
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

    private boolean checkConnection() {
        String prompt = "Give me a short answer. did you just get my message ?";
        log.info("Checking the connection");
        Optional<GeminiResponse> response = Optional.empty();
        response = sendRequest(GeminiRequest.requestMessage(new Message(prompt))).getResponse();
        takeContent();
        return response.isPresent();
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

//Retrive content

    public List<Content> takeContent() {
        List<Content> contentList = null;
        if (isReadyContent()) {
            contentList = new ArrayList<>(this.contents);
            this.contents = null;
            return contentList;
        }
        log.error("Content is not ready this.content is null!");
        response = null;
        request = null;
        return contentList;
    }

    public String takeContentAsString() {
        List<Content> contentList = takeContent();
        if (contentList == null) {
            return null;
        }

        return contentList.stream()
                .map(content -> String.format("role: %s\nmessage: \"%s\"\n",
                        content.role(),
                        content.parts().stream()
                                .map(Part::text)
                                .collect(Collectors.joining()).trim()))
                .collect(Collectors.joining());
    }


    public boolean isReadyContent() {
        return contents != null;
    }

    public void getResponseAsStream(Consumer<GeminiResponse> responseConsumer) throws IOException {
        createHttpRequest(true);
        HttpResponse<InputStream> httpResponse = fetchHttpResponse(HttpResponse.BodyHandlers.ofInputStream());


        try (InputStream inputStream = httpResponse.body();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

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
                        totalTokens = geminiResponse.usageMetadata().totalTokenCount();
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

}