package io.github.demchaav.gemini;

import io.github.demchaav.gemini.request_response.content.Content;
import io.github.demchaav.gemini.request_response.content.Image;
import io.github.demchaav.gemini.request_response.content.Message;
import io.github.demchaav.gemini.request_response.content.part.Blob;
import io.github.demchaav.gemini.request_response.content.part.Part;
import io.github.demchaav.gemini.request_response.request.GeminiRequest;
import io.github.demchaav.gemini.request_response.response.GeminiResponse;
import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Consumer;

@RequiredArgsConstructor
@Slf4j
@Builder
public class GeminiClient {
    private final GeminiConnection connection;

    public Optional<GeminiResponse> generateResponse(@NonNull Message message, Image image) {
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

    public Optional<GeminiResponse> generateResponse(String prompt, Image image) {
        prompt = (prompt == null) ? "" : prompt;
        return generateResponse(new Message(prompt), image);
    }

    public Optional<GeminiResponse> generateResponse(String prompt) {
        log.info("Generating content for prompt: {}", prompt);
        var request = GeminiRequest.requestMessage(new Message(prompt));
        return connection.sendRequest(request).getResponse();
    }

    public Optional<GeminiResponse> generateResponse(Message message) {
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
}

