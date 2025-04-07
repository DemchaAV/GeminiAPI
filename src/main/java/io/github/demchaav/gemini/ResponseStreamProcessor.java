package io.github.demchaav.gemini;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import io.github.demchaav.gemini.request_response.response.GeminiResponse;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Class {@link ResponseStreamProcessor} processes streaming JSON responses from {@link GeminiClient}.
 * This class accumulates JSON chunks from {@link java.net.http.HttpResponse} and adds them to the {@code responseQueue}
 * when a complete JSON object is detected.
 */
@Slf4j
public class ResponseStreamProcessor {
    private final StringBuilder buffer = new StringBuilder();
    private int openBrackets = 0;
    private int closeBrackets = 0;
    private final ObjectMapper mapper = new ObjectMapper();
    @Getter
    private final Queue<GeminiResponse> responseQueue = new ConcurrentLinkedQueue<>();
    private int lastCheckedIndex = 0;
    private int objectStartIndex = -1;

    public boolean addChunk(String chunk) {
        buffer.append(chunk);

        for (int i = lastCheckedIndex; i < buffer.length(); i++) {
            char ch = buffer.charAt(i);

            if (ch == '{') {
                if (openBrackets == 0) {
                    objectStartIndex = i;
                }
                openBrackets++;
            } else if (ch == '}') {
                closeBrackets++;
            }

            if (openBrackets > 0 && openBrackets == closeBrackets) {
                String jsonObject = buffer.substring(objectStartIndex, i + 1);
                processJsonLine(jsonObject);

                buffer.delete(0, i + 1);
                i = -1;
                lastCheckedIndex = 0;
                objectStartIndex = -1;
                openBrackets = closeBrackets = 0;
            }
        }

        lastCheckedIndex = buffer.length();
        return !responseQueue.isEmpty();
    }

    private void processJsonLine(String jsonLine) {
        try {
            GeminiResponse response = mapper.readValue(jsonLine, GeminiResponse.class);
            responseQueue.offer(response);
            log.info("Successfully processed JSON response.");
        } catch (Exception e) {
            log.error("Error parsing JSON: {}, \n Error message: {}",jsonLine, e.getMessage(), e);
        }
    }

    public void processResponses() {
        while (!responseQueue.isEmpty()) {
            GeminiResponse response = responseQueue.poll();
            if (response != null) {
                response.candidates().forEach(candidate ->
                        candidate.content().parts().forEach(part ->
                                log.info("Text part: {}", part.text())
                        )
                );

                log.info("GeminiModel org.gemini.model.model_test.Version: {}", response.modelVersion());
                log.info("Prompt Tokens: {}", response.usageMetadata().promptTokenCount());
                log.info("Total Tokens: {}", response.usageMetadata().totalTokenCount());
            }
        }
    }
}

