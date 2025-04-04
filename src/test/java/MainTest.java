import io.github.demchaav.gemini.GeminiConnection;
import io.github.demchaav.gemini.model.GeminiModel;
import io.github.demchaav.gemini.model.enums.VerAPI;
import io.github.demchaav.gemini.model.enums.gemini.GeminiGenerateMethod;
import io.github.demchaav.gemini.model.enums.gemini.GeminiVariation;
import io.github.demchaav.gemini.model.enums.gemini.GeminiVersion;
import io.github.demchaav.gemini.request_response.content.Content;
import io.github.demchaav.gemini.request_response.request.GeminiRequest;

import java.util.ArrayList;
import java.util.List;

public class MainTest {
    public static void main(String[] args) {
        var client = GeminiConnection.builder()
                .apiKey(System.getenv("API_KEY"))
                .httpClient(GeminiConnection.DEFAULT_HTTP_CLIENT)
                .geminiModel(GeminiModel.builder()
                        .verAPI(VerAPI.V1BETA)
                        .variation(GeminiVariation._1_5)
                        .version(GeminiVersion.PRO_LATEST)
                        .generateMethod(GeminiGenerateMethod.GENERATE_CONTENT)
                        .build())
                .build();

        List<Content> content = new ArrayList<>();
        content.add(new Content("user", "Hello")

        );

        var request = GeminiRequest.builder()
                .contents(content)
                .build();


        client.sendRequest(request).getResponse();
        content = client.takeContent();

        request = GeminiRequest.builder()
                .contents(content)
                .build();

        client.sendRequest(request).getResponse();
        var contentString =  client.takeContentAsString();

        System.out.println(contentString);

    }
}
