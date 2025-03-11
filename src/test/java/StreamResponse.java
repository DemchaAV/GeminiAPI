import org.gemini.core.client.GeminiClient;
import org.gemini.core.client.model_config.Model;
import org.gemini.core.client.request_response.content.Content;
import org.gemini.core.client.request_response.content.part.Part;
import org.gemini.core.client.request_response.request.GeminiRequest;

import java.io.IOException;

public class StreamResponse {
    public static void main(String[] args) {
        var client = GeminiClient.builder()
                .apiKey(System.getenv("API_KEY"))
                .httpClient(GeminiClient.DEFAULT_HTTP_CLIENT)
                .defaultModel(Model.GEMINI_1_5_PRO)
                .build();

        String message = "Hello напиши мне историю добрую для поднятия настроения";

        String code = "package org.gemini.core.client;\n" +
                      "\n" +
                      "import com.fasterxml.jackson.annotation.JsonInclude;\n" +
                      "import com.fasterxml.jackson.core.JsonProcessingException;\n" +
                      "import com.fasterxml.jackson.databind.ObjectMapper;\n" +
                      "import com.fasterxml.jackson.databind.SerializationFeature;\n" +
                      "import io.grpc.internal.JsonUtil;\n" +
                      "import lombok.Builder;\n" +
                      "import lombok.NonNull;\n" +
                      "import lombok.Setter;\n" +
                      "import lombok.extern.slf4j.Slf4j;\n" +
                      "import org.gemini.core.chat.Message;\n" +
                      "import org.gemini.core.client.error.GeminiApiException;\n" +
                      "import org.gemini.core.client.model_config.GenerationConfig;\n" +
                      "import org.gemini.core.client.model_config.Model;\n" +
                      "import org.gemini.core.client.model_config.SystemInstruction;\n" +
                      "import org.gemini.core.client.model_config.safe_setting.SafetySetting;\n" +
                      "import org.gemini.core.client.model_config.tool.Tool;\n" +
                      "import org.gemini.core.client.request_response.content.Content;\n" +
                      "import org.gemini.core.client.request_response.content.part.Part;\n" +
                      "import org.gemini.core.client.request_response.request.GeminiRequest;\n" +
                      "import org.gemini.core.client.request_response.response.GeminiResponse;\n" +
                      "\n" +
                      "import java.io.BufferedReader;\n" +
                      "import java.io.IOException;\n" +
                      "import java.io.InputStream;\n" +
                      "import java.io.InputStreamReader;\n" +
                      "import java.net.URI;\n" +
                      "import java.net.http.HttpClient;\n" +
                      "import java.net.http.HttpRequest;\n" +
                      "import java.net.http.HttpResponse;\n" +
                      "import java.nio.charset.StandardCharsets;\n" +
                      "import java.time.Duration;\n" +
                      "import java.util.ArrayList;\n" +
                      "import java.util.List;\n" +
                      "import java.util.Map;\n" +
                      "import java.util.function.Consumer;\n" +
                      "import java.util.stream.Collectors;\n" +
                      "\n" +
                      "\n" +
                      "@JsonInclude(JsonInclude.Include.NON_NULL)\n" +
                      "@Builder\n" +
                      "@Setter\n" +
                      "@Slf4j\n" +
                      "public class GeminiClient {\n" +
                      "    public static final HttpClient DEFAULT_HTTP_CLIENT = HttpClient.newBuilder()\n" +
                      "            .connectTimeout(Duration.ofSeconds(10))\n" +
                      "            .build();\n" +
                      "    private static final String BASE_URL = \"https://generativelanguage.googleapis.com/v1beta/models/\";\n" +
                      "    private static final int MAX_RETRIES = 3;\n" +
                      "    private static final int RETRY_DELAY_MS = 2000;\n" +
                      "    // Model constants\n" +
                      "    @NonNull\n" +
                      "    private final String apiKey;\n" +
                      "    @NonNull\n" +
                      "    private final HttpClient httpClient;\n" +
                      "    private final ObjectMapper mapper = new ObjectMapper();\n" +
                      "    @NonNull\n" +
                      "    private final String defaultModel;\n" +
                      "    private final SystemInstruction systemInstruction;\n" +
                      "    private final List<Tool> tools;\n" +
                      "    private final List<SafetySetting> safetySettings;\n" +
                      "    private final Map<String, String> labels;\n" +
                      "    private List<Content> contents;\n" +
                      "    private GenerationConfig generationConfig;\n" +
                      "    private HttpRequest httpRequest;\n" +
                      "    private List<Content> lastContent;\n" +
                      "    private GeminiResponse response;\n" +
                      "    private GeminiRequest request;\n" +
                      "\n" +
                      "\n" +
                      "    public GeminiClient(@NonNull String apiKey, String defaultModel, GenerationConfig config) {\n" +
                      "        this(apiKey, GeminiClient.DEFAULT_HTTP_CLIENT, defaultModel,\n" +
                      "                null, null, null, null, null, config, null, null, null, null);\n" +
                      "\n" +
                      "    }\n" +
                      "\n" +
                      "    public GeminiClient(@NonNull String apiKey, @NonNull HttpClient httpClient, @NonNull String defaultModel, SystemInstruction systemInstruction, List<Tool> tools, List<SafetySetting> safetySettings, Map<String, String> labels, List<Content> contents, GenerationConfig generationConfig, HttpRequest httpRequest, List<Content> lastContent, GeminiResponse response, GeminiRequest request) {\n" +
                      "        this.apiKey = apiKey;\n" +
                      "        this.httpClient = httpClient;\n" +
                      "        this.defaultModel = defaultModel;\n" +
                      "        this.systemInstruction = systemInstruction;\n" +
                      "        this.tools = tools;\n" +
                      "        this.safetySettings = safetySettings;\n" +
                      "        this.labels = labels;\n" +
                      "        this.contents = contents;\n" +
                      "        this.generationConfig = generationConfig;\n" +
                      "        this.httpRequest = httpRequest;\n" +
                      "        this.lastContent = lastContent;\n" +
                      "        this.response = response;\n" +
                      "        this.request = request;\n" +
                      "        if (checkConnection()) {\n" +
                      "            log.info(\"Connection was successful!\");\n" +
                      "        } else {\n" +
                      "            log.warn(\"Connection was failed\");\n" +
                      "        }\n" +
                      "    }\n" +
                      "\n" +
                      "    public GeminiClient(String apiKey) {\n" +
                      "        this(apiKey, Model.GEMINI_2_0_FLASH_LATEST.getVersion(), null);\n" +
                      "    }\n" +
                      "\n" +
                      "\n" +
                      "    public GeminiClient(String apiKey, Model model, GenerationConfig config) {\n" +
                      "        this(apiKey, model.getVersion(), config);\n" +
                      "    }\n" +
                      "\n" +
                      "    /**\n" +
                      "     * Send a simple text prompt to the Gemini API\n" +
                      "     */\n" +
                      "    public GeminiResponse generateContent(String prompt) throws IOException, InterruptedException {\n" +
                      "        log.info(\"Generating content for prompt: {}\", prompt);\n" +
                      "        return generateContent(GeminiRequest.builder()\n" +
                      "                .addContent(Content.builder()\n" +
                      "                        .addPart(Part.builder().text(prompt).build())\n" +
                      "                        .build())\n" +
                      "                .build(), defaultModel);\n" +
                      "    }\n" +
                      "\n" +
                      "    public GeminiClient sendRequest(GeminiRequest request,boolean asStream) {\n" +
                      "        log.debug(\"Preparing request: {}\", request);\n" +
                      "        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);\n" +
                      "\n" +
                      "        request = GeminiRequest.builder()\n" +
                      "                .contents(request.contents())\n" +
                      "                .systemInstruction(systemInstruction)\n" +
                      "                .cachedContent(request.cachedContent())\n" +
                      "                .tools(tools)\n" +
                      "                .safetySettings(safetySettings)\n" +
                      "                .generationConfig(generationConfig)\n" +
                      "                .labels(labels)\n" +
                      "                .build();\n" +
                      "        this.request = request;\n" +
                      "        // Build the request\n" +
                      "        try {\n" +
                      "            String stringRequest = mapper.writeValueAsString(request);\n" +
                      "            log.debug(\"Serialized request: {}\", stringRequest);\n" +
                      "            return sendRequest(stringRequest,asStream);\n" +
                      "        } catch (JsonProcessingException e) {\n" +
                      "            log.error(\"Error serializing request\", e);\n" +
                      "            throw new RuntimeException(e);\n" +
                      "        }\n" +
                      "\n" +
                      "\n" +
                      "    }\n" +
                      "    public GeminiClient sendRequest(GeminiRequest request) {\n" +
                      "        return sendRequest(request, false);\n" +
                      "    }\n" +
                      "\n" +
                      "    public GeminiClient sendRequest(String stringRequest) {\n" +
                      "        return sendRequest(stringRequest, false);\n" +
                      "    }\n" +
                      "\n" +
                      "    private GeminiClient sendRequest(String stringRequest, boolean asStream) {\n" +
                      "        log.info(\"Sending request to API\");\n" +
                      "        String content = asStream ? \":streamGenerateContent\" : \":generateContent\";\n" +
                      "        String url = BASE_URL + defaultModel + content + \"?key=\" + apiKey;\n" +
                      "\n" +
                      "        this.httpRequest = HttpRequest.newBuilder()\n" +
                      "                .uri(URI.create(url))\n" +
                      "                .header(\"Content-Type\", \"application/json\")\n" +
                      "                .POST(HttpRequest.BodyPublishers.ofString(stringRequest, StandardCharsets.UTF_8))\n" +
                      "                .build();\n" +
                      "\n" +
                      "        log.debug(\"HTTP Request built: {}\", this.httpRequest);\n" +
                      "        return this;\n" +
                      "    }\n" +
                      "\n" +
                      "    private HttpResponse<String> sendWithRetries(HttpRequest httpRequest) throws IOException, InterruptedException {\n" +
                      "        int attempt = 0;\n" +
                      "        IOException lastException = null;\n" +
                      "\n" +
                      "        while (attempt < MAX_RETRIES) {\n" +
                      "            try {\n" +
                      "                return httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());\n" +
                      "            } catch (IOException e) {\n" +
                      "                lastException = e;\n" +
                      "                log.warn(\"Attempt {} failed. Retrying in {} ms\", attempt + 1, RETRY_DELAY_MS);\n" +
                      "                Thread.sleep(RETRY_DELAY_MS);\n" +
                      "                attempt++;\n" +
                      "            }\n" +
                      "        }\n" +
                      "        throw new GeminiApiException(\"All retry attempts failed.\", lastException);\n" +
                      "    }\n" +
                      "\n" +
                      "    public GeminiResponse getResponse() {\n" +
                      "        log.info(\"Fetching response from API\");\n" +
                      "        if (httpRequest == null) {\n" +
                      "            throw new GeminiApiException(\"httpRequest is null. You must call sendRequest first.\");\n" +
                      "        }\n" +
                      "\n" +
                      "        HttpResponse<String> httpResponse;\n" +
                      "        try {\n" +
                      "            httpResponse = sendWithRetries(httpRequest);\n" +
                      "        } catch (IOException | InterruptedException e) {\n" +
                      "            throw new GeminiApiException(\"Failed to fetch response from Gemini API\", e);\n" +
                      "        }\n" +
                      "\n" +
                      "        if (httpResponse.statusCode() != 200) {\n" +
                      "            throw new GeminiApiException(\n" +
                      "                    \"API request failed with status: \" + httpResponse.statusCode() + \", body: \" + httpResponse.body()\n" +
                      "            );\n" +
                      "        }\n" +
                      "\n" +
                      "        try {\n" +
                      "            response = mapper.readValue(httpResponse.body(), GeminiResponse.class);\n" +
                      "        } catch (JsonProcessingException e) {\n" +
                      "            throw new GeminiApiException(\"Failed to parse response JSON\", e);\n" +
                      "        }\n" +
                      "\n" +
                      "        log.info(\"Response successfully parsed\");\n" +
                      "        addHistoryContent();\n" +
                      "        return response;\n" +
                      "    }\n" +
                      "\n" +
                      "    public void getResponseAsStream(Consumer<GeminiResponse> responseConsumer) throws IOException {\n" +
                      "        log.info(\"Fetching response from API\");\n" +
                      "        if (httpRequest == null) {\n" +
                      "            throw new GeminiApiException(\"httpRequest is null. You must call sendRequest first.\");\n" +
                      "        }\n" +
                      "\n" +
                      "        HttpResponse<InputStream> httpResponse;\n" +
                      "        try {\n" +
                      "            httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofInputStream());\n" +
                      "        } catch (IOException | InterruptedException e) {\n" +
                      "            log.error(\"Failed to fetch response from Gemini API\", e);\n" +
                      "            throw new GeminiApiException(\"Failed to fetch response from Gemini API\", e);\n" +
                      "        }\n" +
                      "\n" +
                      "        // Check for errors - only need to do this once\n" +
                      "        if (httpResponse.statusCode() != 200) {\n" +
                      "            try (InputStream errorStream = httpResponse.body();\n" +
                      "                 BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorStream))) {\n" +
                      "                String errorMessage = errorReader.lines().collect(Collectors.joining(\"\\n\"));\n" +
                      "                log.error(\"API request failed with status code: {}, message: {}\", httpResponse.statusCode(), errorMessage);\n" +
                      "                throw new GeminiApiException(\"API request failed with status code: \" + httpResponse.statusCode() + \", message: \" + errorMessage);\n" +
                      "            }\n" +
                      "        }\n" +
                      "\n" +
                      "        try (InputStream inputStream = httpResponse.body();\n" +
                      "             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {\n" +
                      "\n" +
                      "            String line;\n" +
                      "            GeminiResponse geminiResponse;\n" +
                      "           GeminiResponseProcessor processor = new GeminiResponseProcessor();\n" +
                      "            while ((line = reader.readLine()) != null) {\n" +
                      "\n" +
                      "                processor.addChunk(line);\n" +
                      "                if (!processor.getResponseQueue().isEmpty()) {\n" +
                      "                    responseConsumer.accept(processor. getResponseQueue().poll());\n" +
                      "                }\n" +
                      "            }\n" +
                      "        } catch (IOException e) {\n" +
                      "            log.error(\"Error reading stream from Gemini API\", e);\n" +
                      "            throw new GeminiApiException(\"Error reading stream from Gemini API\", e);\n" +
                      "        }\n" +
                      "\n" +
                      "        addHistoryContent();\n" +
                      "    }\n" +
                      "\n" +
                      "    private GeminiResponse processJson(String jsonObject) throws IOException {\n" +
                      "        GeminiResponse geminiResponse = null;\n" +
                      "        try {\n" +
                      "            geminiResponse = mapper.readValue(jsonObject, GeminiResponse.class);\n" +
                      "        } catch (JsonProcessingException e) {\n" +
                      "            log.error(\"Failed to parse response JSON\", e);\n" +
                      "            throw new GeminiApiException(\"Failed to parse response JSON\", e);\n" +
                      "        }\n" +
                      "\n" +
                      "        log.info(\"Response successfully parsed\");\n" +
                      "        return geminiResponse;\n" +
                      "    }\n" +
                      "\n" +
                      "\n" +
                      "    /**\n" +
                      "     * Send a customized request to the Gemini API\n" +
                      "     */\n" +
                      "    public GeminiResponse generateContent(GeminiRequest request, String model) throws IOException, InterruptedException {\n" +
                      "        String url = BASE_URL + model + \":generateContent\" + \"?key=\" + apiKey;\n" +
                      "\n" +
                      "        // Serialize request to JSON\n" +
                      "        String requestBody = mapper.writeValueAsString(request);\n" +
                      "\n" +
                      "        // Build and send HTTP request\n" +
                      "        HttpRequest httpRequest = HttpRequest.newBuilder()\n" +
                      "                .uri(URI.create(url))\n" +
                      "                .header(\"Content-Type\", \"application/json\")\n" +
                      "                .timeout(Duration.ofSeconds(30))\n" +
                      "                .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))\n" +
                      "                .build();\n" +
                      "\n" +
                      "        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());\n" +
                      "\n" +
                      "        // Check for errors\n" +
                      "        if (response.statusCode() != 200) {\n" +
                      "            throw new IOException(\"API request failed with status code: %d, message: %s\".formatted(response.statusCode(), response.body()));\n" +
                      "        }\n" +
                      "\n" +
                      "        // Parse response\n" +
                      "        return mapper.readValue(response.body(), GeminiResponse.class);\n" +
                      "    }\n" +
                      "\n" +
                      "    //TODO response as stream\n" +
                      "    public void generateContent(GeminiRequest request, Consumer<String> consumer) throws IOException, InterruptedException {\n" +
                      "        String url = BASE_URL + defaultModel + \":streamGenerateContent\" + \"?key=\" + apiKey; // Обратите внимание на streamGenerateContent\n" +
                      "\n" +
                      "        // Serialize request to JSON\n" +
                      "        String requestBody = mapper.writeValueAsString(request);\n" +
                      "\n" +
                      "        // Build and send HTTP request\n" +
                      "        HttpRequest httpRequest = HttpRequest.newBuilder()\n" +
                      "                .uri(URI.create(url))\n" +
                      "                .header(\"Content-Type\", \"application/json\")\n" +
                      "                .timeout(Duration.ofSeconds(30))\n" +
                      "                .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))\n" +
                      "                .build();\n" +
                      "\n" +
                      "        HttpResponse<InputStream> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofInputStream());\n" +
                      "\n" +
                      "        // Check for errors\n" +
                      "        if (response.statusCode() != 200) {\n" +
                      "            try (InputStream errorStream = response.body();\n" +
                      "                 BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorStream))) {\n" +
                      "                String errorMessage = errorReader.lines().reduce(\"\", (a, b) -> a + b + \"\\n\");\n" +
                      "                log.error(\"API request failed with status code: {}, message: {}\", response.body(), errorMessage);\n" +
                      "                throw new IOException(\"API request failed with status code: \" + response.statusCode() + \", message: \" + errorMessage);\n" +
                      "            }\n" +
                      "        }\n" +
                      "\n" +
                      "        try (InputStream inputStream = response.body();\n" +
                      "             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {\n" +
                      "\n" +
                      "            String line;\n" +
                      "            GeminiResponse geminiResponse;\n" +
                      "            while ((line = reader.readLine()) != null) {\n" +
                      "                System.out.println(line);\n" +
                      "//            geminiResponse = mapper.readValue(line, GeminiResponse.class);\n" +
                      "                consumer.accept(line); // Отправляем каждую строку потребителю\n" +
                      "            }\n" +
                      "        }\n" +
                      "    }\n" +
                      "\n" +
                      "    private boolean addHistoryContent() {\n" +
                      "        if (request == null || response == null) {\n" +
                      "            if (request == null) {\n" +
                      "                log.warn(\"Variable {} is null\", \"request\");\n" +
                      "            } else {\n" +
                      "                log.warn(\"Variable {} is null\", \"response\");\n" +
                      "            }\n" +
                      "            return false;\n" +
                      "        }\n" +
                      "        contents = new ArrayList<>();\n" +
                      "        var contentsRequest = request.contents();\n" +
                      "        var contentsResponse = response.candidates().getFirst().content();\n" +
                      "        this.contents.addAll(contentsRequest);\n" +
                      "        log.trace(\"Request {} has been added to content\", contentsRequest);\n" +
                      "        this.contents.add(contentsResponse);\n" +
                      "        log.trace(\"Response {} has been added to content\", contentsResponse);\n" +
                      "        return true;\n" +
                      "    }\n" +
                      "\n" +
                      "    public List<Content> takeContent() {\n" +
                      "        List<Content> contentList = null;\n" +
                      "        if (isReadyContent()) {\n" +
                      "            contentList = new ArrayList<>(this.contents);\n" +
                      "            this.contents = null;\n" +
                      "            return contentList;\n" +
                      "        }\n" +
                      "        response = null;\n" +
                      "        request = null;\n" +
                      "        return contentList;\n" +
                      "    }\n" +
                      "\n" +
                      "    public boolean isReadyContent() {\n" +
                      "        return contents != null;\n" +
                      "    }\n" +
                      "\n" +
                      "    private boolean checkConnection() {\n" +
                      "        String prompt = \"Give me a short answer. did you just get my message ?\";\n" +
                      "        log.info(\"Checking the connection\");\n" +
                      "        GeminiResponse response = null;\n" +
                      "        response = sendRequest(GeminiRequest.requestMessage(new Message(prompt))).getResponse();\n" +
                      "        takeContent();\n" +
                      "        return response != null;\n" +
                      "\n" +
                      "    }\n" +
                      "\n" +
                      "    /**\n" +
                      "     * Additional methods for builder\n" +
                      "     */\n" +
                      "    public static class GeminiClientBuilder {\n" +
                      "        public GeminiClientBuilder defaultModel(Model model) {\n" +
                      "            return (defaultModel(model.getVersion()));\n" +
                      "        }\n" +
                      "\n" +
                      "        public GeminiClientBuilder defaultModel(String defaultModel) {\n" +
                      "            this.defaultModel = defaultModel;\n" +
                      "            return this;\n" +
                      "        }\n" +
                      "    }\n" +
                      "\n" +
                      "\n" +
                      "} ";
        GeminiRequest request = GeminiRequest.builder()
                .addContent(Content.builder()
                        .role("user")
                        .addPart(
                                Part.builder()
                                        .text("Как тебе этот код "+code)
                                        .build()
                        )
                        .build())
                .build();

        client.sendRequest(request,true);
        try {
            client.getResponseAsStream(response -> {
                response.candidates().forEach(candidate -> {
                    candidate.content().parts().forEach(part -> {
                        System.out.print(part.text()); // ← постепенный вывод текста
                    });
                });
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
