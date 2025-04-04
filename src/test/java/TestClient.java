import io.github.demchaav.gemini.GeminiClient;
import io.github.demchaav.gemini.GeminiConnection;
import io.github.demchaav.gemini.model.GeminiModel;
import io.github.demchaav.gemini.model.enums.VerAPI;
import io.github.demchaav.gemini.model.enums.gemini.GeminiVariation;
import io.github.demchaav.gemini.model.enums.gemini.GeminiVersion;

import java.io.IOException;

class TestClient {
    public static void main(String[] args) throws IOException {
        String message = "Могу ли запускать свое приложение java на серверах гугл?";

        var connection = GeminiConnection.builder()
                .apiKey(System.getenv("API_KEY"))
                .httpClient(GeminiConnection.DEFAULT_HTTP_CLIENT)
                .geminiModel( GeminiModel.builder()
                        .verAPI(VerAPI.V1BETA)
                        .variation(GeminiVariation._2_0)
                        .version(GeminiVersion.FLASH_LITE)
                        .build())
                .build();
        GeminiClient client = new GeminiClient(connection);
        client.generateResponse(message).ifPresent(response -> {
            System.out.println(response.asString());
        });


        System.out.println("\nFinish reasoning");
    }

}
