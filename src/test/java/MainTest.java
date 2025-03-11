import org.gemini.core.client.GeminiClient;
import org.gemini.core.client.model_config.Model;
import org.gemini.core.client.request_response.content.Content;
import org.gemini.core.client.request_response.request.GeminiRequest;

import java.util.ArrayList;
import java.util.List;

public class MainTest {
    public static void main(String[] args) {
        var client = GeminiClient.builder()
                .apiKey(System.getenv("API_KEY"))
                .httpClient(GeminiClient.DEFAULT_HTTP_CLIENT)
                .defaultModel(Model.GEMINI_1_5_PRO)
                .build();

        List<Content> content = new ArrayList<>();
        content.add(new Content("user", "Hello")

        );

        var request = GeminiRequest.builder()
                .contents(content)
                .build();


        client.sendRequest(request).getResponse();
        content = client.takeContent();
        content.forEach(System.out::println);

        System.out.println("//////");
        content.add(new Content("user", "Ты можешь сгенерировать изображение?"));
        request = GeminiRequest.builder()
                .contents(content)
                .build();

        client.sendRequest(request).getResponse();
        content = client.takeContent();
        content.forEach(System.out::println);


    }
}
