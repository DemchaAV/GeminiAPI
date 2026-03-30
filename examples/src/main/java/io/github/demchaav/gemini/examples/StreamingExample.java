package io.github.demchaav.gemini.examples;

import io.github.demchaav.gemini.GeminiConnection;
import io.github.demchaav.gemini.request_response.content.Message;
import io.github.demchaav.gemini.request_response.request.GeminiRequest;

import java.io.IOException;

public final class StreamingExample {
    private StreamingExample() {
    }

    public static void main(String[] args) throws IOException {
        String apiKey = ExampleSupport.requireApiKey();
        GeminiConnection connection = ExampleSupport.textConnection(apiKey);

        connection.sendRequest(GeminiRequest.requestMessage(new Message("Explain HTTP clients in Java in plain language.")));
        connection.getResponseAsStream(response -> System.out.print(response.asString()));
    }
}
