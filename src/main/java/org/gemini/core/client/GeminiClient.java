package org.gemini.core.client;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gemini.core.chat.ChatContent;
import org.gemini.core.chat.Image;
import org.gemini.core.chat.Message;
import org.gemini.core.client.request_response.content.Content;
import org.gemini.core.client.request_response.content.part.Blob;
import org.gemini.core.client.request_response.content.part.Part;
import org.gemini.core.client.request_response.request.GeminiRequest;
import org.gemini.core.client.request_response.response.GeminiResponse;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@RequiredArgsConstructor
@Slf4j
public class GeminiClient {
    private final GeminiConnection connection;

    public GeminiResponse generateResponse(@NonNull Message message, Image image) {
        GeminiRequest request = null;

        log.info("Processing message: '{}' from user", message.text());

        Content.ContentBuilder contentBuilder = Content.builder().role("user");

        if (image != null) {
            log.info("Message contains an image attachment.");
            contentBuilder.addPart(Part.builder()
                    .inlineData(Blob.builder().addBlobFromImage(image).build())
                    .build());
        }
        contentBuilder.addPart(Part.builder().text(message.text()).build());

        log.info("Generate request");
        request = GeminiRequest.builder()
                .addContent(contentBuilder.build()).build();
        return connection.sendRequest(request).getResponse();
    }

    public GeminiResponse generateResponse(String prompt, Image image) {
        prompt = (prompt == null) ? "" : prompt;
        return generateResponse(new Message(prompt), image);
    }

    public GeminiResponse generateResponse(String prompt) {
        log.info("Generating content for prompt: {}", prompt);
        var request = GeminiRequest.requestMessage(new Message(prompt));
        return connection.sendRequest(request).getResponse();
    }

    public GeminiResponse generateResponse(Message message) {
        log.info("Generating content for prompt: {}", message);
        var request = GeminiRequest.requestMessage(message);
        return connection.sendRequest(request).getResponse();
    }

    public void generateResponseAsStream(String message, Consumer<GeminiResponse> responseConsumer){
        var request = GeminiRequest.requestMessage(new Message(message));
        try {
            connection.sendRequest(request).getResponseAsStream(responseConsumer);
        } catch (IOException e) {
            log.error("Error during stream",e);
            throw new RuntimeException(e);
        }
    }

    public Map<String, ChatContent> generateContent(@NonNull Message message, Image image) {
        Map<String, ChatContent> returnMap = new HashMap<>(); // Initialize here to avoid null checks

        log.info("Generating content for prompt: {}", message);
        var request = GeminiRequest.requestMessage(message);
        long time = Instant.now().toEpochMilli();
        connection.sendRequest(request).getResponse();
        var contentRequest = request.contents();
        List<Content> contents = connection.takeContent(); // store the list into a variable.
        long responseTime = Instant.now().toEpochMilli(); // Calculate response time here!
        addContentsToMap(contents, returnMap, time);
        return returnMap;
    }

    private void addContentsToMap(List<Content> contents, Map<String, ChatContent> map, long timestamp) {
        for (Content content : contents) {
            if (content != null && content.role() != null) {
                map.put(content.role(), new ChatContent(timestamp, content));
                log.info("Role {} and content \"{}\" has been added to the map.", content.role(), content);
            } else {
                log.warn("Skipping null content or content with null role.");
            }
        }
    }

    public Map<String, ChatContent> generateContent(String prompt) {
        return generateContent(new Message(prompt), null);
    }

}

