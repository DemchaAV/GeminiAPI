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

        String message = "Что ты можешь мне рассказать про Http запросы на Java";

        GeminiRequest request = GeminiRequest.builder()
                .addContent(Content.builder()
                        .role("user")
                        .addPart(
                                Part.builder()
                                        .text(message)
                                        .build()
                        )
                        .build())
                .build();

        client.sendRequest(request);
        try {
            client.getResponseAsStream(response -> {
                response.candidates().forEach(candidate -> {
                    candidate.content().parts().forEach(part -> {
                        System.out.print(part.text());
                    });
                });
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
