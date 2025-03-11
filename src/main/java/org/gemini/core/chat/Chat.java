package org.gemini.core.chat;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.gemini.core.client.GeminiClient;
import org.gemini.core.client.request_response.content.Content;
import org.gemini.core.client.request_response.request.GeminiRequest;
import org.gemini.core.client.request_response.response.GeminiResponse;

import java.io.IOException;

@Slf4j
public class Chat {
    private final GeminiClient connection;
    private final User user;

    public Chat(User user, GeminiClient connection) {
        this.user = user;
        this.connection = connection;
        log.info("Chat instance created for user: {}", user.getUserName());
    }

    public String chat(String message) throws IOException, InterruptedException {
        return chat(message, null);
    }

    public String chat(String message, Image image) throws IOException, InterruptedException {
        GeminiRequest request = image == null ? GeminiRequest.requestMessage(new Message(message), user)
                : GeminiRequest.requestImage(new Message(message == null ? "" : message), image, user);

        GeminiResponse response;
        try {
            response = connection.sendRequest(request).getResponse();
        } catch (Exception e) {
            log.error("Error while sending request to GeminiClient: ", e);
            return null;
        }

        if (response == null) {
            log.warn("Received null response from GeminiClient.");
            return null;
        }

        String responseText = response.candidates().getFirst().content().parts().getFirst().text();
        log.info("Received response: '{}'", responseText);

        if (connection.isReadyContent()) {
            user.getHistoryChat().clear();
            user.getHistoryChat().addAll(connection.takeContent());
        }
        return responseText;
    }

    public String getAnswer(@NonNull String message) {
        if (message.isBlank()) {
            log.warn("Empty or null message received.");
            return null;
        }

        log.info("User '{}' asked: '{}'", user.getUserName(), message);

        GeminiResponse response;
        try {
            response = connection.sendRequest(GeminiRequest.builder().addContent(new Content("user",message)).build()).getResponse();
        } catch (Exception e) {
            log.error("Error while processing request: ", e);
            return null;
        }

        if (response == null) {
            log.warn("Received null response.");
            return null;
        }

        String responseText = response.candidates().getFirst().content().parts().getFirst().text();
        connection.takeContent();
        log.info("Generated response: '{}'", responseText);
        return responseText;
    }
}